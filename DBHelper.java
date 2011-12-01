package com.gg.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Helper class for DB creation/updation
public class DBHelper extends SQLiteOpenHelper {
	// Your local name for the schema
	private static final String DB_NAME = "my_db";
	// Current version (you need to handle upgrades to this version in onUpgrade)
	private static final int DB_VERSION = 2;	

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	/**
	 * Creates any of our tables.
	 * This function is only run once or after every Clear Data
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		try {			
			database.execSQL("" +
				"create table auth (id integer primary key autoincrement,"+
				"username text," +
				"code text"+
			");");	
			
		} catch(Exception e){}
	}
	
	/**
	 * On open we want to make sure we get rid of the stupid setLocale error
	 */	
	@Override
	public void onOpen(SQLiteDatabase database) {
		if(!database.isOpen()) {
			SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS |
				 SQLiteDatabase.CREATE_IF_NECESSARY);
		}
	}

	/**
	 * Handle all database version upgrades
	 */	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.beginTransaction();
		boolean okay = true;
		
		// Add in date table for queue
		if(oldVersion < 2 || newVersion == 2) {
			boolean exists = checkTable(database, QueueDB.DB_TABLE);
			
			// Add the column
			if(exists) {
				database.execSQL("ALTER TABLE users ADD COLUMN used integer");
			} else {
				okay = false;
			}
		} 
		
		if(okay) { 
			database.setTransactionSuccessful();
		}
			
		database.endTransaction();
	}
	
	/**
	 * Check if a table exists. Useful for new table creation in onUpgrade
	 */	
	private boolean checkTable(SQLiteDatabase pDatabase, String pTable) {
		try {
			Cursor c = pDatabase.query(pTable, null, null, null, null, null, null);
			if(c == null) {
				return false;
			}
			c.close();
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Return current date as a string
	 */	
	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date date = new Date();
		return sdf.format(date);
	}
}