package edu.cudenver.bios.glimmpse.client.panels;

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

public class ResultsPanel extends WizardStepPanel
implements OptionsListener, SolvingForListener
{
	protected StudyDesignManager manager;
	
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_CREATED = 201;
	private static final String POWER_URL = "/webapps/power/power";
    private static final String SAMPLE_SIZE_URL = "/webapps/power/samplesize";
	private static final String EFFECT_SIZE_URL = "/webapps/power/difference";
	private static final String CURVE_URL = "http://localhost:10080/chart/scatter";
	// private static final String CURVE_URL = "/webapps/chart/scatter";
	private static final String CURVE_TARGET = "powerCurveFrame";
	private static final String CHART_INPUT_NAME = "chart";
	
	private NumberFormat doubleFormatter = NumberFormat.getFormat("0.0000");
	
	// wait dialog
	protected DialogBox waitDialog;
	
	// google visualization api data table to hold results
	protected DataTable resultsData; 
	// we build a second data table with a column for each combination of 
	// beta/sigma/n/etc. for use with the curve
	protected DataTable resultsCurveData;
	
	// tabular display of results
	protected VerticalPanel resultsTablePanel = new VerticalPanel();
	protected Table resultsTable = new Table(resultsData, null);
	
	// curve display
	protected VerticalPanel resultsCurvePanel = new VerticalPanel();
//	protected Table resultsCurveTable; 

	// we have to use a form submission to display image data
	// I tried to build the curves with Google Chart Api, but the scatter chart
	// didn't have enough control over line types, etc.  Thus, I rolled my own
	// restlet on top of JFreeChart
	protected ImageFramePanel imageFrame = new ImageFramePanel(CURVE_TARGET);
	protected FormPanel curveForm = new FormPanel(imageFrame.getFrame()); 
	protected Hidden curveEntityBodyHidden = new Hidden(CHART_INPUT_NAME);
	
	// options for display of data
	protected boolean showTable = true;
	protected boolean showCurve = true;
	protected XAxisType xaxisType = null;
	protected CurveSubset[] curveSubsets = null;
	// indicates whether we are solving for power, sample size, or effect size
	protected SolutionType solutionType = GlimmpseConstants.DEFAULT_SOLUTION;
	
    public ResultsPanel(StudyDesignManager manager)
    {
    	super(Glimmpse.constants.stepsLeftResults());
    	this.manager = manager;
    	
    	// build the wait dialog
    	buildWaitDialog();
    	
    	// build the data table 
    	buildDataTable();
    	
    	// build the display panels
        buildTablePanel();
        buildCurvePanel();
    	
    	//resultsCurveTable = new Table(resultsCurveData, null);

        VerticalPanel panel = new VerticalPanel();
        
        panel.add(curveForm);
        panel.add(resultsCurvePanel);
        panel.add(resultsTablePanel);
        
        initWidget(panel);
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
    	
    	resultsCurveData = DataTable.create();
    }
    
    private void buildTablePanel()
    {
    	resultsTablePanel.add(new HTML("Results"));
    	resultsTablePanel.add(resultsTable);
    }
    
    private void buildCurvePanel()
    {
        VerticalPanel formContainer = new VerticalPanel();
        formContainer.add(curveEntityBodyHidden);
        curveForm.add(formContainer);
    	curveForm.setAction(CURVE_URL);
        curveForm.setMethod(FormPanel.METHOD_POST);
        
        resultsCurvePanel.add(new HTML("Curves"));
    	resultsCurvePanel.add(curveForm);
    	resultsCurvePanel.add(imageFrame);

    	// set style 
    	imageFrame.setStyleName("powerCurveFrame");
    	
//    	resultsCurvePanel.add(curveGrid);
//    	resultsCurvePanel.add(resultsCurveTable);
    }
    
    public void reset()
    {
    	resultsData.removeRows(0, resultsData.getNumberOfRows());
		resultsCurveData.removeRows(0, resultsCurveData.getNumberOfRows());
    }

    @Override
    public void onEnter()
    {
    	reset();
    	sendPowerRequest();
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
            		curveColumnId.append(",Test=");
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

            	// now add the data as a new column to the curve display
            	if (showCurve)
            	{
            		switch(xaxisType)
            		{
            		case TOTAL_N:
                		fillCurveData(Double.parseDouble(sampleSizeNode.getNodeValue()), 
            					Double.parseDouble(actualPowerNode.getNodeValue()), 
            					curveColumnId.toString());
            			break;
            		case EFFECT_SIZE:
                		fillCurveData(Double.parseDouble(betaScaleNode.getNodeValue()), 
            					Double.parseDouble(actualPowerNode.getNodeValue()), 
            					curveColumnId.toString());
            			break;
            		case VARIANCE:
                		fillCurveData(Double.parseDouble(sigmaScaleNode.getNodeValue()), 
            					Double.parseDouble(actualPowerNode.getNodeValue()), 
            					curveColumnId.toString());
            			break;
            		}

            	}            	
            }
            
        	if (showTable)
        	{
        		resultsTable.draw(resultsData);
        	}
        	
        	if (showCurve)
        	{
        		showCurveResults();
        	}
            
        }
        catch (Exception e)
        {
        	showError(e.getMessage());
        }
    }
    
    
    private void fillCurveData(double xValue, double power, String columnId)
    {
    	int row = insertCurveData(0, xValue);
    	insertCurveColumn(row, columnId, power);
    }
    
    private void insertCurveColumn(int curveResultsRow, String columnId, double power)
    {
    	boolean found = false;
    	int col = 1;
    	for(; col < resultsCurveData.getNumberOfColumns(); col++)
    	{
    		String currentColumnId = resultsCurveData.getColumnId(col);
    		if (currentColumnId.equals(columnId))
    		{
    			resultsCurveData.setCell(curveResultsRow, col, power, Double.toString(power), null);
    			found = true;
    			break;
    		}
    	}
    	if (!found)
    	{
    		int column = resultsCurveData.addColumn(ColumnType.NUMBER, columnId, columnId);
    		resultsCurveData.setCell(curveResultsRow, column, power, Double.toString(power), null);
    	}
    }
    
    private int insertCurveData(int column, double newValue)
    {
    	int row = 0;
    	for(; row < resultsCurveData.getNumberOfRows(); row++)
    	{
    		double currentValue = resultsCurveData.getValueDouble(row, column);
    		if (currentValue > newValue)
    		{
    			resultsCurveData.insertRows(row, 1);
    	    	resultsCurveData.setCell(row, column, newValue, Double.toString(newValue), null);
    	    	return row;
    		}
    		else if (currentValue == newValue)
    		{
    			return row;
    		}
    	}
    	row = resultsCurveData.addRow();
    	resultsCurveData.setCell(row, column, newValue, Double.toString(newValue), null);
    	return row;
    }
        
    private void showCurveResults()
    {
        curveEntityBodyHidden.setValue(curveDataToXML());
        curveForm.submit();
        
//    	resultsCurveTable.draw(resultsCurveData);
//    	switch(xaxisType)
//    	{
//    	case TOTAL_N:
//    		scatterChartOptions.setTitleX("Total Sample Size");
//    		break;
//    	case EFFECT_SIZE:
//    		scatterChartOptions.setTitleX("Effect Size (scale factor)");
//    		break;
//    	case VARIANCE:
//    		scatterChartOptions.setTitleX("Variance (scale factor)");
//    		break;
//    	}
//    	ScatterChart chart = new ScatterChart(resultsCurveData, scatterChartOptions);
//    	curveGrid.setWidget(0, 0, chart);
    }
    
    private String curveDataToXML()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<chart title='Power Curve' legend='true' >");
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
        for(int c = 0; c < resultsCurveData.getNumberOfColumns(); c++)
        {
            buffer.append("<series label='");
            buffer.append(resultsCurveData.getColumnLabel(c));
            buffer.append("'>");
            for(int r = 0; r < resultsCurveData.getNumberOfRows(); r++)
            {
                buffer.append("<d>");
                buffer.append(resultsCurveData.getValueDouble(r, c));
                buffer.append("</d>");
            }
            buffer.append("</series>");

        }
        
        buffer.append("</chart>");

        return buffer.toString();
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
    	Window.alert(requestEntityBody);
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
		resultsCurvePanel.setVisible(showCurve);
		
		resultsCurveData.removeColumns(0, resultsCurveData.getNumberOfColumns());
    	switch(xaxisType)
    	{
    	case TOTAL_N:
    		resultsCurveData.addColumn(ColumnType.NUMBER, "Total Sample Size", "totalN");
    		break;
    	case EFFECT_SIZE:
    		resultsCurveData.addColumn(ColumnType.NUMBER, "Beta Scale", "betaScale");
    		break;
    	case VARIANCE:
    		resultsCurveData.addColumn(ColumnType.NUMBER, "Sigma Scale", "sigmaScale");
    		break;    		
    	}
	}

	@Override
	public void onShowTable(boolean showTable)
	{
		this.showTable = showTable;
		resultsTablePanel.setVisible(showTable);
		
	}

	@Override
	public void onSolvingFor(SolutionType solutionType)
	{
		this.solutionType = solutionType;
	}    
    
}
