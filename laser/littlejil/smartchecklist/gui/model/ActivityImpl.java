package laser.littlejil.smartchecklist.gui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import artifacts.PackageFragmentRoot;

import laser.ddg.ProcedureInstanceNode;
import laser.ddg.ProvenanceData;
import laser.juliette.ams.AMSException;
import laser.juliette.ams.AgendaItem;
import laser.juliette.ams.AgendaItemEvent;
import laser.juliette.ams.AgendaItemListener;
import laser.juliette.ams.IllegalTransition;
import laser.juliette.ams.UnknownParameter;
import laser.juliette.ddgbuilder.DDGBuilder;
import laser.juliette.ddgbuilder.DataInstanceNode;
import laser.juliette.ddgbuilder.StepReference;
import laser.lj.Binding;
import laser.lj.InterfaceDeclaration;
import laser.lj.InterfaceDeclarationSet;
import laser.lj.ResolutionException;
import laser.lj.Step;
import laser.lj.InterfaceDeclaration.DeclarationKind;


public class ActivityImpl implements Activity, AgendaItemListener
{
	public static final String ACTIVITY_STATE_PROPERTY_NAME = "ActivityState";
	
	private AgendaItem agendaItem_;
	// Compile-time information
	private String shortName_;
	private String longName_;
	private boolean useShortNames_;
	private ActivityKind kind_;
	private String description_;
	private String notes_;
	// Run-time information
	private int ID_;
	private static int nextID_;
	private ActivityState state_;
	private Date finishedTimestamp_;
	private Activity parent_;
	private List<Activity> children_;
	private boolean isLeaf_;
	private PropertyChangeSupport eventManager_;
	
	
	public ActivityImpl(Activity parent, AgendaItem agendaItem) {
		super();
		this.agendaItem_ = agendaItem;
		this.parent_ = parent;
		this.ID_ = ActivityImpl.nextID_++;
		this.shortName_ = this.generateShortName();
		this.longName_ = this.generateLongName();
		this.useShortNames_ = true;
		this.kind_ = this.generateActivityKind();
		this.description_ = this.generateDescription();
		this.notes_ = "";
		//TODO: Convert from adaptee's state to our notion of state
		this.state_ = this.generateActivityState();
		this.children_ = new ArrayList<Activity>();
		try {
			this.isLeaf_ = this.agendaItem_.getStep().isLeaf();
		}
		catch (AMSException ae) {
			throw new RuntimeException(ae);
		}
		this.eventManager_ = new PropertyChangeSupport(this);
		try {
			this.agendaItem_.addAgendaItemListener(this);
		}
		catch (AMSException ae) {
			throw new RuntimeException(ae);
		}
		//TODO: Remove the listener at some later point
		if (this.parent_ != null) {
			this.parent_.addChild(this);
		}
	}
	
	protected String generateShortName() {
		String shortName = null;
		try {
			shortName = this.agendaItem_.getStep().getName();
		} catch (AMSException e) {
			shortName = "Activity " + this.getID();
		}
		return shortName;
	}
	
	protected String generateLongName() {
		//TODO: Use the full documentation name
		return this.generateShortName();
	}
	
	protected ActivityKind generateActivityKind() {
		ActivityKind kind = null;
		
		//TODO: The remote agenda items do not give us access
		//      to the step kind.
		//
		//      The following is a stub.
		if (this.isLeaf()) {
			kind =  ActivityKind.LEAF;
		}
		else {
			kind = ActivityKind.SEQUENTIAL;
		}
		
		return kind;
	}
	
	protected String generateDescription() {
		//TODO: Use the full documentation description
		return "";
	}
	
	protected ActivityState generateActivityState() {
		String state = null;
		
		try {
			state = this.agendaItem_.getState();
		} catch (AMSException e) {
			throw new IllegalArgumentException("The activity state cannot be generated.", e);
		}
		
		return this.toActivityState(state);
	}
	
	protected ActivityState toActivityState(String state) {
		ActivityState activityState = null;
		
		if (state.equals(AgendaItem.INITIAL)) {
			activityState = ActivityState.INITIAL;
		}
		else if (state.equals(AgendaItem.POSTED)) {
			activityState = ActivityState.POSTED;
		}
		else if (state.equals(AgendaItem.STARTING)) {
			activityState = ActivityState.STARTING;
		}
		else if (state.equals(AgendaItem.STARTED)) {
			activityState = ActivityState.STARTED;
		}
		else if (state.equals(AgendaItem.COMPLETED)) {
			activityState = ActivityState.COMPLETED;
		}
		else if (state.equals(AgendaItem.OPTED_OUT)) {
			activityState = ActivityState.OPTEDOUT;
		}
		else if (state.equals(AgendaItem.RETRACTED)) {
			activityState = ActivityState.RETRACTED;
		}
		else if (state.equals(AgendaItem.TERMINATED)) {
			activityState = ActivityState.TERMINATED;
		}
		else {
			throw new IllegalArgumentException("AgendaItem state " + state + " is not supported.");
		}
		
		return activityState;
	}
	
	protected ParameterMode toParameterMode(InterfaceDeclaration outputParameterDecl) {
		DeclarationKind ljKind = outputParameterDecl.getDeclarationKind();
		ParameterMode mode = null;
		
		if (ljKind == DeclarationKind.IN_PARAMETER) {
			mode = ParameterMode.IN;
		}
		else if (ljKind == DeclarationKind.IN_OUT_PARAMETER) {
			mode = ParameterMode.INOUT;
		}
		else if (ljKind == DeclarationKind.OUT_PARAMETER) {
			mode = ParameterMode.OUT;
		}
		
		return mode;
	}
	
	public laser.juliette.runner.ams.AgendaItem getAgendaItem() {
		return (laser.juliette.runner.ams.AgendaItem) this.agendaItem_;
	}
	
	//
	// Begin of Activity methods
	//
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getID()
	 */
	@Override
	public int getID() {
		return this.ID_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getName()
	 */
	@Override
	public String getName() {
		if (this.getUseShortNames()) {
			return this.getShortName();
		}
		else {
			return this.getLongName();
		}
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getShortName()
	 */
	@Override
	public String getShortName() {
		return this.shortName_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getLongName()
	 */
	@Override
	public String getLongName() {
		return this.longName_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getUseShortNames()
	 */
	@Override
	public boolean getUseShortNames() {
		return this.useShortNames_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#setUseShortNames(boolean)
	 */
	@Override
	public void setUseShortNames(boolean useShortNames) {
		this.useShortNames_ = useShortNames;
	}
	
	@Override
	public ActivityKind getKind() {
		return this.kind_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getNotes()
	 */
	@Override
	public String getNotes() {
		return this.notes_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#setNotes(java.lang.String)
	 */
	@Override
	public void setNotes(String notes) {
		this.notes_ = notes;
	}
	
	protected Set<ParameterDeclaration> getParameterDeclarations(Set<ParameterMode> modes)
	{
		Set<ParameterDeclaration> parameterDecls = this.getParameterDeclarations();
		Set<ParameterDeclaration> filteredParameterDecls = new LinkedHashSet<ParameterDeclaration>();
		
		if ((modes == null) || modes.isEmpty()) {
			return filteredParameterDecls;
		}
		
		for (ParameterDeclaration currentParameterDecl : parameterDecls) {
			ParameterMode currentMode = currentParameterDecl.getMode();
			if (modes.contains(currentMode)) {
				filteredParameterDecls.add(currentParameterDecl);
			}
		} // end for currentParameterDecl
		
		return filteredParameterDecls;
		
	}
	
	@Override
	public Set<ParameterDeclaration> getInputParameterDeclarations() {
		Set<ParameterMode> inModes = new LinkedHashSet<ParameterMode>();
		inModes.add(ParameterMode.IN);
		inModes.add(ParameterMode.INOUT);
		
		return this.getParameterDeclarations(inModes);
	}
	
	@Override
	public Set<ParameterDeclaration> getOutputParameterDeclarations() {
		Set<ParameterMode> outModes = new LinkedHashSet<ParameterMode>();
		outModes.add(ParameterMode.INOUT);
		outModes.add(ParameterMode.OUT);
		
		return this.getParameterDeclarations(outModes);
	}
	
	@Override
	public Set<ParameterDeclaration> getParameterDeclarations() {
		//TODO: The parameterDecls could be cached.
		Set<ParameterDeclaration> parameterDecls = new LinkedHashSet<ParameterDeclaration>();
		
		// Convert from the static process model
		try {
			Step step = this.agendaItem_.getStep();
			
			// Get the resources that are not the agent
			InterfaceDeclarationSet resourceAcquisitionsSet = step.getDeclarations(DeclarationKind.RESOURCE);
			if ((resourceAcquisitionsSet != null) && (! resourceAcquisitionsSet.isEmpty())) {
				for (InterfaceDeclaration resourceAcquisition : resourceAcquisitionsSet) {
//					System.out.println("RESOURCE: " + resourceAcquisition.getName());
					// Filter out the agent
					if (resourceAcquisition.getName().equals("agent")) {
						continue;
					}
					Set<Binding> resourceAcquisitionBinds = step.getBindingsFor(resourceAcquisition.getName());
					ParameterMode mode;
					if ((resourceAcquisitionBinds != null) &&
						(! resourceAcquisitionBinds.isEmpty())) 
					{
						mode = ParameterMode.INOUT;
					}
					else {
						mode = ParameterMode.IN;
					}
					ParameterDeclaration parameterDeclPrime =
						new ParameterDeclaration(resourceAcquisition.getName(),
								                 mode,
								                 resourceAcquisition.getTemplate().getObjectType());
					parameterDecls.add(parameterDeclPrime);
				} // end for resourceAcquisition
			}
			
			// Get the artifacts
			InterfaceDeclarationSet parameterDeclSet = step.getParameters();
			if ((parameterDeclSet != null) && (! parameterDeclSet.isEmpty())) {
				for (InterfaceDeclaration parameterDecl : parameterDeclSet) {
//					System.out.println("PARAMETER: " + parameterDecl.getName());
					ParameterMode mode = toParameterMode(parameterDecl);
					// Filter out local parameters
					if (mode == null) {
						continue;
					}
					ParameterDeclaration parameterDeclPrime =
						new ParameterDeclaration(parameterDecl.getName(),
												 mode,
												 parameterDecl.getTemplate().getObjectType());
					parameterDecls.add(parameterDeclPrime);
				} // end for parameterDecl
			}
		} 
		catch (AMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (ResolutionException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return parameterDecls;
	}

	@Override
	public Set<String> getExceptionDeclarations() {
		//TODO: The exceptionDecls could be cached
		Set<String> exceptionDecls = new LinkedHashSet<String>();
		
		try {
			Step step = this.agendaItem_.getStep();
			InterfaceDeclarationSet exceptionDeclSet = step.getDeclarations(DeclarationKind.EXCEPTION);
			//System.out.println("Creating list with exceptions " + exceptionsThrown);
			if ((exceptionDeclSet != null) && (! exceptionDeclSet.isEmpty())) {
				for (InterfaceDeclaration exceptionDecl : exceptionDeclSet) {
					String exceptionDeclName = exceptionDecl.getTemplate().getObjectType(); 
					exceptionDecls.add(exceptionDeclName);
					//System.out.println("\tMay throw exception: " + exceptionThrownName);
				} // end for
			}
		} catch (AMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return exceptionDecls;
	}
	
	@Override
	public Serializable getParameterValue(String parameterName)
	{
		try
		{
			// The artifacts may have values.
			// The resources do not have values so the exceptions will be thrown.
			return agendaItem_.getParameter(parameterName);
		} catch (AMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownParameter e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getState()
	 */
	@Override
	public ActivityState getState() {
		return this.state_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#setState(laser.smartchecklist.gui.ActivityState)
	 */
	@Override
	public void setState(ActivityState state) {
		ActivityState oldState = this.state_;
		this.state_ = state;
		this.eventManager_.firePropertyChange(ACTIVITY_STATE_PROPERTY_NAME, oldState, this.state_);
	}
	
	public void startAndComplete(Map<String,Serializable> outputParameters) {
		try {
			System.out.println("Starting and completing activity " + agendaItem_);
			System.out.println("\tStep " + this.getName());
			this.agendaItem_.start();
			//HACK: The interpreter needs time to update the process execution state
			Thread currentThread = Thread.currentThread();
			try {
				currentThread.sleep(25);
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			if (outputParameters != null) {
				for (String currentParameterName : outputParameters.keySet()) {
					Serializable currentParameterValue = outputParameters.get(currentParameterName);
					this.agendaItem_.setParameter(currentParameterName, currentParameterValue);
				} // end for currentParameterName
			}
			this.agendaItem_.complete();
		} catch (AMSException e1) {
			// TODO
			e1.printStackTrace();
		} catch (IllegalTransition e1) {
			// TODO 
			e1.printStackTrace();
		} catch (UnknownParameter up) {
			// TODO
			up.printStackTrace();
		}
	}
	
	public void startAndTerminate(Set<Serializable> exceptionsThrown) {
		try {
			System.out.println("Starting and terminating activity " + this.agendaItem_);
			System.out.println("\tStep " + this.getName());
			this.agendaItem_.start();
			//HACK: The interpreter needs time to update the process execution state
			Thread currentThread = Thread.currentThread();
			try {
				currentThread.sleep(25);
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			this.agendaItem_.terminate(exceptionsThrown);
		} catch (AMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalTransition e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public boolean isFinished() {
		return (this.finishedTimestamp_ != null);
	}
	
	public void setFinishedTimestamp() {
		this.finishedTimestamp_ = Calendar.getInstance().getTime();
	}
	
	public Date getFinishedTimestamp() {
		return this.finishedTimestamp_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getParent()
	 */
	@Override
	public Activity getParent() {
		return this.parent_;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#addChild(laser.smartchecklist.gui.Activity)
	 */
	@Override
	public void addChild(Activity child) {
		this.children_.add(child);
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#removeChild(laser.smartchecklist.gui.ActivityInterface)
	 */
	@Override
	public void removeChild(Activity child) {
		this.children_.remove(child);
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.ActivityInterface#getChildren()
	 */
	@Override
	public List<Activity> getChildren() {
		ArrayList<Activity> childrenCopy = new ArrayList<Activity>();
		childrenCopy.addAll(this.children_);
		return childrenCopy;
	}
	
	/* (non-Javadoc)
	 * @see laser.smartchecklist.gui.activity.Activity#isLeaf()
	 */
	public boolean isLeaf() {
		return this.isLeaf_;
	}
	
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.eventManager_.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.eventManager_.removePropertyChangeListener(listener);
	}
	
	//
	// End of Activity methods
	//
	// Begin of Object methods
	//
	
	public String toString() {
		return "Activity-" + this.getName();
	}
	
	//
	// End of Object methods
	//	
	// Begin of AgendaItemListener methods
	//
	
	@Override
	public void cancelled(AgendaItemEvent event) {
		System.out.println("Cancelled activity: " + this);
		System.out.println("\tStep: " + this.getName());
		this.setState(ActivityState.RETRACTED);
	}
	
	@Override
	public void retracted(AgendaItemEvent event) {
		System.out.println("Retracted activity: " + this);
		System.out.println("\tStep: " + this.getName());
		this.setState(ActivityState.RETRACTED);
		//TODO: Could the activity be re-posted?
	}

	@Override
	public void optedOut(AgendaItemEvent event) {
		System.out.println("Opted-out activity: " + this);
		System.out.println("\tStep: " + this.getName());
		this.setState(ActivityState.OPTEDOUT);
	}

	@Override
	public void started(AgendaItemEvent event) {
		System.out.println("Started activity: " + this);
		System.out.println("\tStep: " + this.getName());
		this.setState(ActivityState.STARTED);
	}
	
	@Override
	public void completed(AgendaItemEvent event) {
		System.out.println("Completed activity: " + this);
		System.out.println("\tStep: " + this.getName());
		this.setFinishedTimestamp();
		this.setState(ActivityState.COMPLETED);
	}
	
	@Override
	public void terminated(AgendaItemEvent event) {
		System.out.println("Terminated activity: " + this);
		System.out.println("\tStep: " + this.getName());
		this.setFinishedTimestamp();
		this.setState(ActivityState.TERMINATED);	
	}

	@Override
	public Set<Serializable> getThrownExceptions()
	{
		Set<Serializable> thrownExceptions = new LinkedHashSet<Serializable>();
		if (!this.state_.equals(ActivityState.TERMINATED))
			throw new RuntimeException("An activity that has not entered the TERMINATED state" +
					" cannot have any thrown exceptions associated with it");

		try
		{
			thrownExceptions = agendaItem_.getExceptions();
		} catch (AMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return thrownExceptions;
	}

	// get a list of ProcedureInstanceNodes
	public ArrayList<ProcedureInstanceNode> getProcedureInstanceNodes() {
		ArrayList<ProcedureInstanceNode> res = new ArrayList<ProcedureInstanceNode>();
		DDGBuilder ddgBuilder = (DDGBuilder) agendaItem_.getDdgbuilder();
		ProvenanceData pd = ddgBuilder.getProvData();
		Iterator<ProcedureInstanceNode> pinIter = pd.pinIter();
		while (pinIter.hasNext()) {
			ProcedureInstanceNode pin = pinIter.next();
			if (!pin.getType().equals("Leaf"))
				continue;
			if (pin.getProcedureDefinition() instanceof StepReference) {
				StepReference sr = (StepReference) pin.getProcedureDefinition();
				try {
					if (sr.getStepDef().getStepDeclaration()
							.equals(agendaItem_.getStep().getStepDeclaration())) {
						res.add(pin);
					}
				} catch (UnsupportedOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	@Override
	public PackageFragmentRoot getMostRecentPFR() {
		DDGBuilder ddgBuilder = (DDGBuilder) agendaItem_.getDdgbuilder();
		ProvenanceData pd = ddgBuilder.getProvData();
		
		Iterator<laser.ddg.DataInstanceNode> dinIter = pd.dinIter();
		PackageFragmentRoot mostRecent = null;
		while(dinIter.hasNext()){
			laser.ddg.DataInstanceNode current = dinIter.next();
			if(current.getValue() instanceof PackageFragmentRoot){
				mostRecent = (PackageFragmentRoot)current.getValue();
			}
		}
		
		return mostRecent;
	}
}
