package br.com.redu.redumobile.activities.lecture;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Folder;
import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.BaseActivity;

public class NewFolderActivity extends BaseActivity{
	
	EditText mEtFolder;
	private static final int NUM_MAX_CHARACERS = 30;
	
	Context mContext = this;
	Space space;
	Folder folder;
	String superFolderId;
	ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_folder);
		folder = (Folder) getIntent().getExtras().get(Folder.class.getName());
		
		space = (Space) getIntent().getExtras().get(Space.class.getName());
		mEtFolder = (EditText) findViewById(R.id.etFolder);
		TextView title = (TextView) findViewById(R.id.tv_title_action_bar);
		title.setText(space.name);
		if (folder == null){
			superFolderId = getIntent().getExtras().getString("id");
		}else{
			mEtFolder.setText(folder.name);
		}
		addActionToActionBar(R.drawable.bt_send_actionbar, new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = mEtFolder.getText().toString();
				if(text.length() <= NUM_MAX_CHARACERS) {
						new SaveFolder().execute(text);
				}
			}
		});
	}
	
	class SaveFolder extends AsyncTask<String, Void, Void> {
		
		
		@Override
		protected void onPreExecute() {
			if (folder == null)
				dialog = ProgressDialog.show(mContext,"Redu","Adicionando diretório...", false, true);
			else
				dialog = ProgressDialog.show(mContext,"Redu","Alterando diretório...", false, true);
			dialog.setIcon(R.drawable.ic_launcher);
			dialog.setCancelable(false);
			super.onPreExecute();
		}
		protected Void doInBackground(String... text) {
			DefaultReduClient redu = ReduApplication.getReduClient(mContext);
			if (folder == null)
				redu.postFolder(text[0], superFolderId);
			else
				redu.editFolder(text[0], folder.id);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			finish();
		};
	}
	
}
