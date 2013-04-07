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
import br.com.developer.redu.models.Link;
import br.com.developer.redu.models.Status;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.util.DateUtil;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "redu.db";
	private static final int DATABASE_VERSION = 1;

	private DbHelperListener mListener;
	
	// TABLE STATUS
	private static final String TABLE_STATUS = "Status";
	
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_USER_ID = "user_id";
	private static final String COLUMN_TYPE = "type";
	private static final String COLUMN_LOGEABLE_TYPE = "logeable_type";
	private static final String COLUMN_CREATE_AT_IN_MILLIS = "create_at_in_millis";
	private static final String COLUMN_TEXT = "text";
	private static final String COLUMN_LECTURE_ALREADY_SEEN = "lecture_already_seen";
	private static final String COLUMN_LAST_SEEN = "last_seen";

	// TABLE LINK
	private static final String TABLE_LINK = "Link";
	
	private static final String COLUMN_STATUS_ID = "status_id";
	private static final String COLUMN_REL = "rel";
	private static final String COLUMN_HREF = "href";

	
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

	private static final String CREATE_TABLE_LINK = "CREATE TABLE "
			+ TABLE_LINK + "(" 
			+ COLUMN_STATUS_ID + " TEXT, " 
			+ COLUMN_REL + " TEXT, " 
			+ COLUMN_HREF + " TEXT, " 
			+ "FOREIGN KEY(" + COLUMN_STATUS_ID + ") REFERENCES " + TABLE_STATUS + "(" + COLUMN_ID + "), "
			+ "PRIMARY KEY(" + COLUMN_STATUS_ID + ", " + COLUMN_REL + "));";
	
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
		database.execSQL(CREATE_TABLE_LINK);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbHelper.class.getName(), 
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
		onCreate(db);
	}
	
	public void setDbHelperListener(DbHelperListener listener) {
		mListener = listener;
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
			Status status = getCurrentStatusInCursor(cursor);
			status.links = getLinks(db, status);
			statuses.add(status);
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
			Status status = getCurrentStatusInCursor(cursor);
			status.links = getLinks(db, status);
			statuses.add(status);
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
			Status status = getCurrentStatusInCursor(cursor);
			status.links = getLinks(db, status);
			statuses.add(status);
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
	
	private List<Link> getLinks(SQLiteDatabase db, Status status) {
		List<Link> links = new ArrayList<Link>();
		
		Cursor cursor;
		cursor = db.query(TABLE_LINK, null, 
				COLUMN_STATUS_ID + " = ?", 
				new String[] {String.valueOf(status.id)}, 
				null, null, null, null);
		
		while(cursor.moveToNext()) {
			Link link = new Link();
			link.rel = cursor.getString(cursor.getColumnIndex(COLUMN_REL));
			link.href = cursor.getString(cursor.getColumnIndex(COLUMN_HREF));
			
			links.add(link);
    	}
		
		return links;
	}
	
	synchronized public long putStatus(Status status) {
		SQLiteDatabase db = this.getWritableDatabase();  

		long id = putStatus(db, status);
        
        db.close();
        
		if(mListener != null) {
			mListener.hasNewStatus();
		}
        
        return id;
	}
	
	synchronized public List<Long> putAllStatuses(List<Status> statuses) {
		if(statuses == null || statuses.size() == 0) {
			return null;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();  
		
		List<Long> ids = new ArrayList<Long>();
		
		for(Status status : statuses) {
			ids.add(putStatus(db, status));
		}
		
		db.close();

		if(mListener != null) {
			mListener.hasNewStatus();
		}
		
		return ids;
	}
	
	private Long putStatus(SQLiteDatabase db, Status status) {
		if(status == null) { 
			return 0L;
		}
		
		// putting Status datas
		ContentValues statusValues = new ContentValues();
		statusValues.put(COLUMN_TEXT, status.text);
		statusValues.put(COLUMN_ID, status.id);
		statusValues.put(COLUMN_USER_ID, status.user.id);
		statusValues.put(COLUMN_TYPE, status.type);
		statusValues.put(COLUMN_LOGEABLE_TYPE, status.logeable_type);
		
		if(status.created_at_in_millis == 0) {
			try {
				status.created_at_in_millis = DateUtil.dfIn.parse(status.created_at).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		statusValues.put(COLUMN_CREATE_AT_IN_MILLIS, status.created_at_in_millis);
		
		statusValues.put(COLUMN_LECTURE_ALREADY_SEEN, status.lectureAreadySeen);
		statusValues.put(COLUMN_LAST_SEEN , status.lastSeen);
		
		long id = db.insert(TABLE_STATUS, null, statusValues);
		
		// putting links datas
		for(Link link : status.links) {
			ContentValues linkValues = new ContentValues();
			linkValues.put(COLUMN_STATUS_ID, status.id);
			linkValues.put(COLUMN_REL, link.rel);
			linkValues.put(COLUMN_HREF, link.href);
			
			db.insert(TABLE_LINK, null, linkValues);
		}
		
		return id;
	}
	
	synchronized public long setStatusAsLastSeen(Status status) {
		SQLiteDatabase db = this.getWritableDatabase();  

		// putting Status datas
        ContentValues statusValues = new ContentValues();
        statusValues.put(COLUMN_LAST_SEEN, 1);
        
        long id = db.update(TABLE_STATUS, statusValues, COLUMN_ID + " = ?", new String[] {status.id});
        
        db.close();
        
        return id;
	}
	
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
