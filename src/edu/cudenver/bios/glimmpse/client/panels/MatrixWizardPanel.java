/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.panels.matrix.BetaPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.ContrastPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.CovariancePanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.DesignPanel;
import edu.cudenver.bios.glimmpse.client.panels.matrix.ThetaPanel;

/**
 * Wizard panel for matrix input mode.  Contains separate steps
 * for each type of matrix required for power calculations on the GLMM
 */
public class MatrixWizardPanel extends Composite
{
	// content panels 
    protected AlphaPanel alphaPanel = new AlphaPanel();
    protected DesignPanel designPanel = new DesignPanel();
    protected ContrastPanel contrastPanel = new ContrastPanel();
    protected BetaPanel betaPanel = new BetaPanel();
    protected ThetaPanel thetaPanel = new ThetaPanel();
    protected CovariancePanel covariancePanel = new CovariancePanel();
    protected OptionsPanel optionsPanel = new OptionsPanel();
    protected ResultsPanel resultsPanel = new ResultsPanel();
	
    // list of panels for the wizard
	WizardStepPanel[] panelList = {
			alphaPanel, 
			designPanel, 
			contrastPanel, 
			betaPanel, 
			thetaPanel,
			covariancePanel,
			optionsPanel,
			resultsPanel};
	
	// wizard navigation panel
	WizardPanel wizardPanel;
	
	/**
	 * Create an empty matrix panel
	 */
	public MatrixWizardPanel()
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
