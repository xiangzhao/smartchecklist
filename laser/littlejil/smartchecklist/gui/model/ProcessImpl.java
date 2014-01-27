package laser.littlejil.smartchecklist.gui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import laser.juliette.ams.AMSException;
import laser.juliette.ams.AgendaItem;


public class ProcessImpl implements Process 
{
	public static final String DEFAULT_NAME = "NewProcess";
	private String name_;
	private Activity rootActivity_;
	private List<ActivityImpl> activities_;
	private PropertyChangeSupport eventManager_;
	
	
	public ProcessImpl() {
		super();

		// Initializations
		this.name_ = DEFAULT_NAME;
		this.activities_ = new ArrayList<ActivityImpl>();
		this.eventManager_ = new PropertyChangeSupport(this);
	}
	
	@Override
	public String getName() {
		return this.name_;
	}
	
	@Override
	public void setName(String newName) {
		if (newName == null) {
			newName = DEFAULT_NAME;
		}
		String oldName = this.name_;
		this.name_ = newName;
		//System.out.println("ProcessImpl.setName: " + this.name_);
		this.eventManager_.firePropertyChange(NAME_PROPERTY_NAME, oldName, this.name_);
	}

	@Override
	public Activity getRootActivity() {
		return this.rootActivity_;
	}

	@Override
	public synchronized Activity createOrFindActivity(AgendaItem agendaItem) {
		ActivityImpl childActivity = null;
				
		// Find or create the Activity for the given agendaItem
		//
		// Search for an Activity for the given agendaItem
		childActivity = this.getActivity(agendaItem);
		// If not found, then create new activity for the
		// given agendaItem
		if (childActivity == null) {
			ActivityImpl parentActivity = null;
			
			try {
				AgendaItem parentAgendaItem = agendaItem.getParent();
				if (parentAgendaItem != null) {
					parentActivity = this.getActivity(parentAgendaItem);
				}
			} catch (AMSException e) {
				throw new RuntimeException(e);
			}
			
			childActivity = new ActivityImpl(parentActivity, agendaItem);			
			if (parentActivity == null) {
				this.rootActivity_ = childActivity;
				this.setName(this.rootActivity_.getName());
			}
			this.activities_.add(childActivity);
			//System.out.println("ProcessImpl.createOrFindActivity:: newActivity: " + childActivity);
			this.eventManager_.firePropertyChange(ACTIVITIES_PROPERTY_NAME, null, childActivity);;
		}
//		else {
//			System.out.println("ProcessImpl.createOrFindActivity:: existingActivity: " + childActivity);
//		}

		return childActivity;
	}
	
	protected ActivityImpl getActivity(AgendaItem agendaItem) {
		//System.out.println("ProcessImpl.getActivity:: agendaItem: " + agendaItem);
		// Search for activity for the given agendaItem
		for (ActivityImpl currentActivity : this.activities_) {
			//System.out.println("\tcurrentActivity: " + currentActivity);
			//System.out.println("\t\tcurrentAgendaItem: " + currentActivity.getAgendaItem());
			if (currentActivity.getAgendaItem().equals(agendaItem)) {
				//System.out.println("\t\t\tFound Activity!");
				return currentActivity;
			}
		} // end for currentActivity
		
		//System.out.println("\t\t\tNot found Activity!");
		return null;
	}

	@Override
	public List<Activity> getAllActivities() {
		ArrayList<Activity> activitiesCopy = new ArrayList<Activity>();
		activitiesCopy.addAll(this.activities_);
		return activitiesCopy;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.eventManager_.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.eventManager_.removePropertyChangeListener(listener);
	}

}
