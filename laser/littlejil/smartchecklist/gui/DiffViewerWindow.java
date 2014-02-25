package laser.littlejil.smartchecklist.gui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import laser.ddg.DataInstanceNode;
import laser.ddg.ProcedureInstanceNode;
import laser.juliette.ams.AgendaItem;
import laser.juliette.ddgbuilder.DDGBuilder;
import laser.littlejil.smartchecklist.gui.model.Activity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import difflib.DiffUtils;
import difflib.Patch;

import artifacts.PackageFragmentRoot;

public class DiffViewerWindow {
	//map from changedFilename to diff
	private HashMap <String,String> diffs;
	private List<String> changedFilenames;
	private Activity activity_;
	private boolean changes;

	public DiffViewerWindow(Activity activity){
		System.out.println("started making the diffviewer");
		this.activity_ = activity;
		//get the agenda item associated with the activity.
		AgendaItem ai = this.activity_.getAgendaItem();
		DDGBuilder ddgBuilder = (DDGBuilder) ai.getDdgbuilder();

		//get the procedure instance node associated with the agenda item.
		ProcedureInstanceNode pin = ddgBuilder.getAgendaItemMapper().getLastPIN(ai);

		//get package fragment roots from the in and out data instance nodes.
		PackageFragmentRoot pfrIn = null;
		PackageFragmentRoot pfrOut = null;

		Iterator<DataInstanceNode> itIn = pin.inputParamValues();
		while(itIn.hasNext()){
			DataInstanceNode din = itIn.next();
			if(!(din instanceof PackageFragmentRoot)){
				continue;
			} else {
				pfrIn = (PackageFragmentRoot)din;
				break;
			}
		}

		Iterator<DataInstanceNode> itOut = pin.inputParamValues();
		while(itOut.hasNext()){
			DataInstanceNode din = itOut.next();
			if(!(din instanceof PackageFragmentRoot)){
				continue;
			} else {
				pfrOut = (PackageFragmentRoot)din;
				break;
			}
		}
		
		System.out.println("got the pfrs in the diffviewer");


		//determine changes from incoming pfr and outgoing pfr
		//for each file:
		//the file exists in input and output OR
		//the file exists in input and not in output OR
		//the file exists in output and not in input

		//uniqueIn refers to all files which came in but were deleted
		HashSet<String> uniqueIn = null;
		//uniqueOut refers to all files which were create by the procedureInstanceNode (came out but not in)
		HashSet<String> uniqueOut = null;
		//commonFiles refers to all files which came both in and out
		HashSet<String> commonFiles = null;

		if(pfrIn == null || pfrOut == null){
			changes = false;
		} else {
			uniqueIn = new HashSet<String>(pfrIn.getCompilationUnitList());
			uniqueIn.removeAll(pfrOut.getCompilationUnitList());

			uniqueOut = new HashSet<String>(pfrOut.getCompilationUnitList());
			uniqueOut.removeAll(pfrIn.getCompilationUnitList());

			commonFiles = new HashSet<String>(pfrIn.getCompilationUnitList());
			commonFiles.retainAll(pfrOut.getCompilationUnitList());


			//now loop through and show differences. not sure how to show that a file was created or deleted...
			//for now just deal with common files.
			this.changedFilenames = new ArrayList<String>(commonFiles);
			if(this.changedFilenames.size() == 0){
				this.changes = false;
			} else {
				for(String changedFilename : this.changedFilenames){
					//TODO: might want to check if they're equal, and not include them
					String incomingFileContents = pfrIn.getCompilationUnitContents().get(changedFilename);
					String outgoingFileContents = pfrOut.getCompilationUnitContents().get(changedFilename);
					this.diffs.put(changedFilename, diff(changedFilename, incomingFileContents, outgoingFileContents));
				}
				//diffs is now initialized with the diffs of the changed files
			}
		}

	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Showing diff of Activity");

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(0, 0, 448, 275);

		final StyledText styledText = new StyledText(composite, SWT.BORDER);
		styledText.setLocation(150, 0);
		styledText.setSize(298, 275);
		final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(composite, SWT.BORDER);
		list.setBounds(0, 0, 144, 275);

		if(changes){
			System.out.println("The files we're considering are: ");
			for(String changedFilename : this.changedFilenames){
				System.out.println(changedFilename);
			}
			
			//add the filenames to the list on the left
			for(String changedFilename : this.changedFilenames){
				list.add(changedFilename);
			}

			//set up the selection listener (when an item is selected, show the diff in the styled box on the right.
			list.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					//this works because our list is a single select
					int selectedIndex = list.getSelectionIndices()[0];
					styledText.setText(diffs.get(changedFilenames.get(selectedIndex)));

				}

				public void widgetDefaultSelected(SelectionEvent event) {
					//don't know what this "default" business is but it has to be implemented
					widgetSelected(event);
				}
			});

			//set up the default
			list.select(0);
			styledText.setText(diffs.get(changedFilenames.get(0)));
		} else {
			//there are no changes to show.
			composite.dispose();
			Label label = new Label(shell, SWT.None);
			label.setText("there are no differences to show");
			label.pack();
		}

		shell.pack();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}


	//extract this to a diff util class
	//params: filename, before contents, after contents
	public String diff(String filename, String file1, String file2){
		Scanner scan1 = new Scanner(file1);
		Scanner scan2 = new Scanner(file2);

		List<String> file1list = new LinkedList<String>();
		while(scan1.hasNextLine()){
			file1list.add(scan1.nextLine());
		}

		List<String> file2list = new LinkedList<String>();
		while(scan2.hasNextLine()){
			file2list.add(scan2.nextLine());
		}

		Patch patch = DiffUtils.diff(file1list, file2list);

		List<String> unified = DiffUtils.generateUnifiedDiff(filename, filename, file2list, patch, 2);

		String ret = "";
		for(String str : unified){
			ret += str + "\r\n";
		}
		return ret;
	}
}
