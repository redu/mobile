package br.com.redu.redumobile.activities;

import java.util.List;

import org.scribe.exceptions.OAuthConnectionException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.developer.redu.models.Statusable;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.StatusDetailAdapter;
import br.com.redu.redumobile.util.DateUtil;

public class StatusDetailActivity extends BaseActivity {

	public static final String EXTRAS_STATUS = "EXTRAS_STATUS";

	private Status mStatusHeader;
	private Status mStatus;
	
	private ListView mListView;
	private View mLlLoadingAnswers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_status_detail);
		setActionBarTitle("Ambientes");

		Bundle extras = getIntent().getExtras();
		mStatus = (Status) extras.get(EXTRAS_STATUS);

		mLlLoadingAnswers = findViewById(R.id.ll_loading_answers);
		mListView = (ListView) findViewById(R.id.list);
	}

	public void onWallClicked(View v) {

	}

	public void onAnswerClicked(View v) {
		Intent i = new Intent(StatusDetailActivity.this, RespondStatusActivity.class);
		i.putExtra(RespondStatusActivity.EXTRAS_STATUS, mStatusHeader);
		startActivity(i);
	}

	private View createOriginalStatusHeaderView(Status status) {
		View v = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.status_detail_header_original_status, null);

		// ((LazyLoadingImageView)
		// v.findViewById(R.id.iv_photo)).setImageUrl(status.user.getThumbnailUrl());
		((TextView) v.findViewById(R.id.tv_date)).setText(DateUtil.getFormattedStatusCreatedAt(status));
		// ((TextView)
		// v.findViewById(R.id.tv_user_name)).setText(status.user.getCompleteName());

		if (status.isActivityType()) {
			((TextView) v.findViewById(R.id.tv_action)).setText("comentou");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.GONE);

		} else if (status.isAnswerType()) {
			((TextView) v.findViewById(R.id.tv_action)).setText("comentou");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.GONE);

		} else if (status.isHelpType()) {
			((TextView) v.findViewById(R.id.tv_action)).setText("pediu ajuda");
			v.findViewById(R.id.iv_help_icon).setVisibility(View.VISIBLE);
		}

		((TextView) v.findViewById(R.id.tv_text)).setText(status.text);

		return v;
	}
	
	private View createPublishingLocalHeaderView(Status status) {
		View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.status_detail_header_publishing_local, null);
		
		Statusable statusable = status.getStatusable();
		
		if(statusable.isTypeUser()) {
			v = null;
		
		} else if(statusable.isTypeLecture()) {
			TextView tv = ((TextView) v.findViewById(R.id.tv_title));
			tv.setText(statusable.name);
			tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_aula_azul, 0, 0, 0);
			
		} else if(statusable.isTypeSpace()) {
			TextView tv = ((TextView) v.findViewById(R.id.tv_title));
			tv.setText(statusable.name);
			tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_disciplina_azul, 0, 0, 0);
		}
		
		return v;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(mStatusHeader == null) {
			new LoadStatusHeaderTask(mStatus).execute();
		} else {
			mLlLoadingAnswers.setVisibility(View.VISIBLE);
			new LoadAnswersStatus(mStatusHeader.id).execute();
		}
	}

	class LoadStatusHeaderTask extends AsyncTask<Void, Void, br.com.developer.redu.models.Status> {
		private br.com.developer.redu.models.Status mStatus;
		
		public LoadStatusHeaderTask(br.com.developer.redu.models.Status status) {
			mStatus = status;
		}
		
		@Override
		protected br.com.developer.redu.models.Status doInBackground(Void... params) {
			br.com.developer.redu.models.Status statusHeader;

			if(mStatus.isAnswerType()) {
				try {
					DefaultReduClient redu = ReduApplication.getReduClient();
					statusHeader = redu.getStatus(mStatus.getInResponseToStatusId());
				} catch (OAuthConnectionException e) {
					e.printStackTrace();
					statusHeader = null;
				}
			} else {
				statusHeader = mStatus;
			}
			
			return statusHeader;
		}
		
		@Override
		protected void onPostExecute(br.com.developer.redu.models.Status statusHeader) {
			if(statusHeader == null) {
				// TODO tratar quando ocorrer excecao
				
			} else {
				mStatusHeader = statusHeader;

				// TODO Setar title da Action bar com o nome da disciplina/
				
				// TODO Adicionar Header com nome da aula/disciplina onde o Status foi postado
//				View publishingLocalHeaderView = createPublishingLocalHeaderView(statusHeader);
//				if(publishingLocalHeaderView != null) {
//					mListView.addHeaderView(publishingLocalHeaderView);
//				}
				
				mListView.addHeaderView(createOriginalStatusHeaderView(statusHeader));
				mListView.setAdapter(new StatusDetailAdapter(getApplicationContext(), null));
				
				new LoadAnswersStatus(statusHeader.id).execute();
			}
		}
	}
	
	class LoadAnswersStatus extends
			AsyncTask<Void, Void, List<br.com.developer.redu.models.Status>> {
		private String mStatusId;

		public LoadAnswersStatus(String statusId) {
			mStatusId = statusId;
		}

		@Override
		protected List<br.com.developer.redu.models.Status> doInBackground(Void... params) {
			List<br.com.developer.redu.models.Status> answers;

			try {
				DefaultReduClient redu = ReduApplication.getReduClient();
				answers = redu.getAnswers(mStatusId);
			} catch (OAuthConnectionException e) {
				e.printStackTrace();
				answers = null;
			}

			return answers;
		}

		@Override
		protected void onPostExecute(List<br.com.developer.redu.models.Status> answers) {
			if (answers != null && answers.size() > 0) {
				mListView.setAdapter(new StatusDetailAdapter(getApplicationContext(), answers));
				mListView.invalidate();
			}

			mLlLoadingAnswers.setVisibility(View.GONE);
		}
	}
}
