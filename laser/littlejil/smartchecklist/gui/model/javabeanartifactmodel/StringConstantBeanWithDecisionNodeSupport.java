package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.DecisionNodeSupport;


public abstract class StringConstantBeanWithDecisionNodeSupport extends StringConstantBean
                                                                implements DecisionNodeSupport
{	
	public StringConstantBeanWithDecisionNodeSupport() {
		super();
	}

	@Override
	public Set<Serializable> getAlternatives() {
		return Collections.unmodifiableSet(this.alternatives_);
	}

	@Override
	public Serializable getSelectedAlternative() {
		return this.getValue();
	}

	@Override
	public void setSelectedAlternative(Serializable selectedAlternative) {
		this.setValue((String)selectedAlternative);
	}
}
