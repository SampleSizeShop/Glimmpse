package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.listener.VariabilityListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class SigmaCovariateMatrixPanel extends WizardStepPanel
implements CovariateListener, MatrixResizeListener
{
    protected ResizableMatrix sigmaG = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_COVARIATE,
    			1, 1, "0", "&Sigma; G (Covariate)"); 
    protected ArrayList<VariabilityListener> listeners = new ArrayList<VariabilityListener>();
    
    public SigmaCovariateMatrixPanel()
    {
		super();
		// regardless of input, forward navigation is allowed from this panel
		complete = true;
		skip = true;
        HTML header = new HTML("Error variability");
        HTML description = new HTML("Enter the error covariance matrix below");
		VerticalPanel panel = new VerticalPanel();
		
        panel.add(header);
        panel.add(description);
		panel.add(sigmaG);        
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
    }
    
	@Override
	public void reset()
	{
		sigmaG.reset(1, 1);
	}

	@Override
	public void loadFromNode(Node node)
	{
		sigmaG.loadFromDomNode(node);
	}
	
	public String toXML()
	{
		if (skip)
			return "";
		else
			return sigmaG.toXML(GlimmpseConstants.MATRIX_SIGMA_COVARIATE);
	}

	@Override
	public void onRows(String name, int newRows)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		skip = !hasCovariate;
	}

    public void addVariabilityListener(VariabilityListener listener)
    {
        listeners.add(listener);
    }
    
    @Override 
    public void onExit()
    {
    	for(VariabilityListener listener: listeners) listener.onCovariateVariance(sigmaG.getData(0,0));
    }
}
