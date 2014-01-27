package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ActivityNoteDialog extends Dialog 
{
//	public static final String NOTE_TITLE = "Activity Note";
	public static final String NOTE_LABEL_PREFIX = "Notes for \"";
	public static final String NOTE_LABEL_SUFFIX = "\"";
	
	/**
	 * The preferred height (in pixels) of the text entry field of the ActivityNoteDialog.
	 */
	private static final int TEXT_BOX_HEIGHT = 75;
	
	private ActivityPanel activityPanel_;
	private String newNote_;
	private int dialogResult_;
	
	private int dialogWidth_;	// The default width of this dialog
	private int dialogHeigth_;	// The default height of this dialog
	
	
	public ActivityNoteDialog(ActivityPanel activityPanel) {
		super(activityPanel.getProcessPanel().getGUI().getView());
		
		this.activityPanel_ = activityPanel;
		this.dialogResult_ = SWT.ERROR;
//		this.setText(NOTE_TITLE);
	}
	
	public ActivityPanel getActivityPanel() {
		return this.activityPanel_;
	}
	
	public String getNewNote()
	{
		return newNote_;
	}
	
	public int open() {
		// GUI code
		Shell view = this.activityPanel_.getProcessPanel().getGUI().getView();
		final Display display = view.getDisplay();
		//final Shell dialog = new Shell(view, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		// Use SWT.SHELL_TRIM (instead of SWT.DIALOG_TRIM) because it makes the dialog resizable.
		final Shell dialog = new Shell(view, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		
		// Open the dialog near the mouse cursor location. Since the ActivityNoteDialog gets created
		// when the activity note button is clicked, this dialog will be opened near that button.
		Point pt = display.getCursorLocation();
		dialog.setLocation(pt.x + 15, pt.y);

		// Set the title of this dialog
		dialog.setText(NOTE_LABEL_PREFIX + activityPanel_.getActivity().getName() + NOTE_LABEL_SUFFIX);
		
		dialog.setLayout(new GridLayout());
		dialogWidth_ = SmartChecklistGUI.GUI_WIDTH / 2;
		dialogHeigth_ = SmartChecklistGUI.GUI_HEIGHT / 2;
		dialog.setMinimumSize(dialogWidth_, dialogHeigth_);
		dialog.setSize(dialogWidth_, dialogHeigth_);

//		// Create the title for the note
//		String noteLabelName = NOTE_LABEL_PREFIX + activityPanel_.getActivity().getName() + NOTE_LABEL_SUFFIX;
//		Label noteTitle = new Label(dialog, SWT.NONE);
//		noteTitle.setText(noteLabelName);
//		FontData[] noteTitleFontData = noteTitle.getFont().getFontData();
//		for (int i = 0; i < noteTitleFontData.length; i++) {
////			noteTitleFontData[i].setHeight(16);
//			noteTitleFontData[i].setStyle(SWT.BOLD);
//		}
//		//TODO: Create a single Font object for all ActivityNoteDialogs to save memory usage. Perhaps,
//		// move this class in some kind of a Font factory enforcing Singleton Font creation...
//		Font noteTitleFont = new Font(dialog.getDisplay(), noteTitleFontData);
//		noteTitle.setFont(noteTitleFont);  
//		noteTitle.pack();
		
		// Create the label which will contain the (uneditable) notes entered so far
		Label previousNotes = new Label(dialog, SWT.WRAP);
		previousNotes.setText(activityPanel_.getActivity().getNotes());
		GridData prevNotesGridData = new GridData();
		prevNotesGridData.horizontalAlignment = SWT.FILL;
		prevNotesGridData.grabExcessHorizontalSpace = true;
		prevNotesGridData.widthHint = dialogWidth_;
		previousNotes.setLayoutData(prevNotesGridData);
		
		FontData[] previousNotesFontData = previousNotes.getFont().getFontData();
		for (int i = 0; i < previousNotesFontData.length; i++) {
//			noteTitleFontData[i].setHeight(16);
			previousNotesFontData[i].setStyle(SWT.ITALIC);
		}
		//TODO: Create a single Font object for all ActivityNoteDialogs to save memory usage. Perhaps,
		// move this class in some kind of a Font factory enforcing Singleton Font creation...
		Font previousNotesFont = new Font(dialog.getDisplay(), previousNotesFontData);
		previousNotes.setFont(previousNotesFont);
		
		// Create the text box in which the note will be written
		// 
		// ASSERTION: The note_ cannot be null.
		final Text noteTextBox = new Text(dialog, SWT.LEFT | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData noteTextBoxGridData = new GridData();
		noteTextBoxGridData.heightHint = TEXT_BOX_HEIGHT;
		noteTextBoxGridData.horizontalAlignment = SWT.FILL;
		noteTextBoxGridData.grabExcessHorizontalSpace = true;
		noteTextBox.setLayoutData(noteTextBoxGridData);
		// noteTextBox.setLayoutData(new GridData(GridData.FILL_BOTH));	// This makes the text box larger than the default,
		// makes the text box fill in any remaining horizontal and vertical space
//		noteTextBox.append(note_);	no need to append the note_ anymore since it will be shown in the previousNotes label
		noteTextBox.pack();
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
				// The note_ field remains unchanged and the dialogResult_ is cancel
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
				// The note_ field was modified and the dialogResult_ is ok
				newNote_ = noteTextBox.getText();
				dialogResult_ = SWT.OK;
				dialog.dispose();
			}
		});
		
		// Open the dialog containing the widgets for creating a note
		dialog.pack();
		dialog.open();
		while (! dialog.isDisposed()) {
			if (! display.readAndDispatch()) {
				display.sleep();
			}
		}
    	
		// Dispose of the Font objects we created
//		noteTitleFont.dispose();
		previousNotesFont.dispose();
		
    	return dialogResult_;
	}
}
