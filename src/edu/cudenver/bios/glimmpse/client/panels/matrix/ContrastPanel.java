package edu.cudenver.bios.glimmpse.client.panels.matrix;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.GlimmpseConstants;
import edu.cudenver.bios.glimmpse.client.listener.CovariateListener;
import edu.cudenver.bios.glimmpse.client.listener.MatrixResizeListener;
import edu.cudenver.bios.glimmpse.client.panels.WizardStepPanel;

public class ContrastPanel extends WizardStepPanel
implements CovariateListener, MatrixResizeListener
{    
    protected ResizableMatrix betweenSubjectFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST_FIXED,
    			GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_Q, "0", "Between Subject (C)<br>Fixed"); 
    protected ResizableMatrix betweenSubjectRandom = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST_RANDOM,
    			GlimmpseConstants.DEFAULT_A, 
    			1, "0", "Between Subject (C)<br>Random"); 
    protected ResizableMatrix withinSubject = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_WITHIN_CONTRAST,
    			GlimmpseConstants.DEFAULT_P, 
    			GlimmpseConstants.DEFAULT_B, "0", "Within Subject"); 
	
    protected int betweenSubjectMaxRows = GlimmpseConstants.DEFAULT_A;
	public ContrastPanel()
	{
		super(Glimmpse.constants.stepsLeftContrast());
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML("Specify your between and within subject contrasts");
        HTML description = new HTML("Write some contrasts, yo.");

        // layout the fixed and random portions of the C contrast matrix
        Grid betweenGrid = new Grid(1,2);
        betweenGrid.setWidget(0, 0, betweenSubjectFixed);
        betweenGrid.setWidget(0, 1, betweenSubjectRandom);
        panel.add(header);
        panel.add(description);
        panel.add(betweenGrid);
        panel.add(withinSubject);
        betweenSubjectRandom.setVisible(false);
        betweenSubjectFixed.addMatrixResizeListener(new MatrixResizeListener() {
			public void onColumns(String name, int newCols) {}
			
			public void onRows(String name, int newRows)
			{
				betweenSubjectRandom.setColumnDimension(newRows);	
			}
        });
		notifyComplete();
		initWidget(panel);
	}


	
	public void reset()
	{
		
	}

	@Override
	public void onHasCovariate(boolean hasCovariate)
	{
		betweenSubjectRandom.setVisible(hasCovariate);		
	}

	@Override
	public void onMean(double mean)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVariance(double variance)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onColumns(String name, int newCols)
	{
		if (GlimmpseConstants.MATRIX_DESIGN_FIXED.equals(name))
		{
			betweenSubjectFixed.setColumnDimension(newCols);
		}
		else if (GlimmpseConstants.MATRIX_BETA_FIXED.equals(name))
		{
			withinSubject.setRowDimension(newCols);
		}		
	}

	@Override
	public void onRows(String name, int newRows)
	{
		if (GlimmpseConstants.MATRIX_DESIGN_FIXED.equals(name))
		{
			betweenSubjectMaxRows = newRows - 1;
			if (betweenSubjectFixed.getRowDimension() > newRows - 1)
			{
				betweenSubjectFixed.setRowDimension(newRows - 1);
			}
		}		
	}
	
	public void addBetweenSubjectMatrixResizeListener(MatrixResizeListener listener)
	{
		betweenSubjectFixed.addMatrixResizeListener(listener);
	}
	
	public void addWithinSubjectMatrixResizeListener(MatrixResizeListener listener)
	{
		withinSubject.addMatrixResizeListener(listener);
	}
}