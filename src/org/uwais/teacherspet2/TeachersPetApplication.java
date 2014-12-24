package org.uwais.teacherspet2;

import java.util.Date;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class TeachersPetApplication extends Application {


	private static TeachersPetApplication instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		instance = null;
	}

	// get the shared preferences for the application
	public SharedPreferences getAppPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}

	// Make Toast :)
	public void makeToast(String message, int toastLength) {
		Toast.makeText(this, message, toastLength).show();
	}

	public void goToLoginActivity(Activity callingActivity) {
		callingActivity.startActivity(new Intent(callingActivity,
				LoginActivity.class));
		callingActivity.finish();
	}

	public void goToMainMenuActivity(Activity callingActivity) {
		callingActivity.startActivity(new Intent(callingActivity,
				MainMenuActivity.class));
		callingActivity.finish();
	}

	public void goToClassSelectorActivity(Activity callingActivity,
			String buttonPressed) {
		callingActivity.startActivity(new Intent(callingActivity,
				ClassSelectorActivity.class).putExtra("Button", buttonPressed));
	}

	public void goToTimeTableActivity(Activity callingActivity) {
		callingActivity.startActivity(new Intent(callingActivity,
				TimeTableActivity.class));
	}

	public void goToRegistrationActivity(Activity callingActivity,
			String classSelected) {
		callingActivity.startActivity(new Intent(callingActivity,
				RegistrationActivity.class).putExtra(Constants.KEY_CLASS, classSelected));
		callingActivity.finish();
	}
	
	public void goToAssessmentActivity(Activity callingActivity, String classSelected){
		callingActivity.startActivity(new Intent(callingActivity, AssessmentActivity.class).putExtra(Constants.KEY_CLASS, classSelected));
		callingActivity.finish();		
	}
	
	public void goToAssessmentResultsActivity(Activity callingActivity, Bundle bundle){
		callingActivity.startActivity(new Intent(callingActivity, AssessmentResultsActivity.class).putExtras(bundle));
		callingActivity.finish();		
	}
	
	public void goToSearchActivity(Activity callingActivity){
		callingActivity.startActivity(new Intent(callingActivity,SearchActivity.class));	
	}
	
	public void SendEmailForConsecutiveAbsences(Activity callingActivity, String[] emailAddress, CharSequence[] students, String classMissed, String[] dates){
		int noOfAbsentees = students.length;
		String message = "The following students have been missing my lesson for 3 consecutive lessons: \n";
		for(int i =0; i<noOfAbsentees; i++){
			message = message + students[i] + " was absent on " + dates[0] + ", " + dates[1] + " and on " + dates[2] +   "\n";
		}
		message = message + "The class name is " + classMissed + "\n";
		message = message + "If you could resolve this issue, it would be very helpful.";
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddress);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pupils Consecutive Absences");
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		callingActivity.startActivity(emailIntent);
	}
	
}
