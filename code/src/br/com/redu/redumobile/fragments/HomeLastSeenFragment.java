package br.com.redu.redumobile.fragments;

import java.util.List;

import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.db.DbHelper;

public class HomeLastSeenFragment extends HomeFragment {

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
		int count = mAdapter.getCount();
		if(count == 0) {
			return 0;
		}
		
		return ((Status) mAdapter.getItem(0)).lastSeenAtInMillis;
	}
}
