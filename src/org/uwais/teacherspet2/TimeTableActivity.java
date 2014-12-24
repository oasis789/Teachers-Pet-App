package org.uwais.teacherspet2;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class TimeTableActivity extends Activity {

	private DatabaseQueries database;
	private Cursor[] cursor;
	private TableRow[] tr;
	private TextView[][] tvClass;
	private int id;
	private TableLayout tblTimeTable;
	private int dp1, dp5, dp10, row, column;
	private TeachersPetApplication app;
	private Activity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);
		context = this;
		app = (TeachersPetApplication) getApplicationContext();
		XMLReferences();
		dp5 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 5, getResources().getDisplayMetrics());
		dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 10, getResources().getDisplayMetrics());
		dp1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 1, getResources().getDisplayMetrics());
		tr = new TableRow[8];
		tvClass = new TextView[8][5];
		database = new DatabaseQueries(this, Constants.DB_NAME_1);
		//Query the database for the timetable
		cursor = database.getTimetable();
		alterLayout();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_SEARCH:
			app.goToSearchActivity(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	//Set up the timetable
	private void alterLayout() {
		id = 0;
		for (int i = 0; i < 8; i++) {
			int iClass = cursor[i].getColumnIndex(Constants.KEY_CLASS);
			tr[i] = new TableRow(this);
			int j = 0;
			while (cursor[i].moveToNext()) {
				tvClass[i][j] = new TextView(this);
				tvClass[i][j].setText(cursor[i].getString(iClass));
				tr[i].addView(tvClass[i][j]);
				LayoutParams params = new TableRow.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT,
						0.2f);
				params.setMargins(dp1, dp1, dp1, dp1);
				tvClass[i][j].setLayoutParams(params);
				tvClass[i][j].setGravity(Gravity.CENTER);
				tvClass[i][j].setPadding(dp5, dp5, dp5, dp5);
				tvClass[i][j].setTextSize(dp10);
				tvClass[i][j].setBackgroundColor(Color.BLACK);
				tvClass[i][j].setId(id);
				id = id + 1;
				tvClass[i][j].setOnClickListener(new View.OnClickListener() {
					//If a class is clicked on
					@Override
					public void onClick(View v) {
						int x = 0;
						for (int i = 0; i < 8; i++) {
							for (int j = 0; j < 5; j++) {
								if (v.getId() == x) {
									row = i;
									column = j;
								}
								x = x + 1;
							}
						}
						app.goToRegistrationActivity(context,
								tvClass[row][column].getText().toString());
					}
				});

				j++;
			}
			tblTimeTable.addView(tr[i]);
		}
	}

	private void XMLReferences() {
		tblTimeTable = (TableLayout) findViewById(R.id.tblTimeTable);
	}

}
