package laser.littlejil.smartchecklist.gui.widgets;

import laser.littlejil.smartchecklist.gui.SmartChecklistGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StatusIndicator
{
	private static final String FINISHED_SUCCESSFULLY_IMAGE_PATH = "images/checkmark.png";
	private static final String FINISHED_FAILED_IMAGE_PATH = "images/redX.png";
	
	public static final String SUCCESSFUL_STATUS = "successfull";
	public static final String FAILED_STATUS = "failed";
	
	private Image finishedSuccessfullyImage_;
	private Image finishedFailedImage_;
	
	private Composite statusIndicatorPanel_;
	private Canvas statusSymbolCanvas_;
	private Label timeStampLabel_;
	
	private int statusIndicatorPanelWidth_;
	
	public StatusIndicator(Composite parent, String status, String timeStamp, int orientation)
	{
		// Check preconditions
//		if ( !(parent.getLayout() instanceof GridLayout) )
//			throw new IllegalArgumentException("The StatusIndicator widget could be used with parents whose" +
//					"layout is GridLayout.");
		if ( !(status.equals(SUCCESSFUL_STATUS) || status.equalsIgnoreCase(FAILED_STATUS)) )
			throw new IllegalArgumentException("The status should be either StatusIndicator.SUCCESSFUL_STATUS" +
					"or StatusIndicator.FAILED_STATUS .");
		if ( !(orientation == SWT.HORIZONTAL || orientation == SWT.VERTICAL) )
			throw new IllegalArgumentException("The orientation should be either SWT.HORIZONTAL or SWT.VERTICAL");
		// DONE with checking prerequisites
		
		finishedSuccessfullyImage_ = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(FINISHED_SUCCESSFULLY_IMAGE_PATH));
		finishedFailedImage_ = new Image(parent.getDisplay(), SmartChecklistGUI.class.getResourceAsStream(FINISHED_FAILED_IMAGE_PATH));
		
		statusIndicatorPanelWidth_ = Math.max(finishedSuccessfullyImage_.getBounds().width, finishedFailedImage_.getBounds().width) + 20;
		
		statusIndicatorPanel_ = new Composite(parent, SWT.NONE);
//		GridData statusIndicatorPanelGridData = new GridData();
//		if (orientation == SWT.VERTICAL)
//			statusIndicatorPanelGridData.widthHint = statusIndicatorPanelWidth_;
//		
//		statusIndicatorPanel_.setLayoutData(statusIndicatorPanelGridData);
		
		GridLayout statusIndicatorPanelLayout = null;;
		if (orientation == SWT.VERTICAL)
			statusIndicatorPanelLayout = new GridLayout(1, false);
		else if (orientation == SWT.HORIZONTAL)
			statusIndicatorPanelLayout = new GridLayout(2, false);
		statusIndicatorPanelLayout.marginHeight = 0;
		statusIndicatorPanelLayout.marginWidth = 0;	//Set to 0 if we don't extra padding around the sides of the status indicator.
		statusIndicatorPanelLayout.verticalSpacing = 0;
		statusIndicatorPanel_.setLayout(statusIndicatorPanelLayout);
		
		
		statusSymbolCanvas_ = new Canvas(statusIndicatorPanel_,SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
	
		
		if (status.equalsIgnoreCase(SUCCESSFUL_STATUS))
		{
			statusSymbolCanvas_.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(finishedSuccessfullyImage_,0,0);
				}
			});
		}
		else if (status.equalsIgnoreCase(FAILED_STATUS))
		{
			statusSymbolCanvas_.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.drawImage(finishedFailedImage_,0,0);
				}
			});
		}
		
		// Set the size of the canvas. We set the size to be slightly larger than the image
		// enclosed by the canvas--the image is 20 by 20 pixels
		GridData statusSymbolCanvasGridData = new GridData(23, 23);
		statusSymbolCanvasGridData.horizontalAlignment = SWT.CENTER;
		statusSymbolCanvas_.setLayoutData(statusSymbolCanvasGridData);
		// Set the background of the canvas to be the same color as the color of its parent widget.
//		activityFinishedSymbol_.setBackground(activityFinishedSymbol_.getParent().getBackground());
		
		
		timeStampLabel_ = new Label(statusIndicatorPanel_, SWT.BORDER);
		GridData timeStampLabelGridData = new GridData();
		timeStampLabelGridData.horizontalAlignment = SWT.CENTER;
		timeStampLabel_.setLayoutData(timeStampLabelGridData);
		timeStampLabel_.setText(timeStamp);
		// Make the font of the label's text size 10
		FontData[] fontData = timeStampLabel_.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(10);
		}
		Font newFont = new Font(parent.getDisplay(), fontData);
		timeStampLabel_.setFont(newFont);    			
		timeStampLabel_.pack();
	}
	
	public void setToolTipText(String text)
	{
		statusSymbolCanvas_.setToolTipText(text);
	}
	
	public void setLayoutData(Object layoutData)
	{
		statusIndicatorPanel_.setLayoutData(layoutData);
	}
	
	public int getWidth()
	{
		return statusIndicatorPanelWidth_;
	}
	
}
