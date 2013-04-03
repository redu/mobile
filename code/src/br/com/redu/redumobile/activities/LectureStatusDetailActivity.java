package br.com.redu.redumobile.activities;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.StatusDetailAdapter;
import br.com.redu.redumobile.util.DateUtil;

public class LectureStatusDetailActivity extends BaseActivity {

	public static final String EXTRAS_STATUS = "EXTRAS_STATUS";
	
	private ListView mListView;
	private ProgressBar mLoadingAnswersPb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_lecture_status_detail);
		setActionBarTitle("Ambientes");

		Bundle extras = getIntent().getExtras();
		Status status = (Status) extras.get(EXTRAS_STATUS);
		
		mLoadingAnswersPb = (ProgressBar) findViewById(R.id.pb);
		
		mListView = (ListView) findViewById(R.id.list);
		mListView.addHeaderView(createHeaderView(status));
		mListView.setAdapter(new StatusDetailAdapter(getApplicationContext(), null));
		
		new LoadAnswersStatus(status.id).execute();
	}
	
	public void onWallClicked(View v) {
		
	}
	
	public void onAnswerClicked(View v) {
	
	}
	
	private View createHeaderView(Status status) {
		View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.status_detail_header, null);

//		((LazyLoadingImageView) v.findViewById(R.id.iv_photo)).setImageUrl(status.user.getThumbnailUrl());
		if(!status.type.equals(Status.TYPE_HELP)) {
			v.findViewById(R.id.iv_help_icon).setVisibility(View.GONE);
		}
		((TextView) v.findViewById(R.id.tv_date)).setText(DateUtil.getFormattedStatusCreatedAt(status));
//		((TextView) v.findViewById(R.id.tv_user_name)).setText(status.user.getCompleteName());
		((TextView) v.findViewById(R.id.tv_text)).setText(status.text);
		
		return v;
	}
	
	class LoadAnswersStatus extends AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {
		private String mStatusId;
		
		public LoadAnswersStatus(String statusId) {
			mStatusId = statusId;
		}
		
		@Override
		protected List<br.com.developer.redu.models.Status> doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getReduClient();
			return redu.getAnswers(mStatusId);
		}
		
		@Override
		protected void onPostExecute(List<br.com.developer.redu.models.Status> answers) {
			if(answers != null && answers.size() > 0) {
				mListView.setAdapter(new StatusDetailAdapter(getApplicationContext(), answers));
				mListView.invalidate();
			}
			
			mLoadingAnswersPb.setVisibility(View.GONE);
		}
	}
}
