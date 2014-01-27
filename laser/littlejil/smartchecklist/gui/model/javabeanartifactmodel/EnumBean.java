package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.DecisionNodeSupport;


public abstract class EnumBean <E extends Enum> implements Serializable
{
	protected E defaultValue_;
	private E value_;
	
	
	public EnumBean() {
		super();
		
		this.defaultValue_ = this.createDefaultValue();
	}
	
	protected abstract E createDefaultValue();
	
	public void setValue(E value) {
		this.value_ = value;
	}
	
	public E getValue() {
		return this.value_;
	}
	
	public String toString() {
		return "" + this.value_;
	}
}
