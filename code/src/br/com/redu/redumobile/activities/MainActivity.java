package br.com.redu.redumobile.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.com.developer.redu.DefaultReduClient;
import br.com.developer.redu.models.Environment;
import br.com.redu.redumobile.R;
import br.com.redu.redumobile.ReduApplication;

public class MainActivity extends Activity {

	private List<Environment> mEnvironments;
	
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((TextView) findViewById(R.id.title)).setText("Ambientes");
		
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent i = new Intent(getApplicationContext(), EnvironmentActivity.class);
				i.putExtra(Environment.class.getName(), mEnvironments.get(position));
				startActivity(i);
			}
		});

		// Log.d("REDU", "Visit this url: " + redu.getAuthorizeUrl());

		// showWebDialog("Redu", redu.getAuthorizeUrl());
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				DefaultReduClient redu = ReduApplication.getClient();
				mEnvironments = redu.getEnvironments();
				return null;
			}

			protected void onPostExecute(Void result) {
				//((TextView) findViewById(R.id.details)).setText(result.first_name + " " + result.last_name + ", ");

				mListView.setAdapter(new ArrayAdapter<Environment>(
								getApplicationContext(),
								android.R.layout.simple_dropdown_item_1line,
								mEnvironments));
			};

		}.execute();

	}

	private void showWebDialog(String title, String url) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(title);
		WebView wv = new WebView(this);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setHorizontalScrollBarEnabled(false);
		wv.loadUrl(url);

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		alert.setView(wv);

		alert.show();
	}

}
