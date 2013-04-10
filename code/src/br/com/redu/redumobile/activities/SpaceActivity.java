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
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;

public class SpaceActivity extends Activity {
	private List<Subject> mSubjects;
	private Space mSpace;
	
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_listview);
		
		((TextView) findViewById(R.id.tvEnvironment)).setText("Módulos");

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
				DefaultReduClient redu = ReduApplication.getReduClient();
				mSubjects = redu.getSubjectsBySpace(mSpace.id);
				return null;
			}

			protected void onPostExecute(Void result) {
//				((TextView) findViewById(R.id.details)).setText(mSpace.name);

				mListView.setAdapter(new ArrayAdapter<Subject>(
								getApplicationContext(),
								android.R.layout.simple_dropdown_item_1line,
								mSubjects));
			};

		}.execute();
	}

}
