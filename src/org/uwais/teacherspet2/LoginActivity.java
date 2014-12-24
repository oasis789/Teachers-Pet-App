package org.uwais.teacherspet2;

import org.uwais.teacherspet2.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private SharedPreferences prefs;
	private Button bLogin;
	private EditText etPassword, etUsername;
	private CheckBox cRememberMe, cShowPassword;
	private DatabaseQueries database;
	private TeachersPetApplication app;
	private String username, password, correctPassword;
	private SharedPreferences.Editor editor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Create a handle to use queried data
		database = new DatabaseQueries(this, Constants.DB_NAME_2);
		app = (TeachersPetApplication) getApplicationContext();
		// get default preferences
		prefs = app.getAppPreferences();
		editor = prefs.edit();
		// Reference Objects from XML
		XMLReferences();
		getPreferences();
	}

	private void getPreferences() {
		/*
		 * If the Remember Me Box is checked, then get the saved username and
		 * password and set them as the text for the edit boxes
		 */
		String str_check = prefs.getString(Constants.CHECKED_PREFS, "false");
		if (str_check.equals("true")) {
			etUsername.setText(prefs.getString(Constants.USERNAME_PREFS, ""));
			etPassword.setText(prefs.getString(Constants.PASSWORD_PREFS, ""));
			cRememberMe.setChecked(true);
		}
	}

	private void XMLReferences() {
		// Reference Objects from XML
		bLogin = (Button) findViewById(R.id.bCheck);
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		cRememberMe = (CheckBox) findViewById(R.id.cbRemember);
		cShowPassword = (CheckBox) findViewById(R.id.cbShowPassword);
		cShowPassword.setOnClickListener(this);
		cRememberMe.setOnClickListener(this);
		bLogin.setOnClickListener(this);
	}

	//When a button is clicked on
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.bCheck):
			username = etUsername.getText().toString();
			password = etPassword.getText().toString();
			// Query database to get corresponding password to the username
			correctPassword = database.getPassword(username);
			// Presence Check
			if (username.equals("") || password.equals("")) {
				app.makeToast("Blank Fields!!!", Toast.LENGTH_SHORT);
			} else {
				//If the password is correct
				if (password.equals(correctPassword)) {
					//Show Prompt
					app.makeToast("You have successfully logged in!",
							Toast.LENGTH_SHORT);
					/*If the Remember Me Box is check the username and password
					 * are saved to the applications preferences					 * 
					 */
					if (cRememberMe.isChecked()) {
						editor.putString(Constants.USERNAME_PREFS, username);
						editor.putString(Constants.PASSWORD_PREFS, password);
						editor.commit();
					}
					//Go to the Main Menu Activity
					app.goToMainMenuActivity(this);
				} else {
					//Show Prompt
					app.makeToast("Incorrect Username or Password!!!",
							Toast.LENGTH_SHORT);
				}

			}

			break;
		case (R.id.cbRemember):
			/*When the Remember ME box is checked or unchecked then its
			 * value is saved to the applications preferences 
			 */
			if (cRememberMe.isChecked()) {
				editor.putString(Constants.CHECKED_PREFS, "true");
				editor.commit();
			} else {
				editor.putString(Constants.CHECKED_PREFS, "false");
				editor.commit();
			}
			break;
		case (R.id.cbShowPassword):
			/*
			 * change the input type of password when the Show Password box is
			 * checked so that the password comes as asterix
			 * When it is unchecked set it to plain text
			 */
			if (cShowPassword.isChecked() == true) {
				etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
			} else {
				etPassword.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			break;

		}

	}
}