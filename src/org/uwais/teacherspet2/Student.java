package org.uwais.teacherspet2;

public class Student extends BaseStudent {
	
	private String dateOfBirth;
	private int yearGroup;
	private String formGroup;
	private String className;
	private String predictedGrade;

	public Student(int id, String fName, String lName, String dob, int yGroup, String fGroup, String className, String predictedgrade){
		super(id, fName, lName);
		dateOfBirth = dob;
		yearGroup = yGroup;
		formGroup = fGroup;
		this.className = className;
		predictedGrade = predictedgrade;
	}
	
	
	//getter methods
	public String getDateOfBirth(){return dateOfBirth;}
	public String getFormGroup(){return formGroup;}
	public int getYearGroup(){return yearGroup;}
	public String getClassName(){return className;}
	public String getPredictedGrade(){return predictedGrade;}
	
}
