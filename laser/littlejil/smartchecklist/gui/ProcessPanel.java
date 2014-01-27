package laser.littlejil.smartchecklist.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import laser.littlejil.smartchecklist.gui.model.Process;
import laser.littlejil.smartchecklist.gui.model.ProcessImpl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;


public class ProcessPanel //implements PropertyChangeListener
{
	public static final String TITLE = "PROCESS";
	private Process processModel_;
	private SmartChecklistGUI gui_;
	private Group processPanelView_;
	// Support for navigator
	// private ProcessNavigatorPanel navigatorPanel_;
	private ProcessChecklistPanel checklistPanel_;
	private ProcessEventManager eventManager_;

	private ProcessHeaderPanel processHeaderPanel_;

	public ProcessPanel(SmartChecklistGUI gui) {
		super();
		
		this.processModel_ = new ProcessImpl();
		this.gui_ = gui;
		Shell view = this.gui_.getView();
		processHeaderPanel_ = new ProcessHeaderPanel(this, SWT.NONE, this.processModel_);
		ScrolledComposite processPanelScrolling = new ScrolledComposite(view, SWT.BORDER | SWT.V_SCROLL);
		processPanelScrolling.setLayoutData(new GridData(GridData.FILL_BOTH));
		processPanelScrolling.setAlwaysShowScrollBars(true);
		processPanelScrolling.setExpandHorizontal(true);
		processPanelScrolling.setSize(SmartChecklistGUI.GUI_WIDTH - 50, SmartChecklistGUI.GUI_HEIGHT - 50);
		this.processPanelView_ = new Group(processPanelScrolling, SWT.SHADOW_ETCHED_IN);
		this.processPanelView_.setSize(SmartChecklistGUI.GUI_WIDTH - 50, SmartChecklistGUI.GUI_HEIGHT - 50);
		// Support for navigator
		//this.processPanel_.setLayout(new GridLayout(2, false));
		this.processPanelView_.setLayout(new GridLayout());
//		this.setTitle(null);
		// Support for navigator
		//this.navigatorPanel_ = new ProcessNavigatorPanel(this);
		this.checklistPanel_ = new ProcessChecklistPanel(this);
		this.processPanelView_.pack();
		processPanelScrolling.setContent(this.processPanelView_);
		
		this.eventManager_ = new ProcessEventManager(this);
//		this.processModel_.addPropertyChangeListener(this);
	}

	public ProcessPanel(SmartChecklistGUI gui, ProcessEventManager eventManager) {
		super();

		this.processModel_ = new ProcessImpl();
		this.gui_ = gui;
		Shell view = this.gui_.getView();
		processHeaderPanel_ = new ProcessHeaderPanel(this, SWT.NONE,
				this.processModel_);
		ScrolledComposite processPanelScrolling = new ScrolledComposite(view,
				SWT.BORDER | SWT.V_SCROLL);
		processPanelScrolling.setLayoutData(new GridData(GridData.FILL_BOTH));
		processPanelScrolling.setAlwaysShowScrollBars(true);
		processPanelScrolling.setExpandHorizontal(true);
		processPanelScrolling.setSize(SmartChecklistGUI.GUI_WIDTH - 50,
				SmartChecklistGUI.GUI_HEIGHT - 50);
		this.processPanelView_ = new Group(processPanelScrolling,
				SWT.SHADOW_ETCHED_IN);
		this.processPanelView_.setSize(SmartChecklistGUI.GUI_WIDTH - 50,
				SmartChecklistGUI.GUI_HEIGHT - 50);
		// Support for navigator
		// this.processPanel_.setLayout(new GridLayout(2, false));
		this.processPanelView_.setLayout(new GridLayout());
		// this.setTitle(null);
		// Support for navigator
		// this.navigatorPanel_ = new ProcessNavigatorPanel(this);
		this.checklistPanel_ = new ProcessChecklistPanel(this);
		this.processPanelView_.pack();
		processPanelScrolling.setContent(this.processPanelView_);

		this.eventManager_ = eventManager;
		// this.processModel_.addPropertyChangeListener(this);
	}

	protected Process getProcessModel() {
		return this.processModel_;
	}

	public SmartChecklistGUI getGUI() {
		return this.gui_;
	}

	protected Composite getProcessPanel() {
		return this.processPanelView_;
	}

	protected ProcessChecklistPanel getChecklistPanel() {
		return this.checklistPanel_;
	}

	// Support for navigator
	// protected ProcessNavigatorPanel getNavigatorPanel() {
	// return this.navigatorPanel_;
	// }

	public void initialize(String agendaName) {
    	// GUI code
    	//this.setTitle(agendaName);
    	
    	// Little-JIL agent code
		this.eventManager_.initialize(agendaName);
	}

	// public void setTitle(final String name) {
	// final Shell view = this.gui_.getView();
	// final Display display = view.getDisplay();
	// // The SWT widgets must be created within the UI thread.
	// // The Little-JIL agents are updated within non-UI threads.
	// // The following ensures that the the updates are within the UI thread.
	// display.syncExec(new Runnable() {
	// public void run() {
	// // String title = TITLE + " ";
	// // if (name != null) {
	// // title += "\"" + name + "\"";
	// // }
	// // processPanelView_.setText(title);
	// processStatusIndicatorPanel_.setProcessName(name);
	// processPanelView_.pack();
	// }
	// });
	// }
	//
	// @Override
	// public void propertyChange(PropertyChangeEvent event) {
	// if (event.getPropertyName().equals(Process.NAME_PROPERTY_NAME)) {
	// this.setTitle((String)event.getNewValue());
	// }
	// }
}
