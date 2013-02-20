package br.com.redu.redumobile.activities;

import java.util.ArrayList;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Course;
import br.com.developer.redu.models.Environment;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.CoursesExpandableListAdapter;

public class EnvironmentActivity extends Activity {

	private List<Course> mEnrollmentedCourses;
	private List<List<Space>> mSpaces;

	private Environment mEnvironment;
	
	private ExpandableListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_environment);

		mListView = (ExpandableListView) findViewById(R.id.list);
		
		Bundle extras = getIntent().getExtras();
		mEnvironment = (Environment) extras.get(Environment.class.getName());
		
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
				((TextView) findViewById(R.id.title)).setText(mEnvironment.name);

				mListView.setAdapter(new CoursesExpandableListAdapter(EnvironmentActivity.this, mEnrollmentedCourses, mSpaces));
			
			};

		}.execute();
	}
}
