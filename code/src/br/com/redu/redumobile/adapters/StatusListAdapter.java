package br.com.redu.redumobile.adapters;

import java.util.List;

import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.widgets.LazyLoadingImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatusListAdapter extends BaseAdapter {

	final private LayoutInflater mInflater;
	final private Context mContext;
	private List<Status> mStatuses;
	
	public StatusListAdapter(Context context, List<Status> statuses) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mStatuses = statuses;
	}
	
	@Override
	public int getCount() {
		return mStatuses.size();
	}

	@Override
	public Object getItem(int position) {
		return mStatuses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.status_item, null);
		}
		
		Status status = mStatuses.get(position);
		
		((TextView) convertView.findViewById(R.id.tv_date)).setText(status.created_at);
		((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
		((LazyLoadingImageView) convertView.findViewById(R.id.iv_photo)).setImageUrl(status.user.thumbnails.get(0).href);
		
		return convertView;
	}

}
