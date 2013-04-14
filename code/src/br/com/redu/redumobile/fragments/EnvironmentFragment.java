package br.com.redu.redumobile.fragments;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Environment;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.EnviromentListAdapter;

public class EnvironmentFragment extends Fragment {

	private List<Environment> mEnvironments;

	private ListView mListView;
	private AlertDialog mDialog;

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
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setView(inflater.inflate(R.layout.loading_dialog, null));
				mDialog = builder.create();
		    	mDialog.show();
			};
			
			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getReduClient();
				mEnvironments = redu.getEnvironments();
				return null;
			}

			protected void onPostExecute(Void result) {
				Activity activity = getActivity();
				if(activity != null && mEnvironments != null) {
					mDialog.dismiss();
					mListView.setAdapter(new EnviromentListAdapter(activity, mEnvironments));
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
