package br.com.redu.redumobile.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WebCachedImageView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Course;
import br.com.developer.redu.models.Environment;
import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.CoursesExpandableListAdapter;
import br.com.redu.redumobile.adapters.SubjectExpandableListAdapter;

public class CoursesAndSpacesFragment extends Fragment {
	
	public static final String EXTRAS_ENVIRONMENT = "EXTRAS_ENVIRONMENT";
	
	private List<Course> mEnrollmentedCourses;
	private List<List<Space>> mSpaces;

	private Environment mEnvironment;
	private Space mSpace;

	private TextView mTvEmptyList;
	
	private TextView mTvEnvironment;
	private WebCachedImageView mIvThumbnail;
	private ProgressBar mProgressBar;
	private ExpandableListView mListView;
	private CoursesExpandableListAdapter mAdapter;
	
	private OnSpaceSelectedListener mListener;

	public interface OnSpaceSelectedListener {
        public void onSpaceSelected(Space space);
    }
	
	public CoursesAndSpacesFragment() {

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mEnvironment = (Environment) getArguments().getSerializable(EXTRAS_ENVIRONMENT);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View v = inflater.inflate(R.layout.fragment_courses, container, false);

		mProgressBar = (ProgressBar) v.findViewById(R.id.pb);
		mTvEmptyList = (TextView) v.findViewById(R.id.tv_empty_list);
		mTvEnvironment = (TextView) v.findViewById(R.id.tvEnvironment);
		mIvThumbnail = (WebCachedImageView) v.findViewById(R.id.iv_thumbnail);
		
		mListView = (ExpandableListView) v.findViewById(R.id.list);
		mListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
					mSpace = (Space) mAdapter.getChild(groupPosition, childPosition);
					mListener.onSpaceSelected(mSpace);
				return false;
			}
		});		
		
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getReduClient(getActivity());

				mEnrollmentedCourses = new ArrayList<Course>();
				mSpaces = new ArrayList<List<Space>>();
				
				List<Course> courses = redu.getCoursesByEnvironment(mEnvironment.path);
				if(courses != null) {
					for(Course course : courses) {
						List<Space> spacesByCourse;
						try {
							spacesByCourse = redu.getSpacesByCourse(course.id);
						} catch(OAuthConnectionException e) {
							// usuario nao matriculado no curso
							e.printStackTrace();
							spacesByCourse = null;
						}

						if(spacesByCourse != null) {
							mEnrollmentedCourses.add(course);
							mSpaces.add(spacesByCourse);
						}
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				if (getActivity() != null){
					mTvEnvironment.setText(mEnvironment.name);
					mTvEnvironment.setVisibility(View.VISIBLE);
					
					mIvThumbnail.setImageUrl(mEnvironment.getThumbnailUrl());
					mIvThumbnail.setVisibility(View.VISIBLE);
					
					if(mEnrollmentedCourses.isEmpty()) {
						mTvEmptyList.setVisibility(View.VISIBLE);
					} else {
						mAdapter = new CoursesExpandableListAdapter(getActivity(), mEnrollmentedCourses, mSpaces);
						mListView.setAdapter(mAdapter);
					}
				}
				
				mProgressBar.setVisibility(View.GONE);
			};

		}.execute();
		
		return v;
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSpaceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCourseSelectedListener");
        }
    }
    
    class EnrollmentTask extends AsyncTask<Void, Void, Void> {
		
		protected Void doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient(getActivity());
			
			/*mEnrollment = new ArrayList<Subject>();
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
			}*/
			return null;
		}

		protected void onPostExecute(Void result) {
			if (getActivity() != null) {
				/*mAdapter = new SubjectExpandableListAdapter(getActivity(), mEnrollmentedSubjects, mLecture, mSpace);
				mExpListView.setAdapter(mAdapter);
				mExpListView.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);*/
			}
		};
	}

}
