package br.com.redu.redumobile.fragments;

import java.util.ArrayList;
import java.util.List;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WallFragment extends Fragment {

	private ListView mListView;

	private User mUser;
	private int mCurrentPage;
	private boolean mUpdatingList;
	
	public WallFragment() {
		mCurrentPage = 1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.detail, container, false);
		
		mListView = (ListView) v.findViewById(R.id.list);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// do nothing
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(!mUpdatingList && firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
					new LoadStatusesTask(mCurrentPage++).execute();
				}
			}
		});
		
		new LoadUserTask().execute();
		
		return v;
	}
	
	class LoadUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getClient();
			Log.i("Redu", redu.getAuthorizeUrl());
			return redu.getMe();
		}
	
		protected void onPostExecute(User user) {
//			((TextView) v.findViewById(R.id.details)).setText(user.first_name + " " + user.last_name + ", ");
			mUser = user;

			new LoadStatusesTask(mCurrentPage).execute();
		};
	}
	
	class LoadStatusesTask extends AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {

		private int page;
		
		public LoadStatusesTask(int page) {
			this.page = page;
		}
		
		protected void onPreExecute() {
			mUpdatingList = true;
		};
		
		protected List<br.com.developer.redu.models.Status> doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getClient();
			return redu.getStatusesTimelineByUser(String.valueOf(mUser.id), null, String.valueOf(page));
		}

		protected void onPostExecute(List<br.com.developer.redu.models.Status> statuses) {
			if(statuses != null) {
				if(page == 1) {
					List<String> statusesTexts = new ArrayList<String>(statuses.size());
					for(br.com.developer.redu.models.Status status : statuses) {
						statusesTexts.add(status.toString());
					}
					
					mListView.setAdapter(new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_dropdown_item_1line,
								statusesTexts));
					
				} else {
					@SuppressWarnings("unchecked")
					ArrayAdapter<String> adapter = (ArrayAdapter<String>) mListView.getAdapter();
					for(br.com.developer.redu.models.Status status : statuses) {
						adapter.add(status.text);
					}
					adapter.notifyDataSetChanged();
				}
			}
			
			mUpdatingList = false;
		};

	}

}
