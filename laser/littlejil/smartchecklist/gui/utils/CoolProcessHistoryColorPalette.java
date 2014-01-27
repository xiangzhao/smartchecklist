package laser.littlejil.smartchecklist.gui.utils;


import org.eclipse.swt.graphics.Device;


public class CoolProcessHistoryColorPalette extends DefaultProcessHistoryColorPalette 
{
	public CoolProcessHistoryColorPalette(Device device) {
		super(device);
	}
	
	public void setDefaultColors() {
		this.setPastColor(this.createColor(200, 200, 200));
//		this.setCurrentColor(this.createColor(135, 185, 135));
		this.setCurrentColor(this.createColor(165, 215, 165));
//		this.setCurrentColor( bleach(this.createColor(127,255,212), (float) 0.05) );
//		this.setCurrentColor(this.createColor(137, 232, 148));
//		this.setCurrentColor(this.createColor(120, 170, 120));
		this.setFutureColor(this.createColor(135, 206, 255));
		this.setAbortedColor(this.createColor(230, 230, 230));
	}
}
