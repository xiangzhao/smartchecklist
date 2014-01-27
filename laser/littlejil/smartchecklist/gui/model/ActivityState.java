package laser.littlejil.smartchecklist.gui.model;

public enum ActivityState {
	/**
	 * States specific to the visualization process
	 */
	
	// Step may happen in the future
	INITIAL {
		public String toString() { return "initial"; }
	},	

	/**
	 * Little-JIL states
	 */
	POSTED {
		public String toString() { return "posted"; }
	},
	// This is needed by steps with pre-requisites
	STARTING{
		public String toString() { return "starting"; }
	},
	STARTED {
		public String toString() { return "started"; }
	},
	RETRACTED {
		public String toString() { return "retracted"; }
	},
	OPTEDOUT {
		public String toString() { return "optedout"; }
	},
	COMPLETED {
		public String toString() { return "completed"; }
	},
	TERMINATED {
		public String toString() { return "terminated"; }
	}
}
