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
	private static final int CENTER = 0;
	private static final int START = 1;
	private static final int END = 2;
	private static final String VERZEKERD = "Verzekerd";
	private static final String ONVERZEKERD = "Onverzekerd";
	
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
		
	}

	@Override
	public void taskCompletionResult(List<JSONObject> result) {
		Log.i(TAG, kentekens.toString());
		setContentView(R.layout.result);
		
		int size = result.size();
		
		if (size != 0) {
			setText(result.get(0), kentekens.get(0), CENTER);
			if (size > 1) {
				setText(result.get(1), kentekens.get(1), START);
			}
			if (size > 2) {
				setText(result.get(2), kentekens.get(2), END);
			}
		} else {
			TextView mVerzekerd = (TextView) findViewById(R.id.verzekerd_center);
			mVerzekerd.setText("Er zijn geen kentekens gevonden");
		}
	}
	
	private void setText(JSONObject json, String kenteken, int place) {
		String verzekerd;
		String apk;
		boolean valid = false;
		try {
			verzekerd = json.getJSONObject("resource").getString(
					"WAMverzekerdgeregistreerd") != null && json.getJSONObject("resource").getString(
							"WAMverzekerdgeregistreerd").equals("true") ? VERZEKERD : ONVERZEKERD;
			String tempapk = json.getJSONObject("resource").getString(
					"VervaldatumAPK");
			Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
					Locale.US).parse(tempapk);
			if (date.compareTo(new Date()) > 0 && verzekerd.equals(VERZEKERD)) {
				valid = true;
			}
			
			apk = new SimpleDateFormat("dd/MM/yyyy",
					Locale.US).format(date);
			
		} catch (JSONException | ParseException e) {
			valid = false;
			verzekerd = "Geen info\ngevonden";
			apk = "";
		}
		
		switch (place) {
			case CENTER:
				setCenterText(kenteken, verzekerd, apk, valid);
				break;
			case START:
				setLeftText(kenteken, verzekerd, apk, valid);
				break;
			case END:
				setRightText(kenteken, verzekerd, apk, valid);
				break;
		}
		
	}
	
	private void setLeftText(String kenteken, String verzekerd, String apk, boolean valid) {
		setColor(R.id.left_content, valid);
		
		TextView mKenteken = (TextView) findViewById(R.id.kenteken_left);
		TextView mVerzekerd = (TextView) findViewById(R.id.verzekerd_left);
		TextView mAPK = (TextView) findViewById(R.id.apk_left);
		
		mKenteken.setText(kenteken);
		mVerzekerd.setText(verzekerd);
		mAPK.setText(apk);
	}
	
	private void setCenterText(String kenteken, String verzekerd, String apk, boolean valid) {
		setColor(R.id.center_content, valid);	
		
		TextView mKenteken = (TextView) findViewById(R.id.kenteken_center);
		TextView mVerzekerd = (TextView) findViewById(R.id.verzekerd_center);
		TextView mAPK = (TextView) findViewById(R.id.apk_center);
		
		mKenteken.setText(kenteken);
		mVerzekerd.setText(verzekerd);
		mAPK.setText(apk);
	}
	
	
	private void setRightText(String kenteken, String verzekerd, String apk, boolean valid) {
		setColor(R.id.right_content, valid);
		
		TextView mKenteken = (TextView) findViewById(R.id.kenteken_right);
		TextView mVerzekerd = (TextView) findViewById(R.id.verzekerd_right);
		TextView mAPK = (TextView) findViewById(R.id.apk_right);
		
		mKenteken.setText(kenteken);
		mVerzekerd.setText(verzekerd);
		mAPK.setText(apk);
	}
	
	private void setColor(int id, boolean valid) {
		if (valid) {
			findViewById(id).setBackgroundResource(R.drawable.green_border);
		} else {
			findViewById(id).setBackgroundResource(R.drawable.red_border);
		}
	}
}