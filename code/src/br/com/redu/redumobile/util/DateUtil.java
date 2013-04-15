package br.com.redu.redumobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.developer.redu.models.Status;

public class DateUtil {
	public static final SimpleDateFormat dfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
	public static final SimpleDateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
	
    public static String getFormattedStatusCreatedAt(Status status) {
    	String formattedCreatAt = null;
    	
    	if(status.createdAtInMillis == 0) {
    		try {
    			status.createdAtInMillis = DateUtil.dfIn.parse(status.created_at).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}

    	formattedCreatAt = DateUtil.dfOut.format(new Date(status.createdAtInMillis));
    	
    	return formattedCreatAt;
    }
}
