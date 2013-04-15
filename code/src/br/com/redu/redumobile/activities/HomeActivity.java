package br.com.redu.redumobile.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import br.com.redu.redumobile.fragments.HomeLastSeenFragment;
import br.com.redu.redumobile.fragments.HomeNewLecturesFragment;
import br.com.redu.redumobile.fragments.HomeWallFragment;
import br.com.redu.redumobile.tasks.LoadStatusesFromWebTask;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.analytics.AnalyticsManager;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeActivity extends BaseActivity implements DbHelperHolder {

	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	private static final int DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES = 30;
	
	static final int NUM_ITEMS = 3;
	
	static final int ITEM_NEW_LECTURES = 0;
	static final int ITEM_WALL = 1;
	static final int ITEM_LAST_SEEN_STATUS = 2;

	private DbHelper mDbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_home);

		setActionBarTitle("Redu");

		// START BuzzNotify
		int openAppStatus = AnalyticsManager.onOpenApp(this);
		if (openAppStatus == AnalyticsManager.OPEN_APP_FIRST_TIME) {
			SchedulerManager.getInstance().saveTask(this, "*/" + DELAY_TO_CHECK_NOTIFICATIONS_IN_MINUTES + " * * * *", LoadStatusesFromWebTask.class);
			SchedulerManager.getInstance().restart(this, LoadStatusesFromWebTask.class);
		} else if (openAppStatus == AnalyticsManager.OPEN_APP_UPGRADE) {
			SchedulerManager.getInstance().restartAll(getApplicationContext());
		}
		SchedulerManager.getInstance().runNow(this, LoadStatusesFromWebTask.class, 0);
		// END BuzzNotify

		mDbHelper = DbHelper.getInstance(this);
		
		final ViewPager vp = (ViewPager) findViewById(R.id.vp);
		vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
		
		PageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
		indicator.setViewPager(vp);
		
		int itemChecked = getIntent().getIntExtra(ITEM_EXTRA_PARAM, ITEM_WALL);
		indicator.setCurrentItem(itemChecked);
		
//		showWebDialog("Redu Mobile", "http://redu.com.br/#modal-sign-up");

	}
	
	@SuppressLint("SetJavaScriptEnabled")
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

			fragments[ITEM_NEW_LECTURES] = new HomeNewLecturesFragment();
			fragments[ITEM_WALL] = new HomeWallFragment();
			fragments[ITEM_LAST_SEEN_STATUS] = new HomeLastSeenFragment();
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
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

	@Override
	public DbHelper getDbHelper() {
		return mDbHelper;
	}
}
