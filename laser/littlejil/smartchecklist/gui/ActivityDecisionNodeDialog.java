package laser.littlejil.smartchecklist.gui;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.DecisionNodeSupport;
import laser.littlejil.smartchecklist.gui.model.ParameterDeclaration;
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


public class ActivityDecisionNodeDialog extends Dialog 
{
	public static final String DECISION_TITLE = "Activity Decision";
	public static final String OPTIONS_LABEL_PREFIX = "Select one of the following alternatives for \"";
	public static final String OPTIONS_LABEL_SUFFIX = "\":";
	
	private ActivityPanel activityPanel_;
	private ParameterDeclaration outputParameterFormal_;
	private Map<String,Serializable> outputParameterFormalAlternatives_;
	private List alternativesList_;
	private Map<String,Serializable> outputParametersActuals_;
	private int dialogResult_;
	
	
	public ActivityDecisionNodeDialog(ActivityPanel activityPanel) {
		super(activityPanel.getProcessPanel().getGUI().getView());
		
		this.activityPanel_ = activityPanel;
		Set<ParameterDeclaration> outputParameterFormals = this.activityPanel_.getActivity().getOutputParameterDeclarations();
		if ((outputParameterFormals == null) ||
			(outputParameterFormals.size() != 1)) 
		{
			throw new IllegalArgumentException("Activity \"" + this.activityPanel_.getActivity().getName() + "\" must declare a single output parameter. It declares " + outputParameterFormals + ".");
		}
		this.outputParameterFormal_ = outputParameterFormals.iterator().next();
		this.outputParameterFormalAlternatives_ = new LinkedHashMap<String,Serializable>();
		this.outputParametersActuals_ = new LinkedHashMap<String,Serializable>();
		this.dialogResult_ = SWT.ERROR;
		this.setText(DECISION_TITLE);
	}
	
	public ActivityPanel getActivityPanel() {
		return this.activityPanel_;
	}
	
	public Map<String,Serializable> getOutputParametersActuals() {
		return this.outputParametersActuals_;
	}
	
	public int open() {
		// GUI code
		final Shell view = this.activityPanel_.getProcessPanel().getGUI().getView();
		final Display display = view.getDisplay();
		final Shell dialog = new Shell(view, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		String alternativesLabelName = null;
		
		Point pt = display.getCursorLocation();
		dialog.setLocation(pt.x + 15, pt.y);

		//TODO: Fix up the layout and sizes
		dialog.setText(getText());
		dialog.setLayout(new GridLayout());
		dialog.setSize(SmartChecklistGUI.GUI_WIDTH / 2, SmartChecklistGUI.GUI_HEIGHT / 2);
		
		// Create the label for the alternatives
		alternativesLabelName = OPTIONS_LABEL_PREFIX + PrettyPrintedNamesFormatter.createHumanReadableName(outputParameterFormal_.getName()) + OPTIONS_LABEL_SUFFIX;
		Label alternativesLabel = new Label(dialog, SWT.WRAP);
		alternativesLabel.setText(alternativesLabelName);
		GridData altsLabelGridData = new GridData();
		altsLabelGridData.horizontalAlignment = SWT.FILL;
		altsLabelGridData.grabExcessHorizontalSpace = true;
		alternativesLabel.setLayoutData(altsLabelGridData);
		alternativesLabel.pack();
		
		// Create the SWT list of possible alternatives
		alternativesList_ = new List(dialog, SWT.SINGLE);
		String decisionResultClassName = outputParameterFormal_.getType();
		try {
			Class decisionResultClass = Class.forName(decisionResultClassName);
			DecisionNodeSupport decisionResultInstance = (DecisionNodeSupport) decisionResultClass.newInstance();
			for (Serializable currentAlternative : decisionResultInstance.getAlternatives()) {
				String currentAlternativeText = currentAlternative.toString();
				outputParameterFormalAlternatives_.put(currentAlternativeText,  currentAlternative);
				alternativesList_.add(currentAlternativeText);
			} // end for currentAlternative
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
		//alternativesList_.setForeground(display.getSystemColor(SWT.COLOR_RED));
		alternativesList_.setLayoutData(new GridData(GridData.FILL_BOTH));
		alternativesList_.pack();
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
				// The user selected alternative and the dialogResult_ is ok
				String[] selectedAlternative = alternativesList_.getSelection();
				// Check that there is a single selection.
				if (selectedAlternative.length == 0) {
					MessageBox noOptionDialog = new MessageBox(view, SWT.ICON_ERROR);
					noOptionDialog.setMessage("One alternative must be selected.");
					noOptionDialog.open();
				}
				else {
					try {
						Class outputParameterActualClass = Class.forName(outputParameterFormal_.getType());
						DecisionNodeSupport outputParameterActual = (DecisionNodeSupport)outputParameterActualClass.newInstance();
						String outputParameterAlternativeText = selectedAlternative[0];
						outputParameterActual.setSelectedAlternative(outputParameterFormalAlternatives_.get(outputParameterAlternativeText));
						outputParametersActuals_.put(outputParameterFormal_.getName(), outputParameterActual);
						System.out.println("Deicision is " + outputParametersActuals_);
						dialogResult_ = SWT.OK;
					} catch (ClassNotFoundException cnfe) {
						cnfe.printStackTrace();
						dialogResult_ = SWT.ERROR;
					} catch (InstantiationException ie) {
						ie.printStackTrace();
						dialogResult_ = SWT.ERROR;
					} catch (IllegalAccessException iae) {
						iae.printStackTrace();
						dialogResult_ = SWT.ERROR;
					}
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
