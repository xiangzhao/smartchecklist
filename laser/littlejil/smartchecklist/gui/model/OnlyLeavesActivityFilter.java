package laser.littlejil.smartchecklist.gui.model;

import laser.juliette.ams.AMSException;
import laser.juliette.ams.AgendaItem;


public class OnlyLeavesActivityFilter implements ActivityFilter 
{
	@Override
	public boolean accept(AgendaItem agendaItem) {
		try {
			if ((agendaItem.getChildren() == null) ||
				agendaItem.getChildren().isEmpty()) {
				return true;
			}
			else {
				return false;
			}
		} catch (AMSException e) {
			throw new IllegalArgumentException("Agenda item " + agendaItem + " cannot determine whether or not it is a leaf.", e);
		}
	}
}
