package br.com.redu.redumobile.fragments;

import java.util.List;

import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.db.DbHelper;

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
	protected List<Status> getStatuses(DbHelper dbHelper, long timestamp, boolean olderThan) {
		return dbHelper.getNewLecturesStatus(timestamp, olderThan, NUM_STATUS_BY_PAGE_DEFAULT);
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

