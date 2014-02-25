package laser.littlejil.smartchecklist.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import artifacts.PackageFragmentRoot;

import laser.ddg.AbstractProcedureInstanceNode;
import laser.ddg.DataInstanceNode;
import laser.ddg.ProcedureInstanceNode;
import laser.ddg.ProvenanceData;
import laser.juliette.ams.AMSException;
import laser.juliette.ddgbuilder.DDGBuilder;
import laser.juliette.ddgbuilder.StepInstanceNode;
import laser.juliette.ddgbuilder.StepReference;
import laser.juliette.runner.ams.AgendaItem;
import laser.littlejil.smartchecklist.gui.model.Activity;

public class AgendaItemWindow {

	Activity activity_;
	protected Shell shell;
	private AgendaItem agendaItem_;

	public AgendaItemWindow(Activity activity){
		this.activity_ = activity;
		this.agendaItem_ = this.activity_.getAgendaItem();
	}
	/**
	 * Launch the application.
	 * @param args
	 * @throws AMSException 
	 */
	public void open() throws AMSException {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		shell.setText("History of " + this.agendaItem_.getStep().getName());
		ExpandBar bar = new ExpandBar (shell, SWT.V_SCROLL);
		Image image = display.getSystemImage(SWT.ICON_INFORMATION);

		ProcedureInstanceNode pin = activity_.getProcedureInstanceNodes().get(0);
		DDGBuilder ddgBuilder = (DDGBuilder) agendaItem_.getDdgbuilder();


		// First item
		Composite composite = new Composite (bar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.numColumns = 2;
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);

		createTwoColumnTitle(composite, shell.getDisplay(), "Input Parameters");

		Iterator<DataInstanceNode> it = pin.inputParamValues();
		while(it.hasNext()){
			DataInstanceNode din = it.next();
			createParameterRow(composite, shell.getDisplay(), din.getName(), din.getValue().toString());
		}

		createTwoColumnSeparator(composite);

		createTwoColumnTitle(composite, shell.getDisplay(), "Output Parameters");

		it = pin.outputParamValues();
		while(it.hasNext()){
			DataInstanceNode din = it.next();
			Serializable s = din.getValue();
			if(s instanceof PackageFragmentRoot){
				PackageFragmentRoot pfr = (PackageFragmentRoot)s;

				for(String str : pfr.getCompilationUnitList()){
					String classContents = pfr.getCompilationUnitContents().get(str); //class content
					System.out.println(classContents);
				}
			}
			createParameterRow(composite, shell.getDisplay(), din.getName(), din.getValue().toString());
		}

		ExpandItem item0 = new ExpandItem (bar, SWT.NONE, 0);

		AgendaItem ai = (AgendaItem) ddgBuilder.getAgendaItemMapper().getItem((StepInstanceNode)pin);

		item0.setText(ai.getState() + " at "+ ((AbstractProcedureInstanceNode)pin).getCreatedTime() +" by " + ai.getAgenda().getName());
		item0.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl(composite);
		item0.setImage(image);


		bar.setSpacing(8);
		shell.setSize(450, 300);
		shell.open();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) {
				display.sleep ();
			}
		}
		image.dispose();
		display.dispose();
	}

	public static void createParameterRow(Composite parent, Display display, String parameterName, String parameterValue){
		Label label = new Label(parent, SWT.NONE);
		label.setText(parameterName + ": ");

		FontData[] fontData = label.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setStyle(SWT.BOLD);
		}
		//TODO: Should we dispose of this Font object somewhere?
		Font newFont = new Font(display, fontData);
		label.setFont(newFont);
		label.pack();

		label = new Label(parent, SWT.NONE);
		label.setText(parameterValue);
	}

	public static void createTwoColumnTitle(Composite parent, Display display, String title){

		Label l = new Label(parent, SWT.CENTER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		l.setLayoutData(gridData);
		l.setText(title);

		FontData[] fontData = l.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setStyle(SWT.BOLD);
		}
		//TODO: Should we dispose of this Font object somewhere?
		Font newFont = new Font(display, fontData);
		l.setFont(newFont);
		l.pack();
	}

	public static void createTwoColumnSeparator(Composite parent){
		Label l = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 2;
		l.setLayoutData(gridData);
	}
}
