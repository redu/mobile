package br.com.redu.redumobile.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Status;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.util.DateUtil;

public class RespondStatusActivity extends BaseActivity {

	public static final String EXTRAS_STATUS = "EXTRA_STATUS";
	
	private static final int NUM_MAX_CHARACERS = 800;
	
	private EditText mEditText; 
	
	private Status mStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_respond_status);
		
		Bundle extras = getIntent().getExtras();
		mStatus = (Status) extras.get(EXTRAS_STATUS);
		
		final TextView tvTextCount = (TextView) findViewById(R.id.tv_text_count);
		mEditText = (EditText) findViewById(R.id.et_text);
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int numRemainingChars = NUM_MAX_CHARACERS - s.length();
				tvTextCount.setText(String.valueOf(numRemainingChars));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// ((LazyLoadingImageView)
		// v.findViewById(R.id.iv_photo)).setImageUrl(status.user.getThumbnailUrl());
		((TextView) findViewById(R.id.tv_date)).setText(DateUtil.getFormattedStatusCreatedAt(mStatus));
		// ((TextView) v.findViewById(R.id.tv_user_name)).setText(status.user.getCompleteName());
		((TextView) findViewById(R.id.tv_text)).setText(mStatus.text);

		addActionToActionBar(R.drawable.ic_add, new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = mEditText.getText().toString();
				if(text.length() <= NUM_MAX_CHARACERS) {
					new PostAnswerTask().execute(text);
				}
			}
		});
	}
	
	class PostAnswerTask extends AsyncTask<String, Void, br.com.developer.redu.models.Status> {

		@Override
		protected void onPreExecute() {
			showProgressDialog("Enviando sua resposta…");
		}
		
		@Override
		protected br.com.developer.redu.models.Status doInBackground(String... params) {
			String text = params[0];
			
			br.com.developer.redu.models.Status status;
			try {
				DefaultReduClient client = ReduApplication.getReduClient(RespondStatusActivity.this);
				status = client.postAnswer(mStatus.id, text);
			} catch(Exception e) {
				status = null;
			}
			
			return status;
		}
		
		@Override
		protected void onPostExecute(br.com.developer.redu.models.Status result) {
			dismissProgressDialog();
			
			if(result == null) {
				Toast.makeText(RespondStatusActivity.this, "Não foi possível enviar sua resposta.", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RespondStatusActivity.this, "Resposta enviada com sucesso.", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
}
