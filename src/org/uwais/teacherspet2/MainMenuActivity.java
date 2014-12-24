package org.uwais.teacherspet2;

import org.uwais.teacherspet2.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity implements OnClickListener {

	private Button timetable, search, assessment, assessmentResults, register;
	private TeachersPetApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		app = (TeachersPetApplication) getApplicationContext();
		//Reference Objects from XML
		XMLReferences();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_SEARCH:
			app.goToSearchActivity(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void XMLReferences() {
		//Reference Objects from XML
		timetable = (Button) findViewById(R.id.bOpenTimeTable);
		timetable.setOnClickListener(this);
		search = (Button) findViewById(R.id.bOpenSearch);
		search.setOnClickListener(this);
		assessment = (Button) findViewById(R.id.bOpenAssessment);
		assessment.setOnClickListener(this);
		assessmentResults = (Button) findViewById(R.id.bOpenAssessmentResults);
		assessmentResults.setOnClickListener(this);
		register = (Button) findViewById(R.id.bOpenRegister);
		register.setOnClickListener(this);

	}

	//When a button is clicked on
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bOpenTimeTable:
			//When the TimeTable button is clicked on 
			//go to the TimeTable Activity
			app.goToTimeTableActivity(this);
			break;
		case R.id.bOpenAssessment:
			//When the Assessment Button is clicked on
			//go to the ClassSelector Activity and pass which button was clicked to 
			//it
			app.goToClassSelectorActivity(this,"Assessment");
			break;
		case R.id.bOpenAssessmentResults:
			//When the Assessment Results Button is clicked on
			//go to the ClassSelector Activity and pass which button was clicked to 
			//it
			app.goToClassSelectorActivity(this, "Assessment Results");
			break;
		case R.id.bOpenRegister:
			//When the Register Button is clicked on
			//go to the ClassSelector Activity and pass which button was clicked to 
			//it
			app.goToClassSelectorActivity(this, "Registration");
			break;
		case R.id.bOpenSearch:
			//If the Search Button is clicked go to the
			// Search Activity
			app.goToSearchActivity(this);
			break;

		}

	}

}
