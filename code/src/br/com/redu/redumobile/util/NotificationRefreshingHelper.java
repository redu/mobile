package br.com.redu.redumobile.util;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationRefreshingHelper {

	private static final String PREFS_NAME = "NotificationRefreshingHelper";
	
	private static final String PREFS_TIMESTAMP_KEY = "timestamp";
	
	
	static public void setLastStatusTimestamp(Context ctx, long timestamp) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit()
		.putLong(PREFS_TIMESTAMP_KEY, timestamp)
		.commit();
	}
			
	static public long getLastStatusTimestamp(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getLong(PREFS_TIMESTAMP_KEY, 0);
	}
	
	static public void clear(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit()
		.clear()
		.commit();
	}

}
