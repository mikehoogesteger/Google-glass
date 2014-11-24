package com.infosupport.licenseplatescanner.activity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.infosupport.licenseplatescanner.R;

public class ScanActivity extends Activity {
	private SurfaceView mPreview;
	private SurfaceHolder mPreviewHolder;
	private android.hardware.Camera mCamera;
	private boolean mInPreview = false;
	private boolean mCameraConfigured = false;

	// code copied from
	// http://developer.android.com/guide/topics/media/camera.html
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			System.out.println("Camera is not available");
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mPreview = (SurfaceView) findViewById(R.id.preview);
		mPreviewHolder = mPreview.getHolder();
		mPreviewHolder.addCallback(surfaceCallback);
		mCamera = Camera.open();
		if (mCamera != null) {
			startPreview();
		}		
	}

	private void configPreview(int width, int height) {
		if (mCamera != null && mPreviewHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(mPreviewHolder);
			} catch (IOException e) {

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
		if (mCameraConfigured && mCamera != null) {
			mCamera.startPreview();
			mInPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			configPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// Re-acquire the camera and start the preview.
		if (mCamera == null) {
			mCamera = getCameraInstance();
			if (mCamera != null) {
				configPreview(640, 360);
				startPreview();
			}
		}
	}

	@Override
	public void onPause() {
		if (mInPreview) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mInPreview = false;
		}
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// see note 2 after the code for explanation
		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			if (mInPreview) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				mInPreview = false;
			}
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			if (mInPreview) {
				System.out.println("Picture taken");
			}
			return false;
		} else
			return super.onKeyDown(keyCode, event);
	}
}
