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
    	
    	if(status.created_at_in_millis == 0) {
    		try {
    			Date date = DateUtil.dfIn.parse(status.created_at);
				status.created_at_in_millis = date.getTime();
				formattedCreatAt = DateUtil.dfOut.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
				formattedCreatAt = null;
			}
    	}
    	
    	return formattedCreatAt;
    }
}
