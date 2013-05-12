package br.com.redu.redumobile.activities;

import java.util.ArrayList;

import org.scribe.exceptions.OAuthConnectionException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Environment;
import br.com.developer.redu.models.Folder;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.fragments.space.MorphologyFragment;
import br.com.redu.redumobile.fragments.space.SpaceWallFragment;
import br.com.redu.redumobile.fragments.space.SupportMaterialFragment;

import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class SpaceActivity extends DbHelperHolderActivity {

	public interface SupportMaterialFragmentListener {
	    void onSwitchToNextFragment(Folder folder);
	    void onBackToPreviousFragment(Folder folder);
	}

	public static final String EXTRAS_SPACE = "EXTRAS_SPACE";
	
	public static final String EXTRAS_SPACE_ID = "EXTRAS_SPACE_ID";
	public static final String EXTRAS_ENVIRONMENT_PATH = "EXTRAS_ENVIRONMENT_PATH";
	
	public static final String EXTRAS_ITEM_CHECKED = "ITEM_CHECKED";

	private static final String[] titles = new String[]{"Aulas", "Mural", "Material de Apoio"};
	
	static final int NUM_ITEMS = 3;

	static final int ITEM_MORPHOLOGY = 0;
	static final int ITEM_WALL = 1;
	static final int ITEM_SUPPORT_MATERIAL = 2;
	
    private PageIndicator mIndicator;
    private MainAdapter mAdapter;
    private ViewPager mVp;
    
    private Environment mEnvironment;
    private Space mSpace;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_home_space);
		
		final Bundle extras = getIntent().getExtras();
		mSpace = (Space) extras.get(EXTRAS_SPACE);
		
		// Se foi passado um objeto, é pq está na navegação normal, caso contrário, está no up
		if (mSpace != null) {
			init();
		} else {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						DefaultReduClient redu = ReduApplication.getReduClient(SpaceActivity.this);
						
						String spaceId = extras.getString(EXTRAS_SPACE_ID);
						String environmentId = extras.getString(EXTRAS_ENVIRONMENT_PATH);

						mSpace = redu.getSpace(spaceId);
						mEnvironment = redu.getEnvironment(environmentId);
					} catch (OAuthConnectionException e) {
						e.printStackTrace();
					}
					return null;
				}
	
				protected void onPostExecute(Void param) {
					if(mSpace != null && mEnvironment != null) {
						Bundle extras = new Bundle();
						extras.putSerializable(EnvironmentActivity.EXTRA_ENVIRONMENT, mEnvironment);
						setUpClass(EnvironmentActivity.class, extras);
						init();
					}

				};
			}.execute();
		}
		
	}
	
	private void init() {
		setActionBarTitle(mSpace.name);

		mVp = (ViewPager) findViewById(R.id.vp2);
		mAdapter = new MainAdapter(getSupportFragmentManager());
		mVp.setAdapter(mAdapter);
		
		mIndicator = (TitlePageIndicator) findViewById(R.id.titles2);
		mIndicator.setViewPager(mVp);
		
		int itemChecked = getIntent().getIntExtra(EXTRAS_ITEM_CHECKED, ITEM_WALL);
		mIndicator.setCurrentItem(itemChecked);
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
			
			args = new Bundle();
			args.putSerializable(SupportMaterialFragment.EXTRAS_SPACE, mSpace);
			items[ITEM_SUPPORT_MATERIAL].setArguments(args);
			
			args = new Bundle();
			args.putSerializable(MorphologyFragment.EXTRAS_SPACE, mSpace);
			items[ITEM_MORPHOLOGY].setArguments(args);
			
			SupportMaterialFragmentListener smfl = new SupportMaterialFragmentListener() {
				@Override
				public void onSwitchToNextFragment(Folder folder) {
					fm.beginTransaction().remove(items[ITEM_SUPPORT_MATERIAL]).commit();
					
					Bundle args = new Bundle();
					args.putSerializable(SupportMaterialFragment.EXTRAS_FOLDER, folder);
					args.putSerializable(SupportMaterialFragment.EXTRAS_SPACE, mSpace);

					SupportMaterialFragment sm = new SupportMaterialFragment();
					sm.setArguments(args);
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
}
