package br.com.redu.redumobile.data;

import java.util.ArrayList;
import java.util.List;

public class LoadingStatusesManager {
	private static final List<OnLoadStatusesListener> mListeners = new ArrayList<OnLoadStatusesListener>();

	public static void add(OnLoadStatusesListener listener) {
		mListeners.add(listener);
	}
	
	public static void clear() {
		mListeners.clear();
	}

	public static void notifyOnStart() {
		for (OnLoadStatusesListener listener : mListeners) {
			listener.onStart();
		}
	}

	public static void notifyOnComplete() {
		for (OnLoadStatusesListener listener : mListeners) {
			listener.onComplete();
		}
	}

	public static void notifyOnError(Exception e) {
		for (OnLoadStatusesListener listener : mListeners) {
			listener.onError(e);
		}
	}
}
