package br.com.redu.redumobile.fragments;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.data.LoadingStatusesManager;
import br.com.redu.redumobile.data.OnLoadStatusesListener;
import br.com.redu.redumobile.db.DbHelper;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class HomeLastSeenFragment extends HomeFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LoadingStatusesManager.add(new OnLoadStatusesListener() {
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
			mAdapter.clear();
			updateStatusesFromDb(false);
			mRefreshView = refreshView;
		}
	}
	
	@Override
	public String getTitle() {
		return "Últimos Visualizados";
	}

	@Override
	public Type getType() {
		return Type.LastSeen;
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
}
