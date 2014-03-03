package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.layout.GridData;

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
