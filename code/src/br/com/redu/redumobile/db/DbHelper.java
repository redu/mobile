package br.com.redu.redumobile.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.com.developer.redu.models.Status;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.util.DateUtil;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "redu.db";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_STATUS = "Status";

	private static final String COLUMN_ID = "id";
	private static final String COLUMN_USER_ID = "user_id";
	private static final String COLUMN_TYPE = "type";
	private static final String COLUMN_LOGEABLE_TYPE = "logeable_type";
	private static final String COLUMN_CREATE_AT_IN_MILLIS = "create_at_in_millis";
	private static final String COLUMN_TEXT = "text";
	private static final String COLUMN_LECTURE_ALREADY_SEEN = "lecture_already_seen";
	private static final String COLUMN_LAST_SEEN = "last_seen";

	// Database creation sql statement
	private static final String CREATE_TABLE_STATUS = "CREATE TABLE "
			+ TABLE_STATUS + "(" 
			+ COLUMN_ID + " TEXT PRIMARY KEY, " 
			+ COLUMN_USER_ID + " INTEGER, " 
			+ COLUMN_TYPE + " TEXT NOT NULL, " 
			+ COLUMN_LOGEABLE_TYPE + " TEXT, " 
			+ COLUMN_CREATE_AT_IN_MILLIS + " INTEGER, "
			+ COLUMN_LECTURE_ALREADY_SEEN + " TEXT NOT NULL, "
			+ COLUMN_LAST_SEEN + " INTEGER, "
			+ COLUMN_TEXT + " TEXT NOT NULL);";
	
	private static DbHelper instance;
	
	private DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }  

	public static DbHelper getInstance(Context context) {
		if(instance == null) {
			instance = new DbHelper(context);
		}
		return instance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_STATUS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbHelper.class.getName(), 
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
		onCreate(db);
	}
	
	synchronized public List<Status> getStatus(long olderThan, int count) {
		List<Status> statuses = new ArrayList<Status>(count);  

		SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor;  
  
        cursor = db.query(TABLE_STATUS, null, 
        		COLUMN_CREATE_AT_IN_MILLIS + " < ?", 
        		new String[] {String.valueOf(olderThan)}, 
        		null, null, 
        		COLUMN_CREATE_AT_IN_MILLIS + " DESC", 
        		String.valueOf(count));  
  
    	while(cursor.moveToNext()) {
			statuses.add(getCurrentStatusInCursor(cursor));
    	}
        
        cursor.close();  
        db.close();
  
        return statuses;  
	}
	
	synchronized public List<Status> getNewLecturesStatus(long olderThan, int count) {
		List<Status> statuses = new ArrayList<Status>(count);  
		
		SQLiteDatabase db = this.getReadableDatabase();  
		Cursor cursor;  
		
		cursor = db.query(TABLE_STATUS, null, 
				COLUMN_CREATE_AT_IN_MILLIS + " < ? AND " + COLUMN_LOGEABLE_TYPE + " = ?", 
				new String[] {String.valueOf(olderThan), Status.LOGEABLE_TYPE_LECTURE}, 
				null, null, 
				COLUMN_CREATE_AT_IN_MILLIS + " DESC", 
				String.valueOf(count));  
		
		while(cursor.moveToNext()) {
			statuses.add(getCurrentStatusInCursor(cursor));
		}
		
		cursor.close();  
		db.close();
		
		return statuses;  
	}
	
	synchronized public List<Status> getLastSeenStatus(long olderThan, int count) {
		List<Status> statuses = new ArrayList<Status>(count);  
		
		SQLiteDatabase db = this.getReadableDatabase();  
		Cursor cursor;  
		
		cursor = db.query(TABLE_STATUS, null, 
				COLUMN_CREATE_AT_IN_MILLIS + " < ? AND " + COLUMN_LAST_SEEN + " = ?", 
				new String[] {String.valueOf(olderThan), "1"}, 
				null, null, 
				COLUMN_CREATE_AT_IN_MILLIS + " DESC", 
				String.valueOf(count));  
		
		while(cursor.moveToNext()) {
			statuses.add(getCurrentStatusInCursor(cursor));
		}
		
		cursor.close();
		db.close();
		
		return statuses;  
	}
	
	private Status getCurrentStatusInCursor(Cursor cursor) {
		Status status = new Status();

		status.id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
		status.type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
		status.logeable_type = cursor.getString(cursor.getColumnIndex(COLUMN_LOGEABLE_TYPE));
		status.text = cursor.getString(cursor.getColumnIndex(COLUMN_TEXT));
		status.created_at_in_millis = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATE_AT_IN_MILLIS));
		
		int lectureAreadySeen = cursor.getInt(cursor.getColumnIndex(COLUMN_TEXT));
		status.lectureAreadySeen = (lectureAreadySeen == 0) ? false : true;
		
		int lastSeen = cursor.getInt(cursor.getColumnIndex(COLUMN_TEXT));
		status.lastSeen = (lastSeen == 0) ? false : true;
		
		status.user = new User();
		status.user.id = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
		
		return status;
	}
	
	synchronized public long putStatus(Status status) {
		SQLiteDatabase db = this.getWritableDatabase();  

        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, status.text);
        values.put(COLUMN_ID, status.id);
        values.put(COLUMN_USER_ID, status.user.id);
        values.put(COLUMN_TYPE, status.type);
        values.put(COLUMN_LOGEABLE_TYPE, status.logeable_type);
        
        if(status.created_at_in_millis == 0) {
        	try {
        		status.created_at_in_millis = DateUtil.dfIn.parse(status.created_at).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        values.put(COLUMN_CREATE_AT_IN_MILLIS, status.created_at_in_millis);
        
        values.put(COLUMN_LECTURE_ALREADY_SEEN, status.lectureAreadySeen);
        values.put(COLUMN_LAST_SEEN , status.lastSeen);
        
        long id = db.insert(TABLE_STATUS, null, values);
        
        db.close();
        
        return id;
	}
	
	//TODO 
	/**
	 * Get the millis of the most recent Status saved at the db
	 * @return the creation time, in millis, of the most recent Status saved at the db. 
	 */
	synchronized public long getTimestamp() {
		long timestamp = 0l;
		
		SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor;  
        
        String query = new StringBuffer("SELECT MAX(").
        		append(COLUMN_CREATE_AT_IN_MILLIS).
        		append(") FROM ").
        		append(TABLE_STATUS).
        		toString();
        
        cursor = db.rawQuery(query, null);
  
        if (cursor.moveToFirst()) {  
    		timestamp = cursor.getLong(0);
        }
        
        cursor.close();  
        db.close();
        
        return timestamp;
	}
	
	@Override
	public synchronized void close() {
		super.close();
		instance = null;
	}
}
