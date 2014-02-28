package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class SmartChecklistGUI {

	public static final String TITLE = "Smart Checklist";
	public static final int GUI_WIDTH = 800;
	public static final int GUI_HEIGHT = 600;

	// Constants to be used as keys in System property (key, value) pairs
	public static final String DEBUGGING_MODE = "smartchecklist.debuggingmode";
	public static final String ONLY_LEAVES = "smartchecklist.onlyleaves";

	/**
	 * The main window of the smart checklist application
	 */
	private Shell shell_;
	private StepInfoPanel stepInfoPanel_;
	private ProcessPanel processPanel_;

	/**
	 * @return the processPanel_
	 */
	public ProcessPanel getProcessPanel_() {
		return processPanel_;
	}

	public SmartChecklistGUI(Display display) {
		super();
		//System.out.println("Creating SmartChecklistGUI...");
		
		this.shell_ = new Shell(display, SWT.SHELL_TRIM);
		this.shell_.setText(TITLE);
		this.shell_.setMinimumSize(GUI_WIDTH, GUI_HEIGHT);
		this.shell_.setLayout(new GridLayout());
		this.stepInfoPanel_ = new StepInfoPanel(this);
		this.processPanel_ = new ProcessPanel(this);
		createHelpMenu(); // extract this into its own class?
		this.shell_.pack();
		MenuItem quitMenuItem = this.getQuitMenuItem();
		if (quitMenuItem != null) {
			quitMenuItem.addSelectionListener(new SelectionAdapter() {
				@SuppressWarnings("unused")
				public void selectedWidget(SelectionEvent se) {
					quit();
				}
			});
		}
		this.shell_.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				quit();
			}
		});
	}

	public SmartChecklistGUI(Display display, ProcessEventManager eventManager) {
		super();
		// System.out.println("Creating SmartChecklistGUI...");

		this.shell_ = new Shell(display, SWT.SHELL_TRIM);
		this.shell_.setText(TITLE);
		this.shell_.setMinimumSize(GUI_WIDTH, GUI_HEIGHT);
		this.shell_.setLayout(new GridLayout());
		this.stepInfoPanel_ = new StepInfoPanel(this);
		this.processPanel_ = new ProcessPanel(this, eventManager);
		createHelpMenu();
		this.shell_.pack();
		MenuItem quitMenuItem = this.getQuitMenuItem();
		if (quitMenuItem != null) {
			quitMenuItem.addSelectionListener(new SelectionAdapter() {
				@SuppressWarnings("unused")
				public void selectedWidget(SelectionEvent se) {
					quit();
				}
			});
		}
		this.shell_.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				quit();
			}
		});
	}

	private MenuItem getQuitMenuItem() {
		Menu guiMenu = this.shell_.getMenu();
		MenuItem quitMenuItem = null;
		
		if (guiMenu != null) {
			for (int i = 0; i < guiMenu.getItemCount(); i++) {
				MenuItem currentMenuItem = guiMenu.getItem(i);
				if (currentMenuItem.getText().equals("Quit")) {
					quitMenuItem = currentMenuItem;
					break;
				} // end if
			} // end for
		} // end if
		
		return quitMenuItem;
	}

	protected Shell getView() {
		return this.shell_;
	}

	public void initialize(String agendaName) {
		this.processPanel_.initialize(agendaName);
	}

	public void open() {
		this.shell_.layout();	// Ask the shell to layout its children; otherwise, the
								// patientInfoPanel and the processPanel are on top of each other.
		this.shell_.open();
	}

	public boolean isDisposed() {
		return this.shell_.isDisposed();
	}

	public void quit() {
		this.shell_.dispose();
		System.exit(0);
	}

	//
	// End of Little-JIL agent methods
	//

	public static void main (String[] args) {
		//System.out.println("Invoking main for SmartChecklistGUI...");
		Display.setAppName(TITLE);
		Display display = new Display();
		SmartChecklistGUI gui = new SmartChecklistGUI(display);
		
		if(args.length == 0)
			gui.initialize("jkjk");
		else
			gui.initialize(args[0]);
		
		gui.open();
		while (! gui.isDisposed()) {
			if (! display.readAndDispatch ()) display.sleep(); 
		}
		display.dispose();
	}

	//might want to refactor into its own class
	private void createHelpMenu()
	{
		// Create the bar menu
	    Menu menuBar = new Menu(shell_, SWT.BAR);

	    // Create all the items in the bar menu
	    MenuItem helpItem = new MenuItem(menuBar, SWT.CASCADE);
	    helpItem.setText("Help");
	    
	    // Create the Help item's drop-down menu
	    Menu helpMenu = new Menu(menuBar);
	    
	    helpItem.setMenu(helpMenu);

	    // Create all the items in the Legend dropdown menu
	    MenuItem legendItem = new MenuItem(helpMenu, SWT.NONE);
	    legendItem.setText("Legend");
	    legendItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                System.out.println("Legend selected.");
                LegendDialog legendDialog = new LegendDialog(shell_);
                legendDialog.open();
            }
        });

	    shell_.setMenuBar(menuBar);
	    
	}

}
