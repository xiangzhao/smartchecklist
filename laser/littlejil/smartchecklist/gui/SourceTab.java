package laser.littlejil.smartchecklist.gui;

import laser.juliette.ams.AMSException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.List;

public class SourceTab extends StepInfoTabItem {
	
	public SourceTab(StepInfoPanel stepInfoPanel){
		super(stepInfoPanel, "Source", null);
	}
	
	public void draw(){
		List list = new List(this.content_, SWT.BORDER);
		list.add("hi");
		list.add("banana");
		list.add("toothbrush");
		list.setBounds(0, 0, 100, 248);

		StyledText styledText = new StyledText(this.content_, SWT.BORDER);
		String activityName = null;
		try {
			activityName = this.activity_.getAgendaItem().getStep().getName();
		} catch (AMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		styledText.setText(activityName);
		styledText.setBounds(106, 10, 326, 228);
	}

	public void dispose() {
		// TODO Implement
	}
	
}
