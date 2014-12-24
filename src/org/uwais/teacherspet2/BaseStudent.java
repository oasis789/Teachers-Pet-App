package org.uwais.teacherspet2;

public class BaseStudent {
	
	private String firstName;
	private String lastName;
	private int studentID;
	
	public BaseStudent(int id, String fName, String lName){
		firstName = fName;
		lastName = lName;
		studentID = id;
	}
	
	public String getFirstName(){return firstName;}
	public String getLastName(){return lastName;}
	public String getName(){ return firstName + " " + lastName;}
	public int getStudentID() {return studentID;}
	
}
