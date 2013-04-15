package br.com.redu.redumobile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.activities.StatusDetailActivity;
import br.com.redu.redumobile.adapters.StatusWallAdapter;
import br.com.redu.redumobile.db.DbHelper;
import br.com.redu.redumobile.db.DbHelperHolder;
import br.com.redu.redumobile.db.DbHelperListener;

public abstract class HomeFragment extends Fragment implements DbHelperListener {

	protected static final int NUM_STATUS_BY_PAGE = 25;

	public enum Type {LastSeen, Wall, NewLectures};

	private ListView mListView;
	protected StatusWallAdapter mAdapter;

	private TextView mTvEmptyList;
	private LinearLayout mLlNewStatus;
	
	public abstract String getTitle();
	public abstract Type getType();
	protected abstract String getEmptyListMessage();
	protected abstract long getOldestStatusTimestamp();
	protected abstract long getEarliestStatusTimestamp();
	
	/**
	 * Starts the status updating
	 * @param olderThan Will get the statuses older than the statuses already showed
	 */
	protected abstract void updateStatuses(boolean olderThan);

	public HomeFragment() {

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((DbHelperHolder) activity).getDbHelper().addDbHelperListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_listview, container, false);

		mTvEmptyList = (TextView) v.findViewById(R.id.tv_empty_list);
		
		mLlNewStatus = (LinearLayout) v.findViewById(R.id.ll_new_status);
		mLlNewStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLlNewStatus.setVisibility(View.GONE);
				mAdapter = new StatusWallAdapter(getActivity());
				updateStatuses(true);
			}
		});
		
		if(mAdapter == null) {
			mAdapter = new StatusWallAdapter(getActivity());
		}
		
		mListView = (ListView) v.findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Status status = (Status) mAdapter.getItem(position);
				
				if(!status.isLogType()) {
					Intent i = new Intent(getActivity(), StatusDetailActivity.class);
					i.putExtra(StatusDetailActivity.EXTRAS_STATUS, status);
					startActivity(i);
					
					DbHelper dbHelper = ((DbHelperHolder) getActivity()).getDbHelper();
					dbHelper.setStatusAsLastSeen(status);
				}
			}
		});
		
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// do nothing
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, 
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem + visibleItemCount == totalItemCount 
						&& totalItemCount != 0) {
					updateStatuses(true);
				}
			}
		});

		updateStatuses(true);

		return v;
	}
	
	@Override
	public void hasNewStatus() {
		updateStatuses(false);
	}

	protected long getTimestamp(boolean olderThan) {
		return (olderThan) ? getOldestStatusTimestamp() : getEarliestStatusTimestamp();	
	}
	
	protected void showEmptyListMessage() {
		mTvEmptyList.setText(getEmptyListMessage());
		mTvEmptyList.setVisibility(View.VISIBLE);
		
		mListView.setVisibility(View.GONE);
	}
	
	protected void hideEmptyListMessage() {
		mTvEmptyList.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}
	
	protected void showNewStatusMessage() {
		mLlNewStatus.setVisibility(View.VISIBLE);
	}
}
