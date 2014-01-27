package laser.littlejil.smartchecklist.gui.model;

import java.beans.PropertyChangeListener;
import java.util.List;

import laser.juliette.ams.AgendaItem;


public interface Process
{
	public static final String NAME_PROPERTY_NAME = "NAME";
	public static final String ACTIVITIES_PROPERTY_NAME = "ACTIVITIES";
	
	public String getName();
	
	public void setName(String name);
	
	public Activity getRootActivity();
	
	public Activity createOrFindActivity(AgendaItem agendaItem);
	
	public List<Activity> getAllActivities();

	public void addPropertyChangeListener(PropertyChangeListener listener);
	
	public void removePropertyChangeListener(PropertyChangeListener listener);
}
