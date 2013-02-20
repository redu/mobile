package br.com.redu.redumobile.activities;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.buzzbox.mob.android.scheduler.analytics.AnalyticsManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioGroup;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.EnvironmentFragment;
import br.com.redu.redumobile.fragments.WallFragment;
import br.com.redu.redumobile.tasks.RefreshNotificationsTask;

public class HomeActivity extends FragmentActivity {

	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	static final int NUM_ITEMS = 2;

	static final int ITEM_WALL = 0;
	static final int ITEM_ENVIRONMENTS = 1;
	static final int ITEM_NEW_MODULES = 2;
	static final int ITEM_LAST_SAW_STATUS = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// 1. call BuzzBox Analytics
		int openAppStatus = AnalyticsManager.onOpenApp(this);

		// 2. add the Task to the Scheduler
		if (openAppStatus == AnalyticsManager.OPEN_APP_FIRST_TIME) {
			// register the Task when the App in installed
			SchedulerManager.getInstance().saveTask(this, "*/15 * * * *", RefreshNotificationsTask.class);
			SchedulerManager.getInstance().restart(this, RefreshNotificationsTask.class);
		} else if (openAppStatus == AnalyticsManager.OPEN_APP_UPGRADE) {
			// restart on upgrade
			SchedulerManager.getInstance().restartAll(getApplicationContext());
		}

		final ViewPager vp = (ViewPager) findViewById(R.id.vp);
		final RadioGroup rg = (RadioGroup) findViewById(R.id.rg);

		vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int page) {
				switch (page) {
				case ITEM_WALL:
					rg.check(R.id.rb_home);
					break;

				case ITEM_ENVIRONMENTS:
					rg.check(R.id.rb_explorar);
					break;
				case ITEM_NEW_MODULES:
					rg.check(R.id.rb_new_modules);
					break;
					
				case ITEM_LAST_SAW_STATUS:
					rg.check(R.id.rb_last_saw_status);
					break;
				}
			}

			@Override
			public void onPageScrolled(int position, float offset,
					int offsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int page) {
			}
		});

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_home:
					vp.setCurrentItem(ITEM_WALL);
					break;

				case R.id.rb_explorar:
					vp.setCurrentItem(ITEM_ENVIRONMENTS);
					break;
				}
			}
		});

		int itemChecked = getIntent().getIntExtra(ITEM_EXTRA_PARAM, ITEM_WALL);
		vp.setCurrentItem(itemChecked);
	}

	class MainAdapter extends FragmentStatePagerAdapter {
		private final Fragment[] items;

		public MainAdapter(FragmentManager fm) {
			super(fm);

			items = new Fragment[NUM_ITEMS];
			items[ITEM_WALL] = new WallFragment();
			items[ITEM_ENVIRONMENTS] = new EnvironmentFragment();
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
