package br.com.redu.redumobile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.lecture.UploadStep3Activity;

public class NewModuleActivity extends BaseActivity {

	private Space space;
	private EditText mEtName;
	private EditText mEtDesc;
	private static final int NUM_MAX_CHARACERS_NAME = 30;
	private static final int NUM_MAX_CHARACERS_DESCRIPTION = 250;
	private Context mContext = this;
	public ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_module);
		space = (Space) getIntent().getExtras().get(Space.class.getName());
		mEtName = (EditText) findViewById(R.id.etModuleName);
		mEtDesc = (EditText) findViewById(R.id.etModuleDesc);

		TextView title = (TextView) findViewById(R.id.tv_title_action_bar);
		title.setText(space.name);
		addActionToActionBar(R.drawable.bt_send_blue,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String name = mEtName.getText().toString();
						String description = mEtDesc.getText().toString();
						if (name.length() <= NUM_MAX_CHARACERS_NAME
								&& description.length() <= NUM_MAX_CHARACERS_DESCRIPTION) {
							new SaveModule().execute(name, description);
						} else {
							Toast toast = Toast
									.makeText(
											mContext,
											"Você ultrapassou a quantidade máxima de caracteres...",
											Toast.LENGTH_LONG);
							toast.show();
						}
					}
				});
	}

	class SaveModule extends AsyncTask<String, Void, Subject> {

		private boolean mError;

		@Override
		protected void onPreExecute() {
			/*
			 * dialog =
			 * ProgressDialog.show(mContext,"Redu","Adicionando Módulo...",
			 * false, true); dialog.setIcon(R.drawable.ic_launcher);
			 * dialog.setCancelable(false);
			 */
			showProgressDialog("Adicionando Módulo...", false);
			super.onPreExecute();
		}

		protected Subject doInBackground(String... text) {
			DefaultReduClient redu = ReduApplication.getReduClient(mContext);
			Subject subject = new Subject();
			try {
				subject.name = text[0];
				subject.description = text[1];
				redu.postSubject(space.id, text[0], text[1]);
			} catch (Exception e) {
				e.printStackTrace();
				subject = null;
				mError = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Subject subject) {
			if (mError) {
				showAlertDialog(
						NewModuleActivity.this,
						"Houve um problema ao adicionar um Módulo. Verifique sua conexão com a internet e tente novamente.",
						null);
				setResult(Activity.RESULT_CANCELED);
			} else {
				if (subject != null) {
					Intent data = new Intent();
					data.putExtra(SpaceActivity.EXTRA_SUBJECT_RESULT, subject);
					setResult(Activity.RESULT_OK, data);
				}
				finish();
			}
		};
	}

}
