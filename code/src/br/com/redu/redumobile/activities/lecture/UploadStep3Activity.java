package br.com.redu.redumobile.activities.lecture;

import java.io.File;

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
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Space;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.BaseActivity;

public class UploadStep3Activity extends BaseActivity {

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
	private Subject mSubject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_inserted_file_or_lecture);
		superId = getIntent().getExtras().getString("id");
		type = getIntent().getExtras().getString("type");
		space = (Space) getIntent().getExtras().get(Space.class.getName());
		mSubject = (Subject) getIntent().getExtras().get(Subject.class.getName());
		if (type.equals("foto")) {
			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
			bmpFactoryOptions.inJustDecodeBounds = true;
			bmpFactoryOptions.inSampleSize = 4;
			bitmap = BitmapFactory.decodeFile(getIntent().getExtras().getString("foto"), bmpFactoryOptions);
			drawable = new BitmapDrawable(bitmap);
			mFile = new File(getIntent().getExtras().getString("foto"));
		} else if (type.equals("video")) {
			mFile = new File(getIntent().getExtras().getString("video"));
		}
		superId = getIntent().getExtras().getString("id");
		space = (Space) getIntent().getExtras().get(Space.class.getName());

		ImageView ivPreview = (ImageView) findViewById(R.id.camera_preview);
		TextView tvPreviewName = (TextView) findViewById(R.id.tvImageName);
		TextView tvWhereLecture = (TextView) findViewById(R.id.tvWhereLecture);
		etTitleLecture = (EditText) findViewById(R.id.etTitleLecture);

		Button btAdicionarPreview = (Button) findViewById(R.id.btAdicionarPreview);
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
				Object[] params = { l, mFile };
				sl = new SaveLecture();
				sl.execute(params);
			}
		});

		Button btCancelarPreview = (Button) findViewById(R.id.btCancelarPreview);
		tvWhereLecture.setText("...>" + space.name + ">" + mSubject.name);
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

		private boolean mError;

		@Override
		protected void onPreExecute() {
			showProgressDialog("Adicionando Aula...", false);
		}

		protected Void doInBackground(Object... obj) {
			try {
				DefaultReduClient redu = ReduApplication.getReduClient(mContext);
				redu.postLecture((Lecture) obj[0], mSubject.id, (java.io.File) obj[1]);
			} catch (Exception e) {
				e.printStackTrace();
				mError = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			dismissProgressDialog();
			if (mError) {
				showAlertDialog(UploadStep3Activity.this, "Houve um problema ao adicionar a aula. Verifique sua conexão com a internet e tente novamente.", null);
			} else {
				finish();
			}
		};
	}
}
