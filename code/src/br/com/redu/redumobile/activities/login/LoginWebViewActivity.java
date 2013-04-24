package br.com.redu.redumobile.activities.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import br.com.developer.redu.DefaultReduClient;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;
import br.com.redu.redumobile.activities.HomeActivity;

public class LoginWebViewActivity extends Activity{
	
	WebView myWebView;
	
	SharedPreferences prefs;
	
	Context mContext = this;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		/* An instance of this class will be registered as a JavaScript interface */
		class MyJavaScriptInterface
		{
		    @SuppressWarnings("unused")
		    public void processHTML(String pin)
		    {
		        ReduApplication.setPIN(pin);
		        prefs.edit().putString("pin", pin).commit();
		        Toast toast = Toast.makeText(mContext, "Aguarde...",Toast.LENGTH_LONG);
		        toast.show();
		        Intent it = new Intent(mContext, HomeActivity.class);
		        mContext.startActivity(it);
		        finish();
		    }
		}
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String pin = prefs.getString("pin", null);
		if (pin != null){
			Intent it = new Intent(mContext, HomeActivity.class);
			mContext.startActivity(it);
	        finish();
		}
			
		setContentView(R.layout.activity_login_web);
		DefaultReduClient client = ReduApplication.getReduClientPIN();
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.getSettings().setJavaScriptEnabled(true);
		//myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		myWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		Log.i("AUTHORIZE URL", client.getAuthorizeUrl());
		
		/* WebViewClient must be set BEFORE calling loadUrl! */
		myWebView.setWebViewClient(new WebViewClient() {
		    @Override
		    public void onPageFinished(WebView view, String url)
		    {
		        /* This call inject JavaScript into the page which just finished loading. */
		    	if (myWebView.getUrl().equals("http://www.redu.com.br/oauth/authorize")){
		    		myWebView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('code')[0].innerHTML);");
		    	}	
		    }
		});
		
		myWebView.loadUrl(client.getAuthorizeUrl());
		super.onCreate(savedInstanceState);
	}
}
