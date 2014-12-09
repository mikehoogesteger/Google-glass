package com.infosupport.notificator.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.infosupport.notificator.R;

public class PicturePreviewActivity extends Activity {

	private static final String TAG = "PicturePreviewActivity";
	private static Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
		ImageView image = (ImageView) findViewById(R.id.imageview);
		image.setImageBitmap(bitmap);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "onKeyDown");
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			openOptionsMenu();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent output = new Intent(this, OutputActivity.class);
			startActivity(output);
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case R.id.stop:
			Intent output = new Intent(this, OutputActivity.class);
			startActivity(output);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static Bitmap getBitmap() {
		Log.v(TAG, "getBitmap");
		return bitmap;
	}

	public static void setBitmap(Bitmap bitmap) {
		Log.v(TAG, "setBitmap");
		PicturePreviewActivity.bitmap = bitmap;
	}

}
