package laser.littlejil.smartchecklist.gui.utils;

import laser.littlejil.smartchecklist.gui.ProcessHistoryColorPalette;
import laser.littlejil.smartchecklist.gui.model.ActivityState;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;


public class DefaultProcessHistoryColorPalette implements ProcessHistoryColorPalette 
{
	public static final int DEFAULT_PAST_COLOR = SWT.COLOR_DARK_GRAY;
	public static final int DEFAULT_CURRENT_COLOR = SWT.COLOR_DARK_GREEN;
	public static final int DEFAULT_FUTURE_COLOR = SWT.COLOR_CYAN;
	public static final int DEFAULT_ABORTED_COLOR = SWT.COLOR_GRAY;
	
	private Device device_;
	private Color pastColor_;
	private Color currentColor_;
	private Color futureColor_;
	private Color abortedColor_;
	
	
	public DefaultProcessHistoryColorPalette(Device device) {
		super();

		this.device_ = device;
		
		// Initialize to the default color palette
		this.setDefaultColors();
	}
	
	protected Color createColor(int rgb) {
		Color color = null;
		
		//TODO: In the future, the following may be needed.
		//
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	//
		//display.syncExec(new Runnable() {
    	//	public void run() {
		color = this.device_.getSystemColor(rgb);
		
		return color;
	}
	
	protected Color createColor(int red, int green, int blue) {
		Color color = null;
		
		//TODO: In the future, the following may be needed.
		//
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	//
		//display.syncExec(new Runnable() {
    	//	public void run() {
		color = new Color(this.device_, red, green, blue);
		
		return color;
	}
	
	@Override
	public Color getPastColor() {
		return this.pastColor_;
	}
	
	@Override
	public void setPastColor(Color pastColor) {
		if (pastColor == null) {
			throw new IllegalArgumentException("The past color must be non-null.");
		}
		
		this.pastColor_ = pastColor;
	}

	@Override
	public Color getCurrentColor() {
		return this.currentColor_;
	}
	
	@Override
	public void setCurrentColor(Color currentColor) {
		if (currentColor == null) {
			throw new IllegalArgumentException("The current color must be non-null.");
		}
		
		this.currentColor_ = currentColor;
	}

	@Override
	public Color getFutureColor() {
		return this.futureColor_;
	}
	
	@Override
	public void setFutureColor(Color futureColor) {
		if (futureColor == null) {
			throw new IllegalArgumentException("The future color must be non-null.");
		}
		
		this.futureColor_ = futureColor;
	}

	@Override
	public Color getAbortedColor() {
		return this.abortedColor_;
	}
	
	@Override
	public void setAbortedColor(Color abortedColor) {
		if (abortedColor == null) {
			throw new IllegalArgumentException("The aborted color must be non-null.");
		}
		
		this.abortedColor_ = abortedColor;
	}
	
	@Override
	public Color getColor(ActivityState state) {
		Color color = null;
		
		switch (state) {
		    case INITIAL:
		    {
		    	color = this.getFutureColor();
		    	break;
		    }
		    case RETRACTED:
		    {
		    	color = this.getAbortedColor();
		    	break;
		    }
		    case OPTEDOUT:
		    {
		    	color = this.getPastColor();
		    	break;
		    }
		    case COMPLETED:
		    {
		    	color = this.getPastColor();
		    	break;
		    }
		    case TERMINATED:
		    {
		    	color = this.getPastColor();
		    	break;
		    }
		    default:
		    {
		    	color = this.getCurrentColor();
		    	break;
		    }
		} // end switch
		
		return color;
	}
	
	@Override
	public void setDefaultColors() {
		this.setPastColor(this.createColor(DEFAULT_PAST_COLOR));
		this.setCurrentColor(this.createColor(DEFAULT_CURRENT_COLOR));
		this.setFutureColor(this.createColor(DEFAULT_FUTURE_COLOR));
		this.setAbortedColor(this.createColor(DEFAULT_ABORTED_COLOR));
	}
}
