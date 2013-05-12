package br.com.redu.redumobile.activities.lecture;

import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.adapters.PopupAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UploadStep3Activity extends Activity {


	private String superId;
	private Space space;
	private Bitmap bitmap;
	private BitmapDrawable drawable;
	
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_inserted_file_or_lecture);
		if (type.equals("foto")){
			bitmap = (Bitmap)getIntent().getExtras().get("foto");
			bitmap = (Bitmap)getIntent().getExtras().get("foto");
			drawable = new BitmapDrawable(bitmap);
		}else if(type.equals("video")){
			
		}
		superId = getIntent().getExtras().getString("id");
		space = (Space)getIntent().getExtras().get(Space.class.getName());
		
		
		ImageView ivPreview = (ImageView)findViewById(R.id.camera_preview);
		TextView tvPreviewName = (TextView)findViewById(R.id.tvImageName);
		TextView tvWhereLecture = (TextView)findViewById(R.id.tvWhereLecture);
		Button btCancelarPreview = (Button)findViewById(R.id.btCancelarPreview);
		tvWhereLecture.setText("Disciplina: "+space.name);
		btCancelarPreview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		if (type.equals("foto"))
			ivPreview.setBackgroundDrawable(drawable);
		if (type.equals("video"))
			ivPreview.setImageResource(R.drawable.ic_midia);
		if (type.equals("audio"))
			ivPreview.setImageResource(R.drawable.ic_audio_big);
	}
	
}

