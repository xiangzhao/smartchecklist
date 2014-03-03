package laser.littlejil.smartchecklist.gui;

import laser.juliette.ams.AMSException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

public class RecurrenceTab extends StepInfoTabItem{
	
	public RecurrenceTab(StepInfoPanel stepInfoPanel) {
		super(stepInfoPanel, "History", null);
		this.content_.setLayoutData(new GridData());
	}

	public void draw(){
		
	}

	public void dispose() {
		//TODO implement
	}
}
