package laser.littlejil.smartchecklist.gui.model;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import artifacts.PackageFragmentRoot;

import laser.ddg.ProcedureInstanceNode;
import laser.juliette.runner.ams.AgendaItem;


/** 
 * The Activity class represents TODO.
 * It is the model in the model/view/controller paradigm.
 * The events generated are PropertyChangeEvents.
 * 
 * @author Heather M. Conboy (laser-software@cs.umass.edu)
 */
public interface Activity 
{
	public int getID();

	public String getName();

	public String getShortName();

	public String getLongName();

	public boolean getUseShortNames();

	public void setUseShortNames(boolean useShortNames);

	public ActivityKind getKind();
	
	public String getDescription();

	public String getNotes();

	public void setNotes(String notes);
	
	public Set<ParameterDeclaration> getInputParameterDeclarations();
	
	public Set<ParameterDeclaration> getOutputParameterDeclarations();
	
	public Set<ParameterDeclaration> getParameterDeclarations();
	
	//TODO: Currently using the same return type as AgendaItem.getParameter(). Should we use another return type instead?
	public Serializable getParameterValue(String parameterName);
	
	public Set<String> getExceptionDeclarations();
	
	public Set<Serializable> getThrownExceptions();

	public ActivityState getState();

	public void setState(ActivityState state);
	
	public void startAndComplete(Map<String,Serializable> outputParameters);
	
	public void startAndTerminate(Set<Serializable> exceptionsThrown);
	
	public boolean isFinished();
	
	public Date getFinishedTimestamp();

	public Activity getParent();

	public void addChild(Activity child);

	public void removeChild(Activity child);

	public List<Activity> getChildren();
	
	public boolean isLeaf();

	public void addPropertyChangeListener(PropertyChangeListener listener);
	
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
	public AgendaItem getAgendaItem();
	
	public List<ProcedureInstanceNode> getProcedureInstanceNodes();
	
	public PackageFragmentRoot getMostRecentPFR();
}