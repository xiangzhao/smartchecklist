package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

import java.util.ArrayList;

import laser.juliette.ams.AgendaItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class AgendaItemWindow {

	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AgendaItemWindow window = new AgendaItemWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		//java.util.List<AgendaItem> agendaItems = new ArrayList<AgendaItem>();
		String[][] agendaItems = new String[3][4];
		agendaItems[0][0] = "optional";
		agendaItems[0][1] = "title of agenda item";
		agendaItems[0][2] = "4/5/16";
		agendaItems[0][3] = "chris";
		
		agendaItems[1][0] = "optional";
		agendaItems[1][1] = "title of agenda item";
		agendaItems[1][2] = "4/5/16";
		agendaItems[1][3] = "chris";
		
		agendaItems[2][0] = "optional";
		agendaItems[2][1] = "title of agenda item";
		agendaItems[2][2] = "4/5/16";
		agendaItems[2][3] = "chris";
		
		shell = new Shell();
		shell.setSize(450,300);
		shell.setLayout(new FillLayout());
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Composite composite_1 = new Composite(scrolledComposite, SWT.NONE);
		composite_1.setLayout(new RowLayout(SWT.VERTICAL));
		
		for (String[] agendaItem : agendaItems){
			createAgendaItemRow(agendaItem, composite_1);
		}
	
		scrolledComposite.setContent(composite_1);
		scrolledComposite.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private Control createAgendaItemRow(String[] agendaItem, Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button button = new Button(composite, SWT.NONE);
		button.setText(agendaItem[0]);
		
		Button button_1 = new Button(composite, SWT.NONE);
		button_1.setText(agendaItem[1]);
		
		Button button_2 = new Button(composite, SWT.NONE);
		button_2.setText(agendaItem[2]);
		
		Button button_3 = new Button(composite,SWT.NONE);
		button_3.setText(agendaItem[3]);
		
		return composite;
	}
}
