package org.uwais.teacherspet2;

public class Results {

	private int testID;
	private int studentID;
	private int mark;
	private String grade;
	private String passFail;
	
	public Results(int testID, int studentID, int mark, String grade,
			String passFail) {
		this.testID = testID;
		this.studentID = studentID;
		this.mark = mark;
		this.grade = grade;
		this.passFail = passFail;
	}

	public int getTestID() {return testID;}
	public int getStudentID() {return studentID;}
	public int getMark() {return mark;}
	public String getGrade() {return grade;}
	public String getPassFail() {return passFail;}
	
}
