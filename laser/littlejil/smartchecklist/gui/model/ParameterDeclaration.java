package laser.littlejil.smartchecklist.gui.model;

import java.io.Serializable;


public class ParameterDeclaration implements Serializable
{
	private String name_;
	private ParameterMode mode_;
	private String type_;
	
	
	public ParameterDeclaration(String name, ParameterMode mode, String type) {
		super();
		if ((name == null) || name.isEmpty()) {
			throw new IllegalArgumentException("The name must be non-null and non-empty.");
		}
		if (mode == null) {
			throw new IllegalArgumentException("The mode must be non-null.");
		}
		if ((type == null) || type.isEmpty()) {
			throw new IllegalArgumentException("The type must be non-null and non-empty.");
		}
		this.name_ = name;
		this.mode_ = mode;
		this.type_ = type;
	}
	
	public String getName() {
		return this.name_;
	}
	
	public ParameterMode getMode() {
		return this.mode_;
	}
	
	public String getType() {
		return this.type_;
	}
	
	public String toString() {
		return "ParameterDeclaration(name: " + this.getName() + ", mode: " + this.getMode() + ", type: " + this.getType() + ")";
	}
}
