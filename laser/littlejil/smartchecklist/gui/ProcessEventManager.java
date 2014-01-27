package laser.littlejil.smartchecklist.gui;

import laser.juliette.agent.AbstractAgent;
import laser.juliette.agent.ItemHandler;
import laser.juliette.agent.ItemHandlerFactory;
import laser.juliette.ams.AMSException;
import laser.juliette.ams.AgendaItem;


public class ProcessEventManager extends AbstractAgent {
	
	private ProcessPanel processPanel_;
	
	public ProcessEventManager(ProcessPanel gui) {
		super();
		//System.out.println("Creating SmartChecklistEventManager...");
		this.processPanel_ = gui;
	}
	
	//
	// Begin of Little-JIL agent methods
	//
    
	public ProcessEventManager() {
		// TODO Auto-generated constructor stub
	}

	public void initialize(String agendaName) {
    	//System.out.println("Initializing SmartChecklistEventManager...");
		
		// Little-JIL agent code
		try {
			super.initialize(agendaName);
		} catch (AMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    protected void configureAgent() {
    	//System.out.println("Configuring SmartChecklistEventManager...");
    	
    	final ProcessPanel processPanel = this.processPanel_;
        setItemHandlerFactory(new ItemHandlerFactory() {
                public ItemHandler createItemHandler(AgendaItem item) {
                        return new ProcessEventHandler(processPanel, item);
                }
        });
        setProcessingMode(ProcessingMode.EXISTING_AND_NEW);
        // setItemFilter() can be called here to specify an item filter
    }

}
