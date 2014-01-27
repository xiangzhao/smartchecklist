package laser.littlejil.smartchecklist.gui.editor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import laser.littlejil.smartchecklist.gui.SmartChecklistGUI;
import laser.littlejil.smartchecklist.gui.utils.PrettyPrintedNamesFormatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


//TODO: 
//  1) This could be an artifact editor or viewer.
//     A field could be used to record whether or not 
//     the artifact is editable. If so, then the values
//     should be editable.
//  2) The table should support:
//      - tabbing to the next cell (up/down, left/right)
//      - hitting enter after edit
public class JavaBeanArtifactTableEditorDialog extends Dialog 
{
	public static final int NAME_COLUMN = 0;
	public static final int EDITABLE_VALUE_COLUMN = 1;
	
	// This is the model
	private String name_;
	private Serializable value_;
	// This is the view
	private Table tableView_;
	private TableEditor tableEditor_;
	private int dialogResult_;
	
	
	public JavaBeanArtifactTableEditorDialog(Shell parent, String name, Serializable value) {
		super(parent);
		
		this.name_ = name;
		this.value_ = value;
		
		this.setText("Configure " + PrettyPrintedNamesFormatter.createHumanReadableName(this.name_).toLowerCase());
	}
	
	public int open() {
		// GUI code
		final Shell view = this.getParent();
		final Display display = view.getDisplay();
		final Shell dialog = new Shell(view, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		
		Point pt = display.getCursorLocation();
		dialog.setLocation(pt.x + 15, pt.y);

		//TODO: Fix up the layout and sizes
		dialog.setText(getText());
		dialog.setLayout(new GridLayout());
		dialog.setSize(SmartChecklistGUI.GUI_WIDTH, SmartChecklistGUI.GUI_HEIGHT);
		Color bgColor = new Color(display, 130, 190, 220);
		dialog.setBackground(bgColor);
		
		// Create and fill in the table view
		//
		// Create the table view
		this.tableView_ = new Table(dialog, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.tableView_.setHeaderVisible(true);
		this.tableView_.setLinesVisible(true);
		TableColumn nameColumn = new TableColumn(this.tableView_, SWT.LEFT);
		FontData[] nameFontData = this.tableView_.getFont().getFontData();
		for (int j = 0; j < nameFontData.length; j++) {
			nameFontData[j].setStyle(SWT.BOLD);
		}
		Font nameFont = new Font(dialog.getDisplay(), nameFontData);
		nameColumn.setText("Name");
		nameColumn.setWidth(this.tableView_.getBounds().width / 4);
		nameColumn.pack();
		TableColumn valueColumn = new TableColumn(this.tableView_, SWT.LEFT);
		valueColumn.setText("Value");
		valueColumn.setWidth(this.tableView_.getBounds().width / 2);
		valueColumn.pack();
		GridData tableEditorGridData = new GridData();
		tableEditorGridData.horizontalAlignment = GridData.FILL;
		tableEditorGridData.verticalAlignment = GridData.FILL;
		tableEditorGridData.grabExcessHorizontalSpace = true;
		tableEditorGridData.grabExcessVerticalSpace = true;
		this.tableView_.setLayoutData(tableEditorGridData);
		this.tableView_.setLayout(new GridLayout());
		this.tableView_.pack();
		// Fill in the table view
		//
		// ASSUMPTION: The value is a Java bean. This means
		//             that each field will have a getter and
		//             setter method.
		System.out.println("Artifact class: " + this.value_.getClass());
		Field[] valueFields = this.value_.getClass().getDeclaredFields();
		for (int i = 0; i < valueFields.length; i++) {
			Field currentField = valueFields[i];
			int currentFieldModifiers = currentField.getModifiers();
			System.out.println("\tcurrentField: " + currentField);
			if ((! Modifier.isStatic(currentFieldModifiers)) &&
				(! Modifier.isFinal(currentFieldModifiers))) 
			{
				String[] currentFieldText = { PrettyPrintedNamesFormatter.createHumanReadableName(currentField.getName()).toLowerCase(), "" };
				TableItem currentFieldView = new TableItem(this.tableView_, SWT.NONE);
				currentFieldView.setFont(NAME_COLUMN, nameFont);
				currentFieldView.setText(currentFieldText);
				System.out.println("\t\tcurrentFieldView: " + currentFieldView);
			}
		} // end for i
		this.tableView_.pack();
		
		// Make each table value editable
		//
		// Create the value editor
		this.tableEditor_ = new TableEditor(this.tableView_);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		this.tableEditor_.horizontalAlignment = SWT.LEFT;
		this.tableEditor_.grabHorizontal = true;
		this.tableEditor_.minimumWidth = 50;
		// Enable the value editor when a row is selected
		this.tableView_.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = tableEditor_.getEditor();
				if (oldEditor != null) oldEditor.dispose();
		
				// Identify the selected row
				TableItem item = (TableItem)e.item;
				if (item == null) return;
		
				// The control that will be the editor must be a child of the Table
				Text newEditor = new Text(tableView_, SWT.NONE);
				newEditor.setText(item.getText(EDITABLE_VALUE_COLUMN));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text)tableEditor_.getEditor();
						tableEditor_.getItem().setText(EDITABLE_VALUE_COLUMN, text.getText());
					}
				});
				//TODO: Recognize when the user hits return
				newEditor.selectAll();
				newEditor.setFocus();
				tableEditor_.setEditor(newEditor, item, EDITABLE_VALUE_COLUMN);
			}
		});
		
		// Create a composite with GridLayout that will hold the Cancel and OK buttons.
		Composite buttonPanel = new Composite(dialog, SWT.NONE);
		buttonPanel.setBackground(dialog.getBackground());
		buttonPanel.setLayoutData(new GridData(SWT.RIGHT));
		buttonPanel.setLayout(new GridLayout(2, true));
		// Create the cancel button and its selection listener
		Button cancelButton = new Button(buttonPanel, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.pack();
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// The user decided to not throw any exceptions and the dialogResult_ is cancel
				dialogResult_ = SWT.CANCEL;
				dialog.dispose();
			}
		});
		// Create the OK button and its selection listener
		Button okButton = new Button(buttonPanel, SWT.PUSH);
		okButton.setText("OK");
		okButton.pack();
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO: Return the edited artifact
				dialogResult_ = SWT.OK;
				dialog.dispose();
			}
		});
		
		// Open the dialog containing the widgets for creating a note
		dialog.pack();
		dialog.open();
		while (! dialog.isDisposed()) {
			if (! display.readAndDispatch()) {
				display.sleep();
			}
		}
    			
    	return dialogResult_;
	}
}
