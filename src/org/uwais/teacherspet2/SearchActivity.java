package org.uwais.teacherspet2;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SearchActivity extends TabActivity {

	private TabHost tabHost;
	private TeachersPetApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		app = (TeachersPetApplication) getApplicationContext();
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		//Set up tabs
		TabSpec studentTab = tabHost.newTabSpec(Constants.STUDENTS_TABLE_NAME);
		TabSpec assessmentTab = tabHost
				.newTabSpec(Constants.ASSESSMENT_TABLE_NAME);

		studentTab.setIndicator("Students").setContent(
				new Intent(this, StudentTabActivity.class));

		assessmentTab.setIndicator("Assessments").setContent(
				new Intent(this, AssessmentTabActivity.class));

		tabHost.addTab(studentTab);
		tabHost.addTab(assessmentTab);

	}

}
