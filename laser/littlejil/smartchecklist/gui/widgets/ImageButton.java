package laser.littlejil.smartchecklist.gui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class ImageButton extends Canvas 
{
	private Image enabledImage_;
	private Image disabledImage_;
	private Image imageView_;
	private GC graphicsContext_;
	
	
	public ImageButton(Composite parent, Image image) {
		super(parent, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
		
		this.enabledImage_ = image;
		//TODO: The disabled image should use transparency.
		//      But it is not working correctly. For now,
		//      the disabled image is simply grayed out.
		/*
		// VERSION 1) This is not working correctly.
		ImageData newImageData = this.enabledImage_.getImageData();
		//newImageData.alpha = 128;
		for (int row = newImageData.x; row < newImageData.x + newImageData.width; row++) {
			for (int col = newImageData.y; col < newImageData.y + newImageData.height; col++) {
				if (newImageData.getAlpha(row,  col) != 0) {
					newImageData.setAlpha(row, col, 164);
				}
			} // end for col
		} // end for row
        this.disabledImage_ = new Image(this.image_.getDevice(), newImageData);
		*/
		// VERSION 2)
		this.disabledImage_ = new Image(this.enabledImage_.getDevice(), this.enabledImage_, SWT.IMAGE_GRAY);
		this.setCurrentImage(this.isEnabled());
		
		this.setBackground(parent.getBackground());
		this.setBounds(this.imageView_.getBounds());
		this.graphicsContext_ = new GC(this.imageView_);
		this.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
//		         e.gc.drawImage(imageView_, 0, getSize().y / 4);
	        	e.gc.drawImage(imageView_, 0, 0);
		    }
		});
//		this.setSize(enabledImage_.getBounds().width+1, enabledImage_.getBounds().height+1);
	}
	
	public Image getImage() {
		return this.enabledImage_;
	}
	
	public void setCurrentImage(Image image)
	{
		enabledImage_ = image;
		this.disabledImage_.dispose();	// Dispose of the old disabled image and then create a new one.
			// (Since this class created the old disabled image, it is responsible for disposing of it).
		this.disabledImage_ = new Image(this.enabledImage_.getDevice(), this.enabledImage_, SWT.IMAGE_GRAY);
		if (isEnabled())
			imageView_ = enabledImage_;
		else
			imageView_ = disabledImage_;
	}
	
	protected void setCurrentImage(boolean enabled) {
		if (enabled) {
			this.imageView_ = this.enabledImage_;
		}
		else {
			this.imageView_ = this.disabledImage_;
		}
	}
	
	public void dispose() {
		if (! this.isDisposed()) {
			this.enabledImage_.dispose();
			this.disabledImage_.dispose();
			this.graphicsContext_.dispose();
			super.dispose();
		}
	}

	public void setEnabled(boolean enabled) {
		this.setCurrentImage(enabled);
		super.setEnabled(enabled);
	}
	
	
	// Overriding computeSize(...), so that the canvas enclosing the image is just big enough to
	// fit the image. Otherwise, the canvas ends up being much larger than the image and this
	// can result in weird "layout-ing" of the ImageButton. 
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point size = super.computeSize (wHint, hHint, changed);
		size.x = enabledImage_.getBounds().width+1;
		size.y = enabledImage_.getBounds().height+1;
		return size;
	}
}
