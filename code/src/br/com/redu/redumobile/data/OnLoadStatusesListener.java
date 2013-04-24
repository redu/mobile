package br.com.redu.redumobile.data;

public interface OnLoadStatusesListener {
	
	public void onStart();
	public void onComplete();
	public void onError(Exception e);

}