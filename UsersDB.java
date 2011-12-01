package com.gg.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;

public class UsersDB extends DB {
	public static final String KEY_BUSINESS = "business";
	public static final String KEY_CODE = "code";
	public static final String KEY_USED = "used";
	public static final String DB_TABLE = "users";

	public UsersDB(Context context) {
		super(context, DB_TABLE, new String[] {
			DB.KEY_ROWID,
			KEY_BUSINESS,
			KEY_CODE,
			KEY_USED
		});
	}
	
	@Override 
	public long create(HashMap<String, String> columns) {
		long id = super.create(columns);
		if(id != -1) {
			QueueDB db = new QueueDB(context);
			db.open();
			HashMap<String,String> values = new HashMap<String,String>();
			values.put(QueueDB.KEY_TYPE, "user");
			values.put(QueueDB.KEY_ROW_ID, id+"");
			values.put(QueueDB.KEY_DATE, DBHelper.getDate());
			db.create(values);
			db.close();
		}
		return id;
	}
	
	@Override 
	public boolean update(int rowId, ContentValues columns) {
		boolean result = super.update(rowId, columns);
		if(result) {
			QueueDB db = new QueueDB(context);
			db.open();
			HashMap<String,String> values = new HashMap<String,String>();
			values.put(QueueDB.KEY_TYPE, "user");
			values.put(QueueDB.KEY_ROW_ID, rowId+"");
			values.put(QueueDB.KEY_DATE, DBHelper.getDate());
			db.create(values);
			db.close();
		}
		return result;
	}		
}