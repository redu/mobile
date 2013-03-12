package br.com.redu.redumobile.fragments;

import java.util.ArrayList;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Course;
import br.com.developer.redu.models.Environment;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.CoursesExpandableListAdapter;

public class CoursesAndSpacesFragment extends Fragment {
	private List<Course> mEnrollmentedCourses;
	private List<List<Space>> mSpaces;

	private Environment mEnvironment;
	private Space mSpace;
	
	private ExpandableListView mListView;
	
	private CoursesExpandableListAdapter mAdapter;
	
	private OnSpaceSelectedListener mListener;

	public interface OnSpaceSelectedListener {
        public void onSpaceSelected(Space space);
    }
	
	public CoursesAndSpacesFragment() {
	}
	
	public CoursesAndSpacesFragment(Environment environment) {
		mEnvironment = environment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View v = inflater.inflate(R.layout.fragment_courses, container, false);

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
		
			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getClient();

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
				((TextView) v.findViewById(R.id.title)).setText(mEnvironment.name);
				mAdapter = new CoursesExpandableListAdapter(getActivity(), mEnrollmentedCourses, mSpaces);
				mListView.setAdapter(mAdapter);
			
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

}
