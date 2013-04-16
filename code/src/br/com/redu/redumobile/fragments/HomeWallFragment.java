package br.com.redu.redumobile.fragments;

import java.util.List;

import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.db.DbHelper;

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
	protected List<Status> getStatuses(DbHelper dbHelper, long timestamp, boolean olderThan) {
		return dbHelper.getStatus(timestamp, olderThan, NUM_STATUS_BY_PAGE_DEFAULT);
	}

	@Override
	public Type getType() {
		return Type.Wall;
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