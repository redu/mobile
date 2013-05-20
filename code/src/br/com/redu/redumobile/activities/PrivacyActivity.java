package br.com.redu.redumobile.activities;

import br.com.redu.redumobile.R;
import android.os.Bundle;
import android.webkit.WebView;

public class PrivacyActivity extends BaseActivity {

	private static final String URL = "http://www.redu.com.br/paginas/politica_privacidade";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_privacy);
		
		WebView wv = (WebView) findViewById(R.id.webview);
		wv.loadUrl(URL);
	}
}
