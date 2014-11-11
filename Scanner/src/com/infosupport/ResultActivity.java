package com.infosupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

/**
 * @author Mike ResultActivity fills the resultpage with information about the
 *         license plate.
 */
public class ResultActivity extends Activity implements TaskDelegate {
	
	private static final String TAG = "ResultActivity";
	
	private TextView mKenteken;
	private TextView mVerzekerd;
	private TextView mAPK;
	private TextView mError;
	private ArrayList<String> kentekens;
	private KentekenServiceCaller sc;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		kentekens = b.getStringArrayList("json");
		sc = new KentekenServiceCaller(kentekens, this);
		sc.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			Log.i(TAG, "Menu item scan");
			Intent intent = new Intent(this, OCRActivity.class);
			startActivity(intent);
			return true;

		case R.id.stop:
			Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosupport.TaskDelegate#taskCompletionResult(org.json.JSONObject)
	 */
	@Override
	public void taskCompletionResult(JSONObject result) {
		setContentView(R.layout.result);
		if (result == null) {
			mVerzekerd = (TextView) findViewById(R.id.verzekerd_center);
			mVerzekerd.setText("Het kenteken komt niet voor in de database");
		} else {
			try {
				String kenteken = result.getJSONObject("resource").getString("Kenteken");
				String verzekerd = result.getJSONObject("resource").getString(
						"WAMverzekerdgeregistreerd");
				String apk = result.getJSONObject("resource").getString(
						"VervaldatumAPK");
				Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
						Locale.US).parse(apk);
				String formattedApk = new SimpleDateFormat("dd/MM/yyyy",
						Locale.US).format(date);

				mKenteken = (TextView) findViewById(R.id.kenteken_center);
				mVerzekerd = (TextView) findViewById(R.id.verzekerd_center);
				mAPK = (TextView) findViewById(R.id.apk_center);

				mKenteken.setText("Kenteken: " + kenteken);
				if (verzekerd == "true") {
					mVerzekerd.setText("Verzekerd: Ja");
				} else {
					mVerzekerd.setText("Verzekerd: Nee");
				}
				mAPK.setText("APK verloopt op: " + formattedApk);
			} catch (JSONException | ParseException e) {
				try {
					String error = result.getJSONObject("error").getString(
							"message");

//					mError = (TextView) findViewById(R.id.error);
//					mError.setText(error);
				} catch (JSONException e1) {
					Log.e(TAG, "catch -> error niet gevonden");
					e1.printStackTrace();
				}
				Log.e(TAG,
						"catch -> resource niet gevonden | datum kan niet geparst worden");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void taskCompletionResult(List<JSONObject> result) {
		Log.i(TAG, kentekens.toString());
		setContentView(R.layout.result);
		
		if (result.size() != 0) {
			
			mKenteken = (TextView) findViewById(R.id.kenteken_center);
			mVerzekerd = (TextView) findViewById(R.id.verzekerd_center);
			mAPK = (TextView) findViewById(R.id.apk_center);
			
			String kenteken = "";
			String verzekerd = "";
			String apk = "";
			
			//for (JSONObject json : result) {
				/*JSONObject json = result.get(0);
				try {
					kenteken = json.getJSONObject("resource").getString("Kenteken") + " ";
					verzekerd = json.getJSONObject("resource").getString(
							"WAMverzekerdgeregistreerd") != null && json.getJSONObject("resource").getString(
									"WAMverzekerdgeregistreerd").equals("true") ? "Verzekerd " : "Onverzekerd ";
					String tempapk = json.getJSONObject("resource").getString(
							"VervaldatumAPK");
					Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
							Locale.US).parse(tempapk);
					apk = new SimpleDateFormat("dd/MM/yyyy",
							Locale.US).format(date) + " ";
					
				} catch (JSONException | ParseException e) {
					kenteken = "";
					verzekerd = "Er konden geen gegevens gevonden worden bij het kenteken: " + kentekens.get(0);
					apk = "";
				}*/
			//}
			/*mKenteken.setText(kenteken);
			mVerzekerd.setText(verzekerd);
			mAPK.setText(apk);*/
			System.out.println("Tekst wordt gewijzigd");
			mKenteken.setText("99WK07");
			mVerzekerd.setText("Onverzekerd");
			mAPK.setText("05/12/2014");
		} else {
			mVerzekerd = (TextView) findViewById(R.id.verzekerd_center);
			mVerzekerd.setText("Er zijn geen kentekens gevonden");
		}
		setLeftText();
		setRightText();
	}
	
	public void setLeftText() {
		mKenteken = (TextView) findViewById(R.id.kenteken_left);
		mVerzekerd = (TextView) findViewById(R.id.verzekerd_left);
		mAPK = (TextView) findViewById(R.id.apk_left);
		
		mKenteken.setText("94TNDL");
		mVerzekerd.setText("Verzekerd");
		mAPK.setText("23/07/2015");
	}
	
	public void setRightText() {
		mKenteken = (TextView) findViewById(R.id.kenteken_right);
		mVerzekerd = (TextView) findViewById(R.id.verzekerd_right);
		mAPK = (TextView) findViewById(R.id.apk_right);
		
		mKenteken.setText("01SK99");
		mVerzekerd.setText("Onverzekerd");
		mAPK.setText("21/11/2020");
	}
}