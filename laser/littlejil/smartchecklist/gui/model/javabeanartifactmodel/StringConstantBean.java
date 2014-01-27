package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.DecisionNodeSupport;


public abstract class StringConstantBean implements Serializable
{
	protected Set<Serializable> alternatives_;
	private String value_;
	
	
	public StringConstantBean() {
		super();
		
		this.alternatives_ = this.createAlternatives();
	}
	
	protected abstract Set<Serializable> createAlternatives();
	
	public void setValue(String value) {
		if (! this.alternatives_.contains(value)) {
			throw new IllegalArgumentException("Value " + value + " must be one of the following: " + this.alternatives_ + ".");
		}
		else {
			this.value_ = value;
		}
	}
	
	public String getValue() {
		return this.value_;
	}
	
	public String toString() {
		return "" + this.value_;
	}
}
