package br.com.redu.redumobile.db;

import java.util.ArrayList;
import java.util.List;

import br.com.developer.redu.models.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "redu.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_STATUS = "Status";
	public static final String TABLE_USER = "User";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_CREATE_AT = "createAt";
	public static final String COLUMN_TEXT = "text";

	private SQLiteDatabase db;  

	// Database creation sql statement
	private static final String CREATE_TABLE_STATUS = "CREATE TABLE "
			+ TABLE_STATUS + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TYPE + " text not null, " 
			+ COLUMN_CREATE_AT + " text not null, "
			+ COLUMN_TEXT + "text not null);";
	
	// TODO
//	private static final String CREATE_TABLE_USER = "CREATE TABLE "
//			+ TABLE_USER + "(" 
//			+ COLUMN_ID + " integer primary key autoincrement, " 
//			+ COLUMN_LOGIN + "login not null);";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = this.getWritableDatabase();  
    }  

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_STATUS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
		onCreate(db);
	}
	
	public List<Status> getStatus(int count) {
		List<Status> statuses = new ArrayList<Status>(count);  
        Cursor cursor;  
  
        cursor = this.db.query(TABLE_STATUS, new String[] {  
                COLUMN_TYPE, COLUMN_CREATE_AT, COLUMN_TEXT},     
                                null, null, null, null, null, String.valueOf(count));  
  
        if (cursor.moveToFirst()) {  
        	while(cursor.moveToNext()) {
        		Status status = new Status();
        		status.type = cursor.getColumnName(cursor.getColumnIndex(COLUMN_TYPE));
        		status.created_at = cursor.getColumnName(cursor.getColumnIndex(COLUMN_CREATE_AT));
        		status.text = cursor.getColumnName(cursor.getColumnIndex(COLUMN_TEXT));
        		
        		statuses.add(status);
        	}
        }
        
        cursor.close();  
  
        return statuses;  
	}
	
	public long putStatus(Status status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, status.text);
        values.put(COLUMN_CREATE_AT, status.created_at);
        values.put(COLUMN_TYPE, status.type);
        
        return this.db.insert(TABLE_STATUS, null, values);
	}

}
