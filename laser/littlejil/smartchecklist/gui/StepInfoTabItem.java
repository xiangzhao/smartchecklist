package laser.littlejil.smartchecklist.gui;

import laser.littlejil.smartchecklist.gui.model.Activity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class StepInfoTabItem {
	protected StepInfoPanel stepInfoPanel_;
	protected Activity activity_;
	protected CTabFolder tabFolder_;
	protected CTabItem tabItem_;
	protected Composite content_;
	
	//TODO dispose of this image
	protected Image tabImage_;
	
	protected StepInfoTabItem(StepInfoPanel stepInfoPanel, String tabLabel, Image tabImage){
		this.stepInfoPanel_ = stepInfoPanel;
		this.tabFolder_ = this.stepInfoPanel_.getTabFolder();
		this.activity_ = this.stepInfoPanel_.getActivity();
		this.tabItem_ = new CTabItem(this.tabFolder_, SWT.NONE);
		this.tabItem_.setText(tabLabel);
		this.content_ = new Composite(tabFolder_, SWT.NONE);
		this.tabItem_.setControl(this.content_);
		
		if(tabImage != null){
			this.tabItem_.setImage(tabImage);
		}
	}
	
	protected void updateActivity(){
		this.activity_ = this.stepInfoPanel_.getActivity();
		for(Control c : this.content_.getChildren()){
			c.dispose();
		}
		this.draw();
	}
	
	public abstract void draw();
	public abstract void dispose();
}
