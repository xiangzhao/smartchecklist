package laser.littlejil.smartchecklist.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import laser.ddg.DataInstanceNode;
import laser.ddg.ProcedureInstanceNode;
import laser.juliette.ams.AgendaItem;
import laser.juliette.ddgbuilder.DDGBuilder;
import laser.littlejil.smartchecklist.gui.utils.DiffUtil;
import laser.littlejil.smartchecklist.gui.utils.ImageManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import artifacts.PackageFragmentRoot;

public class DiffTab extends StepInfoTabItem {
	private HashMap <String,String> diffs;
	private java.util.List<String> changedFilenames;
	private java.util.List<String> addedFilenames;
	private java.util.List<String> removedFilenames;
	
	public DiffTab(StepInfoPanel stepInfoPanel){
		super(stepInfoPanel, "Diff", ImageManager.resize(new Image(Display.getDefault(), "/home/chris/workspace/smartchecklist/laser/littlejil/smartchecklist/gui/images/diff.png"), 16,16));
	}
	
	public void draw(){
		AgendaItem ai = this.activity_.getAgendaItem();
		ProcedureInstanceNode pin = DDGBuilder.getAgendaItemMapper().getLastPIN(ai);

		PackageFragmentRoot pfrIn = null;
		PackageFragmentRoot pfrOut = null;

		Iterator<DataInstanceNode> itIn = pin.inputParamValues();
		while(itIn.hasNext()){
			DataInstanceNode din = itIn.next();
			Serializable dinValue = din.getValue();
			if(dinValue instanceof PackageFragmentRoot){
				pfrIn = (PackageFragmentRoot)dinValue;
				//assumption : at most one package fragment root per data instance node
				break;
			} else {
				continue;
			}
		}

		Iterator<DataInstanceNode> itOut = pin.outputParamValues();
		while(itOut.hasNext()){
			DataInstanceNode din = itOut.next();
			Serializable dinValue = din.getValue();
			if(dinValue instanceof PackageFragmentRoot){
				pfrOut = (PackageFragmentRoot)dinValue;
				//assumption : at most one package fragment root per data instance node
				break;
			} else {
				continue;
			}
		}
		
		//debugging prints
		if(pfrIn == null){
			System.err.println("pfrIn is null");
		}
		if(pfrOut == null){
			System.err.println("pfrOut is null");
		}
		
		//determine changes from incoming pfr and outgoing pfr
		//for each file:
		//the file exists in input and output OR
		//the file exists in input and not in output OR
		//the file exists in output and not in input
		
		java.util.List<String> filesIn;
		if(pfrIn == null){
			filesIn = new ArrayList<String>();
		} else {
			filesIn = pfrIn.getCompilationUnitList();
		}
		
		java.util.List<String> filesOut;
		if(pfrOut == null){
			filesOut = new ArrayList<String>();
		} else {
			filesOut = pfrOut.getCompilationUnitList();
		}

		//removedFilenames refers to all files which came in but were deleted
		HashSet<String> uniqueIn = new HashSet<String>(filesIn);
		uniqueIn.removeAll(filesOut);
		this.removedFilenames = new ArrayList<String>(uniqueIn);

		//addedFilenames refers to all files which were create by the procedureInstanceNode (came out but not in)
		HashSet<String> uniqueOut = new HashSet<String>(filesOut);
		uniqueOut.removeAll(filesIn);
		this.addedFilenames = new ArrayList<String>(uniqueOut);
		
		//changedFilenames refers to all files which came both in and out
		HashSet<String>commonFiles = new HashSet<String>(filesIn);
		commonFiles.retainAll(filesOut);
		this.changedFilenames = new ArrayList<String>(commonFiles);

		//now populate this diffs hashmap by looping through changed files, added files, and removed files
		//also remove the common files that didn't change, they are not interesting in the diff
		this.diffs = new HashMap<String,String>();
		Iterator<String> changedFilenamesIt = this.changedFilenames.iterator();
		while(changedFilenamesIt.hasNext()){
			String changedFilename = changedFilenamesIt.next();
			String incomingFileContents = pfrIn.getCompilationUnitContents().get(changedFilename);
			String outgoingFileContents = pfrOut.getCompilationUnitContents().get(changedFilename);
			//do not include them if there were no changes
			if(incomingFileContents.equals(outgoingFileContents)){
				changedFilenamesIt.remove();
			} else {
				this.diffs.put(changedFilename, DiffUtil.diff(changedFilename, incomingFileContents, outgoingFileContents));
			}
		}
		for(String addedFilename : this.addedFilenames){
			String fileContents = pfrOut.getCompilationUnitContents().get(addedFilename);
			this.diffs.put(addedFilename,  DiffUtil.diff(addedFilename, "", fileContents));
		}
		for(String removedFilename : this.removedFilenames){
			String fileContents = pfrIn.getCompilationUnitContents().get(removedFilename);
			this.diffs.put(removedFilename,  DiffUtil.diff(removedFilename, fileContents, ""));
		}
		//diffs is now initialized with the diffs of the changed files, added files, and removed files
		
		
		final StyledText styledText = new StyledText(this.content_, SWT.BORDER);
		styledText.setLocation(150, 0);
		styledText.setSize(298, 275);
		final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(this.content_, SWT.BORDER);
		list.setBounds(0, 0, 144, 275);

		//add the changed filenames to the list on the left
		for(String changedFilename : this.changedFilenames){
			list.add("<> " + changedFilename);
		}
		
		for(String addedFilename : this.addedFilenames){
			list.add("+ " + addedFilename);
		}
		
		for(String removedFilename: this.removedFilenames){
			list.add("- " + removedFilename);
		}
		
		
		if(this.changedFilenames.size() + this.addedFilenames.size() + this.removedFilenames.size() > 0){
			//set up the selection listener (when an item is selected, show the diff in the styled box on the right.
			list.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					//split on space because the items look like "<> filename", "- filename" and "+ filename"
					String selectedFilename = list.getItem(list.getSelectionIndex()).split(" ")[1];
					styledText.setText(diffs.get(selectedFilename));
				}

				public void widgetDefaultSelected(SelectionEvent event) {
					widgetSelected(event);
				}
			});
			
			//set the default to the first item in the list
			list.select(0);
			String selectedFilename = list.getItem(0).split(" ")[1];
			styledText.setText(diffs.get(selectedFilename));
		} else {
			//there are no changes to show.
			for(Control c : this.content_.getChildren()){
				c.dispose();
			}
			Label label = new Label(this.content_, SWT.None);
			label.setText("there are no changed files to show");
			label.pack();
		}
	}

	public void dispose() {
		//TODO implement
	}
}
