package br.com.redu.redumobile.activities;

import android.os.Bundle;
import br.com.redu.redumobile.db.DbHelper;

public abstract class DbHelperHolderActivity extends BaseActivity {

	private DbHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState, int layoutResID) {
		super.onCreate(savedInstanceState, layoutResID);
		init();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	private void init() {
		mDbHelper = DbHelper.getInstance(this);
	}

	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	public DbHelper getDbHelper() {
		return mDbHelper;
	}
}
