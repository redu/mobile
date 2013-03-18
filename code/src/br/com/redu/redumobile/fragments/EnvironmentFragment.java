package br.com.redu.redumobile.fragments;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Environment;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;

public class EnvironmentFragment extends Fragment {

	private List<Environment> mEnvironments;

	private ListView mListView;

	private OnEnvironmentSelectedListener mListener;
	
	public interface OnEnvironmentSelectedListener {
        public void onEnvironmentSelected(Environment environment);
    }
	
	public EnvironmentFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_environment, container, false);
		mListView = (ListView) v.findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mListener.onEnvironmentSelected(mEnvironments.get(position));
			}
		});
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getClient();
				mEnvironments = redu.getEnvironments();
				return null;
			}

			protected void onPostExecute(Void result) {
				Activity activity = getActivity();
				if(activity != null && mEnvironments != null) {
					mListView.setAdapter(new ArrayAdapter<Environment>(
					activity,
					android.R.layout.simple_dropdown_item_1line,
					mEnvironments));
				}
			};

		}.execute();
		
		return v;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEnvironmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnEnvironmentSelectedListener");
        }
    }
}
