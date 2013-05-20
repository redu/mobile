package br.com.redu.redumobile.activities;

import org.scribe.exceptions.OAuthConnectionException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Lecture;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.fragments.lectures.LectureWallFragment;

public class LectureWallActivity extends DbHelperHolderActivity {

	public static final String EXTRAS_LECTURE = "EXTRAS_LECTURE";

	public static final String EXTRAS_SUBJECT_ID = "EXTRAS_SUBJECT_ID";
	public static final String EXTRAS_LECTURE_ID = "EXTRAS_LECTURE_ID";
	public static final String EXTRAS_SPACE_ID = "EXTRAS_SPACE_ID";
	public static final String EXTRAS_ENVIRONMENT_PATH = "EXTRAS_ENVIRONMENT_PATH";

	private Lecture mLecture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_lecture_wall);

		Bundle extras = getIntent().getExtras();
		mLecture = (Lecture) extras.getSerializable(EXTRAS_LECTURE);

		final String lectureId = extras.getString(EXTRAS_LECTURE_ID);
		final String subjectId = extras.getString(EXTRAS_SUBJECT_ID);
		final String spaceId = extras.getString(EXTRAS_SPACE_ID);
		final String environmentPath = extras
				.getString(EXTRAS_ENVIRONMENT_PATH);

		if (mLecture != null) {
			init();
		} else {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						DefaultReduClient redu = ReduApplication
								.getReduClient(LectureWallActivity.this);
						mLecture = redu.getLecture(lectureId);
						return null;
					} catch (OAuthConnectionException e) {
						e.printStackTrace();
						return null;
					}
				}

				protected void onPostExecute(Void param) {
					if (mLecture != null) {
						Bundle extrasToUp = new Bundle();
						extrasToUp.putSerializable(
								LectureActivity.EXTRAS_SUBJECT_ID, subjectId);
						extrasToUp.putSerializable(
								LectureActivity.EXTRAS_LECTURE_ID, lectureId);
						extrasToUp.putSerializable(
								LectureActivity.EXTRAS_SPACE_ID, spaceId);
						extrasToUp.putSerializable(
								LectureActivity.EXTRAS_ENVIRONMENT_PATH,
								environmentPath);
						setUpClass(LectureActivity.class, extrasToUp);

						init();
					}
				};
			}.execute();
		}

	}

	private void init() {
		setActionBarTitle(mLecture.name);

		Bundle args = new Bundle();
		args.putSerializable(LectureWallFragment.EXTRAS_LECTURE, mLecture);

		Fragment fragment = new LectureWallFragment();
		fragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.add(R.id.fragment_container, fragment);
		transaction.commit();

	}
}
