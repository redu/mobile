package br.com.redu.redumobile.activities;

import android.content.Context;
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

public class NewModuleActivity extends BaseActivity {

	private Space space;
	private EditText mEtName;
	private EditText mEtDesc;
	private static final int NUM_MAX_CHARACERS_NAME = 30;
	private static final int NUM_MAX_CHARACERS_DESCRIPTION = 250;
	private Context mContext = this;
	public Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_module);
		space = (Space) getIntent().getExtras().get(Space.class.getName());
		mEtName = (EditText) findViewById(R.id.etModuleName);
		mEtDesc = (EditText) findViewById(R.id.etModuleDesc);

		TextView title = (TextView) findViewById(R.id.tv_title_action_bar);
		title.setText(space.name);
		addActionToActionBar(R.drawable.ic_add, new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = mEtName.getText().toString();
				String description = mEtDesc.getText().toString();
				if (name.length() <= NUM_MAX_CHARACERS_NAME
						&& description.length() <= NUM_MAX_CHARACERS_DESCRIPTION) {
					new SaveModule().execute(name, description);
				} else {
					Toast toast = Toast.makeText(mContext,
							"Verifique a quantidade máxima de caracteres",
							Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});
	}

	class SaveModule extends AsyncTask<String, Void, Void> {
		protected Void doInBackground(String... text) {
			DefaultReduClient redu = ReduApplication.getReduClient(mContext);
			Subject s = redu.postSubject(space.id, text[0], text[1]);
			/*if (s != null) {
				toast = Toast.makeText(mContext, "Módulo " + s.name
						+ " Adicionado", Toast.LENGTH_LONG);
				toast.show();
			}*/
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			finish();
		};
	}

}
