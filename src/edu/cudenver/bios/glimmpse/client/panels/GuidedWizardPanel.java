package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.listener.NavigationListener;
import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;
import edu.cudenver.bios.glimmpse.client.panels.guided.EffectSizePanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.HypothesisPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.OutcomesPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.PredictorsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.StudyGroupsPanel;
import edu.cudenver.bios.glimmpse.client.panels.guided.VariabilityPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.BetaPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.ContrastPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.CovariancePanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.DesignPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.ThetaPanel;

public class GuidedWizardPanel extends Composite
{
	// content panels 
	AlphaPanel alphaPanel = new AlphaPanel();
	OutcomesPanel outcomesPanel = new OutcomesPanel();
	PredictorsPanel predictorsPanel = new PredictorsPanel();
	StudyGroupsPanel studyGroupsPanel = new StudyGroupsPanel();
	HypothesisPanel hypothesisPanel = new HypothesisPanel();
	EffectSizePanel effectSizePanel = new EffectSizePanel();
	VariabilityPanel variabilityPanel = new VariabilityPanel();
	OptionsPanel optionsPanel = new OptionsPanel();
	ResultsPanel resultsPanel = new ResultsPanel();
	
    // list of panels for the wizard
	WizardStepPanel[] panelList = {
			alphaPanel, 
			outcomesPanel, 
			predictorsPanel, 
			studyGroupsPanel, 
			hypothesisPanel,
			effectSizePanel,
			variabilityPanel,
			optionsPanel,
			resultsPanel};
	
	// wizard navigation panel
	WizardPanel wizardPanel;
	
	/**
	 * Create an empty matrix panel
	 */
	public GuidedWizardPanel()
	{	
		VerticalPanel panel = new VerticalPanel();
		
		wizardPanel = new WizardPanel(panelList);
		panel.add(wizardPanel);

		// initialize
		initWidget(panel);
	}
	
	/**
	 * Fill in the wizard from an XML description of the study matrices
	 */
	public void loadFromXML(Document doc)
	{
		
	}
    
    public void reset()
    {
    	wizardPanel.reset();
    }
}
