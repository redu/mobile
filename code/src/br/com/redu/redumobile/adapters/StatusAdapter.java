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
import android.widget.ImageView;
import android.widget.TextView;

public class StatusAdapter extends BaseAdapter {

	final private LayoutInflater mInflater;
	final private Context mContext;
	private List<Status> mStatuses;
	
	public StatusAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		if(mStatuses == null) {
			return 0;
		}
		
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
	
	public void add(Status status) {
		if(mStatuses != null)
			mStatuses.add(status);
	}

	public void add(List<Status> statuses) {
		if(mStatuses != null)
			mStatuses.addAll(statuses);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.status_item, null);
		}
		
		Status status = mStatuses.get(position);
		
//		((TextView) convertView.findViewById(R.id.tv_breadcrumbs)).setText(status.created_at);
		((LazyLoadingImageView) convertView.findViewById(R.id.iv_photo)).setImageUrl(status.user.thumbnails.get(0).href);
		((TextView) convertView.findViewById(R.id.tv_user_nome)).setText(new StringBuffer(status.user.first_name).append(" ").append(status.user.first_name).toString());
		((TextView) convertView.findViewById(R.id.tv_date)).setText(status.created_at);

		if(status.type.equals(Status.TYPE_ACTIVITY)) {
			((TextView) convertView.findViewById(R.id.tv_action)).setText("comentou");
			((TextView) convertView.findViewById(R.id.tv_result)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_result_name)).setText(status.text);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setImageResource(0);
			((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.GONE);
			
		} else if(status.type.equals(Status.TYPE_ANSWER)) {
			((TextView) convertView.findViewById(R.id.tv_action)).setText("comentou");
			((TextView) convertView.findViewById(R.id.tv_result)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_result_name)).setText(status.text);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setImageResource(0);
			((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.GONE);
		
		} else if(status.type.equals(Status.TYPE_HELP)) {
			((TextView) convertView.findViewById(R.id.tv_action)).setText("pediu ajuda");
			((TextView) convertView.findViewById(R.id.tv_result)).setText("");
			((TextView) convertView.findViewById(R.id.tv_result_name)).setVisibility(View.GONE);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setImageResource(R.drawable.ic_ajuda);
			((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.VISIBLE);
		
		} else if(status.type.equals(Status.TYPE_LOG)) {
			String action = null;
			String result = null;
			int icon = -1;
			
			if(status.type.equals(Status.LOGEABLE_TYPE_COURSE)) {
				action = "criou o";
				result = "Curso";
				icon = R.drawable.ic_curso;
			} else if(status.type.equals(Status.LOGEABLE_TYPE_LECTURE)) {
				action += "criou a";
				result = "Aula";
				icon = -1; //TODO
			} else if(status.type.equals(Status.LOGEABLE_TYPE_SUBJECT)) {
				action += "criou o";
				result = "Módulo";
				icon = R.drawable.ic_modulo;
			}
			
			((TextView) convertView.findViewById(R.id.tv_action)).setText(action);
			((TextView) convertView.findViewById(R.id.tv_result)).setText(result);
			((TextView) convertView.findViewById(R.id.tv_result_name)).setText("");
			((ImageView) convertView.findViewById(R.id.iv_icon)).setImageResource(icon);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.GONE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.GONE);
		}
		
		return convertView;
	}
}
