package br.com.redu.redumobile.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.EnvironmentFragment;
import br.com.redu.redumobile.fragments.WallFragment;
import br.com.redu.redumobile.tasks.RefreshNotificationsTask;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.analytics.AnalyticsManager;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeActivity extends BaseActivity {

	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	private static final String[] titles = new String[]{"Mural", "Ambientes", "Novas aulas"};
	
	static final int NUM_ITEMS = 2;

	static final int ITEM_WALL = 0;
	static final int ITEM_ENVIRONMENTS = 1;
	static final int ITEM_NEW_MODULES = 2;
	static final int ITEM_LAST_SAW_STATUS = 3;

    private PageIndicator mIndicator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// BuzzNotify
		int openAppStatus = AnalyticsManager.onOpenApp(this);
		if (openAppStatus == AnalyticsManager.OPEN_APP_FIRST_TIME) {
			SchedulerManager.getInstance().saveTask(this, "*/15 * * * *", RefreshNotificationsTask.class);
			SchedulerManager.getInstance().restart(this, RefreshNotificationsTask.class);
		} else if (openAppStatus == AnalyticsManager.OPEN_APP_UPGRADE) {
			SchedulerManager.getInstance().restartAll(getApplicationContext());
		}

		final ViewPager vp = (ViewPager) findViewById(R.id.vp);
		vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
		
		mIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		mIndicator.setViewPager(vp);
		
		int itemChecked = getIntent().getIntExtra(ITEM_EXTRA_PARAM, ITEM_WALL);
		mIndicator.setCurrentItem(itemChecked);
	}

	class MainAdapter extends FragmentPagerAdapter {
		private final Fragment[] items;

		public MainAdapter(FragmentManager fm) {
			super(fm);

			items = new Fragment[NUM_ITEMS];
			items[ITEM_WALL] = new WallFragment();
			items[ITEM_ENVIRONMENTS] = new EnvironmentFragment();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
		
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public Fragment getItem(int position) {
			return items[position];
		}
	}

}
