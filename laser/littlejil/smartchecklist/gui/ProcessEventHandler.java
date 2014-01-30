package laser.littlejil.smartchecklist.gui;

import java.util.ArrayList;
import java.util.List;

import laser.juliette.agent.ItemHandlerAdapter;
import laser.juliette.ams.AMSException;
import laser.juliette.ams.AgendaItem;
import laser.littlejil.smartchecklist.gui.model.ActivityFilter;
import laser.littlejil.smartchecklist.gui.model.AllActivityFilter;
import laser.littlejil.smartchecklist.gui.model.OnlyLeavesActivityFilter;
import laser.littlejil.smartchecklist.gui.model.Process;
import laser.littlejil.smartchecklist.gui.model.RefactoringActivityFilter;


public class ProcessEventHandler extends ItemHandlerAdapter
{	
	private Process processModel_;
	private ActivityFilter activityFilter_;
	private ProcessPanel processView_;
	private AgendaItem agendaItem_;
	
	
	public ProcessEventHandler(ProcessPanel gui, AgendaItem item) {
		super();
		this.processModel_ = gui.getProcessModel();
		//HACK: This should use a preferences object.
		boolean onlyLeaves = Boolean.parseBoolean(System.getProperty(SmartChecklistGUI.ONLY_LEAVES, "false"));
		if (onlyLeaves) {
			this.activityFilter_ = new OnlyLeavesActivityFilter();	
		}
		else {
			this.activityFilter_=new RefactoringActivityFilter();
//			this.activityFilter_ = new AllActivityFilter();	
		}
		this.processView_ = gui;
		this.agendaItem_ = item;
	}
	
    protected synchronized void addActivity(AgendaItem item) {
    	// Retrieve the path from the given activity to
    	// the root activity
    	List<AgendaItem> activityPath = new ArrayList<AgendaItem>();
    	AgendaItem currentActivity = item;
    	while (currentActivity != null) {
    		activityPath.add(currentActivity);
    		try {
				currentActivity = currentActivity.getParent();
			} catch (AMSException e) {
				throw new RuntimeException(e);
			}
    	}
    	// Create or find the activities in the path
    	for (int i = activityPath.size() - 1; i >= 0; i--) {
    		currentActivity = activityPath.get(i);
//    		try {
//    			System.out.println("currentActivity: " + currentActivity.getStep().getName());
//    		}
//    		catch (AMSException ae) {
//    			ae.printStackTrace();
//    		}
    		if (this.activityFilter_.accept(currentActivity)) {
    			this.processModel_.createOrFindActivity(currentActivity);
    		}
    	} // end for i
    }
	
	@Override
	public void posted() {
		System.out.println("Posted activity: " + this.agendaItem_);
		try {
			System.out.println("\tStep: " + this.agendaItem_.getStep().getName());
		} catch (AMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addActivity(this.agendaItem_);
	}
}
