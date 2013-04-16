package br.com.redu.redumobile.activities.lecture;

import br.com.redu.redumobile.R;
import br.com.redu.redumobile.adapters.PopupAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class UploadStep1Activity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_file_or_lecture);
		ListView lv = (ListView)findViewById(R.id.lvInsertFileFolder);
		String[] str = {"Foto","Vídeo","Áudio"};
		lv.setAdapter(new PopupAdapter(this, str));
	}
	
}
