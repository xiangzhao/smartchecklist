package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import artifacts.PackageFragmentRoot;

public class SourceTab extends StepInfoTabItem {
	
	public SourceTab(StepInfoPanel stepInfoPanel){
		super(stepInfoPanel, "Source", null);
	}
	
	public void draw(){
		final PackageFragmentRoot pfr = this.activity_.getMostRecentPFR();
		
		if(pfr != null && pfr.getCompilationUnitList().size() > 0){
			final StyledText styledText = new StyledText(this.content_, SWT.BORDER);
			styledText.setLocation(150, 0);
			styledText.setSize(298, 275);
			final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(this.content_, SWT.BORDER);
			list.setBounds(0, 0, 144, 275);
			
			for(String filename : pfr.getCompilationUnitList()){
				list.add(filename);
			}
			
			//set up the selection listener (when an item is selected, show the source in the styled box on the right.
			list.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					String selectedFilename = list.getItem(list.getSelectionIndex());
					styledText.setText(pfr.getCompilationUnitContents().get(selectedFilename));
				}

				public void widgetDefaultSelected(SelectionEvent event) {
					widgetSelected(event);
				}
			});
			
			//set the default to the first item in the list
			list.select(0);
			String selectedFilename = list.getItem(0);
			styledText.setText(pfr.getCompilationUnitContents().get(selectedFilename));
		} else {
			//there are no files to show.
			for(Control c : this.content_.getChildren()){
				c.dispose();
			}
			Label label = new Label(this.content_, SWT.None);
			label.setText("there are no files to show");
			label.pack();
		}
	}

	public void dispose() {
		// TODO Implement
	}
	
}
