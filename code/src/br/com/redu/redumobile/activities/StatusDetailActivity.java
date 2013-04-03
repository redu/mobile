package br.com.redu.redumobile.activities;

import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.StatusDetailAdapter;
import br.com.redu.redumobile.util.DateUtil;

public class StatusDetailActivity extends BaseActivity {

	public static final String EXTRAS_STATUS = "EXTRAS_STATUS";

	private ListView mListView;
	private View mLlLoadingAnswers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_status_detail);
		setActionBarTitle("Ambientes");

		Bundle extras = getIntent().getExtras();
		Status status = (Status) extras.get(EXTRAS_STATUS);

		mLlLoadingAnswers = findViewById(R.id.ll_loading_answers);

		mListView = (ListView) findViewById(R.id.list);
		mListView.addHeaderView(createHeaderView(status));
		mListView.setAdapter(new StatusDetailAdapter(getApplicationContext(),
				null));

		new LoadAnswersStatus(status.id).execute();
	}

	public void onWallClicked(View v) {

	}

	public void onAnswerClicked(View v) {

	}

	private View createHeaderView(Status status) {
		View v = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.status_detail_header, null);

		// ((LazyLoadingImageView)
		// v.findViewById(R.id.iv_photo)).setImageUrl(status.user.getThumbnailUrl());
		((TextView) v.findViewById(R.id.tv_date)).setText(DateUtil
				.getFormattedStatusCreatedAt(status));
		// ((TextView)
		// v.findViewById(R.id.tv_user_name)).setText(status.user.getCompleteName());

		if (status.type.equals(Status.TYPE_ACTIVITY)) {
			((TextView) v.findViewById(R.id.tv_action)).setText("comentou");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.GONE);

		} else if (status.type.equals(Status.TYPE_ANSWER)) {
			((TextView) v.findViewById(R.id.tv_action)).setText("comentou");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.GONE);

		} else if (status.type.equals(Status.TYPE_HELP)) {
			((TextView) v.findViewById(R.id.tv_action)).setText("pediu ajuda");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.VISIBLE);
		}

		((TextView) v.findViewById(R.id.tv_text)).setText(status.text);

		return v;
	}

	class LoadAnswersStatus extends
			AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {
		private String mStatusId;

		public LoadAnswersStatus(String statusId) {
			mStatusId = statusId;
		}

		@Override
		protected List<br.com.developer.redu.models.Status> doInBackground(
				Void... params) {
			List<br.com.developer.redu.models.Status> answers;

			DefaultReduClient redu = ReduApplication.getReduClient();

			try {
				answers = redu.getAnswers(mStatusId);
			} catch (OAuthConnectionException e) {
				e.printStackTrace();
				answers = null;
			}

			return answers;
		}

		@Override
		protected void onPostExecute(
				List<br.com.developer.redu.models.Status> answers) {
			if (answers != null && answers.size() > 0) {
				mListView.setAdapter(new StatusDetailAdapter(
						getApplicationContext(), answers));
				mListView.invalidate();
			}

			mLlLoadingAnswers.setVisibility(View.GONE);
		}
	}
}
