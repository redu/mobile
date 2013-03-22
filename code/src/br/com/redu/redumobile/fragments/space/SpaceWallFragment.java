package br.com.redu.redumobile.fragments.space;

import java.util.List;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Space;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class SpaceWallFragment extends Fragment {
	
	private User mUser;
	private int mCurrentPage;
	private boolean mUpdatingList;
	private Space mSpace;
	
	private List<br.com.developer.redu.models.Status> mStatus;
	
	private ListView mListView;
	
	
	public SpaceWallFragment() {
		mCurrentPage = 1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.fragment_wall_space, container, false);
		
		mSpace = (Space)getActivity().getIntent().getExtras().get(Space.class.getName());
		
		mListView = (ListView) v.findViewById(R.id.lvStatusSpace);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// do nothing
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(!mUpdatingList && firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
					new LoadStatusFromSpaceTask(mCurrentPage++).execute();
				}
			}
		});
		
		new LoadUserTask().execute();
		
		return v;
	}
	
	class LoadUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient();
			Log.i("Redu", redu.getAuthorizeUrl());
			return redu.getMe();
		}
	
		protected void onPostExecute(User user) {
//			((TextView) v.findViewById(R.id.details)).setText(user.first_name + " " + user.last_name + ", ");
			mUser = user;

			new LoadStatusFromSpaceTask(mCurrentPage).execute();
		};
	}
	
	class LoadStatusFromSpaceTask extends AsyncTask<Void, Void, Void> {

		private int page;
		
		public LoadStatusFromSpaceTask(int page) {
			this.page = page;
		}
		
		protected void onPreExecute() {
			mUpdatingList = true;
		};
		
		protected Void doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient();
			//List<br.com.developer.redu.models.Status> status = new ArrayList<br.com.developer.redu.models.Status>();
			mStatus = redu.getStatusesBySpace(mSpace.id, br.com.developer.redu.models.Status.TYPE_ACTIVITY , Integer.toString(page));
			return null;
		}

		protected void onPostExecute(List<br.com.developer.redu.models.Status> statuses) {
			if(statuses != null) {
				if(page == 1) {
					mListView.setAdapter(new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_dropdown_item_1line));
				}
				
				@SuppressWarnings("unchecked")
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) mListView.getAdapter();
				for(br.com.developer.redu.models.Status status : statuses) {
					if(status.type.equals("Log") && status.logeable_type.equals("CourseEnrollment")) {
						continue;
					}
					adapter.add(status.text);
				}
				
				if(adapter.isEmpty()) {
					new LoadStatusFromSpaceTask(++mCurrentPage).execute();
				} else {
					adapter.notifyDataSetChanged();
				}
			}
			
			mUpdatingList = false;
		};
	}
}
