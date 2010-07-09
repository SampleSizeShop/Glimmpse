package edu.cudenver.bios.glimmpse.client.panels.matrix;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public class CovariancePanel extends Composite
{
    protected ArrayList<StepStatusListener> stepStatusListeners = new ArrayList<StepStatusListener>();
    protected String name;
    
	public CovariancePanel(String name)
	{
		this.name = name;
		VerticalPanel panel = new VerticalPanel();
		
		panel.add(new HTML("covariance panel"));
		
		initWidget(panel);
	}
	
	
	
    public void addStepStatusListener(StepStatusListener listener)
    {
    	stepStatusListeners.add(listener);
    }
}
