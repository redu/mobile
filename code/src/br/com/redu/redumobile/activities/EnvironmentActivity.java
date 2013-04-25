package br.com.redu.redumobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import br.com.developer.redu.models.Environment;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.CoursesAndSpacesFragment;
import br.com.redu.redumobile.fragments.CoursesAndSpacesFragment.OnSpaceSelectedListener;

public class EnvironmentActivity extends BaseActivity implements OnSpaceSelectedListener {
	
	public static final String EXTRA_ENVIRONMENT = "EXTRA_ENVIRONMENT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_environment);
		
		Bundle extras = getIntent().getExtras();
		Environment environment = (Environment) extras.getSerializable(EXTRA_ENVIRONMENT);
	
		Bundle args = new Bundle();
		args.putSerializable(CoursesAndSpacesFragment.EXTRAS_ENVIRONMENT, environment);

		Fragment coursesAndServicesFragment = new CoursesAndSpacesFragment();
		coursesAndServicesFragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, coursesAndServicesFragment, CoursesAndSpacesFragment.class.getName());
		transaction.commit();		
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (getSupportFragmentManager().findFragmentByTag(CoursesAndSpacesFragment.class.getName()) != null){
			fm.beginTransaction().remove(fm.findFragmentByTag(CoursesAndSpacesFragment.class.getName())).commit();
		}else{
			finish();
		}
	}

	@Override
	public void onSpaceSelected(Space space) {
		Intent it = new Intent(EnvironmentActivity.this, HomeSpaceActivity.class);
		it.putExtra(Space.class.getName(), space);
		startActivity(it);
	}
}
