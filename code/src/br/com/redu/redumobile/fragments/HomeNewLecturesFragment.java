package br.com.redu.redumobile.fragments;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.db.DbHelperHolder;

public class HomeNewLecturesFragment extends HomeFragment  {

	@Override
	public String getTitle() {
		return "Novas Aulas";
	}

	@Override
	public Type getType() {
		return Type.NewLectures;
	}

	@Override
	protected String getEmptyListMessage() {
		return "Não há Novas Aulas";
	}

	@Override
	protected void updateStatuses(boolean olderThan) {
		new LoadStatusesFromDbTask(olderThan).execute();
	}

	class LoadStatusesFromDbTask extends AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {

		boolean mOlderThan;
		
		public LoadStatusesFromDbTask(boolean olderThan) {
			mOlderThan = olderThan;
		}
		
		protected List<br.com.developer.redu.models.Status> doInBackground(Void... params) {
			List<br.com.developer.redu.models.Status> statuses = null;
			
			Activity activity = getActivity();
			if(activity != null && activity instanceof DbHelperHolder) {
				long timestamp = getTimestamp(mOlderThan);
				
				DbHelper dbHelper = ((DbHelperHolder) activity).getDbHelper();
				statuses = dbHelper.getNewLecturesStatus(timestamp, mOlderThan, NUM_STATUS_BY_PAGE);
			}
			
			return statuses;
		}

		protected void onPostExecute(List<br.com.developer.redu.models.Status> statuses) {
			 if(getActivity() != null) {
				if (statuses != null && statuses.size() > 0) {
					if (mAdapter.isEmpty() || mOlderThan) {
						mAdapter.addAll(statuses, mOlderThan);
						mAdapter.notifyDataSetChanged();
						hideEmptyListMessage();
						
					} else if (!mOlderThan) {
						showNewStatusMessage();
					}
					
				} else if(mAdapter.isEmpty()) {
					showEmptyListMessage();
			 	}	
			}
		};
	}

	@Override
	protected long getOldestStatusTimestamp() {
		int count = mAdapter.getCount();
		if(count == 0) {
			return System.currentTimeMillis();
		}
		
		return ((Status) mAdapter.getItem(count-1)).createdAtInMillis;
	}
	
	@Override
	protected long getEarliestStatusTimestamp() {
		int count = mAdapter.getCount();
		if(count == 0) {
			return 0;
		}
		
		return ((Status) mAdapter.getItem(0)).createdAtInMillis;
	}
}

