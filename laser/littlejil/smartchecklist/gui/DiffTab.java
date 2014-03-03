package laser.littlejil.smartchecklist.gui;

import laser.littlejil.smartchecklist.gui.utils.ImageManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;

public class DiffTab extends StepInfoTabItem {	
	public DiffTab(StepInfoPanel stepInfoPanel){
		super(stepInfoPanel, "Diff", ImageManager.resize(new Image(Display.getDefault(), "/home/chris/workspace/smartchecklist/laser/littlejil/smartchecklist/gui/images/diff.png"), 16,16));
	}
	
	public void draw(){
		List list_1 = new List(this.content_, SWT.BORDER);
		list_1.setBounds(83, 10, 349, 228);

		StyledText styledText_1 = new StyledText(this.content_, SWT.BORDER);
		styledText_1.setBounds(0, 0, 77, 248);
	}

	public void dispose() {
		//TODO implement
	}
}
