package br.com.redu.redumobile.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WebUtil {
	public static boolean checkConnection(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		return (netInfo != null && netInfo.isConnectedOrConnecting());
	}
}
