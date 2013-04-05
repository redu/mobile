package br.com.redu.redumobile.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.developer.redu.models.Status;
import br.com.developer.redu.models.User;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.util.DateUtil;
import br.com.redu.redumobile.widgets.LazyLoadingImageView;

public class StatusWallAdapter extends BaseAdapter {

	final private LayoutInflater mInflater;
	private List<Status> mStatuses;
	
	public StatusWallAdapter(Context context) {
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
		if(mStatuses == null) {
			mStatuses = new ArrayList<Status>();
		}
		mStatuses.add(status);
	}

	public void addAll(List<Status> statuses) {
		if(mStatuses == null) {
			mStatuses = statuses;
		} else {
			mStatuses.addAll(statuses);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.status_wall_item, null);
		}
		
		Status status = mStatuses.get(position);
		
		// TODO
//		((TextView) convertView.findViewById(R.id.tv_breadcrumbs)).setText(status.created_at);
		
		// TODO
//		new LoadUserInfoTask(status, convertView).execute();
		
		((TextView) convertView.findViewById(R.id.tv_date)).setText(DateUtil.getFormattedStatusCreatedAt(status));

		if(status.isTypeActivity()) {
			((TextView) convertView.findViewById(R.id.tv_action)).setText("comentou");
			((TextView) convertView.findViewById(R.id.tv_result)).setText("");
			((TextView) convertView.findViewById(R.id.tv_result_name)).setVisibility(View.GONE);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setVisibility(View.GONE);
			((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.GONE);
			
		} else if (status.isTypeAnswer()) {
			((TextView) convertView.findViewById(R.id.tv_action)).setText("comentou");
			((TextView) convertView.findViewById(R.id.tv_result)).setText("");
			((TextView) convertView.findViewById(R.id.tv_result_name)).setVisibility(View.GONE);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setVisibility(View.GONE);
			((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.GONE);
		
		} else if (status.isTypeHelp()) {
			((TextView) convertView.findViewById(R.id.tv_action)).setText("pediu ajuda");
			((TextView) convertView.findViewById(R.id.tv_result)).setText("");
			((TextView) convertView.findViewById(R.id.tv_result_name)).setVisibility(View.GONE);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setImageResource(R.drawable.ic_ajuda);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_text)).setText(status.text);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setText(status.text); //TODO
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.VISIBLE);
		
		} else if (status.isTypeLog()) {
			String action = null;
			String result = null;
			int icon = 0;
			
			if (status.isLogeableTypeCourse()) {
				action = "criou o";
				result = "Curso";
				icon = R.drawable.ic_curso;
			} else if (status.isLogeableTypeLecture()) {
				action += "criou a";
				result = "Aula";
				icon = R.drawable.ic_aula;
			} else if (status.isLogeableTypeSubject()) {
				action += "criou o";
				result = "Módulo";
				icon = R.drawable.ic_modulo;
			}
			
			((TextView) convertView.findViewById(R.id.tv_action)).setText(action);
			((TextView) convertView.findViewById(R.id.tv_result)).setText(result);
			((TextView) convertView.findViewById(R.id.tv_result_name)).setText(""); // TODO
			((TextView) convertView.findViewById(R.id.tv_result_name)).setVisibility(View.VISIBLE);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setImageResource(icon);
			((ImageView) convertView.findViewById(R.id.iv_icon)).setVisibility(View.VISIBLE);
			((TextView) convertView.findViewById(R.id.tv_text)).setVisibility(View.GONE);
			((TextView) convertView.findViewById(R.id.tv_answers)).setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	class LoadUserInfoTask extends AsyncTask<Void, Void, User> {

		private br.com.developer.redu.models.Status mStatus;
		private View mView;
		
		public LoadUserInfoTask(br.com.developer.redu.models.Status status, View view) {
			mStatus = status;
			mView = view;
		}
		
		protected User doInBackground(Void... params) {
			String userId = String.valueOf(mStatus.user.id);
			return ReduApplication.getReduClient().getUser(userId);
		}

		protected void onPostExecute(User user) {
			if (user != null) {
				mStatus.user = user;
				
				((LazyLoadingImageView) mView.findViewById(R.id.iv_photo)).setImageUrl(user.thumbnails.get(0).href);
				((TextView) mView.findViewById(R.id.tv_user_nome)).setText(new StringBuffer(user.first_name).append(" ").append(user.last_name).toString());
			}
		}
	}
}
