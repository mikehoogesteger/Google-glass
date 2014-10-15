package com.infosupport.hello;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class VideoPlayerActivity extends Activity { //implements MediaPlayer.OnPreparedListener {
	public final static String TAG = "VideoPlayerActivity";
	TextView mTextView;
	MediaPlayer mediaPlayer;
	private static final int VIDEO_PLAY_REQUEST_CODE = 100;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String filepath;
		Bundle extras = getIntent().getExtras();
		if (extras != null)
			filepath = extras.getString("filepath");
		else {
			filepath = copyAsset("hello.mp4");
			Log.v(TAG,  "after copyAsset: filepath="+filepath);
		}        

		// play video using the Glass video player
		Intent i = new Intent();
		i.setAction("com.google.glass.action.VIDEOPLAYER");		
		i.putExtra("video_url", filepath);
		startActivityForResult(i, VIDEO_PLAY_REQUEST_CODE);
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VIDEO_PLAY_REQUEST_CODE) 		
			finish();
	}

	String copyAsset(String filename) {
		final String PATH = Environment.getExternalStorageDirectory().toString() + "/myvideoapps/";
		File dir = new File(PATH);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.v(TAG, "ERROR: Creation of directory " + PATH + " on sdcard failed");
				return null;
			} else {
				Log.v(TAG, "Created directory " + PATH + " on sdcard");
			}
		}

		if (!(new File( PATH + filename).exists())) {
			Log.v(TAG, "copying file " + filename);
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open(filename);
				OutputStream out = new FileOutputStream(PATH + filename);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + filename + e.toString());
				return null;
			}
		}    	
		return PATH + filename; 
	}
}
