package br.com.redu.redumobile.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.widgets.LazyLoadingImageView;

public class StatusDetailAdapter extends BaseAdapter {

	final private LayoutInflater mInflater;
	private List<Status> mAnswers;
	
	public StatusDetailAdapter(Context context, List<Status> answers) {
		mInflater = LayoutInflater.from(context);
		mAnswers = answers;
	}
	
	@Override
	public int getCount() {
		if(mAnswers == null) {
			return 0;
		}
		return mAnswers.size();
	}

	@Override
	public Object getItem(int position) {
		return mAnswers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.status_detail_item, null);
		}
		
		Status status = mAnswers.get(position);
		
		((LazyLoadingImageView) convertView.findViewById(R.id.iv_photo)).setImageUrl(status.user.getThumbnailUrl());
		((TextView) convertView.findViewById(R.id.tv_user_name)).setText(status.user.getCompleteName());
		((TextView) convertView.findViewById(R.id.tv_date)).setText(status.getFormattedCreatedAt());
		((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
		
		return convertView;
	}
}
