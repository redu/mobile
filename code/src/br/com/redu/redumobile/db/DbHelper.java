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

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_LOGEABLE_TYPE = "logeable_type";
	public static final String COLUMN_CREATE_AT = "create_at";
	public static final String COLUMN_TEXT = "text";
	public static final String COLUMN_LECTURE_ALREADY_SEEN = "lecture_already_seen";
	public static final String COLUMN_LAST_SEEN = "last_seen";

	private SQLiteDatabase db;  

	// Database creation sql statement
	private static final String CREATE_TABLE_STATUS = "CREATE TABLE "
			+ TABLE_STATUS + "(" 
			+ COLUMN_ID + " TEXT PRIMARY KEY , " 
			+ COLUMN_TYPE + " TEXT NOT NULL, " 
			+ COLUMN_LOGEABLE_TYPE + " TEXT NOT NULL, " 
			+ COLUMN_CREATE_AT + " TEXT NOT NULL, "
			+ COLUMN_LECTURE_ALREADY_SEEN + " TEXT NOT NULL, "
			+ COLUMN_LAST_SEEN + " INTEGER NOT NULL, "
			+ COLUMN_TEXT + " INTEGER NOT NULL);";
	
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
  
        cursor = this.db.query(TABLE_STATUS, null, null, null, null, null, null, String.valueOf(count));  
  
        if (cursor.moveToFirst()) {  
        	while(cursor.moveToNext()) {
        		Status status = new Status();
        		status.id = cursor.getColumnName(cursor.getColumnIndex(COLUMN_ID));
        		status.type = cursor.getColumnName(cursor.getColumnIndex(COLUMN_TYPE));
        		status.logeable_type = cursor.getColumnName(cursor.getColumnIndex(COLUMN_LOGEABLE_TYPE));
        		status.created_at = cursor.getColumnName(cursor.getColumnIndex(COLUMN_CREATE_AT));
        		status.text = cursor.getColumnName(cursor.getColumnIndex(COLUMN_TEXT));
        		
        		String lectureAreadySeen = cursor.getColumnName(cursor.getColumnIndex(COLUMN_TEXT));
        		if(lectureAreadySeen != null) {
        			status.lectureAreadySeen = lectureAreadySeen.equals("0") ? false : true;
        		}
        		
        		String lastSeen = cursor.getColumnName(cursor.getColumnIndex(COLUMN_TEXT));
        		if(lastSeen != null) {
        			status.lastSeen = lastSeen.equals("0") ? false : true;
        		}
        		
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
