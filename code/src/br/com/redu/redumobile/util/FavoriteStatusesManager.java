package br.com.redu.redumobile.util;

import br.com.developer.redu.models.Status;
import android.content.Context;
import android.content.SharedPreferences;

public class FavoriteStatusesManager {

	private static final String PREFS_NAME = "AlarmManager";
	
	private static final String PREFS_NUMERO_APOLICE_KEY = "numeorApolice";
	private static final String PREFS_CODIGO_SUCURSAL_KEY = "codigoApolice";
	
	
	static public void addStatus(Context ctx, Status status) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit()
//		.putString(PREFS_NUMERO_APOLICE_KEY, apolice.numeroApolice)
//		.putString(PREFS_CODIGO_SUCURSAL_KEY, apolice.codigoSucursal)
		.commit();
	}
			
	static public Status getApoliceSetected(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String numApolice = prefs.getString(PREFS_NUMERO_APOLICE_KEY, null);
		String codApolice = prefs.getString(PREFS_CODIGO_SUCURSAL_KEY, null);
		
		return null;
	}
	
	static public void clear(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit()
		.clear()
		.commit();
	}

}
