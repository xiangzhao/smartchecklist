package laser.littlejil.smartchecklist.gui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


//TODO: The ImageManager class should load and store the 
//      images used within the Smart Checklist GUI.
public class ImageManager 
{
	public static final String ACTIVITY_REQUIRES_IMAGE_PATH = "images/medical-suitecase.png";
//	private static final String ACTIVITY_FAILED_IMAGE_PATH = "images/timestamp-done.png";
	public static final String ACTIVITY_COMPLETE_BUTTON_IMAGE_PATH = "images/done.png";
	public static final String ACTIVITY_FAILED_BUTTON_IMAGE_PATH = "images/failed.png";
	public static final String NOTE_WITH_TEXT_BUTTON_IMAGE_PATH  = "images/noteWithText.png";
	public static final String NOTE_WITHOUT_TEXT_BUTTON_IMAGE_PATH  = "images/noteBlank.png";
	public static final String ACTIVITY_SUCCESSFULL_IMAGE_PATH = "images/checkmark.png";
	public static final String ACTIVITY_FAILED_IMAGE_PATH = "images/redX.png";

	//would like to remove this method and just get a smaller image
	//this method grabbed from http://aniszczyk.org/2007/08/09/resizing-images-using-swt/
	public static Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, 
		image.getBounds().width, image.getBounds().height, 
		0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}
}

