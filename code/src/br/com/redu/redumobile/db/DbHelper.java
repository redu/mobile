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
import br.com.redu.redumobile.util.DataUtil;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "redu.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_STATUS = "Status";
	public static final String TABLE_USER = "User";

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_LOGEABLE_TYPE = "logeable_type";
	public static final String COLUMN_CREATE_AT_IN_MILLIS = "create_at_in_millis";
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
			+ COLUMN_CREATE_AT_IN_MILLIS + " INTEGER NOT NULL, "
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
	
	public List<Status> getStatus(long olderThan, int count) {
		List<Status> statuses = new ArrayList<Status>(count);  
        Cursor cursor;  
  
        cursor = this.db.query(TABLE_STATUS, null, 
        		COLUMN_CREATE_AT_IN_MILLIS + " < ?", 
        		new String[] {String.valueOf(olderThan)}, 
        		null, null, 
        		COLUMN_CREATE_AT_IN_MILLIS + " DESC", 
        		String.valueOf(count));  
  
        if (cursor.moveToFirst()) {  
        	while(cursor.moveToNext()) {
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
        		
        		statuses.add(status);
        	}
        }
        
        cursor.close();  
  
        return statuses;  
	}
	
	public long putStatus(Status status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, status.text);
        values.put(COLUMN_ID, status.id);
        values.put(COLUMN_TYPE, status.logeable_type);
        values.put(COLUMN_LOGEABLE_TYPE, status.type);
        
        if(status.created_at_in_millis == 0) {
        	try {
        		status.created_at_in_millis = DataUtil.df.parse(status.created_at).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        values.put(COLUMN_CREATE_AT_IN_MILLIS, status.created_at_in_millis);
        
        values.put(COLUMN_LECTURE_ALREADY_SEEN, status.lectureAreadySeen);
        values.put(COLUMN_LAST_SEEN , status.lastSeen);
        
        return this.db.insert(TABLE_STATUS, null, values);
	}
	
	//TODO 
	/**
	 * Get the millis of the most recent Status saved at the db
	 * @return the creation time, in millis, of the most recent Status saved at the db. 
	 */
	public long getTimestamp() {
		long timestamp = 0l;
        Cursor cursor;  
        
        String query = new StringBuffer("SELECT MAX(").
        		append(COLUMN_CREATE_AT_IN_MILLIS).
        		append(") FROM ").
        		append(TABLE_STATUS).
        		toString();
        
        cursor = this.db.rawQuery(query, null);
  
        if (cursor.moveToFirst()) {  
    		timestamp = cursor.getLong(0);
        }
        
        cursor.close();  
  
        return timestamp;
	}

}
