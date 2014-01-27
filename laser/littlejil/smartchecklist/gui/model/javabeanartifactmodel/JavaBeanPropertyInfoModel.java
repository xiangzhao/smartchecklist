package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import laser.littlejil.smartchecklist.gui.editor.valueparser.BooleanValueParser;
import laser.littlejil.smartchecklist.gui.editor.valueparser.IntegerValueParser;
import laser.littlejil.smartchecklist.gui.editor.valueparser.StringValueParser;
import laser.littlejil.smartchecklist.gui.editor.valueparser.ValueParser;
import laser.littlejil.smartchecklist.gui.utils.PrettyPrintedNamesFormatter;


public class JavaBeanPropertyInfoModel 
{
	private JavaBeanArtifactModel artifactModel_;
	private Field field_;
	private boolean modifiable_;
	private Method fieldGetterMethod_;
	private Method fieldSetterMethod_;
	private ValueParser valueParser_;
	
	
	public JavaBeanPropertyInfoModel(JavaBeanArtifactModel artifactModel, Field fieldModel, boolean modifiable) {
		super();
		
		this.artifactModel_ = artifactModel;
		this.field_ = fieldModel;
		this.modifiable_ = modifiable;
	}
	
	public JavaBeanArtifactModel getArtifactModel() {
		return this.artifactModel_;
	}
	
	public Field getFieldModel() {
		return this.field_;
	}
	
	public boolean getModifiable() {
		return this.modifiable_;
	}
	
	public void setModifiable(boolean modifiable) {
		this.modifiable_ = modifiable;
	}
	
	public String getName() {
		return this.getFieldModel().getName();
	}
	
	public String getPrettyPrintedFieldName() {
		return PrettyPrintedNamesFormatter.createHumanReadableName(this.getName()).toLowerCase();
	}
	
	protected String getTitleCaseFieldName() {
		String fieldName = null;
		
		fieldName = this.getName().substring(0, 1);
		fieldName = fieldName.toUpperCase();
		fieldName += this.getFieldModel().getName().substring(1);
		
		return fieldName;
	}
	
	public Class getType() {
		return this.getFieldModel().getType();
	}
	
	protected Method getFieldGetterMethod() {
		if (this.fieldGetterMethod_ == null) {
			try {
				this.fieldGetterMethod_ = this.getArtifactModel().getType().getDeclaredMethod("get" + this.getTitleCaseFieldName(), null);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (this.fieldGetterMethod_ == null) {
				throw new UnsupportedOperationException("Artifact " + this.getArtifactModel().getName() + " must have a getter method.");
			}
		}
		
		return this.fieldGetterMethod_;
	}
	
	protected Method getFieldSetterMethod() {
		if (this.fieldSetterMethod_ == null) {
			try {
				this.fieldSetterMethod_ = this.getArtifactModel().getType().getDeclaredMethod("set" + this.getTitleCaseFieldName(), this.getType());
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (this.fieldSetterMethod_ == null) {
				throw new UnsupportedOperationException("Artifact " + this.getArtifactModel().getName() + " must have a setter method.");
			}
		}
		
		return this.fieldSetterMethod_;
	}
	
	protected ValueParser getValueParser() {
		if (this.valueParser_ == null) {
			String fieldTypeName = this.getFieldModel().getType().getName();
//			System.out.println("fieldTypeName: " + fieldTypeName);
			if (fieldTypeName.equals("boolean") || 
				fieldTypeName.equals("java.lang.Boolean")) 
			{
				this.valueParser_ = new BooleanValueParser();
			}
			else if (fieldTypeName.equals("int") ||
					 fieldTypeName.equals("java.lang.Integer")) 
			{
				this.valueParser_ = new IntegerValueParser();
			}
			else if (fieldTypeName.equals("java.lang.String")) {
				this.valueParser_ = new StringValueParser();
			}
			else {
				throw new UnsupportedOperationException("Field " + this.getFieldModel().getName() + " of type " + fieldTypeName + " is not supported.");
			}
		}
		
		return this.valueParser_;
	}
	
	public Object getValue() {
		Serializable artifactInst = this.getArtifactModel().getValue();
		Object value = null;
		
//		System.out.println("Getting value of field " + this.getName() + " from artifactInst: " + artifactInst);
		try {
			Method getterMethod = this.getFieldGetterMethod();
			Object[] getterMethodNoArgs = { };
			value = getterMethod.invoke(artifactInst, getterMethodNoArgs);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
	
	public void setValue(String valueString) {
		if (this.getModifiable()) {
			Serializable artifactInst = this.getArtifactModel().getValue();
			
//			System.out.println("Setting value of field " + this.getName() + " from artifactInst " + artifactInst);
			
			try {
				Method setterMethod = this.getFieldSetterMethod();
				ValueParser valueParser = this.getValueParser();
//				System.out.println("Parsing field " + this.getFieldModel().getName() + ": " + valueString);
				Object[] setterMethodArgs = { valueParser.parse(this.getFieldModel().getType(), valueString) };
				setterMethod.invoke(artifactInst, setterMethodArgs);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
