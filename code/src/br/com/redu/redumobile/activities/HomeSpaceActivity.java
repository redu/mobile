package br.com.redu.redumobile.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.space.MorphologyFragment;
import br.com.redu.redumobile.fragments.space.SpaceWallFragment;
import br.com.redu.redumobile.fragments.space.SupportMaterialFragment;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeSpaceActivity extends BaseActivity {
	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	private static final String[] titles = new String[]{"Aulas", "Mural", "Material de Apoio"};
	
	static final int NUM_ITEMS = 3;

	static final int ITEM_MORPHOLOGY = 0;
	static final int ITEM_WALL = 1;
	static final int ITEM_SUPPORT_MATERIAL = 2;
	
    private PageIndicator mIndicator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_space);
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		
		final ViewPager vp = (ViewPager) findViewById(R.id.vp2);
		vp.setAdapter(new MainAdapter(getSupportFragmentManager()));
		
		
		mIndicator = (TitlePageIndicator) findViewById(R.id.titles2);
		mIndicator.setViewPager(vp);
		
		Space space = (Space)getIntent().getExtras().get(Space.class.getName());
		setActionBarTitle("Disciplina "+space.name);
		
		int itemChecked = getIntent().getIntExtra(ITEM_EXTRA_PARAM, ITEM_WALL);
		mIndicator.setCurrentItem(itemChecked);
		
	}
	
	class MainAdapter extends FragmentPagerAdapter {
		private final Fragment[] items;

		public MainAdapter(FragmentManager fm) {
			super(fm);

			items = new Fragment[NUM_ITEMS];
			items[ITEM_MORPHOLOGY] = new MorphologyFragment();
			items[ITEM_WALL] = new SpaceWallFragment();
			items[ITEM_SUPPORT_MATERIAL] = new SupportMaterialFragment();
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
		
		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			super.notifyDataSetChanged();
		}
		
		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}
	}
	
}
