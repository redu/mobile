package br.com.redu.redumobile;

import android.app.Application;
import br.com.developer.redu.DefaultReduClient;

public class ReduApplication extends Application {

	private static final String CONSUMER_KEY = "YzbH0ulBcOjXSPtmhJuEHNFFf6eZGiamQeOBQhU1";
	private static final String CONSUMER_SECRET_KEY = "kUdQsrimVZqgS7u1JuCnMGvARWhmiLWcbrZKwYO8";
	private static final String USER_PIN = "W2JJlqZTeZ8QSmvuQC3z";

	static private DefaultReduClient redu;
	
	static public DefaultReduClient getClient() {
		if(redu == null) {
			redu = new DefaultReduClient(CONSUMER_KEY, CONSUMER_SECRET_KEY);
			redu.initClient(USER_PIN);
		}
		return redu;
	}
}
