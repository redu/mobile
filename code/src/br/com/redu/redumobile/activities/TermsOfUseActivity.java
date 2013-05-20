package br.com.redu.redumobile.activities;

import android.os.Bundle;
import android.webkit.WebView;
import br.com.redu.redumobile.R;

public class TermsOfUseActivity extends BaseActivity {
	private static final String URL = "http://www.redu.com.br/paginas/termos_uso";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_terms_of_use);

		WebView wv = (WebView) findViewById(R.id.webview);
		wv.loadUrl(URL);
	}
}
