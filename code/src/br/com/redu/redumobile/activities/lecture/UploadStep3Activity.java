package br.com.redu.redumobile.activities.lecture;

import java.io.File;

import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.adapters.PopupAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UploadStep3Activity extends Activity {


	private String superId;
	private Space space;
	private Bitmap bitmap;
	private BitmapDrawable drawable;
	
	private Context mContext = this;
	
	private String type;
	private SaveLecture sl;
	
	private EditText etTitleLecture;
	private static final int NUM_MAX_CHARACERS = 250;
	private File mFile;
	public ProgressDialog dialog;
	private Subject mSubject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_inserted_file_or_lecture);
		superId = getIntent().getExtras().getString("id");
		type = getIntent().getExtras().getString("type");
		space = (Space)getIntent().getExtras().get(Space.class.getName());
		mSubject = (Subject)getIntent().getExtras().get(Subject.class.getName());
		if (type.equals("foto")){
			bitmap = BitmapFactory.decodeFile(getIntent().getExtras().getString("foto"));
			drawable = new BitmapDrawable(bitmap);
			mFile = new File(getIntent().getExtras().getString("foto"));
		}else if(type.equals("video")){
			mFile = new File(getIntent().getExtras().getString("video"));
		}
		superId = getIntent().getExtras().getString("id");
		space = (Space)getIntent().getExtras().get(Space.class.getName());
		
		
		ImageView ivPreview = (ImageView)findViewById(R.id.camera_preview);
		TextView tvPreviewName = (TextView)findViewById(R.id.tvImageName);
		TextView tvWhereLecture = (TextView)findViewById(R.id.tvWhereLecture);
		etTitleLecture = (EditText)findViewById(R.id.etTitleLecture);
		
		Button btAdicionarPreview = (Button)findViewById(R.id.btAdicionarPreview);
		btAdicionarPreview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = etTitleLecture.getText().toString();
				Lecture l = new Lecture();
				l.name = text;
				if (type.equals("foto")) {
					l.type = Lecture.TYPE_DOCUMENT;
				}
				if (type.equals("video")) {
					l.type = Lecture.TYPE_MEDIA;
				}
				Object[] params = {l,mFile};
				sl = new SaveLecture();
				sl.execute(params);
			}
		});
		
		Button btCancelarPreview = (Button)findViewById(R.id.btCancelarPreview);
		tvWhereLecture.setText("...>"+space.name+">"+mSubject.name);
		btCancelarPreview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		if (type.equals("foto"))
			ivPreview.setBackgroundDrawable(drawable);
		if (type.equals("video"))
			ivPreview.setBackgroundResource(R.drawable.ic_midia_big);
		if (type.equals("audio"))
			ivPreview.setImageResource(R.drawable.ic_audio_big);
	}
	
	class SaveLecture extends AsyncTask<Object, Void, Void> {
		
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(mContext,"Redu","Adicionando Aula...", false, true);
			dialog.setIcon(R.drawable.ic_launcher);
			dialog.setCancelable(false);
			super.onPreExecute();
		}
		protected Void doInBackground(Object... obj) {
			DefaultReduClient redu = ReduApplication.getReduClient(mContext);
			redu.postLecture((Lecture)obj[0], mSubject.id,(java.io.File)obj[1]);
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

