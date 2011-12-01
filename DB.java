package com.methodin.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class DB {
	// id should be defined as the Primary Key in all of your tables
	public static final String KEY_ROWID = "id";
	// List of columns - should be populated in child class
	protected String[] columns = new String[] {};
	// Name of the SQLite table - should be populated in child class
	protected String table = "";
	// Application context
	protected Context context;
	// Actual database connection object
	protected SQLiteDatabase database;
	// Our helper class
	protected DBHelper dbHelper;

	public DB(Context context) {
		this.context = context;
	}
	public DB(Context context, String table, String[] pColumns) {
		this.context = context;
		this.columns = pColumns;
		this.table = table;
	}
	
	/**
	 * Open a connection to the database table
	 */
	public DB open() throws SQLException {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Close the connection to the database table
	 */
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Start a transaction
	 */
	public void begin() {
		database.beginTransaction();
	}
	
	/**
	 * Flags the transaction for a commit
	 */
	public void commit() {
		database.setTransactionSuccessful();
	}
	
	/**
	 * Finalized the transaction
	 * This should always be called after a commit or rollback - if not it will not commit
	 */
	public void end() {
		database.endTransaction();
	}
	
	/**
	 * Create a new record If the record is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long create(HashMap<String, String> columns) {
		ContentValues initialValues = createContentValues(columns);
		return database.insert(table, null, initialValues);
	}
	
	/**
	 * Update the record using a HashMap for a given id
	 */
	public boolean update(int rowId, HashMap<String, String> columns) {
		ContentValues updateValues = createContentValues(columns);
		return database.update(table, updateValues, KEY_ROWID + "=" + rowId, null) > 0;
	}	
	
	/**
	 * Update the record using a ContentValues object for a given id
	 */
	public boolean update(int rowId, ContentValues columns) {
		return database.update(table, columns, KEY_ROWID + "=" + rowId, null) > 0;
	}		
	
	/**
	 * Delets a record that matches the passed id
	 */
	public boolean delete(int rowId) {
		return database.delete(table, KEY_ROWID + "=" + rowId, null) > 0;
	}	
	
	/**
	 * Truncates the table
	 */
	public boolean truncate() {
		return database.delete(table, null, null) > 0;
	}	
	
	/**
	 * Return a Cursor over the list of all todo in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetch() {
		return fetch(null, null);
	}
	public Cursor fetch(String conditions) {
		return fetch(conditions, null);
	}	
	public Cursor fetch(String conditions, String[] values) {
		Cursor mCursor = database.query(table, columns, conditions, values, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}		
		return mCursor;
	}	

	/**
	 * Return a ContentValues object that has all of the columns and values
	 */
	public ContentValues fetchOne(String rowId) throws SQLException {
		return fetchOne(KEY_ROWID, rowId);
	}	
	public ContentValues fetchOne(String key, String value) throws SQLException {
		ContentValues values = new ContentValues();
		Cursor mCursor = database.query(true, table, columns,
				key + "='" + value + "'", null, null, null, null, null);
		if (mCursor != null) {
			if(mCursor.getCount() > 0) {
				mCursor.moveToFirst();
				for(int i=0,len=mCursor.getColumnCount();i<len;i++) {
					values.put(mCursor.getColumnName(i), mCursor.getString(i));
				}
			}
			mCursor.close();
		}
		return values;
	}	
	public ContentValues fetchOne(String conditions, String[] values) throws SQLException {
		ContentValues result = new ContentValues();
		Cursor mCursor = database.query(true, table, columns,
				conditions, values, null, null, null, null);
		if (mCursor != null) {
			if(mCursor.getCount() > 0) {
				mCursor.moveToFirst();
				for(int i=0,len=mCursor.getColumnCount();i<len;i++) {
					result.put(mCursor.getColumnName(i), mCursor.getString(i));
				}
			}
			mCursor.deactivate();
			mCursor.close();
		}
		return result;
	}		
	
	/**
	 * Converts a HashMap to a ContentValues object
	 */
	@SuppressWarnings("unchecked")
	protected ContentValues createContentValues(HashMap<String, String> columns) {
		ContentValues values = new ContentValues();
		Iterator it = columns.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        values.put(pairs.getKey().toString(), pairs.getValue().toString());
	    }
		return values;
	}
}
