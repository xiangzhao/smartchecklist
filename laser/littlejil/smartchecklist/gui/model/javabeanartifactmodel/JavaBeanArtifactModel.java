package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;

import laser.littlejil.smartchecklist.gui.utils.PrettyPrintedNamesFormatter;


public class JavaBeanArtifactModel 
{
	private String name_;
	private Serializable value_;
	private boolean modifiable_;
	
	
	public JavaBeanArtifactModel(String name, Serializable value, boolean modifiable) {
		super();
		
		this.name_ = name;
		this.value_ = value;
		this.modifiable_ = modifiable;
	}
	
	public String getName() {
		return this.name_;
	}
	
	public String getPrettyPrintedName() {
		return PrettyPrintedNamesFormatter.createHumanReadableName(this.getName()).toLowerCase();
	}
	
	public Class getType() {
		return this.getValue().getClass();
	}
	
	public Serializable getValue() {
		return this.value_;
	}
	
	public boolean getModifiable() {
		return this.modifiable_;
	}
	
	public void setModifiable(boolean modifiable) {
		this.modifiable_ = modifiable;
	}
}
