package br.com.luismedeiros.redutest;

import java.util.List;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
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

public class SpaceActivity extends Activity {
	private List<Subject> mSubjects;
	private Space mSpace;
	
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((TextView) findViewById(R.id.title)).setText("Módulos");

		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(getApplicationContext(), SubjectActivity.class);
				i.putExtra(Subject.class.getName(), mSubjects.get(position));				
				startActivity(i);
			}
		});
		
		Bundle extras = getIntent().getExtras();
		mSpace = (Space) extras.get(Space.class.getName());
		
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getClient();
				mSubjects = redu.getSubjectsBySpace(mSpace.id);
				return null;
			}

			protected void onPostExecute(Void result) {
				((TextView) findViewById(R.id.details)).setText(mSpace.name);

				mListView.setAdapter(new ArrayAdapter<Subject>(
								getApplicationContext(),
								android.R.layout.simple_dropdown_item_1line,
								mSubjects));
			};

		}.execute();
	}

}
