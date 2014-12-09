package com.infosupport.notificator.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.glass.widget.CardBuilder;
import com.infosupport.notificator.R;

public class OutputActivity extends Activity {

	private static final String TAG = "OutputActivity";
	private static File image;
	private static String description = null;

	private View view = null;
	private Bitmap bitmap = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		description = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
		PicturePreviewActivity.setBitmap(bitmap);

		view = new CardBuilder(this, CardBuilder.Layout.COLUMNS)
				.setText(R.string.input_description).addImage(bitmap).getView();
		setContentView(view);
	}

	@Override
	protected void onResume() {
		if (description != null) {
			view = new CardBuilder(this, CardBuilder.Layout.COLUMNS)
					.setText(description).addImage(bitmap)
					.getView();
			setContentView(view);
		}
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "onKeyDown");
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			openOptionsMenu();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent stopIntent = new Intent(this, MainActivity.class);
			stopIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(stopIntent);
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.picture_output_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case R.id.start:
			Intent startIntent = new Intent(this, MainActivity.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startIntent);
			return true;
		case R.id.show:
			Intent showIntent = new Intent(OutputActivity.this,
					PicturePreviewActivity.class);
			startActivity(showIntent);
			return true;
		case R.id.description:
			Intent descriptionIntent = new Intent(this, VoiceActivity.class);
			startActivity(descriptionIntent);
			return true;
		case R.id.stop:
			Intent stopIntent = new Intent(this, MainActivity.class);
			stopIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(stopIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static void setImage(File image) {
		Log.v(TAG, "setImage");
		OutputActivity.image = image;
	}
	
	public static void setDescription(String description) {
		OutputActivity.description = description;
	}

}
