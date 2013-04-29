package br.com.redu.redumobile.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Course;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;

public class CoursesActivity extends Activity {
	private List<Space> mSpaces;
	private Course mCourse;

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_listview);

		((TextView) findViewById(R.id.tvEnvironment)).setText("Disciplinas");

		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent i = new Intent(getApplicationContext(), HomeSpaceActivity.class);
				i.putExtra(Space.class.getName(), mSpaces.get(position));
				startActivity(i);
			}
		});

		Bundle extras = getIntent().getExtras();
		mCourse = (Course) extras.get(Course.class.getName());

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getReduClient(CoursesActivity.this);
				mSpaces = redu.getSpacesByCourse(mCourse.id);
				return null;
			}

			protected void onPostExecute(Void result) {
//				((TextView) findViewById(R.id.details)).setText(mCourse.name);

				mListView.setAdapter(new ArrayAdapter<Space>(
						getApplicationContext(),
						android.R.layout.simple_dropdown_item_1line, mSpaces));
			};

		}.execute();
	}
}
