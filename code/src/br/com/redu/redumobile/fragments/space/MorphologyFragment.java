package br.com.redu.redumobile.fragments.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.NewModuleActivity;
import br.com.redu.redumobile.adapters.SubjectExpandableListAdapter;

public class MorphologyFragment extends Fragment {
	
	public static final String EXTRAS_SPACE = "EXTRAS_SPACE";
	
	private Space mSpace;
	
	private List<Subject> mEnrollmentedSubjects;
	private List<List<Lecture>> mLecture;
	
	private ExpandableListView mExpListView;
	private SubjectExpandableListAdapter mAdapter;
	
	private ProgressBar mProgressBar;
	private TextView mTvEmpytMsg;
	
	
	public MorphologyFragment() {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.fragment_mophology, container, false);
		Button ibModulo= (Button)v.findViewById(R.id.btNovoModulo);
		mProgressBar = (ProgressBar)v.findViewById(R.id.pb);
		mTvEmpytMsg = (TextView)v.findViewById(R.id.elv_subject_empyt);
		ibModulo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it = new Intent(getActivity(), NewModuleActivity.class);
				it.putExtra(Space.class.getName(), mSpace);
				startActivity(it);
			}
		});
		
		mSpace = (Space) getArguments().get(EXTRAS_SPACE);
				
		mExpListView = (ExpandableListView) v.findViewById(R.id.elvSubject);
		
		new LoadUserTask().execute();
		
		return v;
	}
	
	class LoadUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient(getActivity());
			return redu.getMe();
		}
	
		protected void onPostExecute(User user) {
//			((TextView) v.findViewById(R.id.details)).setText(user.first_name + " " + user.last_name + ", ");
//			mUser = user;

			new LoadSubjectsTask().execute();
		};
	}
	
	/*class LoadCoursesTask extends AsyncTask<Void, Void, List<br.com.developer.redu.models.Course>> {
		protected List<br.com.developer.redu.models.Course> doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getClient();
			List<br.com.developer.redu.models.Environment> environments = redu.getEnvironments();
			return redu.getCoursesByEnvironment(environments.get(0).id);
		}
	
		protected void onPostExecute(User user) {
//			((TextView) v.findViewById(R.id.details)).setText(user.first_name + " " + user.last_name + ", ");
			mUser = user;

			new LoadSubjectsTask(mCurrentPage).execute();
		};
	}*/
	
	class LoadSubjectsTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient(getActivity());
			
			mEnrollmentedSubjects = new ArrayList<Subject>();
			List<Subject> subjects = redu.getSubjectsBySpace(mSpace.id);
			
			mLecture = new ArrayList<List<Lecture>>();
			
			if(subjects != null) {
				subjects.removeAll(Collections.singleton(null));
				for(Subject subject : subjects) {
					List<Lecture> lectureBySubject;
					try {
						lectureBySubject = redu.getLecturesBySubject(subject.id);
					} catch(OAuthConnectionException e) {
						// usuario nao matriculado no curso
						e.printStackTrace();
						lectureBySubject = null;
					}

					if(lectureBySubject != null) {
						mEnrollmentedSubjects.add(subject);
						mLecture.add(lectureBySubject);
					}
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (getActivity() != null) {
				mAdapter = new SubjectExpandableListAdapter(getActivity(), mEnrollmentedSubjects, mLecture, mSpace);
				if (mAdapter.getGroupCount() != 0){
					mExpListView.setAdapter(mAdapter);
					mExpListView.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);
				}else{
					mTvEmpytMsg.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);
				}
			}
		};
	}
}
