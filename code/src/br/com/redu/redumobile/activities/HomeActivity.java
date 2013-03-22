package br.com.redu.redumobile.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.HomeFragment;
import br.com.redu.redumobile.fragments.HomeFragment.Type;
import br.com.redu.redumobile.tasks.RefreshNotificationsTask;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.analytics.AnalyticsManager;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeActivity extends BaseActivity {

	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	static final int NUM_ITEMS = 3;
	
	static final int ITEM_NEW_LECTURES = 0;
	static final int ITEM_WALL = 1;
	static final int ITEM_LAST_SEEN_STATUS = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_home);

		setActionBarTitle("Redu");
		
		// BuzzNotify
		int openAppStatus = AnalyticsManager.onOpenApp(this);
		if (openAppStatus == AnalyticsManager.OPEN_APP_FIRST_TIME) {
			SchedulerManager.getInstance().saveTask(this, "*/30 * * * *", RefreshNotificationsTask.class);
			SchedulerManager.getInstance().restart(this, RefreshNotificationsTask.class);
		} else if (openAppStatus == AnalyticsManager.OPEN_APP_UPGRADE) {
			SchedulerManager.getInstance().restartAll(getApplicationContext());
		}

		final ViewPager vp = (ViewPager) findViewById(R.id.vp);
		vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
		
		PageIndicator indicator = (TitlePageIndicator) findViewById(R.id.titles);
		indicator.setViewPager(vp);
		
		int itemChecked = getIntent().getIntExtra(ITEM_EXTRA_PARAM, ITEM_WALL);
		indicator.setCurrentItem(itemChecked);
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

}
