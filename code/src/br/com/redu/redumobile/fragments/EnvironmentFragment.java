package br.com.redu.redumobile.fragments;

import java.util.List;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Environment;
import br.com.redu.redumobile.activities.EnvironmentActivity;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class EnvironmentFragment extends Fragment {

	private List<Environment> mEnvironments;

	private ListView mListView;
	
	public EnvironmentFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_main, container, false);
		mListView = (ListView) v.findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(getActivity(), EnvironmentActivity.class);
				i.putExtra(Environment.class.getName(), mEnvironments.get(position));
				startActivity(i);
			}
		});
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getClient();
				//mEnvironments = redu.getEnvironments();
				return null;
			}

			protected void onPostExecute(Void result) {

//				mListView.setAdapter(new ArrayAdapter<Environment>(
//						getActivity(),
//						android.R.layout.simple_dropdown_item_1line,
//						mEnvironments));
			};

		}.execute();
		
		return v;
	}
}
