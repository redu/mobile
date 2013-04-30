package br.com.redu.redumobile.activities.lecture;

import br.com.developer.redu.models.Space;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.adapters.PopupAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class UploadStep3Activity extends Activity {

	private String superId;
	private Space space;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_inserted_file_or_lecture);
		superId = getIntent().getExtras().getString("id");
		space = (Space)getIntent().getExtras().get(Space.class.getName());
		ListView lv = (ListView)findViewById(R.id.lvInsertFileFolder);
		String[] str = {"Gravar","Escolher da Galeria"};
		lv.setAdapter(new PopupAdapter(this, str,superId, space));
	}
	
}
