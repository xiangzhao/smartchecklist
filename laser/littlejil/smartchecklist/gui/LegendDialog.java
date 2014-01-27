package laser.littlejil.smartchecklist.gui;

import java.util.ArrayList;
import java.util.List;

import laser.littlejil.smartchecklist.gui.model.ActivityState;
import laser.littlejil.smartchecklist.gui.utils.CoolProcessHistoryColorPalette;
import laser.littlejil.smartchecklist.gui.utils.ImageManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This class represents the legend of the Smart Checklist GUI.
 * 
 * TODO: Add time stamp to the legend when we decide what visual element the time stamp will
 * get associated with.
 */
public class LegendDialog extends Dialog {
	
	private static final String LEGEND_TITLE = "Smart Checklist Legend";
	public static final int COLOR_RECTANGLE_WIDTH = 40;
	public static final int COLOR_RECTANGLE_HEIGHT = 20;
	
	Image activityRequiresButtonImage;
	Image activityCompleteButtonImage;
	Image activityFailedButtonImage;
	Image notesButtonImage;
	Image activitySuccessfulStatusIndicatorImage;
	Image activityFailedStatusIndicatorImage;
	
	/**
	 * We will use a fixed width for the left column of the legend,
	 * so that the text in the right column gets left aligned.
	 */
	private int leftColumnWidth;
	/**
	 * We will use a fixed height for each row in the legend.
	 */
	private int rowHeight;
		
	public LegendDialog (Shell parent, int style) {
		super (parent, style);
		activityRequiresButtonImage = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_REQUIRES_IMAGE_PATH));
		activityCompleteButtonImage = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_COMPLETE_BUTTON_IMAGE_PATH));
		activityFailedButtonImage = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_FAILED_BUTTON_IMAGE_PATH));
		notesButtonImage = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.NOTE_WITH_TEXT_BUTTON_IMAGE_PATH));
		activitySuccessfulStatusIndicatorImage = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_SUCCESSFULL_IMAGE_PATH));
		activityFailedStatusIndicatorImage = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(ImageManager.ACTIVITY_FAILED_IMAGE_PATH));
		
		computeLeftColumnWidthAndRowHeight();
	}
	
	private void computeLeftColumnWidthAndRowHeight()
	{
		int maxWidth = -1;
		int maxHeight = -1;
		List<Image> imagesForLeftColumn = new ArrayList<Image>();
		imagesForLeftColumn.add(activityRequiresButtonImage);
		imagesForLeftColumn.add(activityCompleteButtonImage);
		imagesForLeftColumn.add(activityFailedButtonImage);
		imagesForLeftColumn.add(notesButtonImage);
		imagesForLeftColumn.add(activitySuccessfulStatusIndicatorImage);
		imagesForLeftColumn.add(activityFailedStatusIndicatorImage);
		
		for (Image image : imagesForLeftColumn)
		{
			if (image.getBounds().width > maxWidth)
				maxWidth = image.getBounds().width;
			if (image.getBounds().height > maxHeight)
				maxHeight = image.getBounds().height;
		}
		
		if (COLOR_RECTANGLE_WIDTH > maxWidth)
			maxWidth = COLOR_RECTANGLE_WIDTH;
		if (COLOR_RECTANGLE_HEIGHT > maxHeight)
			maxHeight = COLOR_RECTANGLE_HEIGHT;
		
		leftColumnWidth = maxWidth + 10;
		rowHeight = maxHeight + 2;
	}


	
	public LegendDialog (Shell parent) {
		this (parent, 0); // your default style bits go here (not the Shell's style bits)
	}
	public void open () {
		setText(LegendDialog.LEGEND_TITLE);
		Shell parent = getParent();
//		Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		Shell shell = new Shell(parent.getDisplay(), SWT.SHELL_TRIM);
		shell.setSize(SmartChecklistGUI.GUI_WIDTH, SmartChecklistGUI.GUI_HEIGHT);
		shell.setText(getText());
		// Your code goes here (widget creation, set result, etc).
		shell.setLayout(new GridLayout(1, false));
		populateLegend(shell);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}
	
	private void populateLegend(Shell shell)
	{
		ScrolledComposite scrollablePanel = new ScrolledComposite(shell, SWT.BORDER | SWT.V_SCROLL);
		// The commented out GridData for the scrollablePanel and the legendPanel were intended to
		// make the legend panel grab not only horizontal, but also vertical space when the legend
		// window gets resized. The effect was not accomplished, however. Not sure how to grab the
		// vertical space...
//		GridData scrollablePanelGridData = new GridData(); 
//		scrollablePanelGridData.horizontalAlignment = GridData.FILL;
//		scrollablePanelGridData.verticalAlignment = GridData.FILL;
//		scrollablePanelGridData.grabExcessHorizontalSpace = true;
//		scrollablePanelGridData.grabExcessVerticalSpace = true;
//		scrollablePanel.setLayoutData(scrollablePanelGridData);
		scrollablePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrollablePanel.setAlwaysShowScrollBars(true);
		scrollablePanel.setExpandHorizontal(true);
		
		scrollablePanel.setLayout(new GridLayout(1, false));
		
		Group legendPanel = new Group(scrollablePanel, SWT.NONE);
//		legendPanel.setText("legendpanel");
		GridData legendPanelGridData = new GridData();
//		legendPanelGridData.verticalAlignment = GridData.FILL;
//		legendPanelGridData.horizontalAlignment = GridData.FILL;
//		legendPanelGridData.grabExcessHorizontalSpace = true;
//		legendPanelGridData.grabExcessVerticalSpace = true;
		legendPanel.setLayoutData(legendPanelGridData);
		legendPanel.setLayout(new GridLayout(1, false));
//		legendPanel.setSize(legendPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrollablePanel.setMinSize(legendPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		scrollablePanel.setContent(legendPanel);
		
		Group buttonSection = new Group(legendPanel, SWT.SHADOW_IN);
		GridData buttonSectionGridData = new GridData();
		buttonSectionGridData.horizontalAlignment = GridData.FILL;
		buttonSectionGridData.grabExcessHorizontalSpace = true;
		buttonSection.setLayoutData(buttonSectionGridData);		
		buttonSection.setText("Buttons:");
		FontData[] fontData = buttonSection.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(16);
			fontData[i].setStyle(SWT.BOLD);
		}
		Font groupTitleFont = new Font(shell.getDisplay(), fontData);
		buttonSection.setFont(groupTitleFont);
		buttonSection.setLayout(new GridLayout(2, false));
		
		
		Group statusIndicatorSection = new Group(legendPanel, SWT.NONE);
		GridData statusIndicatorSectionGridData = new GridData();
		statusIndicatorSectionGridData.horizontalAlignment = GridData.FILL;
		statusIndicatorSectionGridData.grabExcessHorizontalSpace = true;
		statusIndicatorSection.setLayoutData(statusIndicatorSectionGridData);
		statusIndicatorSection.setText("Status indicators:");
		statusIndicatorSection.setFont(groupTitleFont);
		statusIndicatorSection.setLayout(new GridLayout(2, false));

		Group colorsSection = new Group(legendPanel, SWT.NONE);
		GridData colorsSectionGridData = new GridData();
		colorsSectionGridData.horizontalAlignment = GridData.FILL;
		colorsSectionGridData.grabExcessHorizontalSpace = true;
		colorsSection.setLayoutData(colorsSectionGridData);
		colorsSection.setText("Colors:");
		colorsSection.setFont(groupTitleFont);
		colorsSection.setLayout(new GridLayout(2, false));
		
		// Create the button subsections
		//
		// Create the requires button subsection
		this.populateButtonSubsection(buttonSection, activityRequiresButtonImage, "Hover over to view what inputs an activity requires.");
		
		// Create the button for successfully completing an activity and its explanation
		this.populateButtonSubsection(buttonSection, activityCompleteButtonImage, "Press when an activity has been successfully completed.");
		
		// Create the button for a problem occurring during activity and its explanation
		this.populateButtonSubsection(buttonSection, activityFailedButtonImage, "Press when a problem arises while performing an activity.");
	
		// Create the notes button and its explanation
		this.populateButtonSubsection(buttonSection, notesButtonImage, "Press to read/write notes. A button with no text indicates that no notes have been entered so far.");
		
		// Create the status indicator subsections
		//
		// Create the status indicator for successfully completing an activity		
		this.populateStatusIndicatorSubsection(statusIndicatorSection, activitySuccessfulStatusIndicatorImage, "Activity performed successfully.");
		
		// Create the status indicator for failed activity
		this.populateStatusIndicatorSubsection(statusIndicatorSection, activityFailedStatusIndicatorImage, "A problem arose while performing activity.");
		
		// Create the color subsections
		//
		//TODO: HACK: Hard coded the color palette for now.
//		final ProcessHistoryColorPalette colorPalette = new DefaultProcessHistoryColorPalette(colorsSection.getDisplay());
		final ProcessHistoryColorPalette colorPalette = new CoolProcessHistoryColorPalette(colorsSection.getDisplay());
		
		// Create the color for an active activity and its explanation
		this.populateColorSubsection(colorsSection, colorPalette.getColor(ActivityState.POSTED), "Active activity.");
		
		// Create the color for successfully completed/failed activity and its explanation
		this.populateColorSubsection(colorsSection, colorPalette.getColor(ActivityState.COMPLETED), "Finished activity.");
		
		// Create the color for "cancelled" activity and its explanation
		this.populateColorSubsection(colorsSection, colorPalette.getColor(ActivityState.RETRACTED), "Withdrawn activity.");
				
		buttonSection.pack();
		statusIndicatorSection.pack();
		colorsSection.pack();
		legendPanel.pack();
		scrollablePanel.pack();

	}
	
	protected void populateButtonSubsection(Group buttonSection, final Image buttonImage, String buttonExplText) {
		Canvas activityButton = new Canvas(buttonSection,SWT.NO_REDRAW_RESIZE);
		// Set the size of the canvas to be just big enough to fit the image. Otherwise, the default
		// size is too big and results in ugly alignment between the button image and its explanation.
		final GridData actCompBtnGridData = new GridData();
		actCompBtnGridData.widthHint = leftColumnWidth;
		actCompBtnGridData.heightHint = rowHeight;
		activityButton.setLayoutData(actCompBtnGridData);
		// Draw the image in the canvas
		activityButton.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// draw the image centering it on the canvas
				e.gc.drawImage(buttonImage,
						(actCompBtnGridData.widthHint - activityCompleteButtonImage.getBounds().width)/2,
						(actCompBtnGridData.heightHint - activityCompleteButtonImage.getBounds().height)/2);
			}
		});
		
		
		// Create the explanation
		Label buttonExpl = new Label(buttonSection, SWT.BORDER);
		GridData actCompBtnExplGridData = new GridData();
		actCompBtnExplGridData.verticalAlignment = SWT.CENTER;
		buttonExpl.setLayoutData(actCompBtnExplGridData);
		buttonExpl.setText(buttonExplText);
		// Make the font of the label's text bold and size 16
		FontData[] fontDataForExpl = buttonExpl.getFont().getFontData();
		for (int i = 0; i < fontDataForExpl.length; i++) {
			fontDataForExpl[i].setHeight(16);
			//fontData[i].setStyle(SWT.BOLD);
		}
		Font fontForExpl = new Font(buttonSection.getDisplay(), fontDataForExpl);
		buttonExpl.setFont(fontForExpl);    			
		buttonExpl.pack();
	}
	
	protected void populateColorSubsection(Group colorsSection, final Color activityColor, String colorExplText) {
		Canvas activityColorSubsection = new Canvas(colorsSection,SWT.NO_REDRAW_RESIZE);
		final GridData activeActivityColorGridData = new GridData();
		activeActivityColorGridData.widthHint = leftColumnWidth;
		activeActivityColorGridData.heightHint = rowHeight;
		activityColorSubsection.setLayoutData(activeActivityColorGridData);
		activityColorSubsection.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setBackground(activityColor);
				e.gc.fillRectangle((activeActivityColorGridData.widthHint - COLOR_RECTANGLE_WIDTH)/2,
								   (activeActivityColorGridData.heightHint - COLOR_RECTANGLE_HEIGHT)/2,
								   COLOR_RECTANGLE_WIDTH, COLOR_RECTANGLE_HEIGHT);
			}
		});		
		Label colorExpl = new Label(colorsSection, SWT.BORDER);
		colorExpl.setText(colorExplText);
		
		// Make the font of the label's text bold and size 16
		FontData[] fontDataForExpl = colorExpl.getFont().getFontData();
		for (int i = 0; i < fontDataForExpl.length; i++) {
			fontDataForExpl[i].setHeight(16);
			//fontData[i].setStyle(SWT.BOLD);
		}
		Font fontForExpl = new Font(colorsSection.getDisplay(), fontDataForExpl);
		colorExpl.setFont(fontForExpl);
		colorExpl.pack();
	}
	
	protected void populateStatusIndicatorSubsection(Group statusIndicatorSection, final Image statusIndicatorImage, String statusIndicatorExplText) {
		Canvas statusIndicatorSubsection = new Canvas(statusIndicatorSection,SWT.NO_REDRAW_RESIZE);
		final GridData stIndGridData = new GridData();
		stIndGridData.widthHint = leftColumnWidth;
		stIndGridData.heightHint = rowHeight;
		statusIndicatorSubsection.setLayoutData(stIndGridData);
		statusIndicatorSubsection.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// draw the image centering it on the canvas
				e.gc.drawImage(statusIndicatorImage,
							  (stIndGridData.widthHint - statusIndicatorImage.getBounds().width)/2,
							  (stIndGridData.heightHint - statusIndicatorImage.getBounds().height)/2 );
			}
		});
		
		// Create the explanation for the status indicator
		Label statusIndExpl = new Label(statusIndicatorSection, SWT.BORDER);
		statusIndExpl.setText(statusIndicatorExplText);
		// Make the font of the label's text bold and size 16
		FontData[] fontDataForExpl = statusIndExpl.getFont().getFontData();
		for (int i = 0; i < fontDataForExpl.length; i++) {
			fontDataForExpl[i].setHeight(16);
			//fontData[i].setStyle(SWT.BOLD);
		}
		Font fontForExpl = new Font(statusIndicatorSection.getDisplay(), fontDataForExpl);
		statusIndExpl.setFont(fontForExpl);    			
		statusIndExpl.pack();
	}
 }
