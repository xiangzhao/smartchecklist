package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.DecisionNodeSupport;


public class BooleanBean implements Serializable
{
	private boolean value_;
	
	
	public BooleanBean() {
		super();
		this.value_ = false;
	}
	
	public void setValue(boolean value) {
		this.value_ = value;
	}
	
	public boolean getValue() {
		return this.value_;
	}
	
	public String toString() {
		return "" + this.value_;
	}
}
