package edu.cudenver.bios.glimmpse.client.panels;

import java.util.HashMap;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.StudyDesignManager;
import edu.cudenver.bios.glimmpse.client.listener.OptionsListener;
import edu.cudenver.bios.glimmpse.client.listener.SolvingForListener;

public class ResultsDisplayPanel extends WizardStepPanel
implements OptionsListener, SolvingForListener
{	
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_CREATED = 201;
	private static final String POWER_URL = "/webapps/power/power";
	private static final String SAMPLE_SIZE_URL = "/webapps/power/samplesize";
	private static final String EFFECT_SIZE_URL = "/webapps/power/difference";
	private static final String CURVE_URL = "/webapps/chart/scatter";
	private static final String IMAGE_FRAME_NAME_SUFFIX = "PowerCurveFrame";
	private static final String STYLE_POWER_CURVE_FRAME = "powerCurveFrame";
	private static final String CHART_INPUT_NAME = "chart";

	private NumberFormat doubleFormatter = NumberFormat.getFormat("0.0000");

	// wait dialog
	protected DialogBox waitDialog;

	// google visualization api data table to hold results
	protected DataTable resultsData; 

	// tabular display of results
	protected VerticalPanel resultsTablePanel = new VerticalPanel();
	protected Table resultsTable = new Table(resultsData, null);

	// curve display
	protected VerticalPanel resultsCurvePanel = new VerticalPanel();
	protected HashMap<String,StringBuffer> curveXMLByColumn = new HashMap<String,StringBuffer>();
	//	protected Table resultsCurveTable; 

	// we have to use a form submission to display image data
	// I tried to build the curves with Google Chart Api, but the scatter chart
	// didn't have enough control over line types, etc.  Thus, I rolled my own
	// restlet on top of JFreeChart
	// hidden iframe to hold the power curve image data
	protected NamedFrame imageFrame;
	protected FormPanel curveForm;
	protected Hidden curveEntityBodyHidden = new Hidden(CHART_INPUT_NAME);

	// options for display of data
	protected boolean showTable = true;
	protected boolean showCurve = true;
	protected XAxisType xaxisType = XAxisType.TOTAL_N;
	protected CurveSubset[] curveSubsets = null;
	// indicates whether we are solving for power, sample size, or effect size
	protected SolutionType solutionType = GlimmpseConstants.DEFAULT_SOLUTION;
	// pointer to the overall wizard panel which can generate the entity body from the other panels
	protected StudyDesignManager manager;

	public ResultsDisplayPanel(StudyDesignManager manager)
	{
		super(Glimmpse.constants.stepsLeftResults());
		this.manager = manager;
		complete = true;
		VerticalPanel panel = new VerticalPanel();

		// build the wait dialog
		buildWaitDialog();
		// build the data table 
		buildDataTable();
		
		// build the display panels
		/* WARNING: this named frame must have a unique name or the curve will not display */
		imageFrame = new NamedFrame(manager.getModeName() + IMAGE_FRAME_NAME_SUFFIX);
		curveForm = new FormPanel(imageFrame);
		
		resultsTablePanel.add(new HTML("Results"));
		resultsTablePanel.add(resultsTable);
		resultsCurvePanel.add(new HTML("Curves"));
		resultsCurvePanel.add(imageFrame);

		// setup the form for submitting curve requests
		curveForm.setAction(CURVE_URL);
		curveForm.setMethod(FormPanel.METHOD_POST);
		VerticalPanel formContainer = new VerticalPanel();
		formContainer.add(curveEntityBodyHidden);
		curveForm.add(formContainer);
		
		// layout the panel
		panel.add(resultsCurvePanel);
		panel.add(resultsTablePanel);        
		panel.add(curveForm);

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		imageFrame.setStyleName(STYLE_POWER_CURVE_FRAME);
		//onShowCurve(true, XAxisType.TOTAL_N, null);

		initWidget(panel);
	}

	@Override
	public void reset()
	{
		curveXMLByColumn.clear();
		resultsData.removeRows(0, resultsData.getNumberOfRows());
		resultsTablePanel.setVisible(false);
		resultsCurvePanel.setVisible(false);
	}

	@Override
	public void onEnter()
	{
		reset();
		sendPowerRequest();
	}

	private void buildDataTable()
	{
		// set up the columns in the data table
		resultsData = DataTable.create();
		resultsData.addColumn(ColumnType.STRING, "Test", "test");
		resultsData.addColumn(ColumnType.NUMBER, "Actual Power", "actualPower");
		resultsData.addColumn(ColumnType.NUMBER, "Total Sample Size", "sampleSize");
		resultsData.addColumn(ColumnType.NUMBER, "&beta; Scale", "betaScale");
		resultsData.addColumn(ColumnType.NUMBER, "&Sigma; Scale", "sigmaScale");
		resultsData.addColumn(ColumnType.NUMBER, "Alpha", "alpha");
		resultsData.addColumn(ColumnType.NUMBER, "Nominal Power", "nominalPower");
		resultsData.addColumn(ColumnType.STRING, "Power Method", "powerMethod");
		resultsData.addColumn(ColumnType.NUMBER, "Quantile", "quantile");
	}

	private void buildWaitDialog()
	{
		waitDialog = new DialogBox();
		HTML text = new HTML("Processing, Please Wait...");
		text.setStyleName("waitDialogText");
		waitDialog.setStyleName("waitDialog");
		waitDialog.setWidget(text);
	}

	private void showWorkingDialog()
	{
		waitDialog.center();
	}

	private void hideWorkingDialog()
	{
		waitDialog.hide();
	}

	private void showError(String message)
	{
		Window.alert("ERROR: " + message);
	}

	private void showResults(String resultXML)
	{
		try
		{
			// parse the returned XML
			Document doc = XMLParser.parse(resultXML);
			NodeList powerListTags = doc.getElementsByTagName("powerList");
			if (powerListTags == null || powerListTags.getLength() != 1)
				throw new IllegalArgumentException("No results returned");
			NamedNodeMap attrList = powerListTags.item(0).getAttributes();
			if (attrList == null)
				throw new IllegalArgumentException("Invalid response from power server");

			Node countAttr = attrList.getNamedItem("count");
			if (countAttr == null)
				throw new IllegalArgumentException("Invalid response from power server");

			int count = Integer.parseInt(countAttr.getNodeValue());

			// fill the google visualization data table
			NodeList glmmPowerList = doc.getElementsByTagName("glmmPower");
			for(int i = 0; i < count; i++)
			{
				Node glmmPower = glmmPowerList.item(i);
				NamedNodeMap attrs = glmmPower.getAttributes();

				// add a blank row to the data table
				int row = resultsData.addRow();
				StringBuffer curveColumnId = new StringBuffer();

				// fill in the columns
				int col = 0;

				Node testNode = attrs.getNamedItem("test");
				if (testNode != null) 
				{
					resultsData.setCell(row, col, testNode.getNodeValue(), 
							formatTestName(testNode.getNodeValue()), null);
					curveColumnId.append("Test=");
					curveColumnId.append(formatTestName(testNode.getNodeValue()));
				}
				col++;

				Node actualPowerNode = attrs.getNamedItem("actualPower");
				if (actualPowerNode != null) 
				{
					resultsData.setCell(row, col, Double.parseDouble(actualPowerNode.getNodeValue()), 
							formatDouble(actualPowerNode.getNodeValue()), null);
				}
				col++;

				Node sampleSizeNode = attrs.getNamedItem("sampleSize");
				if (sampleSizeNode != null) 
				{
					resultsData.setCell(row, col, Integer.parseInt(sampleSizeNode.getNodeValue()), 
							sampleSizeNode.getNodeValue(), null);
					if (xaxisType != XAxisType.TOTAL_N && solutionType != SolutionType.TOTAL_N)
					{
						curveColumnId.append("\nSample Size=");
						curveColumnId.append(sampleSizeNode.getNodeValue());
					}
				}
				col++;

				Node betaScaleNode = attrs.getNamedItem("betaScale");
				if (betaScaleNode != null) 
				{
					resultsData.setCell(row, col, Double.parseDouble(betaScaleNode.getNodeValue()), 
							betaScaleNode.getNodeValue(), null);
					if (xaxisType != XAxisType.EFFECT_SIZE && solutionType != SolutionType.EFFECT_SIZE)
					{
						curveColumnId.append(",Effect Size Scale=");
						curveColumnId.append(betaScaleNode.getNodeValue());
					}
				}
				col++;

				Node sigmaScaleNode = attrs.getNamedItem("sigmaScale");
				if (sigmaScaleNode != null) 
				{
					resultsData.setCell(row, col, Double.parseDouble(sigmaScaleNode.getNodeValue()), 
							sigmaScaleNode.getNodeValue(), null);
					if (xaxisType != XAxisType.VARIANCE)
					{
						curveColumnId.append(",Variance Scale=");
						curveColumnId.append(sigmaScaleNode.getNodeValue());
					}
				}
				col++;     	

				Node alphaNode = attrs.getNamedItem("alpha");
				if (alphaNode != null) 
				{
					resultsData.setCell(row, col, Double.parseDouble(alphaNode.getNodeValue()), 
							alphaNode.getNodeValue(), null);
					curveColumnId.append("Alpha=");
					curveColumnId.append(alphaNode.getNodeValue());
				}
				col++;

				Node nominalPowerNode = attrs.getNamedItem("nominalPower");
				if (nominalPowerNode != null) 
				{
					resultsData.setCell(row, col, Double.parseDouble(nominalPowerNode.getNodeValue()), 
							formatDouble(nominalPowerNode.getNodeValue()), null);
					if (solutionType != SolutionType.POWER)
					{
						curveColumnId.append(",Nominal Power=");
						curveColumnId.append(nominalPowerNode.getNodeValue());
					}
				}
				col++;       

				Node powerMethodNode = attrs.getNamedItem("powerMethod");
				if (powerMethodNode != null) 
				{
					resultsData.setCell(row, col, powerMethodNode.getNodeValue(), 
							formatPowerMethodName(powerMethodNode.getNodeValue()), null);
					curveColumnId.append(",Power Method=");
					curveColumnId.append(formatPowerMethodName(powerMethodNode.getNodeValue()));
				}
				col++;

				Node quantileNode = attrs.getNamedItem("quantile");
				if (quantileNode != null) 
				{
					resultsData.setCell(row, col, Double.parseDouble(quantileNode.getNodeValue()), 
							quantileNode.getNodeValue(), null);
					curveColumnId.append(",Quantile=");
					curveColumnId.append(quantileNode.getNodeValue());
				}

				// update the series XML as we go
				StringBuffer buffer = (StringBuffer) curveXMLByColumn.get(curveColumnId.toString());
				if (buffer == null) 
				{
					buffer = new StringBuffer();
					buffer.append("<series label='");
					buffer.append(curveColumnId);
					buffer.append("'>");
					curveXMLByColumn.put(curveColumnId.toString(), buffer);
				}
				buffer.append("<p y='");
				buffer.append(Double.parseDouble(actualPowerNode.getNodeValue()));
				buffer.append("' x='");
				switch(xaxisType)
				{
				case TOTAL_N:
					buffer.append(Double.parseDouble(sampleSizeNode.getNodeValue()));
					break;
				case EFFECT_SIZE:
					buffer.append(Double.parseDouble(betaScaleNode.getNodeValue()));
					break;
				case VARIANCE:
					buffer.append(Double.parseDouble(sigmaScaleNode.getNodeValue()));
					break;
				}
				buffer.append("' />");
			}            	

			if (showCurve)
			{
				showCurveResults();
			}
			if (showTable)
			{
				resultsTable.draw(resultsData);
				resultsTablePanel.setVisible(true);
			}
		}
		catch (Exception e)
		{
			showError(e.getMessage());
		}
	}

	private void showCurveResults()
	{
		// build the full chart xml
		StringBuffer buffer = new StringBuffer();

		buffer.append("<chart title='Power Curve' legend='false' >");
		buffer.append("<yaxis label='Power' />");
		buffer.append("<xaxis label='"); 
		switch(xaxisType)
		{
		case TOTAL_N:
			buffer.append("Total Sample Size");
			break;
		case EFFECT_SIZE:
			buffer.append("Effect Size Scale Factor");
			break;
		case VARIANCE:
			buffer.append("Variance Scale Factor");
			break;
		}
		buffer.append("' />");
		for(StringBuffer columnBuffer: curveXMLByColumn.values())
		{
			buffer.append(columnBuffer);
			buffer.append("</series>");
		}

		buffer.append("</chart>");
		
		// submit the result to the chart service
		curveEntityBodyHidden.setValue(buffer.toString());
		curveForm.submit();
		resultsCurvePanel.setVisible(true);
	}


	private String formatPowerMethodName(String name)
	{
		return name; // TODO
	}

	private String formatTestName(String name)
	{
		return name; // TODO
	}

	private String formatDouble(String valueStr)
	{
		try
		{
			double value = Double.parseDouble(valueStr);
			return doubleFormatter.format(value);
		}
		catch (Exception e)
		{
			return valueStr;
		}
	}

	private void sendPowerRequest()
	{
		showWorkingDialog();
		String requestEntityBody = manager.getPowerRequestXML();
		//Window.alert(requestEntityBody);
		RequestBuilder builder = null;
		switch(solutionType)
		{
		case POWER:
			builder = new RequestBuilder(RequestBuilder.POST, POWER_URL);
			break;
		case TOTAL_N:
			builder = new RequestBuilder(RequestBuilder.POST, SAMPLE_SIZE_URL);
			break;
		case EFFECT_SIZE:
			builder = new RequestBuilder(RequestBuilder.POST, EFFECT_SIZE_URL);
			break;
		}

		try 
		{
			builder.setHeader("Content-Type", "text/xml");
			builder.sendRequest(requestEntityBody, new RequestCallback() {

				public void onError(Request request, Throwable exception) 
				{
					hideWorkingDialog();
					showError("Calculation failed: " + exception.getMessage());	
				}

				public void onResponseReceived(Request request, Response response) 
				{
					hideWorkingDialog();
					if (STATUS_CODE_OK == response.getStatusCode() ||
							STATUS_CODE_CREATED == response.getStatusCode()) 
					{
						showResults(response.getText());
					} 
					else 
					{
						showError("Calculation failed: [HTTP STATUS " + 
								response.getStatusCode() + "] " + response.getText());
					}
				}
			});
		} 
		catch (Exception e) 
		{
			hideWorkingDialog();
			showError("Failed to send the request: " + e.getMessage());
		}
	}

	@Override
	public void onShowCurve(boolean showCurve, XAxisType axis,
			CurveSubset[] curveSubsets)
	{
		this.showCurve = true;
		this.xaxisType = axis;
		this.curveSubsets = curveSubsets;
	}

	@Override
	public void onShowTable(boolean showTable)
	{
		this.showTable = showTable;
	}

	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		this.solutionType = solutionType;
	}    

}