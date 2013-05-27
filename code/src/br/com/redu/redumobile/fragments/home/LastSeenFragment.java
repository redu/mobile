package br.com.redu.redumobile.fragments.home;

import java.util.List;

import android.app.Activity;
import android.widget.ListView;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.fragments.StatusListFragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class LastSeenFragment extends StatusListFragment {

	@Override
	public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
		Activity activity = getActivity();
		if(activity != null) {
			updateStatusesFromDb(false);
			refreshView.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(refreshView != null) {
							refreshView.onRefreshComplete();
						}
					}
			}, 3000);
		}
	}
	
	@Override
	public String getTitle() {
		return "Últimos Visualizados";
	}

	@Override
	protected String getEmptyListMessage() {
		return "Não há Comentários ou Pedidos de Ajuda recentemente visualizados";
	}

	@Override
	protected List<Status> getStatuses(DbHelper dbHelper, long timestamp, boolean olderThan) {
		return dbHelper.getLastSeenStatus(timestamp, olderThan, NUM_STATUS_BY_PAGE_DEFAULT);
	}

	@Override
	public void onStatusInserted() {
		// ignoring
	}

	@Override
	public void onStatusUpdated() {
		updateStatusesFromDb(false);
	}
	
	@Override
	public void onNoConnectionAlertClicked() {
		// ignoring...
	}
	
	@Override
	protected long getOldestStatusTimestamp() {
		int count = mAdapter.getCount();
		if(count == 0) {
			return System.currentTimeMillis();
		}
		
		return ((Status) mAdapter.getItem(count-1)).lastSeenAtInMillis;
	}
	
	@Override
	protected long getEarliestStatusTimestamp() {
		if(mAdapter.isEmpty()) {
			return 0;
		}
		
		return ((Status) mAdapter.getItem(0)).lastSeenAtInMillis;
	}

	@Override
	protected boolean isEnableGoToWallAction() {
		return true;
	}
}
