package br.com.redu.redumobile.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import br.com.developer.redu.models.Lecture;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.fragments.lectures.LectureWallFragment;

public class LectureWallActivity extends DbHelperHolderActivity {

	public static final String EXTRAS_LECTURE = "EXTRAS_LECTURE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_lecture_wall);
		
		Bundle extras = getIntent().getExtras();
		Lecture lecture = (Lecture) extras.getSerializable(EXTRAS_LECTURE);
	
		Bundle args = new Bundle();
		args.putSerializable(LectureWallFragment.EXTRAS_LECTURE, lecture);

		Fragment fragment = new LectureWallFragment();
		fragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_container, fragment);
		transaction.commit();
	}
}
