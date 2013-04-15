package br.com.redu.redumobile.fragments;

import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Environment;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.EnviromentListAdapter;

public class EnvironmentFragment extends Fragment {

	private List<Environment> mEnvironments;

	private ListView mListView;
	private ProgressBar mProgressBar;

	private OnEnvironmentSelectedListener mListener;
	
	public interface OnEnvironmentSelectedListener {
        public void onEnvironmentSelected(Environment environment);
    }
	
	public EnvironmentFragment() {
		
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_environment, container, false);
		mListView = (ListView) v.findViewById(R.id.lvEnviroment);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				mListener.onEnvironmentSelected(mEnvironments.get(position));
			}
		});
		
		mProgressBar = (ProgressBar) v.findViewById(R.id.pb);
		
		new AsyncTask<Void, Void, List<Environment>>() {
			@Override
			protected List<Environment> doInBackground(Void... params) {
				try {
					DefaultReduClient redu = ReduApplication.getReduClient();
					return redu.getEnvironments();
				} catch (OAuthConnectionException e) {
					e.printStackTrace();
					return null;
				}
			}

			protected void onPostExecute(List<Environment> environments) {
				Activity activity = getActivity();
				if(activity != null && environments != null) {
					mEnvironments = environments;
					mListView.setAdapter(new EnviromentListAdapter(activity, environments));
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
            mListener = (OnEnvironmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnEnvironmentSelectedListener");
        }
    }
}
