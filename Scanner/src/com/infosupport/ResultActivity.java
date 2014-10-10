package com.infosupport;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ResultActivity extends Activity implements TaskDelegate {
	private static final String TAG = "ResultActivity";
	private TextView mKenteken;
	private TextView mVerzekerd;
	private TextView mAPK;
	private TextView mError;
	private String kenteken;
	private ServiceCaller sc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		kenteken = b.getString("json");
		sc = new ServiceCaller(kenteken, this);
		sc.execute();
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event) {
		Log.i(TAG, "onKeyDown");
		if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
			openOptionsMenu();
			return true;
		} else if (keycode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			Log.i(TAG, "Menu item scan");
			Intent intent = new Intent(this, OCRActivity.class);
			startActivity(intent);
			return true;

		case R.id.stop:
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void taskCompletionResult(JSONObject result) {
		Log.i(TAG, result.toString());

		String verzekerd = null;
		String apk = null;
		String error = null;

		try {
			verzekerd = result.getJSONObject("resource").getString(
					"WAMverzekerdgeregistreerd");
			apk = result.getJSONObject("resource").getString("VervaldatumAPK");
		} catch (JSONException e) {
			try {
				error = result.getJSONObject("error").getString("message");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		setContentView(R.layout.result);

		mKenteken = (TextView) findViewById(R.id.kenteken);
		mVerzekerd = (TextView) findViewById(R.id.verzekerd);
		mAPK = (TextView) findViewById(R.id.apk);
		mKenteken.setText(kenteken);
		mVerzekerd.setText(verzekerd);
		mAPK.setText(apk);

		mKenteken.setText(kenteken);
		mError = (TextView) findViewById(R.id.error);
		mError.setText(error);

		Log.i(TAG, "TEKST: " + result.toString());

	}
}