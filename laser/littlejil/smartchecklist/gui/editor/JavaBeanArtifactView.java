package laser.littlejil.smartchecklist.gui.editor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.FieldView;

import laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel.JavaBeanArtifactModel;
import laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel.JavaBeanPropertyInfoModel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class JavaBeanArtifactView 
{
	// This is the model
	private JavaBeanArtifactModel artifactModel_;
	// This is the view
	private Composite parent_;
	private List<JavaBeanPropertyInfoView> fieldEditors_;
	private Composite fieldsView_;
	
	
	public JavaBeanArtifactView (Composite parent, String name, Serializable value, boolean modifiable) {
		this(parent, new JavaBeanArtifactModel(name, value, modifiable));
	}
	
	public JavaBeanArtifactView(Composite parent, JavaBeanArtifactModel artifactModel) {
		super();
		
		// Initialization
		this.parent_ = parent;
		this.artifactModel_ = artifactModel;
		this.fieldEditors_ = new ArrayList<JavaBeanPropertyInfoView>();

		// Create and fill in the field views
		//
		// ASSUMPTION: The value is a Java bean. This means
		//             that each field will have a getter and
		//             setter method.
		this.fieldEditors_.clear(); //TODO: Dispose them?
		// Create a Composite with GridLayout that will contain the
		// field editors
		this.fieldsView_ = new Composite(this.parent_, SWT.NONE);
		this.fieldsView_.setBackground(this.parent_.getBackground());
		GridData fieldsViewLayoutData = new GridData();
		fieldsViewLayoutData.horizontalAlignment = GridData.FILL;
		fieldsViewLayoutData.grabExcessHorizontalSpace = true;
		fieldsViewLayoutData.verticalAlignment = GridData.FILL;
		fieldsViewLayoutData.grabExcessVerticalSpace = true;
		this.fieldsView_.setLayoutData(fieldsViewLayoutData);
		this.fieldsView_.setLayout(new GridLayout());
		//	System.out.println("Artifact class: " + this.value_.getClass());
		Field[] valueFields = this.getArtifactModel().getType().getDeclaredFields();
		int maxFieldNameWidth = -1;
		// For now, the editable fields are not static and not final
		for (int i = 0; i < valueFields.length; i++) {
			Field currentField = valueFields[i];
			int currentFieldModifiers = currentField.getModifiers();
			//		System.out.println("\tcurrentField: " + currentField);
			if ((! Modifier.isStatic(currentFieldModifiers)) &&
					(! Modifier.isFinal(currentFieldModifiers))) 
			{
				JavaBeanPropertyInfoView currentFieldEditor = new JavaBeanPropertyInfoView(this, currentField, this.getModifiable());
				this.fieldEditors_.add(currentFieldEditor);
				if (currentFieldEditor.getNameView().getBounds().width > maxFieldNameWidth) {
					maxFieldNameWidth = currentFieldEditor.getNameView().getBounds().width;
				}
			}
		} // end for i
		// Layout the field editors so that the names are in the first column
		// and the values are the second column
		for (int j = 0; j < this.fieldEditors_.size(); j++) {
			JavaBeanPropertyInfoView currentFieldEditor = this.fieldEditors_.get(j);
			GridData currentFieldNameLayoutData = new GridData();
			currentFieldNameLayoutData.widthHint = maxFieldNameWidth + 5;
			currentFieldEditor.getNameView().setLayoutData(currentFieldNameLayoutData);
		} // end for j
		
		// Layout the artifact view
		this.fieldsView_.pack();
	}
	
	public JavaBeanArtifactModel getArtifactModel() {
		return this.artifactModel_;
	}
	
	public boolean getModifiable() {
		return this.getArtifactModel().getModifiable();
	}
	
	public void setModifiable(boolean modifiable) {
//		System.out.println("JavaBeanArtifactModel - Setting modifiable to " + modifiable);
		this.getArtifactModel().setModifiable(modifiable);
		for (int i = 0; i < this.fieldEditors_.size(); i++) {
			JavaBeanPropertyInfoView currentFieldView = this.fieldEditors_.get(i);
			
			currentFieldView.setModifiable(modifiable);
		}
	}
	
	public Composite getParent() {
		return this.fieldsView_.getParent();
	}
	
	protected Composite getFieldsView() {
		return this.fieldsView_;
	}
	
	public void save() {
		if (this.getModifiable()) {
			// Save the edited artifact
			for (int i = 0; i < fieldEditors_.size(); i++) {
				JavaBeanPropertyInfoView currentFieldEditor = fieldEditors_.get(i);
				JavaBeanPropertyInfoModel currentFieldModel = currentFieldEditor.getFieldModel();
				currentFieldModel.setValue(currentFieldEditor.getValue());
			} // end for i
			System.out.println("Edited artifact " + this.getArtifactModel().getValue());
		}
	}
	
	//TODO: Support dispose
}
