package laser.littlejil.smartchecklist.gui.model;

import laser.juliette.ams.AgendaItem;


public interface ActivityFilter 
{
	public boolean accept(AgendaItem agendaItem);
}
