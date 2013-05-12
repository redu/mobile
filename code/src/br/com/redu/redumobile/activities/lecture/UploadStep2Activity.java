package br.com.redu.redumobile.activities.lecture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.adapters.PopupAdapter;
import br.com.redu.redumobile.util.DownloadHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class UploadStep2Activity extends Activity {

	String superId;
	Space space;
	String type;
	private Bitmap bitmap;
	private BitmapDrawable drawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_file_or_lecture);
		superId = getIntent().getExtras().getString("id");
		type = getIntent().getExtras().getString("type");
		space = (Space)getIntent().getExtras().get(Space.class.getName());
		ListView lv = (ListView)findViewById(R.id.lvInsertFileFolder);
		String[] str = {"Gravar","Escolher da Galeria"};
		lv.setAdapter(new PopupAdapter(this, str,superId, space));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (position == 0) {
					if (type.equals("foto")) {
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(cameraIntent, 2);
					}else if(type.equals("video")){
						Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
						startActivityForResult(cameraIntent, 2);
					}else{
						
					}
					
				}
				if (position == 1) {
					if (type.equals("foto")) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Escolha a Imagem"), 2);
					}
					if (type.equals("video")) {
						Intent intent = new Intent();
						intent.setType("video/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Escolha o video"), 2);
					}
					if (type.equals("audio")) {
						
					}
				}
			}
		});
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
	        if(requestCode == 2) {
	        	if (type.equals("foto")){
	        		bitmap = (Bitmap) data.getExtras().get("data");
		            /*mRlayoutimage.setBackgroundDrawable(drawable);*/
		            Intent it = new Intent(this, UploadStep3Activity.class);
		    		it.putExtra(Space.class.getName(), space);
		    		it.putExtra("id", superId);
		    		it.putExtra("foto", bitmap);
		    		it.putExtra("type", type);
		    		startActivity(it);
		    		super.onActivityResult(requestCode, resultCode, data);
	        	}
	        	if (type.equals("video")){
	        		Uri uriVideo = data.getData();
	        		Log.i("ARQUIVO", uriVideo.getPath());
	        		
	        		try {
	        		    AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
	        		    FileInputStream fis = videoAsset.createInputStream();
	        		    Date now = new Date();
	        		    File tmpFile = new File(DownloadHelper.getLecturePath(),"video_"+now.getDay()+"_"+now.getMonth()+"_"+now.getHours()+"_"+now.getMinutes()+"_"+now.getSeconds()+".3gp"); 
	        		    FileOutputStream fos = new FileOutputStream(tmpFile);

	        		    byte[] buf = new byte[1024];
	        		    int len;
	        		    while ((len = fis.read(buf)) > 0) {
	        		        fos.write(buf, 0, len);
	        		    }       
	        		    fis.close();
	        		    fos.close();
	        		    
	        		    Intent it = new Intent(this, UploadStep3Activity.class);
			    		it.putExtra(Space.class.getName(), space);
			    		it.putExtra("id", superId);
			    		it.putExtra("video", tmpFile);
			    		it.putExtra("type", type);
			    		startActivity(it);
			    		super.onActivityResult(requestCode, resultCode, data);
	        		    
	        		  } catch (IOException io_e) {
	        		    // TODO: handle error
	        		  }
	        		
	        	}
	        }
	    }
		
	}
}
