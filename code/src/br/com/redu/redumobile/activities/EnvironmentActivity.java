package br.com.luismedeiros.redutest;

import java.util.List;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Course;
import br.com.developer.redu.models.Environment;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EnvironmentActivity extends Activity {

	private List<Course> mCourses;
	private Environment mEnvironment;
	
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((TextView) findViewById(R.id.title)).setText("Cursos");

		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(getApplicationContext(), CoursesActivity.class);
				i.putExtra(Course.class.getName(), mCourses.get(position));
				startActivity(i);
			}
		});
		
		Bundle extras = getIntent().getExtras();
		mEnvironment = (Environment) extras.get(Environment.class.getName());
		
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getClient();
				mCourses = redu.getCoursesByEnvironment(mEnvironment.path);
				return null;
			}

			protected void onPostExecute(Void result) {
				((TextView) findViewById(R.id.details)).setText(mEnvironment.name);

				mListView.setAdapter(new ArrayAdapter<Course>(
								getApplicationContext(),
								android.R.layout.simple_dropdown_item_1line,
								mCourses));
			};

		}.execute();
	}
}
