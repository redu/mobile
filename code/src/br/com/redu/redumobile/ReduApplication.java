package br.com.luismedeiros.redutest;

import br.com.developer.redu.DefaultReduClient;
import android.app.Application;

public class ReduApplication extends Application {

	private static final String CONSUMER_KEY = "YzbH0ulBcOjXSPtmhJuEHNFFf6eZGiamQeOBQhU1";
	private static final String CONSUMER_SECRET_KEY = "kUdQsrimVZqgS7u1JuCnMGvARWhmiLWcbrZKwYO8";
	private static final String USER_PIN = "Zogleqh6SKd4vCDuQkMQ";

	static private DefaultReduClient redu;
	
	static public DefaultReduClient getClient() {
		if(redu == null) {
			redu = new DefaultReduClient(CONSUMER_KEY, CONSUMER_SECRET_KEY);
			redu.initClient(USER_PIN);
		}
		return redu;
	}
}
