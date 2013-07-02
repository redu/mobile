package br.com.redu.redumobile.fragments.home;

import java.util.List;

import android.app.Activity;
import android.widget.ListView;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.adapters.StatusWallAdapter.StatusWallAdder;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.fragments.StatusListFragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class LastSeenFragment extends StatusListFragment {

	private static final StatusWallAdder mLastSeenStatusAdder = new StatusWallAdder() {
		@Override
		public void addFromBegin(List<Status> statuses, Status statusToAdd) {
			removeStatusIfExisting(statuses, statusToAdd);
			int size = statuses.size();
			int i;
			for(i = 0; i < size; i++) {
				Status status = statuses.get(i);
				
				if(statusToAdd.id.equals(status.id)) {
					break;
				} else if(statusToAdd.lastSeenAtInMillis >= status.lastSeenAtInMillis) {
					statuses.add(i, statusToAdd);
					break;
				}
			}
			if(i == size) {
				statuses.add(statusToAdd);
			}
		}
		
		@Override
		public void addFromEnd(List<Status> statuses, Status statusToAdd) {
			removeStatusIfExisting(statuses, statusToAdd);
			int i;
			for(i = statuses.size() - 1; i >= 0; i--) {
				Status status = statuses.get(i);
				
				if(statusToAdd.id.equals(status.id)) {
					break;
				} else if(statusToAdd.lastSeenAtInMillis <= status.lastSeenAtInMillis) {
					statuses.add(i, statusToAdd);
					break;
				}
			}
			if(i == -1) {
				statuses.add(0, statusToAdd);
			}
		}
		
		private void removeStatusIfExisting(List<Status> statuses, Status statusToAdd) {
			int size = statuses.size();
			for(int i = 0; i < size; i++) {
				Status status = statuses.get(i);
				if(status.id.equals(statusToAdd.id)) {
					statuses.remove(i);
					break;
				}
			}
		}
	};
	
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
	protected List<Status> getStatuses(DbHelper dbHelper, long timestamp, boolean olderThan, String appUserId) {
		return dbHelper.getLastSeenStatuses(timestamp, olderThan, NUM_STATUS_BY_PAGE_DEFAULT, appUserId);
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

	@Override
	public StatusWallAdder getStatusWallAdder() {
		return mLastSeenStatusAdder;
	}
}
