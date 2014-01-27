package laser.littlejil.smartchecklist.gui.editor;

import java.lang.reflect.Field;

import laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel.JavaBeanPropertyInfoModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class JavaBeanPropertyInfoView 
{
	// This is the model
	private JavaBeanPropertyInfoModel fieldModel_;
	// This is the view
	private JavaBeanArtifactView artifactView_;
	private Composite fieldEditor_;
	private Label nameView_;
	private Text valueView_;
	
	
	public JavaBeanPropertyInfoView(JavaBeanArtifactView artifactView, Field fieldModel, boolean modifiable) {
		super();
		
		// Initialization
		this.artifactView_ = artifactView;
		
		// This is the model
		this.fieldModel_ = new JavaBeanPropertyInfoModel(this.artifactView_.getArtifactModel(), fieldModel, modifiable);
		
		// This is the view
		this.fieldEditor_ = new Composite(artifactView.getFieldsView(), SWT.NONE);
		this.fieldEditor_.setBackground(artifactView.getFieldsView().getBackground());
		GridData fieldEditorLayoutData = new GridData();
		fieldEditorLayoutData.horizontalAlignment = GridData.FILL;
		fieldEditorLayoutData.grabExcessHorizontalSpace = true;
		this.fieldEditor_.setLayoutData(fieldEditorLayoutData);
		this.fieldEditor_.setLayout(new GridLayout(2, false));
		this.nameView_ = new Label(this.fieldEditor_, SWT.NONE);
		FontData[] nameFontData = this.nameView_.getFont().getFontData();
		for (int j = 0; j < nameFontData.length; j++) {
			nameFontData[j].setStyle(SWT.BOLD);
		}
		Font nameFont = new Font(artifactView.getParent().getDisplay(), nameFontData);
		this.nameView_.setFont(nameFont);
		this.nameView_.setBackground(artifactView.getFieldsView().getBackground());
		this.nameView_.setText(this.getFieldModel().getPrettyPrintedFieldName() + ": ");
		this.nameView_.pack();
		this.valueView_ = new Text(this.fieldEditor_, SWT.NONE);
		this.valueView_.setEditable(modifiable);
		GridData currentFieldValueLayoutData = new GridData();
		currentFieldValueLayoutData.horizontalAlignment = GridData.FILL;
		currentFieldValueLayoutData.grabExcessHorizontalSpace = true;
		this.valueView_.setLayoutData(currentFieldValueLayoutData);
		Object currentFieldValue = this.getFieldModel().getValue();
		if (currentFieldValue == null) {
			//TODO: Default value could be based on the field type
			this.valueView_.setText("");
		}
		else {
			this.valueView_.setText(currentFieldValue.toString());
		}
//		System.out.println("Set propertyInfo with name " + this.getFieldModel().getName() + " to value " + valueView_.getText());
		this.valueView_.pack();
	}
	
	public JavaBeanPropertyInfoModel getFieldModel() {
		return this.fieldModel_;
	}
	
	public boolean getModifiable() {
		return this.getFieldModel().getModifiable();
	}
	
	public void setModifiable(boolean modifiable) {
		this.getFieldModel().setModifiable(modifiable);
		this.valueView_.setEditable(modifiable);
//		System.out.println("JavaBeanPropertyInfo with name " + this.getFieldModel().getName() + " - editable: " + this.valueView_.getEditable());
	}
	
	public String getValue() {
		return this.valueView_.getText();
	}
	
	public JavaBeanArtifactView getArtifactView() {
		return this.artifactView_;
	}
	
	protected Label getNameView() {
		return this.nameView_;
	}
} // end of FieldView