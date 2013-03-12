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
import br.com.redu.redumobile.fragments.EnvironmentFragment;
import br.com.redu.redumobile.fragments.EnvironmentFragment.OnEnvironmentSelectedListener;

public class EnvironmentActivity extends BaseActivity implements OnSpaceSelectedListener, OnEnvironmentSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_environment);
		
		setActionBarTitle("Ambientes");
		
		// Create new fragment and transaction
		Fragment environmentFragment = new EnvironmentFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.fragment_container, environmentFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	@Override
	public void onEnvironmentSelected(Environment environment) {
		// Create new fragment and transaction
		Fragment coursesAndServicesFragment = new CoursesAndSpacesFragment(environment);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.fragment_container, coursesAndServicesFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();	
	}

	@Override
	public void onSpaceSelected(Space space) {
		Intent it = new Intent(EnvironmentActivity.this, HomeSpaceActivity.class);
		it.putExtra(Space.class.getName(), space);
		startActivity(it);
	}
}
