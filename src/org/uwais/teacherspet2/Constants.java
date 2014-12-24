package org.uwais.teacherspet2;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {

	public static final String USERNAME_PREFS = "Username";
	public static final String PASSWORD_PREFS = "Password";
	public static final String CHECKED_PREFS = "Checked";

	public static final String DB_NAME_1 = "TeachersPet";
	public static final String DB_NAME_2 = "LoginDetails";

	// Constants for the students Table
	public static final String STUDENTS_TABLE_NAME = "Students";
	public static final String KEY_ROW_ID = "StudentID";
	public static final String KEY_FIRST_NAME = "FirstName";
	public static final String KEY_LAST_NAME = "LastName";
	public static final String KEY_YEAR_GROUP = "YearGroup";
	public static final String KEY_FORM_GROUP = "FormGroup";
	public static final String KEY_DOB = "DoB";
	public static final String KEY_CLASS = "Class";
	public static final String KEY_PREDICTED_GRADE = "PredictedGrade";

	// Constants for Lessons Table
	public static final String LESSONS_TABLE_NAME = "Lessons";
	public static final String KEY_PERIOD = "Period";
	public static final String KEY_DAY = "Day";
	public static final String KEY_DAY_OF_WEEK = "DayOfWeek";

	//Constants for the Attendance Table
	public static final String ATTENDANCE_TABLE_NAME = "Attendance";
	public static final String KEY_DATE = "Date";
	public static final String KEY_REG_MARK = "Mark";

	//Constants for the Assessment Table
	public static final String ASSESSMENT_TABLE_NAME = "Assessment";
	public static final String KEY_TEST_ID = "TestID";
	public static final String KEY_TEST_NAME = "TestName";
	public static final String KEY_MAX_MARKS = "MaxMarks";
	public static final String KEY_A_PERCENT = "APercent";
	public static final String KEY_B_PERCENT = "BPercent";
	public static final String KEY_C_PERCENT = "CPercent";
	public static final String KEY_PASS_PERCENT = "PassPercent";

	//Constants for the Assessment Results Table
	public static final String ASSESSMENT_RESULT_TABLE_NAME = "AssessmentResults";
	public static final String KEY_GRADE = "Grade";
	public static final String KEY_PASS_FAIL = "PassFail";
	
	//Constants for the HOYs Table
	public static final String HEAD_OF_YEAR_TABLE_NAME = "HOYs";
	public static final String KEY_HOY_NAME = "HOYName";
	public static final String KEY_HOY_EMAIL = "HOYEmail";
	
	
}
