package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;


public class PatientInfoPanel
{
	public static final String AGE_PREFIX = "Age: ";
	public static final String BIRTHDATE_PREFIX = "Birthdate: ";
	public static final String GENDER_PREFIX = "Gender: ";
	public static final String MRN_PREFIX = "MRN: ";
	
	private SmartChecklistGUI gui_;
	private Group infoPanel_;
	private Composite rightSubpanel_;
	private Label photoLabel_;
	private Label fullNameLabel_;
	private Label genderLabelValue_;
	private Label ageLabelValue_;
	private Label birthdateLabelValue_;
	private Label MRNLabelValue_;
	private List filenames_;
	
	
	public PatientInfoPanel(SmartChecklistGUI gui) {
		super();
		
		this.gui_ = gui;
		Shell view = this.gui_.getView();
		this.infoPanel_ = new Group(view, SWT.SHADOW_ETCHED_IN);
		this.infoPanel_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.infoPanel_.setSize(SmartChecklistGUI.GUI_WIDTH - 50, 50);
		this.infoPanel_.setLayout(new GridLayout(2, false));
		
		this.filenames_ = new List(this.infoPanel_, SWT.BORDER);
		this.filenames_.setBounds(0, 0, 144, 275);
		this.filenames_.add("first string");
		
		System.out.println("Created PatientInfoPanel.");
	}
	
	protected Label createLabelSubpanel(Composite labelPanel, String labelNameText, Font labelNameFont) {
		Composite labelSubpanel = new Composite(labelPanel, SWT.NONE);
		labelSubpanel.setLayout(new GridLayout(2, false));
		Label labelName = new Label(labelSubpanel, SWT.BORDER);
		labelName.setFont(labelNameFont);
		labelName.setText(labelNameText);
		labelName.pack();
		
		return new Label(labelSubpanel, SWT.BORDER);
	}
	
	public void setPatientInfo(PatientInfo patientInfo) {
		System.out.println("Setting PatientInfo to " + patientInfo);
		if (patientInfo != null) {
			this.photoLabel_.setImage(patientInfo.getPhoto());
			this.fullNameLabel_.setText(patientInfo.getFullName());
			this.genderLabelValue_.setText("" + patientInfo.getGender());
			this.ageLabelValue_.setText("" + patientInfo.getAge());
			this.birthdateLabelValue_.setText(patientInfo.getBirthdate());
			this.MRNLabelValue_.setText(patientInfo.getMRN());
		}
		else {
			this.photoLabel_.setImage(null);
			this.fullNameLabel_.setText("");
			this.genderLabelValue_.setText("");
			this.birthdateLabelValue_.setText("");
			this.ageLabelValue_.setText("");
			this.MRNLabelValue_.setText("");			
		}
		this.photoLabel_.pack();
		this.fullNameLabel_.pack();
		this.genderLabelValue_.pack();
		this.birthdateLabelValue_.pack();
		this.ageLabelValue_.pack();
		this.MRNLabelValue_.pack();
		this.rightSubpanel_.pack();
		this.infoPanel_.pack();
	}
}
