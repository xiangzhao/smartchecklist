package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;


public class StepInfoPanel
{
	
	private SmartChecklistGUI gui_;
	private Group infoPanel_;
	
	public StepInfoPanel(SmartChecklistGUI gui) {
		super();
		
		this.gui_ = gui;
		Shell view = this.gui_.getView();
		this.infoPanel_ = new Group(view, SWT.NONE);
		this.infoPanel_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.infoPanel_.setSize(SmartChecklistGUI.GUI_WIDTH - 50, 50);
		this.infoPanel_.setLayout(new GridLayout(2, false));
		
		CTabFolder tabFolder = new CTabFolder(this.infoPanel_, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tabFolder.setBounds(0, 0, 448, 275);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Source");
		
		CTabItem tbtmNewItem_1 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("Diff");
		
		CTabItem tbtmNewItem_2 = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem_2.setText("History");
	}
}
