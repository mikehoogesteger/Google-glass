package com.infosupport.licenseplatescanner.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.infosupport.licenseplatescanner.R;
import com.infosupport.licenseplatescanner.callback.PictureTaker;
import com.infosupport.licenseplatescanner.callback.TaskReady;

public class ScanActivity extends Activity implements TaskReady {
	protected static final String TAG = "ScanActivity";
	private SurfaceView mPreview;
	private SurfaceHolder mPreviewHolder;
	private static android.hardware.Camera mCamera;
	private boolean mInPreview = false;
	private boolean mCameraConfigured = false;
	private boolean cameraResetted = false;

	// code copied from
	// http://developer.android.com/guide/topics/media/camera.html
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		mCamera = null;
		while (mCamera == null) {
			try {
				mCamera = Camera.open(); // attempt to get a Camera instance
			} catch (Exception e) {
				// Camera is not available (in use or does not exist)
				System.out.println("Camera is not available");
			}
			if (mCamera == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return mCamera; // returns null if camera is unavailable
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mInPreview = false;
		mCameraConfigured = false;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mPreview = (SurfaceView) findViewById(R.id.preview);
		mPreviewHolder = mPreview.getHolder();
		mPreviewHolder.addCallback(surfaceCallback);
		mCamera = getCameraInstance();
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
				parameters.setJpegQuality(100);
				
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
	
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			Log.v(TAG, "surfaceCreated");
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.v(TAG, "surfaceChanged=" + width + "," + height);
			configPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.v(TAG, "surfaceDestroyed");
			if (!cameraResetted) {
				if (mCamera != null) {
					mCamera.release();
					mCamera = null;
				}
			}
		}
	};


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// see note 2 after the code for explanation
		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			if (mInPreview) {
				PictureTaker pt = new PictureTaker(mCamera, this);
				mCamera.takePicture(pt.getmShutterCallback(), pt.getmPictureCallback(), pt.getMjpeg());
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

	@Override
	public void resume() {
		Log.i(TAG, "Resuming");
		
		cameraResetted = true;
		
		Intent intent = new Intent(ScanActivity.this, ScanActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
