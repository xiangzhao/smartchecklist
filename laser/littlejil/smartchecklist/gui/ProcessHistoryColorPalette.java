package laser.littlejil.smartchecklist.gui;

import laser.littlejil.smartchecklist.gui.model.ActivityState;

import org.eclipse.swt.graphics.Color;


public interface ProcessHistoryColorPalette 
{
	public Color getPastColor();
	
	public void setPastColor(Color pastColor);
	
	public Color getCurrentColor();
	
	public void setCurrentColor(Color currentColor);
	
	public Color getFutureColor();
	
	public void setFutureColor(Color futureColor);
	
	public Color getAbortedColor();
	
	public void setAbortedColor(Color abortedColor);
	
	public Color getColor(ActivityState state);
	
	public void setDefaultColors();
}
