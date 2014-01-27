package laser.littlejil.smartchecklist.gui.editor;

import java.io.Serializable;

import laser.littlejil.smartchecklist.gui.SmartChecklistGUI;
import laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel.JavaBeanArtifactModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class JavaBeanArtifactViewDialog extends Dialog 
{
	public static final int EDITABLE_VALUE_COLUMN = 1;

	// This is the model
	private JavaBeanArtifactModel artifactModel_;
	// This is the view
	private JavaBeanArtifactView artifactView_;
	private Shell dialog_;
	private int dialogResult_;
	
	
	public JavaBeanArtifactViewDialog(Shell parent, String name, Serializable value, boolean modifiable) {
		super(parent);
		
		this.artifactModel_ = new JavaBeanArtifactModel(name, value, modifiable);
		
		this.setText("Configure " + this.getArtifactModel().getPrettyPrintedName());
	}
	
	public JavaBeanArtifactModel getArtifactModel() {
		return this.artifactModel_;
	}
	
	protected Shell getArtifactView() {
		return this.dialog_;
	}
	
	public int open() {
		// GUI code
		final Shell view = this.getParent();
		final Display display = view.getDisplay();
		this.dialog_ = new Shell(view, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		
		Point pt = display.getCursorLocation();
		this.dialog_.setLocation(pt.x + 15, pt.y);

		//TODO: Fix up the layout and sizes
		this.dialog_.setText(getText());
		this.dialog_.setLayout(new GridLayout());
		this.dialog_.setSize(SmartChecklistGUI.GUI_WIDTH, SmartChecklistGUI.GUI_HEIGHT);
		Color bgColor = new Color(display, 130, 190, 220);
		this.dialog_.setBackground(bgColor);
		
		Composite artifactViewPanel = new Composite(this.dialog_, SWT.NONE);
		artifactViewPanel.setBackground(this.dialog_.getBackground());
		GridData artifactViewPanelLayoutData = new GridData();
		artifactViewPanelLayoutData.horizontalAlignment = GridData.FILL;
		artifactViewPanelLayoutData.grabExcessHorizontalSpace = true;
		artifactViewPanelLayoutData.verticalAlignment = GridData.FILL;
		artifactViewPanelLayoutData.grabExcessVerticalSpace = true;
		artifactViewPanel.setLayoutData(artifactViewPanelLayoutData);
		artifactViewPanel.setLayout(new GridLayout());
		this.artifactView_ = new JavaBeanArtifactView(artifactViewPanel, this.artifactModel_);
		
		// Create a composite with GridLayout that will hold the Cancel and OK buttons.
		Composite buttonPanel = new Composite(this.dialog_, SWT.NONE);
		buttonPanel.setBackground(this.dialog_.getBackground());
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
				dialog_.dispose();
			}
		});
		// Create the OK button and its selection listener, if the artifact is modifiable
		if (this.getArtifactModel().getModifiable()) {
			Button okButton = new Button(buttonPanel, SWT.PUSH);
			okButton.setText("OK");
			okButton.pack();
			okButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// Return the edited artifact
					artifactView_.save();
					dialogResult_ = SWT.OK;
					dialog_.dispose();
				}
			});
		}
		// Open the dialog containing the widgets for editing an artifact
		dialog_.pack();
		dialog_.open();
		while (! dialog_.isDisposed()) {
			if (! display.readAndDispatch()) {
				display.sleep();
			}
		}
    			
    	return dialogResult_;
	}
}
