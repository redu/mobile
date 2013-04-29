package br.com.redu.redumobile;

import android.app.Application;
import android.content.Context;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.util.PinCodeHelper;

public class ReduApplication extends Application {

	private static final String CONSUMER_KEY = "YzbH0ulBcOjXSPtmhJuEHNFFf6eZGiamQeOBQhU1";
	private static final String CONSUMER_SECRET_KEY = "kUdQsrimVZqgS7u1JuCnMGvARWhmiLWcbrZKwYO8";

//	private static final String USER_PIN = "hxEhgW4RY2WOI0q8Gcfh";

	static private DefaultReduClient reduClient;
	static private User user;

	static public DefaultReduClient getReduClient(Context context) {
		if(reduClient == null) {
			DefaultReduClient tempClient = new DefaultReduClient(CONSUMER_KEY, CONSUMER_SECRET_KEY);

			String pinCode = PinCodeHelper.getPinCode(context);
			if(pinCode != null) {
				tempClient.initClient(pinCode);
				reduClient = tempClient;
			}

			return tempClient;

		} else {
			return reduClient;
		}
	}

	static public User getUser(Context context) {
		if(user == null) {
			user = getReduClient(context).getMe();
		}
		return user;
	}
}