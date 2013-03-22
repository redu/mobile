package br.com.redu.redumobile.fragments;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.adapters.StatusAdapter;
import br.com.redu.redumobile.db.DbHelper;

public class HomeFragment extends Fragment {

	public enum Type {LastSeen, Wall, NewLectures};

	private static final int NUM_STATUS_BY_PAGE = 25;
	
	private StatusAdapter mAdapter;

	private long mTimestamp;
	private boolean mUpdatingList;

	private Type mType;
	
	public HomeFragment() {

	}
	
	public String getTitle() {
		switch (mType) {
			case Wall:
				return "Início";
			
			case NewLectures:
				return "Novas Aulas";
				
			case LastSeen:
				return "Últimos Visualizados";
	
			default:
				return null;
		}
	}
	
	@Override
	public void setArguments(Bundle bundle) {
		mType = (Type) bundle.get(Type.class.getName());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_listview, container, false);

		if(mAdapter == null) {
			mAdapter = new StatusAdapter(getActivity());
			mTimestamp = System.currentTimeMillis();
		}
		
		ListView lv = (ListView) v.findViewById(R.id.list);
		lv.setAdapter(mAdapter);
		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// do nothing
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (!mUpdatingList
						&& firstVisibleItem + visibleItemCount == totalItemCount
						&& totalItemCount != 0) {
					new LoadStatusesTask().execute();
				}
			}
		});

		new LoadStatusesTask().execute();

		return v;
	}

	class LoadStatusesTask extends AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {

		protected void onPreExecute() {
			mUpdatingList = true;
		};

		protected List<br.com.developer.redu.models.Status> doInBackground(Void... params) {
			DbHelper dbHelper = DbHelper.getInstance(getActivity());
			
			switch (mType) {
				case LastSeen:
					return dbHelper.getLastSeenStatus(mTimestamp, NUM_STATUS_BY_PAGE);
				
				case Wall:
					return dbHelper.getStatus(mTimestamp, NUM_STATUS_BY_PAGE);
					
				case NewLectures:
					return dbHelper.getNewLecturesStatus(mTimestamp, NUM_STATUS_BY_PAGE);
				
				default:
					return null;
			}
		}

		protected void onPostExecute(List<br.com.developer.redu.models.Status> statuses) {
			if (statuses != null && statuses.size() > 0) {
				mAdapter.add(statuses);
				mAdapter.notifyDataSetChanged();
				mTimestamp = statuses.get(statuses.size()-1).created_at_in_millis;
			}

			mUpdatingList = false;
		};
	}
}
