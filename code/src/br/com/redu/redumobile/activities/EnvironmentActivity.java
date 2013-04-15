package br.com.redu.redumobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import br.com.developer.redu.models.Environment;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.CoursesAndSpacesFragment;
import br.com.redu.redumobile.fragments.CoursesAndSpacesFragment.OnSpaceSelectedListener;
import br.com.redu.redumobile.fragments.EnvironmentFragment;
import br.com.redu.redumobile.fragments.EnvironmentFragment.OnEnvironmentSelectedListener;

public class EnvironmentActivity extends BaseActivity implements OnSpaceSelectedListener, OnEnvironmentSelectedListener {
	
	FragmentManager fm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_environment);
		
		setActionBarTitle("Ambientes");
		
		// Create new fragment and transaction
		Fragment environmentFragment = new EnvironmentFragment();
		fm = getSupportFragmentManager();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.fragment_container, environmentFragment, EnvironmentFragment.class.getName());

		// Commit the transaction
		transaction.commit();
	}
	
	// metodo para não chamar outros ambientes.
	@Override
	public void onEnvironmentClicked(View v) {
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (fm.findFragmentByTag(CoursesAndSpacesFragment.class.getName()) != null){
			//Fragment environmentFragment = (EnvironmentFragment)fm.findFragmentByTag(EnvironmentFragment.class.getName());
			fm.beginTransaction().remove(fm.findFragmentByTag(CoursesAndSpacesFragment.class.getName())).commit();
		}else{
			finish();
		}
	}

	@Override
	public void onEnvironmentSelected(Environment environment) {
		Bundle args = new Bundle();
		args.putSerializable(CoursesAndSpacesFragment.EXTRAS_ENVIRONMENT, environment);

		Fragment coursesAndServicesFragment = new CoursesAndSpacesFragment();
		coursesAndServicesFragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, coursesAndServicesFragment, CoursesAndSpacesFragment.class.getName());
		transaction.commit();	
	}

	@Override
	public void onSpaceSelected(Space space) {
		Intent it = new Intent(EnvironmentActivity.this, HomeSpaceActivity.class);
		it.putExtra(Space.class.getName(), space);
		startActivity(it);
	}
}
