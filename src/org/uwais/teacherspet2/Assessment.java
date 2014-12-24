package org.uwais.teacherspet2;

import android.os.Bundle;

public class Assessment extends BaseAssessment {

	private String date;
	private int maxMarks;
	private int aPercent;
	private int bPercent;
	private int cPercent;
	private int passPercent;

	public Assessment(int testID, String classGroup, String testName,
			String date, int maxMarks, int aPercent, int bPercent,
			int cPercent, int passMark) {
		super(testID, testName, classGroup);
		this.date = date;
		this.maxMarks = maxMarks;
		this.aPercent = aPercent;
		this.bPercent = bPercent;
		this.cPercent = cPercent;
		this.passPercent = passMark;
	}

	public Assessment(Bundle bundle) {
		super(bundle.getInt(Constants.KEY_TEST_ID), bundle
				.getString(Constants.KEY_TEST_NAME), bundle
				.getString(Constants.KEY_CLASS));
		date = bundle.getString(Constants.KEY_DATE);
		maxMarks = bundle.getInt(Constants.KEY_MAX_MARKS);
		aPercent = bundle.getInt(Constants.KEY_A_PERCENT);
		bPercent = bundle.getInt(Constants.KEY_B_PERCENT);
		cPercent = bundle.getInt(Constants.KEY_C_PERCENT);
		passPercent = bundle.getInt(Constants.KEY_PASS_PERCENT);
	}

	public String getDate() {return date;}

	public int getMaxMarks() {return maxMarks;}

	public int getaPercent() {return aPercent;}

	public int getbPercent() {return bPercent;}

	public int getcPercent() {return cPercent;}

	public int getPassPercent() {return passPercent;}

	public Bundle packageAssessmentIntoBundle() {
		Bundle bundle = new Bundle();
		bundle.putInt(Constants.KEY_TEST_ID, getTestID());
		bundle.putString(Constants.KEY_CLASS, getClassGroup());
		bundle.putString(Constants.KEY_TEST_NAME, getTestName());
		bundle.putString(Constants.KEY_DATE, date);
		bundle.putInt(Constants.KEY_MAX_MARKS, maxMarks);
		bundle.putInt(Constants.KEY_A_PERCENT, aPercent);
		bundle.putInt(Constants.KEY_B_PERCENT, bPercent);
		bundle.putInt(Constants.KEY_C_PERCENT, cPercent);
		bundle.putInt(Constants.KEY_PASS_PERCENT, passPercent);
		return bundle;
	}
}
