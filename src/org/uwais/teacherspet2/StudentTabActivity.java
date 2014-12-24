package org.uwais.teacherspet2;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StudentTabActivity extends Activity implements OnClickListener {

	private DatabaseQueries database;
	private TeachersPetApplication app;
	private AutoCompleteTextView actv;
	private TextView tvForm, tvYear, tvClass, tvDoB, tvGrade;
	private Button bSearch;
	private BaseStudent[] students;
	private String[] studentNames;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.studenttab);
		database = new DatabaseQueries(this, Constants.DB_NAME_1);
		app = (TeachersPetApplication) getApplicationContext();
		//Reference Objects from XML
		XMLReferences();
		//Query all the students from the database
		students = database.getAllStudentsNames();
		studentNames = new String[students.length];
		for (int i = 0; i < students.length; i++) {
			studentNames[i] = students[i].getName();
		}
		//Set up the Auto Complete Text View
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, studentNames);
		actv.setThreshold(2);
		actv.setAdapter(adapter);
	}
	
	

	private void XMLReferences() {
		//Reference Objects from XML
		actv = (AutoCompleteTextView) findViewById(R.id.actvStudent);
		tvForm = (TextView) findViewById(R.id.tvSFormGroup);
		tvYear = (TextView) findViewById(R.id.tvSYearGroup);
		tvClass = (TextView) findViewById(R.id.tvSClass);
		tvDoB = (TextView) findViewById(R.id.tvSDoB);
		tvGrade = (TextView) findViewById(R.id.tvSPredictedGrade);
		bSearch = (Button) findViewById(R.id.bSearchStudent);
		bSearch.setOnClickListener(this);
	}

	//When a button is clicked
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bSearchStudent:
			//Presence Check
			if (actv.getText().toString().equals("")) {
				app.makeToast("Please enter a name", Toast.LENGTH_SHORT);
			} else {
				//Get the position of the selected name
				int position = -1;
				for (int i = 0; i < studentNames.length; i++) {
					if (actv.getText().toString().equals(studentNames[i])) {
						position = i;
					}
				}
				try {
					//Query database from info about the selected student
					Student s = database.getStudentDetails(
							students[position].getStudentID(),
							students[position].getFirstName(),
							students[position].getLastName());
					//Set up the Text Views to show the information
					tvForm.setText(s.getFormGroup());
					tvForm.setGravity(Gravity.CENTER_HORIZONTAL);
					tvYear.setText(Integer.toString(s.getYearGroup()));
					tvYear.setGravity(Gravity.CENTER_HORIZONTAL);
					tvClass.setText(s.getClassName());
					tvClass.setGravity(Gravity.CENTER_HORIZONTAL);
					tvDoB.setText(s.getDateOfBirth());
					tvDoB.setGravity(Gravity.CENTER_HORIZONTAL);
					tvGrade.setText(s.getPredictedGrade());
					tvGrade.setGravity(Gravity.CENTER_HORIZONTAL);
					//Catch if position = -1
				} catch (IndexOutOfBoundsException i) {
					//Show Prompt
					app.makeToast("Invalid Entry!!!", Toast.LENGTH_SHORT);					
				}

			}

			break;

		}

	}

}
