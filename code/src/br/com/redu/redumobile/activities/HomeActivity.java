package br.com.redu.redumobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.db.DbHelperHolder;
import br.com.redu.redumobile.fragments.HomeFragment;
import br.com.redu.redumobile.fragments.HomeFragment.Type;
import br.com.redu.redumobile.tasks.RefreshNotificationsTask;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.analytics.AnalyticsManager;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeActivity extends BaseActivity implements DbHelperHolder {

	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	private static final int DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES = 1;
	
	static final int NUM_ITEMS = 3;
	
	static final int ITEM_NEW_LECTURES = 0;
	static final int ITEM_WALL = 1;
	static final int ITEM_LAST_SEEN_STATUS = 2;

	private DbHelper mDbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_home);

		setActionBarTitle("Redu");

		startService(new Intent(this, RefreshNotificationsTask.class));
		
		// START BuzzNotify
		int openAppStatus = AnalyticsManager.onOpenApp(this);
		if (openAppStatus == AnalyticsManager.OPEN_APP_FIRST_TIME) {
			SchedulerManager.getInstance().saveTask(this, "*/" + DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES + " * * * *", RefreshNotificationsTask.class);
			SchedulerManager.getInstance().restart(this, RefreshNotificationsTask.class);
		} else if (openAppStatus == AnalyticsManager.OPEN_APP_UPGRADE) {
			SchedulerManager.getInstance().restartAll(getApplicationContext());
		}
		// END BuzzNotify

		final ViewPager vp = (ViewPager) findViewById(R.id.vp);
		vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
		
		PageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
		indicator.setViewPager(vp);
		
		int itemChecked = getIntent().getIntExtra(ITEM_EXTRA_PARAM, ITEM_WALL);
		indicator.setCurrentItem(itemChecked);
		
//		showWebDialog("Redu Mobile", "http://redu.com.br/#modal-sign-up");
	}
	
	private void showWebDialog(String title, String url) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(title);
		WebView wv = new WebView(this);
		wv.getSettings().setJavaScriptEnabled(true);
//		wv.setHorizontalScrollBarEnabled(false);
		wv.loadUrl(url);

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		alert.setView(wv);

		alert.show();
	}

	class MainAdapter extends FragmentPagerAdapter {
		private final HomeFragment[] fragments;

		public MainAdapter(FragmentManager fm) {
			super(fm);

			fragments = new HomeFragment[NUM_ITEMS];

			Bundle b;
			
			fragments[ITEM_WALL] = new HomeFragment();
			b = new Bundle();
			b.putSerializable(Type.class.getName(), Type.Wall);
			fragments[ITEM_WALL].setArguments(b);
			
			fragments[ITEM_NEW_LECTURES] = new HomeFragment();
			b = new Bundle();
			b.putSerializable(Type.class.getName(), Type.NewLectures);
			fragments[ITEM_NEW_LECTURES].setArguments(b);

			fragments[ITEM_LAST_SEEN_STATUS] = new HomeFragment();
			b = new Bundle();
			b.putSerializable(Type.class.getName(), Type.LastSeen);
			fragments[ITEM_LAST_SEEN_STATUS].setArguments(b);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragments[position].getTitle();
		}
		
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			return fragments[position];
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mDbHelper = DbHelper.getInstance(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mDbHelper.close();
	}

	@Override
	public DbHelper getDbHelper() {
		return mDbHelper;
	}
}
