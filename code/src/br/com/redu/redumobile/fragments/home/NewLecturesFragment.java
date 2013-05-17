package br.com.redu.redumobile.fragments.home;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.data.LoadStatusesFromWebTask;
import br.com.redu.redumobile.data.OnLoadStatusesFromWebListener;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.fragments.StatusListFragment;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class NewLecturesFragment extends StatusListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LoadStatusesFromWebTask.addOnLoadStatusesFromWebListener(new OnLoadStatusesFromWebListener() {
			@Override
			public void onStart() {
			}
			
			@Override
			public void onError(Exception e) {
				if(isWaitingNotification) {
					isWaitingNotification = false;
					// TODO show a error message
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(mRefreshView != null) {
								mRefreshView.onRefreshComplete();								
								showNoConnectionAlert();

							}
						}
					});
				}
			}
			
			@Override
			public void onComplete() {
				if(isWaitingNotification) {
					isWaitingNotification = false;
					updateStatusesFromDb(false);
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(mRefreshView != null) {
								mRefreshView.onRefreshComplete();
							}
						}
					});
				}
			}
		});
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		Activity activity = getActivity();
		if(activity != null) {
			isWaitingNotification = true;
			SchedulerManager.getInstance().runNow(activity, LoadStatusesFromWebTask.class, 0);
			mRefreshView = refreshView;
		}
	}
	
	@Override
	public String getTitle() {
		return "Novas Aulas";
	}

	@Override
	protected String getEmptyListMessage() {
		return "Não há Novas Aulas";
	}

	@Override
	protected List<Status> getStatuses(DbHelper dbHelper, long timestamp, boolean olderThan) {
		return dbHelper.getNewLecturesStatus(timestamp, olderThan, NUM_STATUS_BY_PAGE_DEFAULT);
	}

	@Override
	public void onStatusInserted() {
		updateStatusesFromDb(false);
	}

	@Override
	public void onStatusUpdated() {
		// ignoring
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

	@Override
	protected boolean isEnableGoToWallAction() {
		return false;
	}
}
