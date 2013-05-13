package br.com.redu.redumobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import br.com.developer.redu.models.Environment;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.CoursesAndSpacesFragment;
import br.com.redu.redumobile.fragments.CoursesAndSpacesFragment.OnSpaceSelectedListener;

public class EnvironmentActivity extends BaseActivity implements OnSpaceSelectedListener {
	
	public static final String EXTRA_ENVIRONMENT = "EXTRA_ENVIRONMENT";
	
	private Environment mEnvironment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_environment);
		
		Bundle args = new Bundle();
		args.putInt(HomeActivity.ITEM_EXTRA_PARAM, HomeActivity.ITEM_ENVIRONMENTS);
		
		setUpClass(HomeActivity.class, args);
		
		Bundle extras = getIntent().getExtras();
		mEnvironment = (Environment) extras.getSerializable(EXTRA_ENVIRONMENT);
	
		args = new Bundle();
		args.putSerializable(CoursesAndSpacesFragment.EXTRAS_ENVIRONMENT, mEnvironment);

		Fragment coursesAndServicesFragment = new CoursesAndSpacesFragment();
		coursesAndServicesFragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, coursesAndServicesFragment, CoursesAndSpacesFragment.class.getName());
		transaction.commit();		
	}

	@Override
	public void onSpaceSelected(Space space) {
		Intent it = new Intent(EnvironmentActivity.this, SpaceActivity.class);
		it.putExtra(SpaceActivity.EXTRAS_SPACE, space);
		startActivity(it);
	}
}
