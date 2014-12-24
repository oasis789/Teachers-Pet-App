 package org.uwais.teacherspet2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentActivity extends Activity implements OnClickListener {

	private DatabaseQueries database;
	private EditText etAssessmentName, etMaxMarks, etAPercent, etBPercent,
			etCPercent, etPassPercent;
	private TextView Class, tvAssessmentNamePrompt, tvMaxMarksPrompt,
			tvAPercentPrompt, tvBPercentPrompt, tvCPercentPrompt,
			tvPassPercentPrompt;
	private Button selectDate, newAssessment;
	private BaseLesson classSelected;
	private int yr, month, day;
	private String date, assessmentName;
	private int maxMarks, aPercent, bPercent, cPercent, passPercent;
	private Bundle bundle;
	private TeachersPetApplication app;
	private SimpleDateFormat sdf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessment);
		app = (TeachersPetApplication) getApplicationContext();
		//Get the class selected
		classSelected = new BaseLesson(getIntent().getExtras().getString(
				Constants.KEY_CLASS));
		database = new DatabaseQueries(this, Constants.DB_NAME_1);
		//Set up calendar with today's date
		Calendar today = Calendar.getInstance();
		yr = today.get(Calendar.YEAR);
		month = today.get(Calendar.MONTH);
		day = today.get(Calendar.DAY_OF_MONTH);
		sdf = new SimpleDateFormat("dd/MM/yy");
		//Reference the XML Objects
		XMLReferences();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		//If the Search button is clicked
		case KeyEvent.KEYCODE_SEARCH:
			app.goToSearchActivity(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
	
	private void XMLReferences() {
		//Reference XML Object to be used in this Activity
		etAssessmentName = (EditText) findViewById(R.id.etAssessmentName);
		etMaxMarks = (EditText) findViewById(R.id.etMaxMarks);
		etAPercent = (EditText) findViewById(R.id.etA);
		etBPercent = (EditText) findViewById(R.id.etB);
		etCPercent = (EditText) findViewById(R.id.etC);
		etPassPercent = (EditText) findViewById(R.id.etPass);
		Class = (TextView) findViewById(R.id.tvAssessmentClass);
		tvAssessmentNamePrompt = (TextView) findViewById(R.id.tvAssessmentNamePrompt);
		tvMaxMarksPrompt = (TextView) findViewById(R.id.tvMaxMarksPrompt);
		tvAPercentPrompt = (TextView) findViewById(R.id.tvAPercentPrompt);
		tvBPercentPrompt = (TextView) findViewById(R.id.tvBPercentPrompt);
		tvCPercentPrompt = (TextView) findViewById(R.id.tvCPercentPrompt);
		tvPassPercentPrompt = (TextView) findViewById(R.id.tvPassPercentPrompt);
		//Set text with the class selected
		Class.setText(classSelected.getClassName());

		selectDate = (Button) findViewById(R.id.bselectDateForAssessment);
		selectDate.setOnClickListener(this);
		newAssessment = (Button) findViewById(R.id.bCreateAssessment);
		newAssessment.setOnClickListener(this);
	}
	
	//When a button is selected
	@Override
	public void onClick(View v) {
		//Switch and case is used to determine which button was pressed
		switch (v.getId()) {
		case R.id.bselectDateForAssessment:
			//Show the Date Picker Dialog
			showDialog(1);
			break;
		case R.id.bCreateAssessment:
			//Validate fields
			boolean startActivity = ValidateFields();
			if (startActivity) {
				//Save the assessment to the database
				long l = database.saveAssessment(classSelected.getClassName(), assessmentName, date,
						maxMarks, aPercent, bPercent, cPercent, passPercent);
				//If saved successfully
				if (l != -1){
					//Pass all the data on to the next Activity
					int testID = database.getLastTestID();
					bundle = new Bundle();
					bundle.putInt(Constants.KEY_TEST_ID, testID);
					bundle.putString(Constants.KEY_CLASS, classSelected.getClassName());
					bundle.putString(Constants.KEY_TEST_NAME, assessmentName);
					bundle.putString(Constants.KEY_DATE, date);
					bundle.putInt(Constants.KEY_MAX_MARKS, maxMarks);
					bundle.putInt(Constants.KEY_A_PERCENT, aPercent);
					bundle.putInt(Constants.KEY_B_PERCENT, bPercent);
					bundle.putInt(Constants.KEY_C_PERCENT, cPercent);
					bundle.putInt(Constants.KEY_PASS_PERCENT, passPercent);
					app.makeToast("Assessment has been successfully created", Toast.LENGTH_SHORT);
					//Go to Assessment Results Activity
					app.goToAssessmentResultsActivity(this, bundle);				
				}
			} else {
				//Show Prompt
				app.makeToast("Fields are not Validated", Toast.LENGTH_SHORT);
			}

			break;
		}

	}

	private boolean ValidateFields() {
		boolean startActivity = true;

		//Validate fields
		assessmentName = etAssessmentName.getText().toString();
		//Presence check
		if (assessmentName.equals("")) {
			//Make the prompt visible
			tvAssessmentNamePrompt.setVisibility(View.VISIBLE);
			startActivity = false;
		} else {
			tvAssessmentNamePrompt.setVisibility(View.INVISIBLE);
		}
		//If a date has not been selected
		if (date == null) {
			Toast.makeText(this, "Please Select a Date for the Assessment",
					Toast.LENGTH_LONG).show();
			//Show the Date Picker Dialog
			showDialog(1);
			startActivity = false;
		}

		String sMaxMarks = etMaxMarks.getText().toString();
		//Presence Check
		if (sMaxMarks.equals("")) {
			//Show Prompt
			tvMaxMarksPrompt.setVisibility(View.VISIBLE);
			startActivity = false;
		} else {
			maxMarks = Integer.parseInt(sMaxMarks);
			tvMaxMarksPrompt.setVisibility(View.INVISIBLE);
		}

		String sAPercent = etAPercent.getText().toString();
		//Presence Check
		if (sAPercent.equals("")) {
			tvAPercentPrompt.setText("Blank Field: Please enter a value");
			//Show Prompt
			tvAPercentPrompt.setVisibility(View.VISIBLE);
			startActivity = false;
		} else {
			//Range Check
			aPercent = Integer.parseInt(sAPercent);
			if (aPercent > 100) {
				//Show Prompt
				tvAPercentPrompt.setVisibility(View.VISIBLE);
				tvAPercentPrompt.setText("Please enter a valid percentage");
			} else {
				tvAPercentPrompt.setVisibility(View.INVISIBLE);
			}
		}

		String sBPercent = etBPercent.getText().toString();
		//Presence Check
		if (sBPercent.equals("")) {
			//Show Prompt
			tvBPercentPrompt.setText("Blank Field: Please enter a value");
			tvBPercentPrompt.setVisibility(View.VISIBLE);
			startActivity = false;
		} else {
			//Range Check
			bPercent = Integer.parseInt(sBPercent);
			if (bPercent >= aPercent) {
				//Show Prompt
				tvBPercentPrompt.setVisibility(View.VISIBLE);
				tvBPercentPrompt.setText("Please enter a valid percentage");
				startActivity = false;
			} else {
				tvBPercentPrompt.setVisibility(View.INVISIBLE);
			}
		}

		String sCPercent = etCPercent.getText().toString();
		//Presence Check
		if (sCPercent.equals("")) {
			//Show Prompt
			tvCPercentPrompt.setText("Blank Field: Please enter a value");
			tvCPercentPrompt.setVisibility(View.VISIBLE);
			startActivity = false;
		} else {
			//Range Check
			cPercent = Integer.parseInt(sCPercent);
			if (cPercent >= bPercent) {
				//Show Prompt
				tvCPercentPrompt.setVisibility(View.VISIBLE);
				tvCPercentPrompt.setText("Please enter a valid percentage");
				startActivity = false;
			} else {
				tvCPercentPrompt.setVisibility(View.INVISIBLE);
			}
		}

		String sPassPercent = etPassPercent.getText().toString();
		//Presence Check
		if (sPassPercent.equals("")) {
			tvPassPercentPrompt.setText("Blank Field: Please enter a value");
			tvPassPercentPrompt.setVisibility(View.VISIBLE);
			startActivity = false;
		} else {
			passPercent = Integer.parseInt(sPassPercent);
			tvPassPercentPrompt.setVisibility(View.INVISIBLE);
		}

		return startActivity;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			//Make a new Date Picker Dialog
			return new DatePickerDialog(this, mDateSetListener, yr, month, day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		//When a date is selected
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			yr = year;
			month = monthOfYear;
			day = dayOfMonth;
			Date d = new Date(yr, month, day);
			date = sdf.format(d);
			selectDate.setText(date);
			
		}
	};

}
