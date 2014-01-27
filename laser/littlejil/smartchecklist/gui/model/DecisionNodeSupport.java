package laser.littlejil.smartchecklist.gui.model;

import java.io.Serializable;
import java.util.Set;


public interface DecisionNodeSupport extends Serializable 
{
	public Set<Serializable> getAlternatives();
	
	public Serializable getSelectedAlternative();
	
	public void setSelectedAlternative (Serializable selectedAlternative);
}
