package org.uwais.teacherspet2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentTabActivity extends Activity implements OnClickListener {

	private DatabaseQueries database;
	private TeachersPetApplication app;
	private AutoCompleteTextView actv;
	private TextView tvDate, tvMaxMarks, tvA, tvB, tvC, tvPass, tvClass;
	private Button bSearch;
	private String classSelected;
	private BaseAssessment[] assessments;
	private String[] testNames;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Starts the Class Selector Activity
		startActivityForResult(
				new Intent(this, ClassSelectorActivity.class).putExtra(
						"Button", "Search"), 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assessmenttab);
		database = new DatabaseQueries(this, Constants.DB_NAME_1);
		app = (TeachersPetApplication) getApplicationContext();
		// Reference Objects from XML
		XMLReferences();

	}

	// When a Class has been selected through the Class Selector Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// If the selection of the class has been successful
		if (resultCode == RESULT_OK) {
			classSelected = data.getExtras().getString(Constants.KEY_CLASS);
			// Set the text to the Class Selected
			tvClass.setText(classSelected);
			// Query the database for the Assessments of the Selected Class
			assessments = database.getAssessmentsForClass(classSelected);
			try {
				// Create a String Array with the names of all the assessments
				testNames = new String[assessments.length];
				for (int i = 0; i < assessments.length; i++) {
					testNames[i] = assessments[i].getTestName();
				}
				// Set up the Auto Complete Text View
				adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_dropdown_item_1line, testNames);
				actv.setThreshold(2);
				actv.setAdapter(adapter);
				// Catch if there are no assessments for the class
			} catch (NullPointerException e) {
				app.makeToast("There are no assessments for this class!!",
						Toast.LENGTH_SHORT);
				finish();
			}
		}
	}

	private void XMLReferences() {
		// References the Objects from XML
		actv = (AutoCompleteTextView) findViewById(R.id.actvAssessment);
		tvDate = (TextView) findViewById(R.id.tvSDate);
		tvMaxMarks = (TextView) findViewById(R.id.tvSMaxMarks);
		tvA = (TextView) findViewById(R.id.tvSA);
		tvB = (TextView) findViewById(R.id.tvSB);
		tvC = (TextView) findViewById(R.id.tvSC);
		tvPass = (TextView) findViewById(R.id.tvSPass);
		tvClass = (TextView) findViewById(R.id.tvSAClass);

		bSearch = (Button) findViewById(R.id.bSearchAssessment);
		bSearch.setOnClickListener(this);
	}

	// When a Button is clicked
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bSearchAssessment:
			// Presence Check
			if (actv.getText().toString().equals("")) {
				app.makeToast("Please enter an assessment name",
						Toast.LENGTH_SHORT);
			} else {
				// Checks to see if the assessment exists for the Selected Class
				// If not position = -1
				int position = -1;
				for (int i = 0; i < testNames.length; i++) {
					if (actv.getText().toString().equals(testNames[i])) {
						position = i;
					}
				}
				try {
					// Queries the database to get the assessment details of the
					// chosen assessment
					Assessment a = database
							.getAssessmentDetails(assessments[position]);
					// Sets up the Text Views with the corresponding data
					tvDate.setText(a.getDate());
					tvDate.setGravity(Gravity.CENTER_HORIZONTAL);
					tvMaxMarks.setText(Integer.toString(a.getMaxMarks()));
					tvMaxMarks.setGravity(Gravity.CENTER_HORIZONTAL);
					tvA.setText(Integer.toString(a.getaPercent()));
					tvA.setGravity(Gravity.CENTER_HORIZONTAL);
					tvB.setText(Integer.toString(a.getbPercent()));
					tvB.setGravity(Gravity.CENTER_HORIZONTAL);
					tvC.setText(Integer.toString(a.getcPercent()));
					tvC.setGravity(Gravity.CENTER_HORIZONTAL);
					tvPass.setText(Integer.toString(a.getPassPercent()));
					tvPass.setGravity(Gravity.CENTER_HORIZONTAL);
					// Catches if position = -1
				} catch (IndexOutOfBoundsException i) {
					// Shows Prompt
					app.makeToast("Invalid Entry!!!", Toast.LENGTH_SHORT);
				}

			}

			break;
		}

	}

}
