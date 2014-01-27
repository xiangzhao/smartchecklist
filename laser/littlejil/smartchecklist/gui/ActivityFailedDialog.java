package laser.littlejil.smartchecklist.gui;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.utils.PrettyPrintedNamesFormatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public class ActivityFailedDialog extends Dialog 
{
	public static final String FAILED_TITLE = "Activity Failed";
	public static final String EXCEPTIONS_LABEL_PREFIX = "Indicate problems that occurred while performing \"";
	public static final String EXCEPTIONS_LABEL_SUFFIX = "\":";
	
	private ActivityPanel activityPanel_;
	private List exceptionsList_;
	private Set<Serializable> exceptionsThrown_;
	private int dialogResult_;
	
	
	public ActivityFailedDialog(ActivityPanel activityPanel) {
		super(activityPanel.getProcessPanel().getGUI().getView());
		
		this.activityPanel_ = activityPanel;
		this.exceptionsThrown_ = new LinkedHashSet<Serializable>();
		this.dialogResult_ = SWT.ERROR;
		this.setText(FAILED_TITLE);
	}
	
	public ActivityPanel getActivityPanel() {
		return this.activityPanel_;
	}
	
	public Set<Serializable> getExceptionsThrown() {
		return Collections.unmodifiableSet(this.exceptionsThrown_);
	}
	
	public int open() {
		// GUI code
		final Shell view = this.activityPanel_.getProcessPanel().getGUI().getView();
		final Display display = view.getDisplay();
//		final Shell dialog = new Shell(view, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final Shell dialog = new Shell(view, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		String exceptionsLabelName = null;
		
		Point pt = display.getCursorLocation();
		dialog.setLocation(pt.x + 15, pt.y);

		//TODO: Fix up the layout and sizes
		dialog.setText(getText());
		dialog.setLayout(new GridLayout());
		dialog.setSize(SmartChecklistGUI.GUI_WIDTH / 2, SmartChecklistGUI.GUI_HEIGHT / 2);
		
		// Create the label for the exceptions
		exceptionsLabelName = EXCEPTIONS_LABEL_PREFIX + activityPanel_.getActivity().getName() + EXCEPTIONS_LABEL_SUFFIX;
		Label exceptionsLabel = new Label(dialog, SWT.WRAP);
		exceptionsLabel.setText(exceptionsLabelName);
		GridData excLabelGridData = new GridData();
		excLabelGridData.horizontalAlignment = SWT.FILL;
		excLabelGridData.grabExcessHorizontalSpace = true;
		exceptionsLabel.setLayoutData(excLabelGridData);
		//exceptionsLabel.pack();
		
		// Create a list of human-readable exceptions that the activity can throw
		final PrettyPrintedNamesFormatter excNamesFormatter = new PrettyPrintedNamesFormatter(activityPanel_.getActivity().getExceptionDeclarations()); 
		// Create the SWT list of possible exceptions to be thrown
		exceptionsList_ = new List(dialog, SWT.MULTI);
		for (String exceptionName : excNamesFormatter.getHumanReadableExceptionNames()) {
			exceptionsList_.add(exceptionName);
			//System.out.println("\tMay throw exception: " + exceptionThrownName);
		} // end for
		exceptionsList_.setForeground(display.getSystemColor(SWT.COLOR_RED));
		exceptionsList_.setLayoutData(new GridData(GridData.FILL_BOTH));
		exceptionsList_.pack();
		//System.out.println("Created list.");
		// Create a composite with GridLayout that will hold the Cancel and OK buttons.
		Composite buttonPanel = new Composite(dialog, SWT.NONE);
		buttonPanel.setLayoutData(new GridData(SWT.RIGHT));
		buttonPanel.setLayout(new GridLayout(2, true));
		// Create the cancel button and its selection listener
		Button cancelButton = new Button(buttonPanel, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.pack();
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// The user decided to not throw any exceptions and the dialogResult_ is cancel
				dialogResult_ = SWT.CANCEL;
				dialog.dispose();
			}
		});
		// Create the OK button and its selection listener
		Button okButton = new Button(buttonPanel, SWT.PUSH);
		okButton.setText("OK");
		okButton.pack();
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// The user selected exceptions to be thrown and the dialogResult_ is ok
				String[] exceptionsThrownNames = exceptionsList_.getSelection();
				// Check that there are one or more selected exceptions.
				if (exceptionsThrownNames.length == 0) {
					MessageBox noExceptionsDialog = new MessageBox(view, SWT.ICON_ERROR);
					noExceptionsDialog.setMessage("One or more problems must be selected.");
					noExceptionsDialog.open();
				}
				else {
					for (int i = 0; i < exceptionsThrownNames.length; i++) {
						try {
							// The exceptionThrownNames are in human readable format. Get the fully-qualified class name
							// to create an instance of the exception class.
							String fullyQualifiedExceptionName = excNamesFormatter.getFullyQualifiedName(exceptionsThrownNames[i]);
							Class currentExceptionsThrownClass = Class.forName(fullyQualifiedExceptionName);
							Serializable currentExceptionThrownInst = (Serializable)currentExceptionsThrownClass.newInstance();
							exceptionsThrown_.add(currentExceptionThrownInst);
							System.out.println("\tIs throwing exception " + currentExceptionThrownInst.getClass().getName());
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InstantiationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IllegalAccessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} // end for i
					dialogResult_ = SWT.OK;
					dialog.dispose();
				}
			}
		});
		
		// Open the dialog containing the widgets for creating a note
		dialog.open();
		while (! dialog.isDisposed()) {
			if (! display.readAndDispatch()) {
				display.sleep();
			}
		}
    			
    	return dialogResult_;
	}
}
