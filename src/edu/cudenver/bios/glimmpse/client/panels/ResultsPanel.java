package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;

public class ResultsPanel extends WizardStepPanel
{
    public ResultsPanel()
    {
    	super(Glimmpse.constants.stepsLeftResults());
        VerticalPanel panel = new VerticalPanel();
        
        initWidget(panel);
    }
    
    public void reset()
    {
    	
    }
}
