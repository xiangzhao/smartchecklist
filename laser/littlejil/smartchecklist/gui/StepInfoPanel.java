package laser.littlejil.smartchecklist.gui;

import java.util.ArrayList;

import laser.littlejil.smartchecklist.gui.model.Activity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class StepInfoPanel {
	
	private SmartChecklistGUI gui_;
	private Composite infoPanel_;
	private Activity activity_ = null;
	private CTabFolder tabFolder_;
	private java.util.List<StepInfoTabItem> tabs_;
	
	public StepInfoPanel(SmartChecklistGUI gui) {
		super();

		this.gui_ = gui;
		Shell view = this.gui_.getView();
		this.infoPanel_ = new Composite(view, SWT.NONE);
		this.infoPanel_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.infoPanel_.setSize(SmartChecklistGUI.GUI_WIDTH - 50, 50);
		this.infoPanel_.setLayout(new GridLayout(2, false));

		this.tabFolder_ = new CTabFolder(this.infoPanel_, SWT.BORDER);
		this.tabFolder_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.tabFolder_.setBounds(0, 0, 448, 275);
		this.tabFolder_.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		this.tabs_ = new ArrayList<StepInfoTabItem>();
		tabs_.add(new SourceTab(this));
		tabs_.add(new DiffTab(this));
		tabs_.add(new RecurrenceTab(this));
	}

	public void updateActivity(Activity activity){
		this.activity_ = activity;
		for(StepInfoTabItem stepInfoTabItem : tabs_){
			stepInfoTabItem.updateActivity();
		}
	}

	public void dispose(){
		//TODO implement this
	}

	public Activity getActivity(){
		return this.activity_;
	}
	
	public CTabFolder getTabFolder(){
		return this.tabFolder_;
	}
}
