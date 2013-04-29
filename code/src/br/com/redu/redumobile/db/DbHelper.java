package br.com.redu.redumobile.db;

import java.lang.ref.WeakReference;
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
import br.com.developer.redu.models.Thumbnail;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.util.DateUtil;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "redu.db";
	private static final int DATABASE_VERSION = 1;

	private static List<WeakReference<DbHelperListener>> mListeners;
	
	// TABLE APP_USER
	private static class TableAppUser {
		public static final String NAME = "AppUser";
		
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_OLDEST_STATUSES_WERE_DOWNLOADED = "oldest_statuses_were_downloaded";
		
		public static final String CREATE = "CREATE TABLE "
				+ NAME + "(" 
				+ COLUMN_ID + " TEXT PRIMARY KEY, "
				+ COLUMN_OLDEST_STATUSES_WERE_DOWNLOADED + " INTEGER );";
	}
	
	// TABLE STATUS
	private static class TableStatus {
		public static final String NAME = "Status";
		
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_APP_USER_ID = "app_user_id";
		public static final String COLUMN_USER_ID = "user_id";
		public static final String COLUMN_TYPE = "type";
		public static final String COLUMN_ANSWERS_COUNT = "answers_count";
		public static final String COLUMN_LOGEABLE_TYPE = "logeable_type";
		public static final String COLUMN_CREATED_AT_IN_MILLIS = "created_at_in_millis";
		public static final String COLUMN_LAST_SEEN_AT_IN_MILLIS = "last_seen_at_in_millis";
		public static final String COLUMN_TEXT = "text";
		public static final String COLUMN_LECTURE_ALREADY_SEEN = "lecture_already_seen";
		public static final String COLUMN_LAST_SEEN = "last_seen";
		
		public static final String CREATE = "CREATE TABLE "
				+ NAME + "(" 
				+ COLUMN_ID + " TEXT PRIMARY KEY, " 
				+ COLUMN_APP_USER_ID + " INTEGER, " 
				+ COLUMN_USER_ID + " INTEGER, " 
				+ COLUMN_TYPE + " TEXT NOT NULL, " 
				+ COLUMN_ANSWERS_COUNT + " INTEGER, " 
				+ COLUMN_LOGEABLE_TYPE + " TEXT, " 
				+ COLUMN_CREATED_AT_IN_MILLIS + " INTEGER, "
				+ COLUMN_LECTURE_ALREADY_SEEN + " TEXT NOT NULL, "
				+ COLUMN_LAST_SEEN + " INTEGER, "
				+ COLUMN_LAST_SEEN_AT_IN_MILLIS + " INTEGER, "
				+ COLUMN_TEXT + " TEXT NOT NULL, "
				+ "FOREIGN KEY(" + COLUMN_APP_USER_ID + ") REFERENCES " + TableAppUser.NAME + "(" + TableAppUser.COLUMN_ID + "), "
				+ "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TableUser.NAME + "(" + TableUser.COLUMN_ID + "));";
	}

	// TABLE LINK
	private static class TableLink {
		public static final String NAME = "Link";
		
		public static final String COLUMN_STATUS_ID = "status_id";
		public static final String COLUMN_REL = "rel";
		public static final String COLUMN_HREF = "href";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_PERMALINK = "permalink";
		
		public static final String CREATE = "CREATE TABLE "
				+ NAME + "(" 
				+ COLUMN_STATUS_ID + " TEXT, " 
				+ COLUMN_REL + " TEXT, " 
				+ COLUMN_HREF + " TEXT, " 
				+ COLUMN_NAME + " TEXT, " 
				+ COLUMN_PERMALINK + " TEXT, " 
				+ "FOREIGN KEY(" + COLUMN_STATUS_ID + ") REFERENCES " + TableStatus.NAME + "(" + TableStatus.COLUMN_ID + "), "
				+ "PRIMARY KEY(" + COLUMN_STATUS_ID + ", " + COLUMN_REL + "));";
	}
	
	// TABLE THUMBNAIL
	private static class TableThumbnail {
		public static final String NAME = "Thumbnail";
		
		public static final String COLUMN_USER_ID = "user_id";
		public static final String COLUMN_HREF = "href";
		public static final String COLUMN_SIZE = "size";
		
		public static final String CREATE = "CREATE TABLE "
				+ NAME + "(" 
				+ COLUMN_USER_ID + " TEXT, " 
				+ COLUMN_HREF + " TEXT, " 
				+ COLUMN_SIZE + " TEXT, " 
				+ "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TableUser.NAME + "(" + TableUser.COLUMN_ID + "), "
				+ "PRIMARY KEY(" + COLUMN_USER_ID + ", " + COLUMN_SIZE + "));";
	}	
	
	// TABLE USER
	private static class TableUser {
		public static final String NAME = "User";
		
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_FIRST_NAME = "first_name";
		public static final String COLUMN_LAST_NAME = "last_name";
		
		private static final String CREATE = "CREATE TABLE "
				+ NAME + "(" 
				+ COLUMN_ID + " INTEGER, " 
				+ COLUMN_FIRST_NAME + " TEXT, " 
				+ COLUMN_LAST_NAME + " TEXT );";
	}
	
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
		database.execSQL(TableStatus.CREATE);
		database.execSQL(TableUser.CREATE);
		database.execSQL(TableLink.CREATE);
		database.execSQL(TableThumbnail.CREATE);
		database.execSQL(TableAppUser.CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbHelper.class.getName(), 
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TableStatus.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TableLink.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TableUser.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TableThumbnail.NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TableAppUser.NAME);
		onCreate(db);
	}
	
	public void addDbHelperListener(DbHelperListener listener) {
		if(mListeners == null) {
			mListeners = new ArrayList<WeakReference<DbHelperListener>>();
		}
		mListeners.add(new WeakReference<DbHelperListener>(listener));
	}
	
	synchronized public List<Status> getStatus(long timestamp, boolean olderThan, int count) {
		List<Status> statuses = new ArrayList<Status>(count);  

		SQLiteDatabase db = this.getReadableDatabase();  
        Cursor cursor;  
  
        cursor = db.query(TableStatus.NAME, null, 
        		TableStatus.COLUMN_CREATED_AT_IN_MILLIS + (olderThan ? "<" : ">") + "?", 
        		new String[] {String.valueOf(timestamp)}, 
        		null, null, 
        		TableStatus.COLUMN_CREATED_AT_IN_MILLIS + " DESC", 
        		String.valueOf(count));  
  
    	while(cursor.moveToNext()) {
			Status status = getCurrentStatusInCursor(cursor);
			status.user = getUser(db, status);
			status.links = getLinks(db, status);
			statuses.add(status);
    	}
        
        cursor.close();  
        db.close();
  
        return statuses;  
	}
	
	synchronized public List<Status> getNewLecturesStatus(long timestamp, boolean olderThan, int count) {
		List<Status> statuses = new ArrayList<Status>(count);  
		
		SQLiteDatabase db = this.getReadableDatabase();  
		Cursor cursor;  
		
		cursor = db.query(TableStatus.NAME, null, 
				TableStatus.COLUMN_CREATED_AT_IN_MILLIS + (olderThan ? "<" : ">") + " ? AND " + TableStatus.COLUMN_LOGEABLE_TYPE + " = ?", 
				new String[] {String.valueOf(timestamp), Status.LOGEABLE_TYPE_LECTURE}, 
				null, null, 
				TableStatus.COLUMN_CREATED_AT_IN_MILLIS + " DESC", 
				String.valueOf(count));  
		
		while(cursor.moveToNext()) {
			Status status = getCurrentStatusInCursor(cursor);
			status.user = getUser(db, status);
			status.links = getLinks(db, status);
			statuses.add(status);
		}
		
		cursor.close();  
		db.close();
		
		return statuses;  
	}
	
	synchronized public List<Status> getLastSeenStatus(long timestamp, boolean olderThan, int count) {
		List<Status> statuses = new ArrayList<Status>(count);  
		
		SQLiteDatabase db = this.getReadableDatabase();  
		Cursor cursor;  
		
		cursor = db.query(TableStatus.NAME, null, 
				TableStatus.COLUMN_LAST_SEEN_AT_IN_MILLIS + (olderThan ? "<" : ">") + "? AND " + TableStatus.COLUMN_LAST_SEEN + " = 1", 
				new String[] {String.valueOf(timestamp)}, 
				null, null, 
				TableStatus.COLUMN_LAST_SEEN_AT_IN_MILLIS + " DESC", 
				String.valueOf(count));  
		
		while(cursor.moveToNext()) {
			Status status = getCurrentStatusInCursor(cursor);
			status.user = getUser(db, status);
			status.links = getLinks(db, status);
			statuses.add(status);
		}
		
		cursor.close();
		db.close();
		
		return statuses;  
	}
	
	private Status getCurrentStatusInCursor(Cursor cursor) {
		Status status = new Status();

		status.id = cursor.getString(cursor.getColumnIndex(TableStatus.COLUMN_ID));
		status.type = cursor.getString(cursor.getColumnIndex(TableStatus.COLUMN_TYPE));
		status.answers_count = cursor.getInt(cursor.getColumnIndex(TableStatus.COLUMN_ANSWERS_COUNT));
		status.logeable_type = cursor.getString(cursor.getColumnIndex(TableStatus.COLUMN_LOGEABLE_TYPE));
		status.text = cursor.getString(cursor.getColumnIndex(TableStatus.COLUMN_TEXT));
		status.createdAtInMillis = cursor.getLong(cursor.getColumnIndex(TableStatus.COLUMN_CREATED_AT_IN_MILLIS));
		status.lastSeenAtInMillis = cursor.getLong(cursor.getColumnIndex(TableStatus.COLUMN_LAST_SEEN_AT_IN_MILLIS));
		
		int lectureAreadySeen = cursor.getInt(cursor.getColumnIndex(TableStatus.COLUMN_TEXT));
		status.lectureAreadySeen = (lectureAreadySeen == 0) ? false : true;
		
		int lastSeen = cursor.getInt(cursor.getColumnIndex(TableStatus.COLUMN_TEXT));
		status.lastSeen = (lastSeen == 0) ? false : true;
		
		status.user = new User();
		status.user.id = cursor.getInt(cursor.getColumnIndex(TableStatus.COLUMN_USER_ID));
		
		return status;
	}
	
	private User getUser(SQLiteDatabase db, Status status) {
		User user = new User();
		
		Cursor cursor;
		cursor = db.query(TableUser.NAME, null, 
				TableUser.COLUMN_ID + " = ?", 
				new String[] {String.valueOf(status.user.id)}, 
				null, null, null, null);
		
		if(cursor.moveToNext()) {
			user.id = cursor.getInt(cursor.getColumnIndex(TableUser.COLUMN_ID));
			user.first_name = cursor.getString(cursor.getColumnIndex(TableUser.COLUMN_FIRST_NAME));
			user.last_name = cursor.getString(cursor.getColumnIndex(TableUser.COLUMN_LAST_NAME));
			user.thumbnails = getThumbnails(db, status.user);
    	}
		
		return user;
	}
	
	private List<Thumbnail> getThumbnails(SQLiteDatabase db, User user) {
		List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		
		Cursor cursor;
		cursor = db.query(TableThumbnail.NAME, null, 
				TableThumbnail.COLUMN_USER_ID + " = ?", 
				new String[] {String.valueOf(user.id)}, 
				null, null, null, null);
		
		while(cursor.moveToNext()) {
			Thumbnail thumbnail = new Thumbnail();
			thumbnail.size = cursor.getString(cursor.getColumnIndex(TableThumbnail.COLUMN_SIZE));
			thumbnail.href = cursor.getString(cursor.getColumnIndex(TableThumbnail.COLUMN_HREF));

			thumbnails.add(thumbnail);
		}
		
		return thumbnails;
	}
	
	private List<Link> getLinks(SQLiteDatabase db, Status status) {
		List<Link> links = new ArrayList<Link>();
		
		Cursor cursor;
		cursor = db.query(TableLink.NAME, null, 
				TableLink.COLUMN_STATUS_ID + " = ?", 
				new String[] {String.valueOf(status.id)}, 
				null, null, null, null);
		
		while(cursor.moveToNext()) {
			Link link = new Link();
			link.rel = cursor.getString(cursor.getColumnIndex(TableLink.COLUMN_REL));
			link.href = cursor.getString(cursor.getColumnIndex(TableLink.COLUMN_HREF));
			link.name = cursor.getString(cursor.getColumnIndex(TableLink.COLUMN_NAME));
			link.permalink = cursor.getString(cursor.getColumnIndex(TableLink.COLUMN_PERMALINK));
			
			links.add(link);
    	}
		
		return links;
	}
	
	synchronized public long putAppUser(User appUser) {
		SQLiteDatabase db = this.getWritableDatabase();  
		
		ContentValues values = new ContentValues();
		values.put(TableAppUser.COLUMN_ID , appUser.id);
		
		long id = db.insert(TableAppUser.NAME, null, values);
		
		db.close();
		
		return id;
	}
	
	synchronized public long putStatus(Status status, String appUserId) {
		SQLiteDatabase db = this.getWritableDatabase();  

		long id = putStatus(db, status, appUserId);
        
        db.close();
        
        notifyListenters();
        
        return id;
	}
	
	synchronized public List<Long> putAllStatuses(List<Status> statuses, String appUserId) {
		if(statuses == null || statuses.size() == 0) {
			return null;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();  
		
		List<Long> ids = new ArrayList<Long>();
		
		for(Status status : statuses) {
			ids.add(putStatus(db, status, appUserId));
		}
		
		db.close();

		notifyListenters();
		
		return ids;
	}
	
	private Long putStatus(SQLiteDatabase db, Status status, String appUserId) {
		if(status == null) { 
			return 0L;
		}
		
		putUser(db, status.user);
		
		// putting Status datas
		ContentValues values = new ContentValues();
		values.put(TableStatus.COLUMN_TEXT, status.text);
		values.put(TableStatus.COLUMN_ID, status.id);
		values.put(TableStatus.COLUMN_APP_USER_ID, appUserId);
		values.put(TableStatus.COLUMN_USER_ID, status.user.id);
		values.put(TableStatus.COLUMN_TYPE, status.type);
		values.put(TableStatus.COLUMN_ANSWERS_COUNT, status.answers_count);
		values.put(TableStatus.COLUMN_LOGEABLE_TYPE, status.logeable_type);
		
		if(status.createdAtInMillis == 0) {
			try {
				status.createdAtInMillis = DateUtil.dfIn.parse(status.created_at).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		values.put(TableStatus.COLUMN_CREATED_AT_IN_MILLIS, status.createdAtInMillis);
		
		values.put(TableStatus.COLUMN_LECTURE_ALREADY_SEEN, status.lectureAreadySeen);
		values.put(TableStatus.COLUMN_LAST_SEEN , status.lastSeen);
		
		long id = db.insert(TableStatus.NAME, null, values);
		
		// putting links datas
		for(Link link : status.links) {
			putLink(db, link, status.id);
		}
		
		return id;
	}

	private long putLink(SQLiteDatabase db, Link link, String statusId) {
		ContentValues values = new ContentValues();
		values.put(TableLink.COLUMN_STATUS_ID, statusId);
		values.put(TableLink.COLUMN_REL, link.rel);
		values.put(TableLink.COLUMN_HREF, link.href);
		values.put(TableLink.COLUMN_NAME, link.name);
		values.put(TableLink.COLUMN_PERMALINK, link.permalink);
		
		return db.insert(TableLink.NAME, null, values);	
	}

	private long putUser(SQLiteDatabase db, User user) {
		ContentValues values = new ContentValues();
		values.put(TableUser.COLUMN_ID, user.id);
		values.put(TableUser.COLUMN_FIRST_NAME, user.first_name);
		values.put(TableUser.COLUMN_LAST_NAME, user.last_name);
		
		long id = db.insert(TableUser.NAME, null, values);	

		// putting thumbnails datas
		for(Thumbnail thumbnail : user.thumbnails) {
			putThumbnail(db, thumbnail, user.id);
		}
		
		return id;
	}
	
	private long putThumbnail(SQLiteDatabase db, Thumbnail thumbnail, int userId) {
		ContentValues values = new ContentValues();
		values.put(TableThumbnail.COLUMN_USER_ID, userId);
		values.put(TableThumbnail.COLUMN_HREF, thumbnail.href);
		values.put(TableThumbnail.COLUMN_SIZE, thumbnail.size);
		
		return db.insert(TableThumbnail.NAME, null, values);	
	}
	
	synchronized public long setStatusAsLastSeen(Status status) {
		SQLiteDatabase db = this.getWritableDatabase();  

        ContentValues statusValues = new ContentValues();
        statusValues.put(TableStatus.COLUMN_LAST_SEEN, 1);
        statusValues.put(TableStatus.COLUMN_LAST_SEEN_AT_IN_MILLIS, System.currentTimeMillis());
        
        long id = db.update(TableStatus.NAME, statusValues, TableStatus.COLUMN_ID + " = ?", new String[] {status.id});
        
        db.close();

        notifyListenters();
        
        return id;
	}
	
	synchronized public long setOldestStatusesWereDownloaded(String appUserId) {
		SQLiteDatabase db = this.getWritableDatabase();  
		
		ContentValues values = new ContentValues();
		values.put(TableAppUser.COLUMN_OLDEST_STATUSES_WERE_DOWNLOADED, 1);
		
		long id = db.update(TableAppUser.NAME, values, TableAppUser.COLUMN_ID + " = ?", new String[] {appUserId});
		
		db.close();
		
		return id;
	}
	
	synchronized public boolean getOldestStatusesWereDownloaded(String appUserId) {
		boolean oledestStatusesWereDownloaded = false;
		
		SQLiteDatabase db = this.getReadableDatabase();  
		
		Cursor cursor = db.query(TableAppUser.NAME, null, TableAppUser.COLUMN_ID + " = ?", new String[] {appUserId}, null, null, null);
        if (cursor.moveToFirst()) {  
        	int value = cursor.getInt(cursor.getColumnIndex(TableAppUser.COLUMN_OLDEST_STATUSES_WERE_DOWNLOADED));
        	oledestStatusesWereDownloaded = (value == 0) ? false : true;
        }
        
        cursor.close();  
        db.close();
        
        return oledestStatusesWereDownloaded;
	}

	private void notifyListenters() {
		if(mListeners != null) {
			for(WeakReference<DbHelperListener> wrListener : mListeners) {
				DbHelperListener listener = wrListener.get();
				if(listener != null) {
					listener.hasNewStatus();
				}
			}
		}
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
        		append(TableStatus.COLUMN_CREATED_AT_IN_MILLIS).
        		append(") FROM ").
        		append(TableStatus.NAME).
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
