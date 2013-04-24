package br.com.redu.redumobile;

import android.app.Application;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.User;

public class ReduApplication extends Application {

	private static final String CONSUMER_KEY = "YzbH0ulBcOjXSPtmhJuEHNFFf6eZGiamQeOBQhU1";
	private static final String CONSUMER_SECRET_KEY = "kUdQsrimVZqgS7u1JuCnMGvARWhmiLWcbrZKwYO8";
	
	private static final String USER_PIN = "lRfdZWKxMPRgH4NLl7aS";
	private static String PIN = "lRfdZWKxMPRgH4NLl7aS";
	

	static private DefaultReduClient reduClient;
	static private User user;
	
	static public DefaultReduClient getReduClient() {
		if(reduClient == null) {
			reduClient = new DefaultReduClient(CONSUMER_KEY, CONSUMER_SECRET_KEY);
			reduClient.initClient(PIN);
		}
		return reduClient;
	}
	
	static public DefaultReduClient getReduClientPIN(){
		if(reduClient == null) {
			reduClient = new DefaultReduClient(CONSUMER_KEY, CONSUMER_SECRET_KEY);
		}
		return reduClient;
	}
	
	public static String getPIN() {
		return PIN;
	}

	public static void setPIN(String pIN) {
		PIN = pIN;
		reduClient.initClient(PIN);
	}

	static public User getUser() {
		if(user == null) {
			user = getReduClient().getMe();
		}
		return user;
	}
}
