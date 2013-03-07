package br.com.redu.redumobile.activities;

import java.util.ArrayList;
import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class EnvironmentActivity extends Activity {

	private List<Course> mEnrollmentedCourses;
	private List<List<Space>> mSpaces;

	private Environment mEnvironment;
	private Space mSpace;
	
	private ExpandableListView mListView;
	
	private CoursesExpandableListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_environment);

		mListView = (ExpandableListView) findViewById(R.id.list);
		
		/*mListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
					mSpace = (Space) mAdapter.getChild(groupPosition, childPosition);
					Intent it = new Intent(EnvironmentActivity.this, HomeSpaceActivity.class);
					it.putExtra(Space.class.getName(), mSpace);
					startActivity(it);
				return false;
			}
		});		*/
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
				mAdapter = new CoursesExpandableListAdapter(EnvironmentActivity.this, mEnrollmentedCourses, mSpaces);
				mListView.setAdapter(mAdapter);
			
			};

		}.execute();
	}
}
