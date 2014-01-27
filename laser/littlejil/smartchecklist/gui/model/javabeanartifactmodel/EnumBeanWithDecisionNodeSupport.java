package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.DecisionNodeSupport;


public abstract class EnumBeanWithDecisionNodeSupport <E extends Enum> 
extends EnumBean<E>
implements DecisionNodeSupport
{	
	public EnumBeanWithDecisionNodeSupport() {
		super();
	}

	@Override
	public Set<Serializable> getAlternatives() {
		Object[] enumConsts = this.defaultValue_.getDeclaringClass().getEnumConstants();
		Set<Serializable> alternatives = new LinkedHashSet<Serializable>();
		for (int i = 0; i < enumConsts.length; i++) {
			//NOTE: Since each enum constant is an Enum,
			//      the following cast is safe.
			alternatives.add((Serializable)enumConsts[i]);
		}
		return alternatives;
	}

	@Override
	public Serializable getSelectedAlternative() {
		return this.getValue();
	}

	@Override
	public void setSelectedAlternative(Serializable selectedAlternative) {
		this.setValue((E)selectedAlternative);
	}
}
