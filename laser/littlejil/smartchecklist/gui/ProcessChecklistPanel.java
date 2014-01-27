package laser.littlejil.smartchecklist.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import laser.littlejil.smartchecklist.gui.model.Activity;
import laser.littlejil.smartchecklist.gui.model.Process;


public class ProcessChecklistPanel implements PropertyChangeListener 
{
	private Process processModel_;
	private ProcessPanel processPanel_;
	private Composite checklistPanel_;
	private List<ActivityPanel> activityPanels_;
	
	
	public ProcessChecklistPanel(ProcessPanel processPanel) {
		super();
		
		this.processPanel_ = processPanel;
		this.checklistPanel_ = new Composite(this.processPanel_.getProcessPanel(), SWT.NONE);
		this.checklistPanel_.setLayout(new GridLayout());
		GridData checklistPanelGridData = new GridData();
		checklistPanelGridData.horizontalAlignment = GridData.FILL;
		checklistPanelGridData.grabExcessHorizontalSpace = true;
		this.checklistPanel_.setLayoutData(checklistPanelGridData);
		this.activityPanels_ = new ArrayList<ActivityPanel>();
		this.checklistPanel_.pack();
		
		this.processModel_ = this.processPanel_.getProcessModel();
		this.processModel_.addPropertyChangeListener(this);
	}
	
	protected ProcessPanel getProcessPanel() {
		return this.processPanel_;
	}
	
	protected Composite getChecklistPanel() {
		return this.checklistPanel_;
	}

	public synchronized ActivityPanel addActivity(Activity newActivity) {
		ActivityPanel parentActivityPanel = this.getActivityPanel(newActivity.getParent());
		ActivityPanel childActivityPanel = new ActivityPanel(this, parentActivityPanel, newActivity);
		
		this.activityPanels_.add(childActivityPanel);
		
		return childActivityPanel;
	}
	
	public ActivityPanel getActivityPanel(Activity activity) {
		for (ActivityPanel currentActivityPanel : this.activityPanels_) {
			//System.out.println("Getting ActivityPanel for activity " + activity);
			if (currentActivityPanel.getActivity() == activity) {
				//System.out.println("\tFound ActivityPanel!");
				return currentActivityPanel;
			}
		} // end for currentActivityPanel

		//System.out.println("\tNot found ActivityPanel!");
		return null;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(Process.ACTIVITIES_PROPERTY_NAME)) {
			Activity newActivity = (Activity)event.getNewValue();
			this.addActivity(newActivity);
		}
	}
}
