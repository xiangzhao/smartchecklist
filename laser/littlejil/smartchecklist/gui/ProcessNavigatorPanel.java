package laser.littlejil.smartchecklist.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import laser.littlejil.smartchecklist.gui.model.Activity;
import laser.littlejil.smartchecklist.gui.model.ActivityKind;
import laser.littlejil.smartchecklist.gui.model.Process;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


public class ProcessNavigatorPanel implements PropertyChangeListener
{
	private Process processModel_;
	private ProcessPanel processPanel_;
	private Tree navigatorPanel_;
	private Map<ActivityKind,Image> imageMap_;
	private List<TreeItem> treeItems_;
	
	
	public ProcessNavigatorPanel(ProcessPanel processPanel) {
		super();
		
		this.processPanel_ = processPanel;
		this.navigatorPanel_ = new Tree(this.processPanel_.getProcessPanel(), SWT.BORDER);
		Display display = this.processPanel_.getGUI().getView().getDisplay();
		this.imageMap_ = new LinkedHashMap<ActivityKind,Image>();
		Image sequentialImage = new Image(display, ProcessNavigatorPanel.class.getResourceAsStream("images/sequential.gif"));
		this.imageMap_.put(ActivityKind.SEQUENTIAL, sequentialImage);
		Image tryImage = new Image(display, ProcessNavigatorPanel.class.getResourceAsStream("images/try.gif"));
		this.imageMap_.put(ActivityKind.TRY, tryImage);
		Image parallelImage = new Image(display, ProcessNavigatorPanel.class.getResourceAsStream("images/parallel.gif"));
		this.imageMap_.put(ActivityKind.PARALLEL, parallelImage);
		Image choiceImage = new Image(display, ProcessNavigatorPanel.class.getResourceAsStream("images/choice.gif"));
		this.imageMap_.put(ActivityKind.CHOICE, choiceImage);
		Image leafImage = new Image(display, ProcessNavigatorPanel.class.getResourceAsStream("images/leaf.gif"));
		this.imageMap_.put(ActivityKind.LEAF, leafImage);
		this.treeItems_ = new ArrayList<TreeItem>();
		
		this.processModel_ = this.processPanel_.getProcessModel();
		this.processModel_.addPropertyChangeListener(this);
	}
	
	protected ProcessPanel getProcessPanel() {
		return this.processPanel_;
	}
	
	protected Tree getNavigatorPanel() {
		return this.navigatorPanel_;
	}
	
	public Image getImage(Activity activity) {
		Image image = this.imageMap_.get(activity.getKind());
		
		return image;
	}

	private synchronized void addTreeItem(final Activity newActivity) {
		Shell view = this.getProcessPanel().getGUI().getView();
    	Display display = view.getDisplay();
    	// The SWT widgets must be created within the UI thread.
    	// The Little-JIL agents are updated within non-UI threads.
    	// The following ensures that the the updates are within the UI thread.
    	display.syncExec(new Runnable() {
    		public void run() {
    			Activity parentActivity = newActivity.getParent();
    			TreeItem childItem = null;

    			if (parentActivity == null) {
    				childItem = new TreeItem(navigatorPanel_, 0);
    			}
    			else {
    				TreeItem parentItem = getTreeItem(parentActivity);
    				childItem = new TreeItem(parentItem, 0);
    			}
    			childItem.setText(newActivity.getName());
    			childItem.setImage(getImage(newActivity));
    			childItem.setData(newActivity);
    			treeItems_.add(childItem);
    		}
    	});
	}
	
	private TreeItem getTreeItem(Activity activity) {
		for (TreeItem currentTreeItem : this.treeItems_) {
			if (currentTreeItem.getData() == activity) {
				return currentTreeItem;
			}
		} // end for currentTreeItem
		
		return null;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(Process.ACTIVITIES_PROPERTY_NAME)) {
			Activity newActivity = (Activity)event.getNewValue();
			this.addTreeItem(newActivity);
		}
	}
}
