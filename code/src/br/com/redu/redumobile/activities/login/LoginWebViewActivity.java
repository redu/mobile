package br.com.redu.redumobile.activities.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import br.com.developer.redu.DefaultReduClient;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.BaseActivity;
import br.com.redu.redumobile.activities.HomeActivity;
import br.com.redu.redumobile.util.PinCodeHelper;

public class LoginWebViewActivity extends BaseActivity {

	private WebView mWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(PinCodeHelper.hasPinCode(this)) {
			startActivity(new Intent(this, HomeActivity.class));
			finish();
		}

		setContentView(R.layout.activity_login_web);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (url.equals("http://www.redu.com.br/oauth/authorize")) {
					showProgressDialog("Aguarde alguns instantes enquanto você é redirecionado ao aplicativo Redu Mobile…", false);
				}
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				// This call inject JavaScript into the page which just finished loading.
				if (url.equals("http://www.redu.com.br/oauth/authorize")) {
					mWebView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('code')[0].innerHTML);");
				}
			}
		});

//		Log.i("AUTHORIZE URL", client.getAuthorizeUrl());
		DefaultReduClient client = ReduApplication.getReduClient(this);
		mWebView.loadUrl(client.getAuthorizeUrl());
	}

	// An instance of this class will be registered as a JavaScript interface
	class MyJavaScriptInterface {
		public void processHTML(String pinCode) {
			PinCodeHelper.setPinCode(getApplicationContext(), pinCode);
			dismissProgressDialog();
			Intent it = new Intent(LoginWebViewActivity.this, HomeActivity.class);
			startActivity(it);
			finish();
		}
	}
}