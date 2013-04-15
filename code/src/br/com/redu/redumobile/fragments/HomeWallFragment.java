package br.com.redu.redumobile.fragments;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.db.DbHelperHolder;

public class HomeWallFragment extends HomeFragment {

	@Override
	public String getTitle() {
		return "Início";
	}

	@Override
	public String getEmptyListMessage() {
		return "Não há novidades, comece a fazer parte dos Ambientes e Cursos para interagir com outras pessoas";
	}

	@Override
	public Type getType() {
		return Type.Wall;
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
				statuses = dbHelper.getStatus(timestamp, mOlderThan, NUM_STATUS_BY_PAGE);
			}
			
			return statuses;
		}

		protected void onPostExecute(List<br.com.developer.redu.models.Status> statuses) {
			 if(getActivity() != null) {
				if (statuses != null && statuses.size() > 0) {
					mAdapter.addAll(statuses, mOlderThan);
					mAdapter.notifyDataSetChanged();
					hideEmptyListMessage();
					
				} else if(mAdapter.getCount() == 0) {
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
