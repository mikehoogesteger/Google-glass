package com.infosupport;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class PreviewActivity extends Activity {
	public static String TAG = "PreviewActivity";

	private SurfaceView mPreview;
	private SurfaceHolder mPreviewHolder;
	private Camera mCamera;
	private boolean mInPreview = false;
	private boolean mCameraConfigured = false;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	// code copied from
	// http://developer.android.com/guide/topics/media/camera.html
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			Log.e(TAG, "Camera is not available");
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.preview);

		// as long as this window is visible to the user, keep the device's
		// screen turned on and bright.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mPreview = (SurfaceView) findViewById(R.id.preview);
		mPreviewHolder = mPreview.getHolder();
		mPreviewHolder.addCallback(surfaceCallback);

		mCamera = getCameraInstance();
		if (mCamera != null)
			startPreview();

	}

	private void configPreview(int width, int height) {
		Log.i(TAG, "configPreview");
		Log.i(TAG, mCamera == null ? "mCamera is null" : "mCamera is not null");
		Log.i(TAG,
				mPreviewHolder.getSurface() == null ? "mPreviewHolder.getSurface() is null"
						: "mPreviewHolder.getSurface() is not null");

		if (mCamera != null && mPreviewHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(mPreviewHolder);
			} catch (IOException e) {
				Toast.makeText(PreviewActivity.this, e.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!mCameraConfigured) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setPreviewFpsRange(30000, 30000);
				parameters.setPreviewSize(640, 360);
				mCamera.setParameters(parameters);
				mCameraConfigured = true;
			}
		}
	}

	private void startPreview() {
		Log.i(TAG, "entering startPreview");

		if (mCameraConfigured && mCamera != null) {
			Log.i(TAG, "before calling mCamera.startPreview");
			mCamera.startPreview();
			mInPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			Log.i(TAG, "surfaceCreated");
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.i(TAG, "surfaceChanged=" + width + "," + height);
			configPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i(TAG, "surfaceDestroyed");
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}
	};

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();

		// Re-acquire the camera and start the preview.
		if (mCamera == null) {
			mCamera = getCameraInstance();
			if (mCamera != null) {
				Log.i(TAG, "mCamera!=null");
				configPreview(640, 360);
				startPreview();
			} else
				Log.i(TAG, "mCamera==null");
		}
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		if (mInPreview) {
			Log.i(TAG, "mInPreview is true");
			mCamera.stopPreview();

			mCamera.release();
			mCamera = null;
			mInPreview = false;
		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown");
		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			if (mInPreview) {
				mCamera.stopPreview();

				mCamera.release();
				mCamera = null;
				mInPreview = false;
			}
			return false;
		} else {
			Log.i(TAG, "NOT KEYCODE_CAMERA");
			return super.onKeyDown(keyCode, event);
		}
	}
}