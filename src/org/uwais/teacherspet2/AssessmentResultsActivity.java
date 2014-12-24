package org.uwais.teacherspet2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentResultsActivity extends Activity implements
		OnClickListener {

	private Bundle bundle;
	private TextView tvClass, tvName;
	private TableLayout tblAssessment;
	private Button saveAssessmentMarks;
	private TableRow heading;
	private EditText[] marks;
	private TextView[] displayNames, tvPercent, tvGrade, tvPassFail;
	private TableRow[] trContents;
	private BaseStudent[] listOfStudents;
	private DatabaseQueries database;
	private int dp5, dp20;
	private TeachersPetApplication app;
	private Assessment assessment;
	private Results[] results;
	private TableRow trAverage;
	private TextView averageHeader;
	private TextView averageMark;
	private TextView averagePercent;
	private TextView averageGrade;
	private TextView averagePassFail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessmentresults);
		app = (TeachersPetApplication) getApplicationContext();
		// Set up values for density pixels
		dp5 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 5, getResources().getDisplayMetrics());
		dp20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 20, getResources().getDisplayMetrics());
		// Retrieve info about the assessment selected
		getInfoFromBundle();
		database = new DatabaseQueries(this, Constants.DB_NAME_1);
		// Query database for a list of Students from the selected class
		listOfStudents = database.getAllStudentsFromClass(assessment
				.getClassGroup());
		// Query database to check if there is results stored for the assessment
		results = database.getAssessmentResultsForClass(assessment.getTestID(),
				assessment.getClassGroup());
		// Reference Objects from XML
		XMLReferences();

		try {
			// Initialize arrays
			displayNames = new TextView[listOfStudents.length];
			tvPercent = new TextView[listOfStudents.length];
			tvGrade = new TextView[listOfStudents.length];
			tvPassFail = new TextView[listOfStudents.length];
			trContents = new TableRow[listOfStudents.length];
			marks = new EditText[listOfStudents.length];
			// Set up the Names Column
			setUpNamesLayout();
			// If there are already results stored
			setupAverageHeader();
			setUpMarkLayout();
			// Catch if there are no students in this class

		} catch (NullPointerException e) {
			app.makeToast("There are no students for this Class!!!",
					Toast.LENGTH_SHORT);
			// Close the Activity
			finish();
		}

	}

	private String getPassFail(int percent) {
		if (percent >= assessment.getPassPercent()) {
			return "Pass";
		} else {
			return "Fail";
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		//If the search key was pressed
		case KeyEvent.KEYCODE_SEARCH:
			app.goToSearchActivity(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	private void setUpMarkLayout() {
		// If the student's results are stored already
		setupHeadings();
		int average = 0;
		for (int i = 0; i < listOfStudents.length; i++) {
			marks[i] = new EditText(this);
			marks[i].setLayoutParams(new TableRow.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// Set the Imput Type of the edit boxes to only Numbers
			marks[i].setPadding(dp5, dp5, dp5, dp5);
			marks[i].setInputType(InputType.TYPE_CLASS_NUMBER);
			// Set up the maximum length of input: 3 characters
			int maxLength = 3;
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(maxLength);
			marks[i].setFilters(FilterArray);
			trContents[i].addView(marks[i]);
			setUpLayout(i);
			if (results[i] != null) {
				average = average + results[i].getMark();
				marks[i].setText((Integer.toString(results[i].getMark())));
				int percent = (int) (((double) results[i].getMark() / assessment
						.getMaxMarks()) * 100);
				alterLayout(i, percent, results[i].getGrade(),
						results[i].getPassFail());
			}
		}
		if (average != 0) {
			average = average / listOfStudents.length;
			int percent = (int) (((double) average / assessment.getMaxMarks()) * 100);
			setupAverageRow(average, percent, CalculateGrade(percent),
					getPassFail(percent));
		}
	}

	private void setUpNamesLayout() {
		for (int i = 0; i < listOfStudents.length; i++) {
			// Set up the Names Column
			trContents[i] = new TableRow(this);
			displayNames[i] = new TextView(this);
			displayNames[i].setText(listOfStudents[i].getName());
			displayNames[i].setTextSize(15);
			displayNames[i].setPadding(dp5, dp5, dp5, dp5);
			trContents[i].addView(displayNames[i]);
			// Set up the marks for this student, i
			tblAssessment.addView(trContents[i]);
		}
	}

	private boolean ValidateEditTexts() {
		// Validate edit text boxes
		boolean saveMarks = true;
		for (int i = 0; i < listOfStudents.length; i++) {
			String sMarks = marks[i].getText().toString();
			// Presence Check
			if (sMarks.equals("")) {
				saveMarks = false;
				// Show Prompt
				marks[i].setHint("00");
				marks[i].setHintTextColor(Color.RED);
			} else {
				int mark = Integer.parseInt(sMarks);
				// Range Check
				if (mark > assessment.getMaxMarks()) {
					// Show Prompt
					marks[i].setText("");
					marks[i].setHint(Integer.toString(mark));
					marks[i].setHintTextColor(Color.RED);
					app.makeToast(
							"Mark is higher than Maximum Marks for this Assessment",
							Toast.LENGTH_SHORT);
					saveMarks = false;
				}
			}
		}
		return saveMarks;
	}

	private void XMLReferences() {
		// Reference XML Objects
		tvClass = (TextView) findViewById(R.id.tvAssessmentMarksClass);
		tblAssessment = (TableLayout) findViewById(R.id.tblAssessmentMarks);
		saveAssessmentMarks = (Button) findViewById(R.id.bSaveAssessmentMarks);
		heading = (TableRow) findViewById(R.id.trAssessmentResultsHeading);
		tvName = (TextView) findViewById(R.id.tvAssessmentName);
		saveAssessmentMarks.setOnClickListener(this);
		tvClass.setText(assessment.getClassGroup());
		tvName.setText(assessment.getTestName());
	}

	private void getInfoFromBundle() {
		// Create assessment with the information passed from
		// the previous Activity in the bundle
		bundle = getIntent().getExtras();
		assessment = new Assessment(bundle);
	}

	// When a button is clicked
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bSaveAssessmentMarks:
			// If the fields are validated
			if (ValidateEditTexts()) {
				int average = 0;
				long result = 0;
				int percent = 0;
				String grade = null, passFail = null;

				for (int i = 0; i < listOfStudents.length; i++) {
					// Calculate Average, Grade, Pass Fail and Percentage
					int mark = Integer.parseInt(marks[i].getText().toString());
					average = average + mark;
					percent = (int) (((double) mark / assessment.getMaxMarks()) * 100);
					grade = CalculateGrade(percent);
					passFail = getPassFail(percent);
					// Save the student's result to the database
					result = database.SaveAssessmentResults(
							assessment.getTestID(),
							listOfStudents[i].getStudentID(), mark, grade,
							passFail);
					// If the result saved
					if (result != -1) {
						// Set up the row with the mark, percent, grade and
						// Pass/Fail
						alterLayout(i, percent, grade, passFail);
					}
				}

				if (result != -1) {
					// Set up the average row
					average = (average / listOfStudents.length);
					percent = (int) (((double) average / assessment
							.getMaxMarks()) * 100);
					setupAverageRow(average, percent, CalculateGrade(percent),
							getPassFail(percent));
					app.makeToast("Marks saved successfully",
							Toast.LENGTH_SHORT);
					// Change the Edit boxes to Text Views
				} else {
					// Show prompt if the marks are not saved
					app.makeToast("Marks have not been saved",
							Toast.LENGTH_SHORT);
				}
			} else {
				// Show prompt if fields are not validated
				app.makeToast("Fields are not validated", Toast.LENGTH_SHORT);
			}
			break;
		}
	}

	private void setupAverageHeader() {
		trAverage = new TableRow(this);
		averageHeader = new TextView(this);
		averageMark = new TextView(this);
		averagePercent = new TextView(this);
		averageGrade = new TextView(this);
		averagePassFail = new TextView(this);
		trAverage.addView(averageHeader);
		trAverage.addView(averageMark);
		trAverage.addView(averagePercent);
		trAverage.addView(averageGrade);
		trAverage.addView(averagePassFail);
		trAverage.setLayoutParams(new TableRow.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		trAverage.setPadding(0, dp20, 0, dp20);
		trAverage.setGravity(Gravity.CENTER_VERTICAL);
		tblAssessment.addView(trAverage);
	}

	private void setupAverageRow(int average, int percent, String grade,
			String passFail) {
		// Set up the Average row

		averageHeader.setText("Average");
		averageHeader.setTextSize(40);
		averageHeader.setPadding(dp5, 0, 0, 0);
		averageHeader.setTextColor(Color.GREEN);
		averageHeader.setTextSize(20);

		averageMark.setText(Integer.toString(average));
		averageMark.setGravity(Gravity.CENTER_HORIZONTAL);
		averageMark.setTextColor(Color.GREEN);
		averageMark.setTextSize(20);

		averagePercent.setText(Integer.toString(percent) + "%");
		averagePercent.setGravity(Gravity.CENTER_HORIZONTAL);
		averagePercent.setTextColor(Color.GREEN);
		averagePercent.setTextSize(20);

		averageGrade.setText(grade);
		averageGrade.setGravity(Gravity.CENTER_HORIZONTAL);
		averageGrade.setTextColor(Color.GREEN);
		averageGrade.setTextSize(20);

		averagePassFail.setText(passFail);
		averagePassFail.setGravity(Gravity.CENTER_HORIZONTAL);
		averagePassFail.setTextColor(Color.GREEN);
		averagePassFail.setTextSize(20);
	}

	private void setupHeadings() {
		// Set up the Headings Row
		TextView percentHeading = new TextView(this);
		percentHeading.setText("Percentage");
		percentHeading.setTextSize(20);
		percentHeading.setPadding(dp20, 0, 0, 0);
		heading.addView(percentHeading);

		TextView gradeHeading = new TextView(this);
		gradeHeading.setText("Grade");
		gradeHeading.setTextSize(20);
		gradeHeading.setPadding(dp20, 0, 0, 0);
		heading.addView(gradeHeading);

		TextView passFailHeading = new TextView(this);
		passFailHeading.setText("Pass/Fail");
		passFailHeading.setTextSize(20);
		passFailHeading.setPadding(dp20, 0, 0, 0);
		heading.addView(passFailHeading);
	}

	private void alterLayout(int i, int percent, String grade, String passFail) {
		// Set up the student i's row with their percent, grade and pass/Fail
		tvPercent[i].setText(Integer.toString(percent) + "%");
		tvPercent[i].setGravity(Gravity.CENTER_HORIZONTAL);
		tvGrade[i].setText(grade);
		tvGrade[i].setGravity(Gravity.CENTER_HORIZONTAL);
		tvPassFail[i].setText(passFail);
		tvPassFail[i].setGravity(Gravity.CENTER_HORIZONTAL);
	}

	private void setUpLayout(int i) {
			tvPercent[i] = new TextView(this);
			tvGrade[i] = new TextView(this);
			tvPassFail[i] = new TextView(this);
			trContents[i].addView(tvPercent[i]);
			trContents[i].addView(tvGrade[i]);
			trContents[i].addView(tvPassFail[i]);
	}

	private String CalculateGrade(int percent) {
		// Caluclate the student's grade using their percentage
		if (percent >= assessment.getaPercent()) {
			return "A";
		} else {
			if (percent >= assessment.getbPercent()) {
				return "B";
			} else {
				if (percent >= assessment.getcPercent()) {
					return "C";
				} else {
					return "D";
				}
			}
		}
	}

}
