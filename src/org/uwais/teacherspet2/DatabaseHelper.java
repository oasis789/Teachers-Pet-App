package org.uwais.teacherspet2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/sdcard/TeachersPetApplication";
	private static String DB_NAME;
	private static String myPath; 
	private SQLiteDatabase ourDB;
	private Context ourContext;

	public DatabaseHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.ourContext = context;
		DB_NAME = databaseName;
		myPath = DB_PATH + DB_NAME + ".db";
	}

	public SQLiteDatabase getDatabase() {
		return ourDB;
	}
	
	public void closeDatabase(){
		ourDB.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) throws SQLException {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void createDatabase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			Log.d("createDatabase",
					"Database successfully opened by this.getReadableDatabase()");
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	// Opens the Databse for reading and writing
	public void openDataBase() throws SQLException {
		ourDB = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	private void copyDataBase() throws IOException {

		File myFile = new File(myPath);
		myFile.createNewFile();
		// Open your local db as the input stream
		InputStream myInput = ourContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = myPath;

		// Open the empty db as the output stream
		FileOutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

}
