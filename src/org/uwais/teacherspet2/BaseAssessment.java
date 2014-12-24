package org.uwais.teacherspet2;

public class BaseAssessment {

	private int testID;
	private String testName;
	private String classGroup;

	public BaseAssessment(int testID, String testName, String classGroup) {
		this.testID = testID;
		this.testName = testName;
		this.classGroup = classGroup;
	}

	public int getTestID() {return testID;}

	public String getTestName() {return testName;}

	public String getClassGroup() {return classGroup;}

}
