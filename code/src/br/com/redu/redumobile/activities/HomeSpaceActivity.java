package br.com.redu.redumobile.activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import br.com.developer.redu.models.Folder;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.db.DbHelperHolder;
import br.com.redu.redumobile.fragments.space.MorphologyFragment;
import br.com.redu.redumobile.fragments.space.SpaceWallFragment;
import br.com.redu.redumobile.fragments.space.SupportMaterialFragment;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeSpaceActivity extends BaseActivity implements DbHelperHolder {

	public interface SupportMaterialFragmentListener {
	    void onSwitchToNextFragment(Folder folder);
	    void onBackToPreviousFragment(Folder folder);
	}

	public static final String ITEM_EXTRA_PARAM = "ITEM_CHECKED";

	private static final String[] titles = new String[]{"Aulas", "Mural", "Material de Apoio"};
	
	static final int NUM_ITEMS = 3;

	static final int ITEM_MORPHOLOGY = 0;
	static final int ITEM_WALL = 1;
	static final int ITEM_SUPPORT_MATERIAL = 2;
	
	private DbHelper mDbHelper;

    private PageIndicator mIndicator;
    public MainAdapter mAdapter;
    private ViewPager mVp;
    
    private Space mSpace;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_home_space);
		
		Bundle extras = getIntent().getExtras();
		mSpace = (Space) extras.get(Space.class.getName());
		int itemChecked = extras.getInt(ITEM_EXTRA_PARAM, ITEM_WALL);
		
		setActionBarTitle(mSpace.name);

		mVp = (ViewPager) findViewById(R.id.vp2);
		mAdapter = new MainAdapter(getSupportFragmentManager());
		mVp.setAdapter(mAdapter);
		
		mIndicator = (TitlePageIndicator) findViewById(R.id.titles2);
		mIndicator.setViewPager(mVp);
		
		mIndicator.setCurrentItem(itemChecked);
		
		mDbHelper = DbHelper.getInstance(this);
	}
    
	@Override
	protected void onRestart() {
		super.onRestart();
		mAdapter.notifyDataSetChanged();
	}

	class MainAdapter extends FragmentStatePagerAdapter {
		private final Fragment[] items;
		private ArrayList<SupportMaterialFragment> materialFragments;

		public MainAdapter(final FragmentManager fm) {
			super(fm);
			items = new Fragment[NUM_ITEMS];
			materialFragments = new ArrayList<SupportMaterialFragment>();
			items[ITEM_MORPHOLOGY] = new MorphologyFragment();
			items[ITEM_WALL] = new SpaceWallFragment();
			items[ITEM_SUPPORT_MATERIAL] = new SupportMaterialFragment();
			materialFragments.add((SupportMaterialFragment)items[ITEM_SUPPORT_MATERIAL]);

			Bundle args = new Bundle();
			args.putSerializable(SpaceWallFragment.SPACE_EXTRAS, mSpace);
			items[ITEM_WALL].setArguments(args);
			
			SupportMaterialFragmentListener smfl = new SupportMaterialFragmentListener() {
				@Override
				public void onSwitchToNextFragment(Folder folder) {
					fm.beginTransaction().remove(items[ITEM_SUPPORT_MATERIAL]).commit();
					SupportMaterialFragment sm = new SupportMaterialFragment(folder);
					sm.setListener(this);
					items[ITEM_SUPPORT_MATERIAL] = sm;
					materialFragments.add((SupportMaterialFragment)items[ITEM_SUPPORT_MATERIAL]);
                    notifyDataSetChanged();
				}
				
				public void onBackToPreviousFragment(Folder folder){
					materialFragments.remove((SupportMaterialFragment)items[ITEM_SUPPORT_MATERIAL]);
					fm.beginTransaction().remove(items[ITEM_SUPPORT_MATERIAL]).commit();
					items[ITEM_SUPPORT_MATERIAL] = materialFragments.get(materialFragments.size()-1);
					
					notifyDataSetChanged();
				}
			};
			
			((SupportMaterialFragment) items[ITEM_SUPPORT_MATERIAL]).setListener(smfl);
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
		public int getItemPosition(Object object) {
			if (object instanceof SupportMaterialFragment) {
	            return POSITION_NONE;
			}
			
	        return POSITION_UNCHANGED;
		}
	}
	
	@Override
	public void onBackPressed() {
		if (mVp.getCurrentItem() == ITEM_SUPPORT_MATERIAL){
			//TODO Fazer back.
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		mDbHelper.close();
		super.onDestroy();
	}

	@Override
	public DbHelper getDbHelper() {
		return mDbHelper;
	}
	
}
