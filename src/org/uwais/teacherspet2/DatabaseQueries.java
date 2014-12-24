package org.uwais.teacherspet2;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseQueries {

	private DatabaseHelper dbhelp;
	private String[] SELECT;
	private String WHERE;
	private String TABLE_NAME;
	private String ORDERBY;
	private SQLiteDatabase database;

	// Initiate a dbhelper to allow use of the DB
	public DatabaseQueries(Context context, String databaseName) {

		dbhelp = new DatabaseHelper(context, databaseName);
		try {
			dbhelp.createDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			dbhelp.openDataBase();
			database = dbhelp.getDatabase();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	public void closeDB(){
		dbhelp.closeDatabase();
	}

	// Queries for Login Details Table
	// Query the LoginDetails Table to retrieve the password for the supplied
	// username
	public String getPassword(String username) {
		SELECT = new String[] { "Username", "Password" };
		WHERE = "Username='" + username + "'";
		TABLE_NAME = "Details";
		String password;
		Cursor cursor = database.query(TABLE_NAME, SELECT, WHERE, null, null,
				null, null);
		int iPass = cursor.getColumnIndex("Password");
		if (cursor.moveToFirst()) {
			password = cursor.getString(iPass);
			cursor.close();
			return password;
		} else {
			cursor.close();
			return null;
		}
	}

	public BaseStudent[] getAllStudentsNames() {
		SELECT = new String[] { Constants.KEY_ROW_ID, Constants.KEY_FIRST_NAME,
				Constants.KEY_LAST_NAME };
		Cursor cursor = database.query(Constants.STUDENTS_TABLE_NAME, SELECT,
				null, null, null, null, null);
		int iID = cursor.getColumnIndex(Constants.KEY_ROW_ID);
		int iFirstName = cursor.getColumnIndex(Constants.KEY_FIRST_NAME);
		int iLastName = cursor.getColumnIndex(Constants.KEY_LAST_NAME);

		if (cursor.moveToFirst()) {
			BaseStudent[] students = new BaseStudent[cursor.getCount()];
			for (int i = 0; i < cursor.getCount(); i++) {
				students[i] = new BaseStudent(cursor.getInt(iID),
						cursor.getString(iFirstName),
						cursor.getString(iLastName));
				cursor.moveToNext();
			}
			return students;
		} else {
			return null;
		}
	}

	public Student getStudentDetails(int id, String fName, String lName) {
		SELECT = new String[] { "*" };
		WHERE = Constants.KEY_ROW_ID + "=" + id;
		Cursor cursor = database.query(Constants.STUDENTS_TABLE_NAME, SELECT,
				WHERE, null, null, null, null);
		int iForm = cursor.getColumnIndex(Constants.KEY_FORM_GROUP);
		int iClass = cursor.getColumnIndex(Constants.KEY_CLASS);
		int iYear = cursor.getColumnIndex(Constants.KEY_YEAR_GROUP);
		int iDoB = cursor.getColumnIndex(Constants.KEY_DOB);
		int iGrade = cursor.getColumnIndex(Constants.KEY_PREDICTED_GRADE);
		if (cursor.moveToFirst()) {
			Student s = new Student(id, fName, lName, cursor.getString(iDoB),
					cursor.getInt(iYear), cursor.getString(iForm),
					cursor.getString(iClass), cursor.getString(iGrade));
			return s;
		} else {
			return null;
		}
	}

	// Queries for Students Table
	// Get a list of Student Objects for the register
	public BaseStudent[] getAllStudentsFromClass(String str_Class) {
		SELECT = new String[] { Constants.KEY_ROW_ID, Constants.KEY_FIRST_NAME,
				Constants.KEY_LAST_NAME };
		WHERE = Constants.KEY_CLASS + "='" + str_Class + "'";
		ORDERBY = Constants.KEY_LAST_NAME;
		TABLE_NAME = Constants.STUDENTS_TABLE_NAME;
		Cursor cursor = database.query(TABLE_NAME, SELECT, WHERE, null, null,
				null, ORDERBY);
		int iFirstName = cursor.getColumnIndex(Constants.KEY_FIRST_NAME);
		int iLastName = cursor.getColumnIndex(Constants.KEY_LAST_NAME);
		int iId = cursor.getColumnIndex(Constants.KEY_ROW_ID);
		if (cursor.moveToFirst()) {
			BaseStudent[] listOfStudents = new BaseStudent[cursor.getCount()];
			for (int i = 0; i < cursor.getCount(); i++) {
				String fName = cursor.getString(iFirstName);
				String lName = cursor.getString(iLastName);
				int id = cursor.getInt(iId);
				listOfStudents[i] = new BaseStudent(id, fName, lName);
				cursor.moveToNext();
			}
			return listOfStudents;
		} else {
			return null;
		}
	}

	// Get a list of classes for class selector
	public BaseLesson[] getClassList() {
		SELECT = new String[] { "DISTINCT " + Constants.KEY_CLASS };
		WHERE = Constants.KEY_CLASS + " != 'FREE' AND " + Constants.KEY_CLASS
				+ " != 'PPA'";
		TABLE_NAME = Constants.LESSONS_TABLE_NAME;
		Cursor cursor = database.query(TABLE_NAME, SELECT, WHERE, null, null,
				null, null);
		int iClass = cursor.getColumnIndex(Constants.KEY_CLASS);

		if (cursor.moveToFirst()) {
			BaseLesson[] classList = new BaseLesson[cursor.getCount()];
			for (int i = 0; i < cursor.getCount(); i++) {
				classList[i] = new BaseLesson(cursor.getString(iClass));
				cursor.moveToNext();
			}
			return classList;
		} else {
			return null;
		}
	}

	// Get the Day Of Week of the lesson
	public int getDayOfWeek(String className) {
		SELECT = new String[] { Constants.KEY_DAY_OF_WEEK };
		WHERE = Constants.KEY_CLASS + "='" + className + "'";
		TABLE_NAME = Constants.LESSONS_TABLE_NAME;
		Cursor cursor = database.query(TABLE_NAME, SELECT, WHERE, null, null,
				null, null);
		int iDoW = cursor.getColumnIndex(Constants.KEY_DAY_OF_WEEK);

		if (cursor.moveToFirst()) {
			return cursor.getInt(iDoW) + 1;
		}
		return (Integer) null;
	}

	public Cursor[] getTimetable() {
		Cursor[] cursor = new Cursor[8];
		SELECT = new String[] { Constants.KEY_CLASS };
		ORDERBY = Constants.KEY_DAY_OF_WEEK;
		for (int i = 0; i < 8; i++) {
			int period = i + 1;
			WHERE = Constants.KEY_PERIOD + "=" + period;
			cursor[i] = database.query(Constants.LESSONS_TABLE_NAME, SELECT,
					WHERE, null, null, null, ORDERBY);
		}
		return cursor;
	}

	// Method to save the register to the database
	public long saveRegister(int id, String mark, String date, String classGroup) {
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_ROW_ID, id);
		cv.put(Constants.KEY_REG_MARK, mark);
		cv.put(Constants.KEY_DATE, date);
		cv.put(Constants.KEY_CLASS, classGroup);
		return database.insert(Constants.ATTENDANCE_TABLE_NAME, null, cv);
	}

	// Method to update the register
	public int updateRegister(int id, String mark, String date,
			String classGroup) {
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_REG_MARK, mark);
		WHERE = Constants.KEY_DATE + " ='" + date + "' AND "
				+ Constants.KEY_ROW_ID + " =" + id;
		int result = database.update(Constants.ATTENDANCE_TABLE_NAME, cv,
				WHERE, null);
		if (result != 1) {
			return (int) saveRegister(id, mark, date, classGroup);
		} else {
			return result;
		}
	}

	public String getRegistrationMarks(int id, String date) {
		SELECT = new String[] { Constants.KEY_REG_MARK };
		WHERE = Constants.KEY_DATE + " ='" + date + "' AND "
				+ Constants.KEY_ROW_ID + " =" + id;
		ORDERBY = Constants.KEY_ROW_ID;
		Cursor cursor = database.query(Constants.ATTENDANCE_TABLE_NAME, SELECT,
				WHERE, null, null, null, ORDERBY);
		int iMark = cursor.getColumnIndex(Constants.KEY_REG_MARK);
		if (cursor.moveToFirst()) {
			return cursor.getString(iMark);
		} else {
			return null;
		}
	}

	public long saveAssessment(String classGroup, String testName, String date,
			int maxMarks, int aPercent, int bPercent, int cPercent,
			int passPercent) {
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_CLASS, classGroup);
		cv.put(Constants.KEY_TEST_NAME, testName);
		cv.put(Constants.KEY_DATE, date);
		cv.put(Constants.KEY_MAX_MARKS, maxMarks);
		cv.put(Constants.KEY_A_PERCENT, aPercent);
		cv.put(Constants.KEY_B_PERCENT, bPercent);
		cv.put(Constants.KEY_C_PERCENT, cPercent);
		cv.put(Constants.KEY_PASS_PERCENT, passPercent);
		return database.insert(Constants.ASSESSMENT_TABLE_NAME, null, cv);
	}

	public int getLastTestID() {
		String query = "SELECT " + Constants.KEY_TEST_ID + " from "
				+ Constants.ASSESSMENT_TABLE_NAME + " order by "
				+ Constants.KEY_TEST_ID + " DESC limit 1";
		Cursor c = database.rawQuery(query, null);
		if (c != null && c.moveToFirst()) {
			return c.getInt(0); // The 0 is the column index, we only have 1
								// column, so the index is 0
		} else {
			return 0;
		}
	}

	public BaseAssessment[] getAssessmentsForClass(String classGroup) {
		SELECT = new String[] { Constants.KEY_TEST_ID, Constants.KEY_TEST_NAME };
		WHERE = Constants.KEY_CLASS + " ='" + classGroup + "'";
		ORDERBY = Constants.KEY_TEST_NAME;
		Cursor cursor = database.query(Constants.ASSESSMENT_TABLE_NAME, SELECT,
				WHERE, null, null, null, ORDERBY);
		int iName = cursor.getColumnIndex(Constants.KEY_TEST_NAME);
		int iID = cursor.getColumnIndex(Constants.KEY_TEST_ID);
		if (cursor.moveToFirst()) {
			BaseAssessment[] list = new BaseAssessment[cursor.getCount()];
			for (int i = 0; i < cursor.getCount(); i++) {
				list[i] = new BaseAssessment(cursor.getInt(iID),
						cursor.getString(iName), classGroup);
				cursor.moveToNext();
			}
			return list;
		} else {
			return null;
		}
	}

	public Assessment getAssessmentDetails(BaseAssessment assessment) {
		SELECT = new String[] { Constants.KEY_DATE, Constants.KEY_MAX_MARKS,
				Constants.KEY_A_PERCENT, Constants.KEY_B_PERCENT,
				Constants.KEY_C_PERCENT, Constants.KEY_PASS_PERCENT };
		WHERE = Constants.KEY_TEST_ID + " =" + assessment.getTestID();
		Cursor cursor = database.query(Constants.ASSESSMENT_TABLE_NAME, SELECT,
				WHERE, null, null, null, null);
		int iDate = cursor.getColumnIndex(Constants.KEY_DATE);
		int iA = cursor.getColumnIndex(Constants.KEY_A_PERCENT);
		int iB = cursor.getColumnIndex(Constants.KEY_B_PERCENT);
		int iC = cursor.getColumnIndex(Constants.KEY_C_PERCENT);
		int iPass = cursor.getColumnIndex(Constants.KEY_PASS_PERCENT);
		int iMax = cursor.getColumnIndex(Constants.KEY_MAX_MARKS);
		if (cursor.moveToFirst()) {
			Assessment a = new Assessment(assessment.getTestID(),
					assessment.getClassGroup(), assessment.getTestName(),
					cursor.getString(iDate), cursor.getInt(iMax),
					cursor.getInt(iA), cursor.getInt(iB), cursor.getInt(iC),
					cursor.getInt(iPass));
			return a;
		} else {
			return null;
		}
	}

	public Results getAssessmentResults(int testID, int studentID) {
		SELECT = new String[] { Constants.KEY_REG_MARK, Constants.KEY_GRADE,
				Constants.KEY_PASS_FAIL };
		WHERE = Constants.KEY_TEST_ID + " =" + testID + " AND "
				+ Constants.KEY_ROW_ID + " =" + studentID;
		Cursor cursor = database.query(Constants.ASSESSMENT_RESULT_TABLE_NAME,
				SELECT, WHERE, null, null, null, null);
		int iMark = cursor.getColumnIndex(Constants.KEY_REG_MARK);
		int iGrade = cursor.getColumnIndex(Constants.KEY_GRADE);
		int iPass = cursor.getColumnIndex(Constants.KEY_PASS_FAIL);

		if (cursor.moveToFirst()) {
			Results results = new Results(testID, studentID,
					cursor.getInt(iMark), cursor.getString(iGrade),
					cursor.getString(iPass));
			return results;
		} else {
			return null;
		}
	}

	public Results[] getAssessmentResultsForClass(int testID, String classGroup) {
		BaseStudent[] listOfStudents = getAllStudentsFromClass(classGroup);
		if (listOfStudents != null) {
			Results[] results = new Results[listOfStudents.length];
			for (int i = 0; i < listOfStudents.length; i++) {
				results[i] = getAssessmentResults(testID,
						listOfStudents[i].getStudentID());
			}
			return results;
		} else {
			return null;
		}
	}
	
	public int updateAssessmentResults(int testID, int studentID, int mark, String grade, String passFail){
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_REG_MARK, mark);
		cv.put(Constants.KEY_GRADE, grade);
		cv.put(Constants.KEY_PASS_FAIL, passFail);
		WHERE = Constants.KEY_TEST_ID + "=" + testID + " AND " + Constants.KEY_ROW_ID + "=" + studentID;
		int result = database.update(Constants.ASSESSMENT_RESULT_TABLE_NAME, cv, WHERE, null);
		if (result != 1){
			return (int) SaveAssessmentResults(testID, studentID, mark, grade, passFail);
		}else{
			return result;
		}
	}

	public long SaveAssessmentResults(int testID, int studentID, int mark,
			String grade, String passFail) {
		ContentValues cv = new ContentValues();
		cv.put(Constants.KEY_TEST_ID, testID);
		cv.put(Constants.KEY_ROW_ID, studentID);
		cv.put(Constants.KEY_REG_MARK, mark);
		cv.put(Constants.KEY_GRADE, grade);
		cv.put(Constants.KEY_PASS_FAIL, passFail);
		return database
				.insert(Constants.ASSESSMENT_RESULT_TABLE_NAME, null, cv);
	}
	
	public HeadOfYear getHeadOfYear(int yearGroup){
		SELECT = new String[]{Constants.KEY_HOY_NAME, Constants.KEY_HOY_EMAIL};
		WHERE = Constants.KEY_YEAR_GROUP + " = " + yearGroup;
		Cursor cursor = database.query(Constants.HEAD_OF_YEAR_TABLE_NAME, SELECT, WHERE, null, null, null, null);
		int iName = cursor.getColumnIndex(Constants.KEY_HOY_NAME);
		int iEmail = cursor.getColumnIndex(Constants.KEY_HOY_EMAIL);
		if(cursor.moveToFirst()){
			HeadOfYear hoy = new HeadOfYear(cursor.getString(iName), yearGroup, cursor.getString(iEmail));
			return hoy;
		}else{
			return null;
		}
	}

}
