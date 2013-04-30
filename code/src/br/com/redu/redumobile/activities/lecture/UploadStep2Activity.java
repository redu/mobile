package br.com.redu.redumobile.activities.lecture;

import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.adapters.PopupAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class UploadStep2Activity extends Activity {

	String superId;
	Space space;
	String type;
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
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}
	
}
