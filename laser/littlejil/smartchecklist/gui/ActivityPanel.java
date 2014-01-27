package laser.littlejil.smartchecklist.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.Activity;
import laser.littlejil.smartchecklist.gui.model.ActivityImpl;
import laser.littlejil.smartchecklist.gui.model.ActivityState;
import laser.littlejil.smartchecklist.gui.model.ParameterDeclaration;
import laser.littlejil.smartchecklist.gui.utils.CoolProcessHistoryColorPalette;
import laser.littlejil.smartchecklist.gui.utils.ImageManager;
import laser.littlejil.smartchecklist.gui.utils.TooltipGenerator;
import laser.littlejil.smartchecklist.gui.widgets.ImageButton;
import laser.littlejil.smartchecklist.gui.widgets.StatusIndicator;

import org.eclipse.nebula.widgets.pgroup.AbstractGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.RectangleGroupStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.jface.dialogs.InputDialog;


// After the item is posted, the human performer may 
// select among opting out, completing, and terminating.
// For now, only completing and terminating are supported.
public class ActivityPanel implements PropertyChangeListener
{
	public static final int ACTIVITY_PANEL_INDENT = 25;
	
	protected static final int PGROUP_SPACING = 7;	// The number of pixels between the borders of 2 enclosed PGroups.
													// Not sure if this number is exactly right.
	
	protected static final int MAX_NUM_LEVELS = 9;	// The maximum number of levels of step nesting in the process.
	// HACK: For now we hard-code it. Can try to compute it from static tree, but in general this number
	// might not be bounded due to recursion and unbounded cardinality.
	public static final String AGENT_NAME = "Nurse J. Smith";
	
	private ProcessHistoryColorPalette colorPalette_;
	private ProcessChecklistPanel checklistPanel_;
	private ActivityPanel parentActivityPanel_;
	private Activity activity_;
	private Composite activityPanelView_;
	private ImageButton activityRequiresButton_;
	private ImageButton activitySuccessfulButton_;
	private ImageButton activityFailedButton_;
	private Canvas activityFinishedSymbol_;
	private Label activityLabel_;
	private ImageButton activityNoteButton_;
	private Composite requiredInputsPanel_;
	private Composite statusIndicatorPanel_;
	private Composite stepNamePanel_; 
	private Composite buttonsPanel_;
	
	//Images used for icons
	Image successfulImage_;
	Image failedImage_;
	Image noteWithTextImage_;
	Image noteWithoutTextImage_;
	Image activityRequiresImage_;
	Image activityCompletedImage_;
	Image activityFailedImage_;
	
	private int requiredThingsPanelWidth_;
	
	/**
	 * The width of the buttons panel. Used to ensure all buttons appear "in the same column".
	 */
	private int buttonsPanelWidth_;
	/**
	 * The width of the status indicator panel. Used to ensure all status indicators appear "in the same column". 
	 */
	private int statusIndicatorPanelWidth_;
//	/**
//	 * The height of the activity panel;
//	 */
//	private int activityPanelHeight_;
	
	private boolean debuggingMode_ = Boolean.parseBoolean(System.getProperty(SmartChecklistGUI.DEBUGGING_MODE, "false"));
	
	public ActivityPanel(ProcessChecklistPanel processPanel, ActivityPanel parentActivityPanel, Activity activity) {
		super();

		this.checklistPanel_ = processPanel;
		this.parentActivityPanel_ = parentActivityPanel;
		this.activity_ = activity;
		System.out.println("Activity: " + this.activity_);
		System.out.println("\tgetInputParameterDeclarations: " + this.activity_.getInputParameterDeclarations());
		System.out.println("\tgetOutputParameterDeclarations: " + this.activity_.getOutputParameterDeclarations());		
		System.out.println("\tgetExceptionDeclarations: " + this.activity_.getExceptionDeclarations());
		
		this.createGUI();
	}
	
	private void createGUI() {
		final ActivityPanel activityPanel = this;
    	final Shell view = this.getProcessPanel().getGUI().getView();
    	final Display display = view.getDisplay();
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	display.syncExec(new Runnable() {
    		public void run() {
//    			System.out.println("Creating ActivityPanel for activity " + activity_.getName());
    			Device device = checklistPanel_.getProcessPanel().getGUI().getView().getDisplay();
//    			colorPalette_ = new DefaultProcessHistoryColorPalette(device);
    			colorPalette_ = new CoolProcessHistoryColorPalette(device);
    			
    			successfulImage_ = new Image(display, SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_COMPLETE_BUTTON_IMAGE_PATH));
    			failedImage_ = new Image(display, SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_FAILED_BUTTON_IMAGE_PATH));
    			noteWithTextImage_ = new Image(display, SmartChecklistGUI.class.getResourceAsStream(ImageManager.NOTE_WITH_TEXT_BUTTON_IMAGE_PATH));
    			noteWithoutTextImage_ = new Image(display, SmartChecklistGUI.class.getResourceAsStream(ImageManager.NOTE_WITHOUT_TEXT_BUTTON_IMAGE_PATH));
    			activityRequiresImage_ = new Image(display, SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_REQUIRES_IMAGE_PATH));
    			activityCompletedImage_ = new Image(display, SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_SUCCESSFULL_IMAGE_PATH));
    			activityFailedImage_ = new Image(display, SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_FAILED_IMAGE_PATH));
    			
    			requiredThingsPanelWidth_ = activityRequiresImage_.getBounds().width + 10;
    			// Compute the width of the buttons panel and of the status indicator panel
    			buttonsPanelWidth_ = successfulImage_.getBounds().width + failedImage_.getBounds().width + 20;
    			
    			//TODO: Get this width somehow from the StatusIndicator widget or by ACTIVITY_SUCCESSFULL_IMAGE_PATH a constant that all classes use.
    			statusIndicatorPanelWidth_ = Math.max(activityCompletedImage_.getBounds().width, activityFailedImage_.getBounds().width) + 10;
//    			activityPanelHeight_ = successfulImage.getBounds().height + 15;
    			
    			Composite parentPanel = null;
    			// If this is the root activity, then the parent panel
    			// will be the process panel
    			if (parentActivityPanel_ == null) {
//    				System.out.println("\tThis is the root activity.");
    				parentPanel = checklistPanel_.getChecklistPanel();
    			}
    			// Otherwise the parent panel will be the ActivityPanel
    			// for the parent Activity
    			else {
//    				System.out.println("\tThis is another activity.");
    				parentPanel = parentActivityPanel_.activityPanelView_;
    			}
    			
    			if (activity_.isLeaf()) {
//    				System.out.println("\t\tThis is a leaf.");
    				activityPanelView_ = new Group(parentPanel, SWT.SHADOW_ETCHED_IN);
    				GridData activityPanelGridData = new GridData();
    				activityPanelGridData.horizontalIndent = ACTIVITY_PANEL_INDENT;
    				activityPanelGridData.horizontalAlignment = GridData.FILL;
    				activityPanelGridData.grabExcessHorizontalSpace = true;
//    				activityPanelGridData.heightHint = activityPanelHeight_;
    				activityPanelView_.setLayoutData(activityPanelGridData);
    				GridLayout activityPanelViewLayoutManager = new GridLayout(6, false);
    				System.out.println("activityPanelViewLayoutManager.marginTop = " + activityPanelViewLayoutManager.marginTop);
    				System.out.println("activityPanelView_.computeTrim(0, 0, 20, 10) = " + activityPanelView_.computeTrim(0, 0, 20, 10));
    				activityPanelView_.setLayout(activityPanelViewLayoutManager);
    				
    				// Create the step name panel that contains the 
    				// the activityLabel_.
    				stepNamePanel_ = new Composite(activityPanelView_, SWT.NONE);
    				stepNamePanel_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    				stepNamePanel_.setLayout(new GridLayout(2, false));    				
    				// Create the label with the activity name
    				activityLabel_ = new Label(stepNamePanel_, SWT.BORDER);
    				activityLabel_.setText(getActivity().getName());
    				// Make the font of the label's text bold and size 16
    				FontData[] fontData = activityLabel_.getFont().getFontData();
    				for (int i = 0; i < fontData.length; i++) {
    					fontData[i].setHeight(16);
    					fontData[i].setStyle(SWT.BOLD);
    				}
    				Font newFont = new Font(display, fontData);
    				activityLabel_.setFont(newFont);    			
    				activityLabel_.pack();
    				//System.out.println("Created label.");
    				
    				// Create the panel that contains the icon/button for required artifacts/resources
    				requiredInputsPanel_ = new Composite(activityPanelView_, SWT.NONE);
    				GridData requiredInputsPanelGridData = new GridData();
    				requiredInputsPanelGridData.widthHint = requiredThingsPanelWidth_;
    				requiredInputsPanel_.setLayoutData(requiredInputsPanelGridData);
    				
    				GridLayout requiredInputsPanelLayout = new GridLayout(1, false);
    				requiredInputsPanelLayout.marginHeight = 0;	// This way, there won't be any blank space between the buttons and the top/bottom borders of the buttonsPanel_.  
    				requiredInputsPanel_.setLayout(requiredInputsPanelLayout);
    	
			    	// Create the requires button, if there are any inputs
					Set<ParameterDeclaration> inputParameterDeclSet = activity_.getInputParameterDeclarations();
					if ((inputParameterDeclSet != null) && (! inputParameterDeclSet.isEmpty())) {
						activityRequiresButton_ = new ImageButton(requiredInputsPanel_, activityRequiresImage_);
						//TODO: Add a requires view
						// Set the tooltip of the activityRequiresButton_ -- the tooltip will contain
						// the inputs of this activity
						String activityRequiresTooltip = TooltipGenerator.generateParameterDeclsTooltip(activity_, TooltipGenerator.REQUIRES_TITLE, inputParameterDeclSet);
						activityRequiresButton_.setToolTipText(activityRequiresTooltip);
//						System.out.println("set tooltip of requires button to: " + activityRequiresButton_.getToolTipText());
						// END OF Set the tooltip of the activityRequiresButton_
						activityRequiresButton_.pack();
//						System.out.println("Created requires button.");
					}

    				// Create the panel that contains the 
    				// activitySuccessfulButton_ and the activityFailedButton_
    				buttonsPanel_ = new Composite(activityPanelView_, SWT.NONE);
    				GridData buttonsPanelGridData = new GridData();
    				buttonsPanelGridData.widthHint = buttonsPanelWidth_;
    				buttonsPanel_.setLayoutData(buttonsPanelGridData);
    				int numberOfButtons = 0;
    				
    				
    				// Create the successful button
    				activitySuccessfulButton_ = new ImageButton(buttonsPanel_, successfulImage_);
    				numberOfButtons++;
    				activitySuccessfulButton_.addMouseListener(new MouseAdapter() {
    					public void mouseDown(MouseEvent e) {
    						//HACK: The dialog may need to be specialized based on
    						//      the output parameter declarations' types.
    						Set<ParameterDeclaration> outputParameterDeclSet = activity_.getOutputParameterDeclarations();
    						if ((outputParameterDeclSet != null) && (outputParameterDeclSet.size() == 1)) 
    						{
    							ParameterDeclaration outputParameterDecl = outputParameterDeclSet.iterator().next();
    							if (outputParameterDecl.getType().endsWith("WithDecisionNodeSupport")) {
    								ActivityDecisionNodeDialog activityDecisionDialog = new ActivityDecisionNodeDialog(activityPanel);
    								int dialogResult = activityDecisionDialog.open();
    								if (dialogResult == SWT.OK) {
    									performActivitySuccessfully(activitySuccessfulButton_, activityDecisionDialog.getOutputParametersActuals());
    								}
    							}
    							else {
    								performActivitySuccessfully(activitySuccessfulButton_, null);
    							}
    						}
    						else {
    							performActivitySuccessfully(activitySuccessfulButton_, null);
    						}
    					}
    				});
    				activitySuccessfulButton_.pack();
    				//System.out.println("Created success button.");
    				
    				// Create the failed button, if the step throws any exceptions
    				if (!activity_.getExceptionDeclarations().isEmpty()) {
    					activityFailedButton_ = new ImageButton(buttonsPanel_, failedImage_);
    					numberOfButtons++;
    					activityFailedButton_.addMouseListener(new MouseAdapter() {
    						public void mouseDown(MouseEvent e) {
    							ActivityFailedDialog activityFailedDialog = new ActivityFailedDialog(activityPanel);
    							int dialogResult = activityFailedDialog.open();
    							if (dialogResult == SWT.OK) {
    								performActivityFailed(activityFailedButton_, activityFailedDialog.getExceptionsThrown());
    							}
    						}
    					});
    					activityFailedButton_.pack();
    					//System.out.println("Created failed button.");
    				}
    				
    				// Layout the buttons panel
    				GridLayout buttonsPanelLayout = new GridLayout(numberOfButtons, false);
        			buttonsPanelLayout.marginHeight = 0;	// This way, there won't be any blank space between the buttons and the top/bottom borders of the buttonsPanel_.  
        			buttonsPanel_.setLayout(buttonsPanelLayout);
        			
    				
    				statusIndicatorPanel_ = new Composite(activityPanelView_, SWT.NONE);
    				// Set the grid data of the statusIndicatorPanel to set size
    				GridData statusIndicatorPanelGridData = new GridData();
//    				statusIndicatorPanelGridData.heightHint = 25;
    				statusIndicatorPanelGridData.widthHint = statusIndicatorPanelWidth_;
    				statusIndicatorPanel_.setLayoutData(statusIndicatorPanelGridData);
    				GridLayout statusIndicatorPanelLayout = new GridLayout(1, false);
    				statusIndicatorPanelLayout.marginHeight = 0;
    				statusIndicatorPanelLayout.marginWidth = 0;
    				statusIndicatorPanelLayout.verticalSpacing = 0;
    				statusIndicatorPanel_.setLayout(statusIndicatorPanelLayout);
    				
    				// Create the note button
    				activityNoteButton_ = new ImageButton(activityPanelView_, noteWithoutTextImage_);
    				GridData actNoteBtnGridData = new GridData(SWT.END, SWT.CENTER, false, false);
    				activityNoteButton_.setLayoutData(actNoteBtnGridData);
    				activityNoteButton_.addMouseListener(new MouseAdapter() {
    					public void mouseDown(MouseEvent e) {
    						editNote();
    					}
    				});
    				activityNoteButton_.pack();
    				
    				// This blank panel is placed after the activityNoteButton to ensure that activityNoteButtons
    				// are right aligned. Could not find another way to add blank space to the right of a
    				// composite, the GridData has just horizontalIndent, which is for adding space to the left.
    				Composite panelForRightAlignment = new Composite(activityPanelView_, SWT.NONE);
    				GridData panelForRightAlgnmtGridData = new GridData();
    				panelForRightAlgnmtGridData.widthHint = computeWidthOfPanelForRightAlignment(activityPanelView_.getParent());
    				// TODO: Consider using a constant for the panelForRightAlgnmtGridData.heightHint,
    				// instead of hard-coding it as below.
    				panelForRightAlgnmtGridData.heightHint = noteWithTextImage_.getBounds().height + 1;
    				panelForRightAlignment.setLayoutData(panelForRightAlgnmtGridData);
    			}
    			else {
//    				System.out.println("\t\tThis is a non-leaf.");
    				PGroup activityPanel = new PGroup(parentPanel, SWT.SMOOTH);
    				activityPanel.setTogglePosition(SWT.LEFT);
    				//activityPanel.setToggleRenderer(new TwisteToggleRenderer());
    				activityPanel.setLayout(new GridLayout());
    				final Composite finalParentPanel = parentPanel;
    				activityPanel.addExpandListener(new ExpandListener() {
						@Override
						public void itemCollapsed(ExpandEvent e) {
							layoutAncestors();
						}
						@Override
						public void itemExpanded(ExpandEvent e) {
							layoutAncestors();
						}
						private void layoutAncestors()
						{
							Composite parentsParent = finalParentPanel.getParent();
							while (parentsParent != null)
							{
								parentsParent.layout();
								parentsParent = parentsParent.getParent();
							}
						}
    				});
    			    GridData activityPanelGridData = new GridData();
    			    if (parentPanel instanceof PGroup) {
    			    	activityPanelGridData.horizontalIndent = ACTIVITY_PANEL_INDENT;
    			    }
    			    activityPanelGridData.horizontalAlignment = GridData.FILL;
    			    activityPanelGridData.grabExcessHorizontalSpace = true;
    			    activityPanel.setLayoutData(activityPanelGridData);
    				activityPanel.setText(activity_.getName());
    				activityPanelView_ = activityPanel;
    			}
    			
    			System.out.println("Before calling activityPanelView_.pack() ");
    			activityPanelView_.pack();
    			System.out.println("Packed activityPanelView");
				checklistPanel_.getChecklistPanel().pack();
				getProcessPanel().getProcessPanel().pack();
				System.out.println("Packed the process panel");
				//    			view.pack();	// Not packing the view allows the GUI to stay at the size user left it
				// before a new ActivityPanel is added to it.
				// Calling layout() instead of pack() on the above widgets did not work...
				//    			leftPanel.layout();
				//    			rightPanel.layout();
				//    			activityPanel_.layout();
				//    			processPanel_.getProcessPanel().layout();
				//    			view.layout();
    			
				setBackgroundGUI(activity_.getState());
				System.out.println("Set background GUI");
				
				//TODO: The dispose method should removePropertyChangeListener
				activity_.addPropertyChangeListener(activityPanel);
    		}

			// Computes the width of the blank panel that is used for aligning things to the right.
    		// The width is based on the number of PGroups enclosing this activity panel,
    		// the space between 2 nested, PGroups, and the maximum possible number of nested PGroups.
    		// Basically, the smaller the nesting, the larger the width of the blank panel, so that the
    		// last widget on the activity panel is pushed more to the left.
			private int computeWidthOfPanelForRightAlignment(
					Composite parent)
			{
				// The maximum number of levels of step nesting in the process.
				// HACK: For now hard-code it, can compute from static tree, but in general this number
				// might not be bounded due to recursion and unbounded cardinality.
				int maxNumLevels = MAX_NUM_LEVELS;
				
				int maxWidth = maxNumLevels * PGROUP_SPACING + 1;
				
				int currentNumLevels = 0;
				Composite parentComposite = parent;
				while (parentComposite instanceof PGroup)
				{
					currentNumLevels++;
					parentComposite = parentComposite.getParent();
				}
				
				System.out.println("WidthOfPanelForRightAlignment = " + (maxWidth - currentNumLevels * PGROUP_SPACING));
				return maxWidth - currentNumLevels * PGROUP_SPACING;
			}
    	});
	}
	
	protected void disableGUI() {
		System.out.println("Entering disableGUI");
		if (this.activity_.isLeaf()) {
			if (! this.activitySuccessfulButton_.isDisposed()) {
				this.activitySuccessfulButton_.setEnabled(false);
			}
			if (this.activityFailedButton_ != null) {
				if (! this.activityFailedButton_.isDisposed())
					this.activityFailedButton_.setEnabled(false);
			}
			//this.activityNoteButton_.setEnabled(false);
		}
	}
	
	protected void setBackgroundGUI(ActivityState state) {
		Color bgColor;
		
		//System.out.println("Entering setBackgroundGUI() for activity \"" + activity_.getName() + "\" with state " + state.toString());

		bgColor = this.colorPalette_.getColor(state);
		
		this.setBackgroundGUI(bgColor);
	}
	
	protected void setBackgroundGUI(final Color bgColor) {
		final Display display = this.getProcessPanel().getGUI().getView().getDisplay();
		
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	display.syncExec(new Runnable() {
    		public void run() {    			
    			// This is defined recursively
    			//System.out.println("Setting ActivityPanel \"" + activity_.getName() + "\" to color " + bgColor);
    			// This is a leaf activity
    			if (activity_.isLeaf()) {
    				System.out.println("\tUpdated leaf \"" + activity_.getName() + "\" to use given color.");
    				// MODIFIED: Not explicitly setting the background colors anymore, because setting the
    				// background of the activityPanelView_ seems to automatically set the background color
    				// of enclosed widgets correctly. Otherwise, the activityPanelView_ background color ends
    				// up being slightly darker since it is a Group.
    				// Enclosed the setting of background colors in a conditional, so that we can see the
    				// bounds of the different in a debugging mode. 
    				// Deal with the buttons
    				if (debuggingMode_)
    				{
    					//TODO: The following needs to use the color palette
    					if ((activityRequiresButton_ != null) &&
    						(! activityRequiresButton_.isDisposed())) 
    					{
    						activityRequiresButton_.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
    					}
	    				if (! activitySuccessfulButton_.isDisposed()) {
//	    					activitySuccessfulButton_.setBackground(bgColor);
	    					activitySuccessfulButton_.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
	    				}
	    				if (activityFinishedSymbol_ != null) {
//	    					activityFinishedSymbol_.setBackground(bgColor);
	    					activityFinishedSymbol_.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
	    				}
	    				if ((activityFailedButton_ != null) && 
	    				    (! activityFailedButton_.isDisposed()))
	    				{
//	    					activityFailedButton_.setBackground(bgColor);
	    					activityFailedButton_.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
	    				}
//	    				activityNoteButton_.setBackground(bgColor);
	    				activityNoteButton_.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
	    				 //Deal with the internal panels
	    				for (Control child : activityPanelView_.getChildren()) {
	    					System.out.println("Setting background for child " + child);
//	    					child.setBackground(bgColor);
	    					child.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
	    				} // end for child    				
    				}
    				// Deal with the activityPanelView_ that encloses all the widgets.
    				activityPanelView_.setBackground(bgColor);
    				//System.out.println("\tUpdated color for leaf \"" + activity_.getName() + "\".");
    				
    			}
    			// This is a non-leaf activity
    			//
    			// NOTE: The constructor ensures that the following is a PGroup
    			else // if (activityPanel_ instanceof PGroup)
    			{
    				//System.out.println("\tUpdating non-leaf \"" + activity_.getName() + "\" to use given color for borders.");
    				AbstractGroupStrategy pGroupStrategy = ((PGroup) activityPanelView_).getStrategy();
    				// Here, we make an assumption that the PGroup's strategy is RectangleGroupStrategy.
    				RectangleGroupStrategy rectPGroupStrategy = (RectangleGroupStrategy) pGroupStrategy;
    				// Set the border of the PGroup that surrounds enclosed widgets
    				rectPGroupStrategy.setBorderColor(bgColor);
    				// Set the color of the title area of the PGroup
    				rectPGroupStrategy.setBackground(new Color[] {bgColor, bgColor }, new int[] {100 }, true);

    				rectPGroupStrategy.update();
    				((PGroup) activityPanelView_).redraw(); 
    				//System.out.println("\tUpdated color for non-leaf \"" + activity_.getName() + "\".");
    			}
       		}
        });
	}
	
	public ProcessPanel getProcessPanel() {
		return this.checklistPanel_.getProcessPanel();
	}
	
	protected Composite getActivityPanel() {
		return this.activityPanelView_;
	}
	
	public Activity getActivity() {
		return this.activity_;
	}
	
	protected void editNote() {
		//TODO: It would be better to implement the ActivityNoteDialog in SWT not JFace
		/*
		try {
			String noteLabelName = NOTE_LABEL_PREFIX + "\"" + activity_.getStep().getName() + "\"";
			InputDialog activityNoteDialog = new InputDialog(processPanel_.getGUI().getView(),
                    NOTE_TITLE,
                    noteLabelName,
                    activityNote_,
                    null);
			int dialogResult = activityNoteDialog.open();
			if (dialogResult == SWT.OK) {
				activityNote_ = activityNoteDialog.getValue();
				System.out.println("NOTE: " + activityNote_);
			}
		} catch (AMSException amsExc) {
			// TODO Auto-generated catch block
			amsExc.printStackTrace();
		}
		*/
		ActivityNoteDialog activityNoteDialog = new ActivityNoteDialog(this);
		int dialogResult = activityNoteDialog.open();
		if (dialogResult == SWT.OK) {
			// Store the previous notes to determine below what note icon to use
			String previousNotes = this.getActivity().getNotes();
			// If the new note is not empty, add it to the activity notes
			if (activityNoteDialog.getNewNote().trim().length() > 0)
			{
				// Get the current time
				String timeStamp = new SimpleDateFormat("d MMM yyyy HH:mm").format(Calendar.getInstance().getTime());
				// Add the new notes (with time stamp) to the activity's notes.
				//TODO: The notes are currently a single String. Consider using some more sophisticated
				// data structure. E.g., the notes for an activity can be a List of Note objects. 
				this.getActivity().setNotes(this.getActivity().getNotes() + timeStamp + 
						" (" + AGENT_NAME + "):\t " + activityNoteDialog.getNewNote().trim() + "\n");
			}
			
			// Determine whether the note icon should change.
			if (previousNotes.trim().isEmpty())
			{
				// There were no previous notes, but now there are -- switch to image of note with text 
				if (!this.getActivity().getNotes().trim().isEmpty())
					activityNoteButton_.setCurrentImage(noteWithTextImage_);
			}
			else // There were previous notes, but now there aren't -- switch to blank note image
				if (this.getActivity().getNotes().trim().isEmpty())
					activityNoteButton_.setCurrentImage(noteWithoutTextImage_);
			// END of determine whether the note icon should change
			
			// Set the tooltip text of the activityNoteButton_ to the current notes associated with the activity. 
			activityNoteButton_.setToolTipText(activity_.getNotes());
			
			activityPanelView_.redraw();	// Need to redraw the activityNoteButton_ since its image changed.
			// We redraw the entire activity panel, because redrawing just the activityNoteButton_ 
			// causes it to have different background color from the activity panel's background color
			System.out.println("Activity Note is \"" + this.getActivity().getNotes() + "\".");
		}
	}
	
	protected void performActivity() {
		this.disableGUI();
		// The propertyChange method is responsible for updating the background
		// color based on the activity's state
	}
	
	protected void performActivitySuccessfully(ImageButton activitySuccessfulButton, Map<String,Serializable> outputParameters) {
		this.performActivity();
		
		// Update the activity model
		this.getActivity().startAndComplete(outputParameters);
		
		// The propertyChange method is responsible for updating the GUI
		// based on the current process execution state
	}
	
	protected void performActivityFailed(ImageButton activityFailedButton, Set<Serializable> exceptionsThrown) {
		this.performActivity();
		
		// Update the activity model
		this.getActivity().startAndTerminate(exceptionsThrown);
		
		// The propertyChange method is responsible for updating the GUI
		// based on the current process execution state
	}
	
	protected void cancelActivity() {
		System.out.println("Entering cancelActivuty()");
		Shell view = this.getProcessPanel().getGUI().getView();
    	Display display = view.getDisplay();
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	display.syncExec(new Runnable() {
    		public void run() {
    			activitySuccessfulButton_.dispose();
				if (activityFailedButton_ != null)
					activityFailedButton_.dispose();
    			disableGUI();
    			// The propertyChange method is responsible for updating the background
    			// color based on the activity's state
    		}
    	});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Update the background color based on the activity's state
		if (evt.getPropertyName().equals(ActivityImpl.ACTIVITY_STATE_PROPERTY_NAME)) {
			ActivityState newActivityState = (ActivityState)evt.getNewValue();
			System.out.println("Activity " + this.activity_.getName() + " changed state to " + newActivityState);
			if (newActivityState == ActivityState.RETRACTED) {
				this.cancelActivity();
			}
			else if (newActivityState == ActivityState.COMPLETED) {
				final Display display = this.getProcessPanel().getGUI().getView().getDisplay();

				// The SWT widgets must be created within the UI thread.
				// The Little-JIL agents are updated within non-UI threads.
				// The following ensures that the the updates are within the UI thread.
				display.syncExec(new Runnable() {
					public void run() {   
						// Update the GUI if not already done
						if ((activitySuccessfulButton_ == null) ||
							activitySuccessfulButton_.isDisposed()) {
							return;
						}
						
//						// Create a new widget to show the completion of an activity, add a time stamp and place them
//						// in the statusIndicatorPanel. Dispose of
//						// the old activitySuccessfulButton and activityFailedButton.
//
//						final Image activityCompletedImage = new Image(activitySuccessfulButton_.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ACTIVITY_SUCCESSFULL_IMAGE_PATH));
//
//
//						// Use canvas to hold the image, since canvas supports transparency in images,
//						// unlike Label and Button.
//						// Also, use a GC object and draw the text on top of it.
//						GC graphicsContext = new GC(activityCompletedImage);
//						activityFinishedSymbol_ = new Canvas(statusIndicatorPanel_,SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
//						activityFinishedSymbol_.addPaintListener(new PaintListener() {
//							public void paintControl(PaintEvent e) {
//								e.gc.drawImage(activityCompletedImage,0,0);
//							}
//						});
//						
//						// Set the size of the canvas. We set the size to be slightly larger than the image
//						// enclosed by the canvas--this image is 20 by 20 pixels
//						GridData gd = new GridData(23, 23);
//						gd.horizontalAlignment = SWT.CENTER;
//						activityFinishedSymbol_.setLayoutData(gd);
//						// Set the background of the canvas to be the same color as the color of its parent widget.
//						activityFinishedSymbol_.setBackground(activityFinishedSymbol_.getParent().getBackground());
						
						// Get the current time
						String timeStamp = new SimpleDateFormat("H:mm").format(getActivity().getFinishedTimestamp());
//						// Add the current time stamp to the statusIndicatorPanel
//						Label timeStampLabel = new Label(statusIndicatorPanel_, SWT.BORDER);
//						timeStampLabel.setText(timeStamp);
//	    				// Make the font of the label's text bold and size 16
//	    				FontData[] fontData = timeStampLabel.getFont().getFontData();
//	    				for (int i = 0; i < fontData.length; i++) {
//	    					fontData[i].setHeight(10);
//	    				}
//	    				Font newFont = new Font(display, fontData);
//	    				timeStampLabel.setFont(newFont);    			
//	    				timeStampLabel.pack();
						
						StatusIndicator activityStatusIndicator = new StatusIndicator(statusIndicatorPanel_, StatusIndicator.SUCCESSFUL_STATUS, timeStamp, SWT.VERTICAL);
						System.out.println("Created a new StatusIndicator for a successfully completed activity " + getActivity().getName());
    				
						//TODO: Potentially move the determination of all tooltips in a separate utility class.
	    				// Set the tooltip of the activityFinishedSymbol_ -- the tooltip will contain
	    				// the outputs of this activity
	    				String activityFinishedSymbolTooltip = "";
	    				Set<ParameterDeclaration> outputParams = activity_.getOutputParameterDeclarations();
	    				System.out.println("Number of outputParams = " + outputParams.size());
	    				if ((outputParams != null) && (! outputParams.isEmpty())) {
	    					activityFinishedSymbolTooltip = TooltipGenerator.generateParameterDeclsTooltip(activity_, TooltipGenerator.FINISHED_SUCCESSFULLY_TITLE, outputParams);
	    				}
//	    				activityFinishedSymbol_.setToolTipText(activityFinishedSymbolTooltip);
	    				activityStatusIndicator.setToolTipText(activityFinishedSymbolTooltip);
	    				// System.out.println("set tooltip of status indicator panel to: " + activityFinishedSymbolTooltip);
	    				// END OF Set the tooltip of the activityFinishedSymbol_

						activitySuccessfulButton_.dispose();
						if (activityFailedButton_ != null)
							activityFailedButton_.dispose();


						//canvas.getParent().layout();
						// Tell the activityPanel_ to re-layout its children.
						activityPanelView_.layout();
						//canvas.getParent().pack();

						// END of Create a new widget to show the completion of an activity and add a time stamp.	
					}
				});
			}
			else if (newActivityState == ActivityState.TERMINATED) {
				final Display display = this.getProcessPanel().getGUI().getView().getDisplay();

				// The SWT widgets must be created within the UI thread.
				// The Little-JIL agents are updated within non-UI threads.
				// The following ensures that the the updates are within the UI thread.
				display.syncExec(new Runnable() {
					public void run() {
						// Update the GUI if not already done
						if ((activityFailedButton_ == null) || 
							activityFailedButton_.isDisposed()) {
							return;
						}
						
//						// Create a new widget to show the failure of an activity and add a time stamp. Dispose of
//						// the old activitySuccessfulButton and place the new widget on its place.
//						final Image activityFailedImage = new Image(activityFailedButton_.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ACTIVITY_FAILED_IMAGE_PATH));
//
//						// Use canvas to hold the image, since canvas supports transparency in images,
//						// unlike Label and Button.
//						// Also, use a GC object and draw the text on top of it.
//						GC graphicsContext = new GC(activityFailedImage);
//						activityFinishedSymbol_ = new Canvas(statusIndicatorPanel_,SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
//						activityFinishedSymbol_.addPaintListener(new PaintListener() {
//							public void paintControl(PaintEvent e) {
//								e.gc.drawImage(activityFailedImage,0,0);
//							}
//						});
//						
//						// Set the size of the canvas. We set the size to be slightly larger than the image
//						// enclosed by the canvas--this image is 20 by 20 pixels
//						GridData gd = new GridData(23, 23);
//						gd.horizontalAlignment = SWT.CENTER;
//						activityFinishedSymbol_.setLayoutData(gd);
//						// Set the background of the canvas to be the same color as the color of its parent widget.
//						activityFinishedSymbol_.setBackground(activityFinishedSymbol_.getParent().getBackground());
						
						// Get the current time
						String timeStamp = new SimpleDateFormat("H:mm").format(getActivity().getFinishedTimestamp());
//						// Add the current time stamp to the statusIndicatorPanel
//						Label timeStampLabel = new Label(statusIndicatorPanel_, SWT.BORDER);
//						timeStampLabel.setText(timeStamp);
//	    				// Make the font of the label's text bold and size 16
//	    				FontData[] fontData = timeStampLabel.getFont().getFontData();
//	    				for (int i = 0; i < fontData.length; i++) {
//	    					fontData[i].setHeight(10);
//	    				}
//	    				Font newFont = new Font(display, fontData);
//	    				timeStampLabel.setFont(newFont);    			
//	    				timeStampLabel.pack();
						
						StatusIndicator activityStatusIndicator = new StatusIndicator(statusIndicatorPanel_, StatusIndicator.FAILED_STATUS, timeStamp, SWT.VERTICAL);
						System.out.println("Created a new StatusIndicator for the failed activity " + getActivity().getName());
						
						//TODO: Potentially move the determination of all tooltips in a separate utility class.
	    				// Set the tooltip of the activityFinishedSymbol_ . Since this is the case
	    				// when an activity fails, the tooltip contains the thrown exceptions (i.e.,
	    				// problems that arose. 
	    				String activityFinishedSymbolTooltip = "";
	    				Set<Serializable> thrownExceptions = activity_.getThrownExceptions();
	    				//System.out.println("Number of thrownExceptions = " + thrownExceptions.size());
	    				if ((thrownExceptions != null) && (! thrownExceptions.isEmpty())) {
	    					activityFinishedSymbolTooltip = TooltipGenerator.generateProblemsTooltip(thrownExceptions);
	    				}
	    				activityStatusIndicator.setToolTipText(activityFinishedSymbolTooltip);
	    				//System.out.println("set tooltip of status indicator panel to: " + activityFinishedSymbolTooltip);
	    				// END OF set the tooltip of the activityFinishedSymbol_

						activitySuccessfulButton_.dispose();
						if (activityFailedButton_ != null)
							activityFailedButton_.dispose();
//						System.out.println("Disposed of activityFailedButton");

						// Move the activityCompletedLabel to the top of the drawing order with respect to its siblings.
						//				activityFinishedTimestamp_.moveAbove(null);
						// Tell the activityPanel_ to re-layout its children.
						activityPanelView_.layout();
						System.out.println("Re-layed out the activityPanelView");

						// END of Create a new widget to show the failure of an activity and add a time stamp.
					}
				});
			}
			this.setBackgroundGUI(newActivityState);
		}
	}
}
