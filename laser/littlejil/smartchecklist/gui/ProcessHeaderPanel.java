package laser.littlejil.smartchecklist.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.Activity;
import laser.littlejil.smartchecklist.gui.model.ActivityImpl;
import laser.littlejil.smartchecklist.gui.model.ActivityState;
import laser.littlejil.smartchecklist.gui.model.Process;
import laser.littlejil.smartchecklist.gui.utils.ImageManager;
import laser.littlejil.smartchecklist.gui.utils.PrettyPrintedNamesFormatter;
import laser.littlejil.smartchecklist.gui.utils.TooltipGenerator;
import laser.littlejil.smartchecklist.gui.widgets.ImageButton;
import laser.littlejil.smartchecklist.gui.widgets.StatusIndicator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The process header contains the process name and the process status indicator. Generally,
 * it will be shown right above the process checklist panel.
 *
 */
public class ProcessHeaderPanel implements PropertyChangeListener
{
	/**
	 * 
	 */
//	private static final int RIGHT_SIDE_PADDING = 39;
	private static final int RIGHT_SIDE_PADDING = 24;
	
	private Composite processHeaderComposite_;
	private Label processName_;
//	private Label processStatusVerbal_;
//	private Label processStatusImageHolder_;
//	private Image finishedSuccessfullyImage_;
//	private Image finishedFailedImage_;
	private Composite statusIndicatorPanel_;
	private ImageButton activityNoteButton_;
	private Composite panelForRightAlignment_;
	private Process process_;
	private ProcessPanel processPanel_;
	
	
	private Shell shell_;
	
	private Image noteWithoutTextImage_;
	private Image noteWithTextImage_;
	
	private boolean debuggingMode_ = Boolean.parseBoolean(System.getProperty(SmartChecklistGUI.DEBUGGING_MODE, "false"));
	
	
	public ProcessHeaderPanel(ProcessPanel processPanel, int style, Process process)
	{
		processPanel_ = processPanel;
		shell_ = processPanel.getGUI().getView();
		
		noteWithoutTextImage_ = new Image(processPanel.getGUI().getView().getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.NOTE_WITHOUT_TEXT_BUTTON_IMAGE_PATH));
		noteWithTextImage_ = new Image(processPanel.getGUI().getView().getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.NOTE_WITH_TEXT_BUTTON_IMAGE_PATH));
		
		processHeaderComposite_ = new Composite(processPanel.getGUI().getView(), style);
		GridData processHeaderCompositeGridData = new GridData();
		processHeaderCompositeGridData.horizontalAlignment = GridData.FILL;
		processHeaderCompositeGridData.grabExcessHorizontalSpace = true;
		//processHeaderCompositeGridData.heightHint = 50;
		processHeaderComposite_.setLayoutData(processHeaderCompositeGridData);
		processHeaderComposite_.setLayout(new GridLayout(4, false));
		
		processName_ = new Label(processHeaderComposite_, SWT.WRAP);
		GridData processNameGridData = new GridData();
		processNameGridData.horizontalAlignment = GridData.FILL;
		processNameGridData.grabExcessHorizontalSpace = true;
		processName_.setLayoutData(processNameGridData);
		
		//processStatusVerbal_ = new Label(statusIndicatorComposite_, SWT.NONE);
//		processStatusImageHolder_ = new Label(statusIndicatorComposite_, SWT.NONE);
		
		statusIndicatorPanel_ = new Composite(processHeaderComposite_, SWT.NONE);
		GridData statusIndPanelGridData = new GridData();
//		statusIndPanelGridData.heightHint = processHeaderCompositeGridData.heightHint-2;
//		statusIndPanelGridData.widthHint = 
		statusIndicatorPanel_.setLayoutData(statusIndPanelGridData);
		GridLayout statusIndicatorPanelLayout = new GridLayout();
		statusIndicatorPanelLayout.marginHeight = 0;
		statusIndicatorPanelLayout.marginWidth = 0;
		statusIndicatorPanel_.setLayout(statusIndicatorPanelLayout);	//If the layout is not set, the statusIndicatorPanel_ becomes weird size.		
		
		// Create the note button
		activityNoteButton_ = new ImageButton(processHeaderComposite_, noteWithoutTextImage_);
		GridData actNoteBtnGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		activityNoteButton_.setLayoutData(actNoteBtnGridData);
		activityNoteButton_.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				editNote();
			}
		});
		activityNoteButton_.pack();
		
		panelForRightAlignment_ = new Composite(processHeaderComposite_, SWT.NONE);
		GridData panelForRightAlignmentGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		panelForRightAlignmentGridData.widthHint = computeWidthOfPanelForRightAlignment();
		panelForRightAlignment_.setLayoutData(panelForRightAlignmentGridData);
		panelForRightAlignment_.setLayout(new GridLayout()); //If the layout is not set, the statusIndicatorPanel_ becomes weird size.
		
		if (debuggingMode_)
		{
			processHeaderComposite_.setBackground(processPanel.getGUI().getView().getDisplay().getSystemColor(SWT.COLOR_RED));
			processName_.setBackground(processPanel.getGUI().getView().getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW));
			statusIndicatorPanel_.setBackground(processPanel.getGUI().getView().getDisplay().getSystemColor(SWT.COLOR_CYAN));
			panelForRightAlignment_.setBackground(processPanel.getGUI().getView().getDisplay().getSystemColor(SWT.COLOR_CYAN));
		}
		
		
//		processStatusImageHolder_.pack();
//		processHeaderComposite_.pack();
		processHeaderComposite_.layout();
		processHeaderComposite_.getParent().layout();
		System.out.println("ProcessStatusIndicatorPanel created successfully.");
		process.addPropertyChangeListener(this);
		process_ = process;
	}
	
	public void setProcessName(final String name)
	{
		final Display display = shell_.getDisplay();
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	display.syncExec(new Runnable() {
    		public void run() {
			String title = ProcessPanel.TITLE + " ";
			if (name != null) {
				title += "\"" + name + "\"";
			}
			
			processName_.setText(title);
			// Set the font to bold and 16
			FontData[] fontData = processName_.getFont().getFontData();
			for (int i = 0; i < fontData.length; i++) {
				fontData[i].setHeight(16);
	//			fontData[i].setStyle(SWT.BOLD);
			}
			//TODO: Should we dispose of this Font object somewhere?
			Font newFont = new Font(processHeaderComposite_.getDisplay(), fontData);
			processName_.setFont(newFont);
			processName_.pack();
			
			processHeaderComposite_.layout();
//			processHeaderComposite_.pack();
    	}
    	});
	}
	
//	public void setProcessStatusVerbal(String processStatus)
//	{
//		processStatusVerbal_.setText(processStatus);
//		// Set the font to bold and 16
//		FontData[] fontData = processStatusVerbal_.getFont().getFontData();
//		for (int i = 0; i < fontData.length; i++) {
//			fontData[i].setHeight(16);
////			fontData[i].setStyle(SWT.BOLD);
//		}
//		//TODO: Should we dispose of this Font object somewhere?
//		Font newFont = new Font(statusIndicatorComposite_.getDisplay(), fontData);
//		processStatusVerbal_.setFont(newFont);
//		//processName_.pack();
//		statusIndicatorComposite_.pack();
//	}
	
	public void setProcessState(final ActivityState processState)
	{
		System.out.println("Entering ProcessStatusIndicatorPanel.setProcessState()");
		System.out.println("Process state = " + processState);
		final Display display = shell_.getDisplay();
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	display.syncExec(new Runnable() {
    		public void run() {
    			GridData statusIndicatorGridData = new GridData();
				statusIndicatorGridData.grabExcessHorizontalSpace = true;
				statusIndicatorGridData.horizontalAlignment = SWT.FILL;
    			if (processState == ActivityState.COMPLETED) {
//    				processStatusImageHolder_.setImage(finishedSuccessfullyImage_);
    				String timeStamp = new SimpleDateFormat("H:mm").format(process_.getRootActivity().getFinishedTimestamp());
    				StatusIndicator statusIndicator = new StatusIndicator(statusIndicatorPanel_, StatusIndicator.SUCCESSFUL_STATUS, timeStamp, SWT.VERTICAL);
    				statusIndicator.setLayoutData(statusIndicatorGridData);
    				statusIndicatorPanel_.pack();
    				statusIndicator.setToolTipText(TooltipGenerator.generateProcessCompletedSuccessfullyTooltip());
    				    				
    			}
    			else if (processState == ActivityState.TERMINATED) {
//    				processStatusImageHolder_.setImage(finishedFailedImage_);
    				String timeStamp = new SimpleDateFormat("H:mm").format(process_.getRootActivity().getFinishedTimestamp());
    				StatusIndicator statusIndicator = new StatusIndicator(statusIndicatorPanel_, StatusIndicator.FAILED_STATUS, timeStamp, SWT.VERTICAL);
    				statusIndicator.setLayoutData(statusIndicatorGridData);
    				statusIndicatorPanel_.pack();
    				
    				Set<Serializable> thrownExceptions = process_.getRootActivity().getThrownExceptions();
    				String statusIndicatorTooltip = TooltipGenerator.generateProblemsTooltipForProcessHeader(thrownExceptions);
    				statusIndicator.setToolTipText(statusIndicatorTooltip);
    			}
//    			else {
//    				Image runningImage = new Image(display, SmartChecklistGUI.class.getResourceAsStream("images/blankStatusIndicator.png"));
//    				processStatusImageHolder_.setImage(runningImage);
//    			}
    			processHeaderComposite_.layout();
    			processHeaderComposite_.getParent().layout();
//    			processHeaderComposite_.redraw();
    			//statusIndicatorComposite_.layout();
    		}
    	});
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(Process.NAME_PROPERTY_NAME)) {
			this.setProcessName((String)event.getNewValue());
		}
		else if (event.getPropertyName().equals(Process.ACTIVITIES_PROPERTY_NAME)) {
			Activity newActivity = (Activity)event.getNewValue();
			if (newActivity.getParent() == null) {
				// This is the root activity
				newActivity.addPropertyChangeListener(this);
				System.out.println("Registered as listener for activity" + newActivity.getName());
				//TODO: Remove the property change listener
			}
		}
		else if (event.getPropertyName().equals(ActivityImpl.ACTIVITY_STATE_PROPERTY_NAME)) {
			ActivityState newActivityState = (ActivityState)event.getNewValue();
			this.setProcessState(newActivityState);
		}
	}
	
	private int computeWidthOfPanelForRightAlignment()
	{
		int width = ActivityPanel.MAX_NUM_LEVELS * ActivityPanel.PGROUP_SPACING + RIGHT_SIDE_PADDING;
		return width;
	}
	
	//TODO: Consider creating a separate note widget. Currently, this editNote() method is 
	// almost the same as the editNote() method in Activity panel.
	protected void editNote()
	{
		Activity rootActivity = process_.getRootActivity();
		ActivityPanel rootActivityPanel = processPanel_.getChecklistPanel().getActivityPanel(rootActivity);
		ActivityNoteDialog activityNoteDialog = new ActivityNoteDialog(rootActivityPanel);
		int dialogResult = activityNoteDialog.open();
		if (dialogResult == SWT.OK) {
			// Store the previous notes to determine below what note icon to use
			String previousNotes = rootActivity.getNotes();
			// If the new note is not empty, add it to the activity notes
			if (activityNoteDialog.getNewNote().trim().length() > 0)
			{
				// Get the current time
				String timeStamp = new SimpleDateFormat("d MMM yyyy HH:mm").format(Calendar.getInstance().getTime());
				// Add the new notes (with time stamp) to the activity's notes.
				//TODO: The notes are currently a single String. Consider using some more sophisticated
				// data structure. E.g., the notes for an activity can be a List of Note objects. 
				rootActivity.setNotes(rootActivity.getNotes() + timeStamp + 
						" (" + ActivityPanel.AGENT_NAME + "):\t" + activityNoteDialog.getNewNote().trim() + "\n");
			}
			
			// Determine whether the note icon should change.
			if (previousNotes.trim().isEmpty())
			{
				// There were no previous notes, but now there are -- switch to image of note with text 
				if (!rootActivity.getNotes().trim().isEmpty())
					activityNoteButton_.setCurrentImage(noteWithTextImage_);
			}
			else // There were previous notes, but now there aren't -- switch to blank note image
				if (rootActivity.getNotes().trim().isEmpty())
					activityNoteButton_.setCurrentImage(noteWithoutTextImage_);
			// END of determine whether the note icon should change
			
			// Set the tooltip text of the activityNoteButton_ to the current notes associated with the activity. 
			activityNoteButton_.setToolTipText(rootActivity.getNotes());
			
			processHeaderComposite_.redraw();	// Need to redraw the activityNoteButton_ since its image changed.
			// We redraw the entire activity panel, because redrawing just the activityNoteButton_ 
			// causes it to have different background color from the activity panel's background color
			System.out.println("Activity Note is \"" + rootActivity.getNotes() + "\".");
		}
	}
	
}
