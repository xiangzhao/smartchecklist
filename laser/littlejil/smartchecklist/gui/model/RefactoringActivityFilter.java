/**
 * 
 */
package laser.littlejil.smartchecklist.gui.model;

import laser.juliette.ams.AMSException;
import laser.juliette.ams.AgendaItem;

/**
 * @author xiang
 * 
 */
public class RefactoringActivityFilter implements ActivityFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * laser.littlejil.smartchecklist.gui.model.ActivityFilter#accept(laser.
	 * juliette.ams.AgendaItem)
	 */
	@Override
	public boolean accept(AgendaItem agendaItem) {
		try {
			if (agendaItem.getStep().getName().equals("skip"))
				return false;
		} catch (AMSException e) {
			e.printStackTrace();
		}
		return true;
	}

}
