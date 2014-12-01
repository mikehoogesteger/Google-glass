package com.infosupport.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.glass.widget.CardBuilder;
import com.infosupport.R;

/**
 * Makes an activity for the main screen. This Screen displays text and has a
 * menu to start the OCRActivity or stop the application.
 * 
 * @author Mike
 * @version 1.1
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = new CardBuilder(this, CardBuilder.Layout.MENU)
				.setText("Kenteken scanner").setFootnote("TAP voor het menu.")
				.getView();

		setContentView(view);
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
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
