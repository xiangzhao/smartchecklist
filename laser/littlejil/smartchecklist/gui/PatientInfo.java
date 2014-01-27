package laser.littlejil.smartchecklist.gui;

import org.eclipse.swt.graphics.Image;

public class PatientInfo 
{
	private Image photo_;
	private String fullName_;
	private Gender gender_;
	private int age_;
	private String birthdate_;
	private String MRN_;
	
	
	public PatientInfo(Image photo, String fullName, Gender gender, int age, String birthdate, String MRN) {
		super();
		this.photo_ = photo;
		this.fullName_ = fullName;
		this.gender_ = gender;
		this.age_ = age;
		this.birthdate_ = birthdate;
		this.MRN_ = MRN;
	}
	
	public Image getPhoto() {
		return this.photo_;
	}
	
	public String getFullName() {
		return this.fullName_;
	}
	
	public Gender getGender() {
		return gender_;
	}
	
	public int getAge() {
		return this.age_;
	}
	
	public String getBirthdate() {
		return this.birthdate_;
	}
	
	public String getMRN() {
		return this.MRN_;
	}
	
	public String toString() {
		String toReturn = 
				"{ FullName: " + this.getFullName() + 
				", Gender: " + this.getGender() +
				", Age: " + this.getAge() + 
				", Birthdate: " + this.getBirthdate() + 
				", MRN: " + this.getMRN() + " }";
		
		return toReturn;
	}
}
