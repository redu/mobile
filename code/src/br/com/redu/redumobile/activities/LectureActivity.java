package br.com.redu.redumobile.activities;

import br.com.developer.redu.models.Lecture;
import br.com.developer.redu.models.Subject;
import br.com.redu.redumobile.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LectureActivity extends BaseActivity{
	
	private TextView mTvSubject;
	private TextView mTvLecture;
	private Button mBtEdit;
	private Button mBtRemove;
	
	private ImageView mIvImage;
	private TextView mTvFileName;
	
	private Button mBtIsDone;
	private Button mBtWall;
	
	private Lecture mLecture;
	private Subject mSubject;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecture);
		
		mTvSubject = (TextView) findViewById(R.id.tvSubject);
		mTvLecture = (TextView) findViewById(R.id.tvLecture);
		mBtEdit = (Button) findViewById(R.id.btEdit);
		mBtRemove = (Button) findViewById(R.id.btRemove);
		mIvImage = (ImageView) findViewById(R.id.ivImage);
		mTvFileName = (TextView) findViewById(R.id.tvFileName);
		mBtIsDone = (Button) findViewById(R.id.btIsDone);
		mBtWall = (Button) findViewById(R.id.btWall);
	
		
		mLecture = (Lecture)getIntent().getExtras().get(Lecture.class.getName());
		mSubject = (Subject)getIntent().getExtras().get(Subject.class.getName());
		
		mTvSubject.setText(mSubject.name);
		mTvLecture.setText(mLecture.name);
		
		mBtEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		
		mBtRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		String extension = mLecture.mimetype;
		
		if (extension.equals("application/pdf"))
			mIvImage.setImageResource(R.drawable.vpi__tab_indicator);
		else
			mIvImage.setImageResource(R.drawable.ic_launcher);
		
		mTvFileName.setText(mLecture.name+" Tipo:"+mLecture.type);
		
		mBtIsDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		
		mBtWall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		
	}

/*	class LoadUserTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			DefaultReduClient redu = ReduApplication.getClient();
			Log.i("Redu", redu.getAuthorizeUrl());
			redu.getLecture(lectureId)
			return null;
		}
	
		protected void onPostExecute(Void... params) {
//			((TextView) v.findViewById(R.id.details)).setText(user.first_name + " " + user.last_name + ", ");
			
		};
	}*/
	
}
