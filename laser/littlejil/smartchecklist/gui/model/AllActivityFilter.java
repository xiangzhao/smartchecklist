package laser.littlejil.smartchecklist.gui.model;

import laser.juliette.ams.AgendaItem;


public class AllActivityFilter implements ActivityFilter 
{
	@Override
	public boolean accept(AgendaItem agendaItem) {
		return true;
	}
}
