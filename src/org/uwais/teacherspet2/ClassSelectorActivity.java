package org.uwais.teacherspet2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ClassSelectorActivity extends ListActivity {

	private BaseLesson[] classList;
	private String[] classes, names;
	private DatabaseQueries database;
	private TeachersPetApplication app;
	private String button;
	private BaseAssessment[] list;
	private int num;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		app = (TeachersPetApplication) getApplicationContext();
		database = new DatabaseQueries(this, Constants.DB_NAME_1);
		// Queries the databse for a list of classes
		classList = database.getClassList();
		classes = new String[classList.length];
		// Gets the button which was pressed to starts this Activity
		button = getIntent().getExtras().getString("Button");
		for (int i = 0; i < classList.length; i++) {
			classes[i] = classList[i].getClassName();
		}

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, classes));
	}

	// When a Class is Selected
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Shows prompt with the Selected Class
		app.makeToast("You selected " + classes[position], Toast.LENGTH_SHORT);
		list = database.getAssessmentsForClass(classes[position]);
		// If the registration button was clicked
		if (button.equals("Registration")) {
			// Go to the registration Activity
			app.goToRegistrationActivity(this, classes[position]);
		} else {
			// If the assessment Button was clicked
			if (button.equals("Assessment")) {
				// Go to the assessment Activity
				app.goToAssessmentActivity(this, classes[position]);
			} else {
				// If the Assessment Results Button was clicked
				if (button.equals("Assessment Results")) {
					// Gets the assessment for the Selected Class
					try{
						// Creates a Dialog allowing the user to select an
						// Assessment
						names = new String[list.length];
						for (int i = 0; i < list.length; i++) {
							names[i] = list[i].getTestName();
						}
						// Creates the Dialog
						onCreateDialog(0);
						// Shows the dialog
						showDialog(0);
					} catch(NullPointerException e) {
						// Shows Prompt
						app.makeToast("No assessments found",
								Toast.LENGTH_SHORT);
					}

				} else {
					// If the Search button was clicked
					if (button.equals("Search")) {
						Intent data = new Intent();
						data.putExtra(Constants.KEY_CLASS, classes[position]);
						setResult(RESULT_OK, data);
						finish();
						// Passess the Selected Class back to the Search
						// Activity
					}
				}
			}
		}
	}

	//Create a Dialog with a list of Assessments for the Selected Class
	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Please select an Assessment")
				//Shows the list of assessment
				.setSingleChoiceItems(names, 0,
						new DialogInterface.OnClickListener() {
					//When an assessment is clicked on
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								num = which;
							}
						})
						//When the OK button is clicked
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Show Prompt
						app.makeToast(names[num] + " has been selected",
								Toast.LENGTH_SHORT);
						//Query the database for details about this assessment
						Assessment assessment = database
								.getAssessmentDetails(list[num]);
						//Opens the assessment Results Activity
						app.goToAssessmentResultsActivity((Activity) context,
								assessment.packageAssessmentIntoBundle());
					}
				})
				//When the cancel button is clicked
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).create();
	}
}
