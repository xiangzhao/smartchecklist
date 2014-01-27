package laser.littlejil.smartchecklist.gui.model.javabeanartifactmodel;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import laser.littlejil.smartchecklist.gui.model.DecisionNodeSupport;


public class BooleanBeanWithDecisionNodeSupport extends BooleanBean
                                                implements DecisionNodeSupport
{	
	public BooleanBeanWithDecisionNodeSupport() {
		super();
	}

	@Override
	public Set<Serializable> getAlternatives() {
		//TODO: This could be cached.
		Set<Serializable> alternatives = new LinkedHashSet<Serializable>();
		
		alternatives.add(Boolean.FALSE);
		alternatives.add(Boolean.TRUE);
		
		return alternatives;
	}

	@Override
	public Serializable getSelectedAlternative() {
		return this.getValue();
	}

	@Override
	public void setSelectedAlternative(Serializable selectedAlternative) {
		this.setValue((Boolean)selectedAlternative);
	}
}
