package com.infosupport.licenseplatescanner.callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class PictureTaker {

	private static final String TAG = "PictureTaker";
	private static final int MEDIA_TYPE_IMAGE = 1;

	private Camera.ShutterCallback mShutterCallback;
	private Camera.PictureCallback mPictureCallback;
	private Camera.PictureCallback mjpeg;

	public PictureTaker(final Camera mCamera, final TaskReady delegation) {
		
		mShutterCallback = new Camera.ShutterCallback() {
			@Override
			public void onShutter() {
				System.out.println("Camera shutted");
			}
		};

		mPictureCallback = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera c) {
				System.out.println("Raw callback");
				if (data != null) {
				}
			}
		};

		mjpeg = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				Log.i(TAG, "Picture was taken");

				File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
				if (pictureFile == null) {
					Log.v(TAG,
							"Error creating media file, check storage permissions: ");
					return;
				}

				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
				} catch (FileNotFoundException e) {
					Log.e(TAG, "File not found: " + e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, "Error accessing file: " + e.getMessage());
				}

				Log.v(TAG, pictureFile.getAbsolutePath());
				camera.release();
				camera = null;
				delegation.resume();
			}
		};
		
		Log.i(TAG, "Callbacks created");
	}

	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"SmartCamera");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}
		return mediaFile;
	}

	public Camera.ShutterCallback getmShutterCallback() {
		return mShutterCallback;
	}

	public void setmShutterCallback(Camera.ShutterCallback mShutterCallback) {
		this.mShutterCallback = mShutterCallback;
	}

	public Camera.PictureCallback getmPictureCallback() {
		return mPictureCallback;
	}

	public void setmPictureCallback(Camera.PictureCallback mPictureCallback) {
		this.mPictureCallback = mPictureCallback;
	}

	public Camera.PictureCallback getMjpeg() {
		return mjpeg;
	}

	public void setMjpeg(Camera.PictureCallback mjpeg) {
		this.mjpeg = mjpeg;
	}

}
