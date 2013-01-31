package br.com.luismedeiros.redutest;

import br.com.developer.redu.models.Subject;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SubjectActivity extends Activity {
	private Subject mSubject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((TextView) findViewById(R.id.title)).setText("Módulo");

		Bundle extras = getIntent().getExtras();
		mSubject = (Subject) extras.get(Subject.class.getName());

		((TextView) findViewById(R.id.details)).setText(mSubject.name);
	}
}
