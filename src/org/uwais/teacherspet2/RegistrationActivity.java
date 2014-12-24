package org.uwais.teacherspet2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnClickListener {

	private TeachersPetApplication app;
	private DatabaseQueries database;
	private BaseStudent[] listOfStudents;
	private BaseLesson classSelected;
	private String[] regMark = new String[] { "/ - Present", "O - Absent",
			"L - Late" };
	private TableLayout tbl;
	private TableRow[] trcontents;
	private TextView[] displayNames;
	private TextView[][] displayRegMark;
	private Spinner[] spinners;
	private int dp5, dp10;
	private ArrayAdapter<String> adapter;
	private Button save;
	private TextView classSelectedHeading;
	private TabHost tabHost;
	private LinearLayout tab1, tab2, tab3;
	private int doWLesson;
	private boolean lessonToday, regSavedToday;
	private String dateLastSaved;
	private Calendar cal;
	private SharedPreferences prefs;
	private SimpleDateFormat sdf;
	private List<String> absentees;
	private CharSequence[] charAbsentees;
	private int randomStudent;
	private Activity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		context = this;
		// Set up calendar with today's date
		sdf = new SimpleDateFormat("dd/MM/yy");
		// Set up values for density pixels
		dp5 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 5, getResources().getDisplayMetrics());
		dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 10, getResources().getDisplayMetrics());
		database = new DatabaseQueries(this, Constants.DB_NAME_1);
		// Get Selected Class
		classSelected = new BaseLesson(getIntent().getExtras().getString(
				Constants.KEY_CLASS));
		// Query database for list of students from the selected class
		listOfStudents = database.getAllStudentsFromClass(classSelected
				.getClassName());
		// Get the Day Of week of the Selected Class
		doWLesson = database.getDayOfWeek(classSelected.getClassName());
		cal = Calendar.getInstance();
		// If today is the day of the lesson
		if (doWLesson == (cal.get(Calendar.DAY_OF_WEEK))) {
			lessonToday = true;
		}

		app = (TeachersPetApplication) getApplicationContext();
		prefs = app.getAppPreferences();
		// Get preferences
		dateLastSaved = prefs.getString("dateLastSaved", "00/00/00");
		// If the last time the register was saved is today
		if (dateLastSaved.equals(getTodaysDate())) {
			regSavedToday = true;
		}
		// Reference objects from XML
		XMLReferences();
		try {
			// Initialize array
			trcontents = new TableRow[listOfStudents.length];
			displayNames = new TextView[listOfStudents.length];
			displayRegMark = new TextView[listOfStudents.length + 1][3];
			// set up spinners
			spinners = new Spinner[listOfStudents.length];
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, regMark);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Set up the names row
			setupNamesLayout();
			setupRegLayout();
			// Find those students who have been
			// absent for 3 consecutive lessons
			absentees = FindConsecutiveAbsentees();
			if (absentees.size() != 0) {
				charAbsentees = new CharSequence[absentees.size()];
				for (int i = 0; i < absentees.size(); i++) {
					charAbsentees[i] = absentees.get(i);
				}
				showDialog(0);
			}
			// Catch if there are no students in the class
		} catch (NullPointerException e) {
			app.makeToast("There are no students for this class!!!",
					Toast.LENGTH_SHORT);
			// Close the Activity
			finish();
		}

	}

	private void CreateMenu(Menu menu) {
		MenuItem mnu1 = menu.add(0, 0, 0, "Choose a random student");
	}

	private boolean MenuChoice(MenuItem menu) {
		switch (menu.getItemId()) {
		case 0:
			Random randomNum = new Random();
			randomStudent = randomNum.nextInt(listOfStudents.length);
			while (displayRegMark[randomStudent][0].getText().toString().equals("O")){
				randomStudent = randomNum.nextInt(listOfStudents.length);
			}
			showDialog(1);
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new AlertDialog.Builder(this)
					.setTitle(
							"The following students have missed 3 lessons in a row")
					.setItems(charAbsentees, null)
					.setPositiveButton("Email Head Of Year",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Student student = database
											.getStudentDetails(
													listOfStudents[0]
															.getStudentID(),
													listOfStudents[0]
															.getFirstName(),
													listOfStudents[0]
															.getLastName());
									HeadOfYear hoy = database
											.getHeadOfYear(student
													.getYearGroup());

									String[] dates = new String[] {
											getDateLastLesson(0),
											(getDateLastLesson(1)),
											getDateLastLesson(2) };
									app.SendEmailForConsecutiveAbsences(
											context,
											new String[] { hoy.getEmail() },
											charAbsentees,
											classSelected.getClassName(), dates);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).create();
		case 1:
			return new AlertDialog.Builder(this)
					.setTitle("Random Student has been selected")
					.setItems(
							new CharSequence[] { listOfStudents[randomStudent]
									.getName() },
							null)
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									removeDialog(1);
								}
							}).create();
		}
		return null;
	}

	// If the search key is pressed
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_SEARCH:
			// Goto search activity
			app.goToSearchActivity(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// Set up the layout for the register
	private void setupRegLayout() {
		// Remove anything from the tabs
		tab1.removeAllViews();
		tab2.removeAllViews();
		tab3.removeAllViews();

		// 3 times for the last 3 lessons
		for (int j = 0; j < 3; j++) {
			// get the date of the last lesson, i weeks ago
			String dateLastLesson = getDateLastLesson(j);
			for (int i = 0; i < listOfStudents.length; i++) {
				// Query the database to get the students register mark
				String mark = database.getRegistrationMarks(
						listOfStudents[i].getStudentID(), dateLastLesson);
				// Set up the Text Views to show the marks
				displayRegMark[i][j] = new TextView(this);
				displayRegMark[i][j].setText(mark);
				displayRegMark[i][j].setTextSize(15);
				displayRegMark[i][j].setPadding(dp5, dp10, dp5, 0);
				// Chooses which tab to display it under
				switch (j) {
				case 0:
					tab1.addView(displayRegMark[i][j]);
					break;
				case 1:
					tab2.addView(displayRegMark[i][j]);
					break;
				case 2:
					tab3.addView(displayRegMark[i][j]);
					break;
				}

			}

		}

	}

	private List<String> FindConsecutiveAbsentees() {
		List<String> absentees = new ArrayList<String>();
		for (int i = 0; i < listOfStudents.length; i++) {
			int absents = 0;
			for (int j = 0; j < 3; j++) {
				if (displayRegMark[i][j].getText().toString().equals("O")) {
					absents = absents + 1;
				}
			}
			if (absents == 3) {
				displayNames[i].setTextColor(Color.RED);
				absentees.add(listOfStudents[i].getName());
			}
		}
		return absentees;
	}

	private void setupNamesLayout() {
		// Set up the names column
		for (int i = 0; i < listOfStudents.length; i++) {
			trcontents[i] = new TableRow(this);
			displayNames[i] = new TextView(this);
			displayNames[i].setText(listOfStudents[i].getName());
			displayNames[i].setTextSize(15);
			displayNames[i].setPadding(dp5, dp5, dp5, dp5);
			// Set up the spinners
			spinners[i] = new Spinner(this);
			spinners[i].setLayoutParams(new TableRow.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			spinners[i].setPadding(dp5, dp5, dp5, dp5);
			spinners[i].setAdapter(adapter);
			spinners[i].setBackgroundDrawable(getResources().getDrawable(
					R.drawable.custom_spinner));
			trcontents[i].addView(displayNames[i]);
			trcontents[i].addView(spinners[i]);
			tbl.addView(trcontents[i]);
		}
	}

	// Gets todays date
	private String getTodaysDate() {
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}

	private void XMLReferences() {
		// References Objects from XML
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tbl = (TableLayout) findViewById(R.id.tblReg);
		save = (Button) findViewById(R.id.bSaveReg);
		classSelectedHeading = (TextView) findViewById(R.id.tvRegClass);
		classSelectedHeading.setText(classSelected.getClassName());
		tab1 = (LinearLayout) findViewById(R.id.tab1);
		tab2 = (LinearLayout) findViewById(R.id.tab2);
		tab3 = (LinearLayout) findViewById(R.id.tab3);
		tabHost.setup();
		// If there is not a lesson today OR the register has already been taken
		if (lessonToday == false | regSavedToday == true) {
			// Changes the Text of the button
			save.setText("Edit Registration");
		}
		// Sets up the 3 tabs
		TabSpec specs = tabHost.newTabSpec("tag 1");
		specs.setContent(R.id.tab1);
		specs.setIndicator(getDateLastLesson(0));
		tabHost.addTab(specs);
		specs = tabHost.newTabSpec("tag 2");
		specs.setContent(R.id.tab2);
		specs.setIndicator(getDateLastLesson(1));
		tabHost.addTab(specs);
		specs = tabHost.newTabSpec("tag 3");
		specs.setContent(R.id.tab3);
		specs.setIndicator(getDateLastLesson(2));
		tabHost.addTab(specs);
		save.setOnClickListener(this);
	}

	// Gets the date of the last lesson, i weeks ago
	private String getDateLastLesson(int week) {
		Calendar now = Calendar.getInstance();
		// Gets todays day of the month
		int day = now.get(Calendar.DAY_OF_MONTH);
		Calendar cal = new GregorianCalendar(now.get(Calendar.YEAR),
				now.get(Calendar.MONTH), day);
		// if today is not the day of the lesson
		if ((doWLesson) != now.get(Calendar.DAY_OF_WEEK)) {
			week++;
		}
		// Go back a week to find the last Lesson
		cal.add(Calendar.WEEK_OF_YEAR, -week);
		// Add a day until you find the Day of the lesson
		while (cal.get(Calendar.DAY_OF_WEEK) != (doWLesson)) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		// Return the date of the last lesson, i weeks ago
		return sdf.format(cal.getTime());
	}

	// Returns the mark from the spinner, i
	private String getMark(int i) {
		int pos = spinners[i].getSelectedItemPosition();
		String data = null;
		switch (pos) {
		case 0:
			data = "/";
			break;
		case 1:
			data = "O";
			break;
		case 2:
			data = "L";
			break;
		}
		return data;
	}

	// When the button is clicked
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bSaveReg:
			String tag = tabHost.getCurrentTabTag();
			String date = getDateLastLesson(0);
			long saved = 0;

			if (tag.equals("tag 1")) {
				// If there is a lesson today AND the register hasn't already
				// been taken
				if (lessonToday && (regSavedToday == false)) {
					// Save the register for the whole class
					for (int i = 0; i < listOfStudents.length; i++) {
						saved = database.saveRegister(
								listOfStudents[i].getStudentID(), getMark(i),
								date, classSelected.getClassName());
					}
					// if it saved successfully
					if (saved != -1) {
						// Change system preferences
						regSavedToday = true;
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("dateLastSaved", date);
						editor.commit();
						// Show Prompt
						app.makeToast("Registration Saved Successfully",
								Toast.LENGTH_SHORT);
					} else {
						// Show prompt
						app.makeToast("Registration has not been saved",
								Toast.LENGTH_SHORT);
					}

				} else {
					// Update the previous lesson register
					for (int i = 0; i < listOfStudents.length; i++) {
						saved = database.updateRegister(
								listOfStudents[i].getStudentID(), getMark(i),
								date, classSelected.getClassName());
					}

					if (saved != -1) {
						// Show prompt
						app.makeToast(
								"Registration has been updated Successfully",
								Toast.LENGTH_SHORT);
					} else {
						// Show prompt
						app.makeToast("Registration has not been updated",
								Toast.LENGTH_SHORT);
					}

				}

			} else {
				// Update the register for the previous lesson
				if (tag.equals("tag 2")) {
					date = getDateLastLesson(1);
				} else {
					if (tag.equals("tag 3")) {
						date = getDateLastLesson(2);
					}
				}
				for (int i = 0; i < listOfStudents.length; i++) {
					saved = database.updateRegister(
							listOfStudents[i].getStudentID(), getMark(i), date,
							classSelected.getClassName());
				}
				if (saved != -1) {
					// Show prompt
					app.makeToast("Registration has been updated Successfully",
							Toast.LENGTH_SHORT);
				} else {
					// Show prompt
					app.makeToast("Registration has not been updated",
							Toast.LENGTH_SHORT);
				}

			}
			// Show the saved register in the layout
			setupRegLayout();
			break;
		}
	}
}
