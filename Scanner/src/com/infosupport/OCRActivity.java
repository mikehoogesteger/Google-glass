package com.infosupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Mike OCRActivity makes a picture and then uses Tesseract to read the
 *         license.
 */
public class OCRActivity extends Activity implements
		GestureDetector.OnGestureListener, Camera.OnZoomChangeListener,
		Runnable, TaskDelegate {

	private static final String TAG = "OCRActivity";
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int KENTEKEN_SIZE = 6;

	private SurfaceView mPreview;
	private SurfaceHolder mPreviewHolder;
	private Camera mCamera;
	private boolean mInPreview = false;
	private boolean mCameraConfigured = false;
	private TextView mZoomLevelView;
	private GestureDetector mGestureDetector;
	private String mPath;

	/**
	 * Makes a file to store the captured picture on.
	 * 
	 * @param type
	 *            the type of media. This can be a video or image.
	 * @return a path to the place where an image can be stored.
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.zoom);

		// as long as this window is visible to the user, keep the device's
		// screen turned on and bright.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mPreview = (SurfaceView) findViewById(R.id.preview);
		mPreviewHolder = mPreview.getHolder();
		mPreviewHolder.addCallback(surfaceCallback);

		mZoomLevelView = (TextView) findViewById(R.id.zoomLevel);

		mGestureDetector = new GestureDetector(this, this);

		mCamera = Camera.open();
		startPreview();
	}

	private void initPreview(int width, int height) {
		if (mCamera != null && mPreviewHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(mPreviewHolder);
			} catch (Throwable t) {
				Log.e(TAG, "Exception in initPreview()", t);
				Toast.makeText(OCRActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!mCameraConfigured) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setPreviewFpsRange(30000, 30000);
				parameters.setPreviewSize(640, 360);
				parameters.setJpegQuality(100);

				mCamera.setParameters(parameters);
				mCamera.setZoomChangeListener(this);

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
			Log.v(TAG, "surfaceCreated");
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.v(TAG, "surfaceChanged=" + width + "," + height);
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.v(TAG, "surfaceDestroyed");
			if (mCamera != null) {
				mCamera.stopPreview();

				mCamera.release();
				mCamera = null;
				mInPreview = false;
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onGenericMotionEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.
	 * MotionEvent)
	 */
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.
	 * MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Log.v(TAG, mCamera == null ? "mCamere is null" : "mCamera NOT null");
		if (mCamera == null || mPreviewHolder.getSurface() == null) {
			return true;
		}
		Camera.Parameters parameters = mCamera.getParameters();
		Log.v(TAG, "parameters.getMaxZoom=" + parameters.getMaxZoom());
		int zoom = parameters.getZoom();

		if (velocityX < 0.0f) {
			zoom -= 10;
			if (zoom < 0)
				zoom = 0;
		} else if (velocityX > 0.0f) {
			zoom += 10;
			if (zoom > parameters.getMaxZoom())
				zoom = parameters.getMaxZoom();
		}

		try {
			mCamera.stopSmoothZoom();
			mCamera.startSmoothZoom(zoom);
		} catch (RuntimeException e) {
			mCamera.release();
			mCamera = null;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onLongPress(android.view
	 * .MotionEvent)
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		Log.v(TAG, "onLongPress");
		finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onScroll(android.view.
	 * MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onShowPress(android.view
	 * .MotionEvent)
	 */
	@Override
	public void onShowPress(MotionEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.
	 * view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.hardware.Camera.OnZoomChangeListener#onZoomChange(int,
	 * boolean, android.hardware.Camera)
	 */
	@Override
	public void onZoomChange(int zoomValue, boolean stopped, Camera camera) {
		mZoomLevelView.setText("ZOOM: " + zoomValue);
	}

	Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};
	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {
			if (data != null) {
			}
		}
	};

	Camera.PictureCallback mjpeg = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// copied from
			// http://developer.android.com/guide/topics/media/camera.html#custom-camera
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
			mPath = pictureFile.getAbsolutePath();

			Thread thread = new Thread(OCRActivity.this);
			thread.start();
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		File image = new File(mPath);
		OCRServiceCaller sv = new OCRServiceCaller(this, image);
		sv.execute();
	}

	/**
	 * Show result of the recognized text by using a ResultActivity, if the text
	 * is 6 characters long it means it has found a correct kenteken.
	 * 
	 * @param recognizedText
	 *            is the text that the OCR reader has found
	 */
	private void showResult(List<String> kentekens) {
		if (kentekens.size() > 0) {
			Log.v(TAG, ">>>> " + kentekens);

			Intent intent = new Intent(this, ResultActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle b = new Bundle();
			b.putStringArrayList("json", (ArrayList<String>) kentekens);
			intent.putExtras(b);
			startActivity(intent);

		} else {
			String msg = null;
			msg = "Geen correct kenteken gescand!";
			if (!isWiFiConnected(this)) {
				msg = "U bent niet verbonden met het internet!";
			}
			Toast.makeText(OCRActivity.this, msg, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, OCRActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	public static boolean isWiFiConnected(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State wifi = conMan.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == NetworkInfo.State.CONNECTED
				|| wifi == NetworkInfo.State.CONNECTING)
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v(TAG, "onKeyDown");

		if (keyCode == KeyEvent.KEYCODE_CAMERA) {
			Log.v(TAG, "Pressed camera button to start ocr");
			mCamera.takePicture(mShutterCallback, mPictureCallback, mjpeg);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		Log.v(TAG, "onPause");
		if (mInPreview) {
			mCamera.stopPreview();

			mCamera.release();
			mCamera = null;
			mInPreview = false;
		}
		super.onPause();
	}

	@Override
	public void taskCompletionResult(JSONObject result) {
		List<String> kentekens = new ArrayList<String>();
		try {
			kentekens = getAllKentekensFromJSON(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		showResult(kentekens);
	}

	public List<String> getAllKentekensFromJSON(JSONObject json)
			throws JSONException {
		List<String> kentekens = new ArrayList<String>();
		int jobstatus = Integer.parseInt(json.get("job_status").toString());
		if (jobstatus == OCRServiceCaller.READY) {
			JSONObject plates = json.getJSONObject("plates");
			JSONArray plateArray = plates.getJSONArray("results");
			for (int i = 0; i < plateArray.length(); i++) {
				String kenteken = plateArray.getJSONObject(i).get("plate")
						.toString();
				if (kenteken != null && kenteken.length() == KENTEKEN_SIZE) {
					kentekens.add(kenteken);
				}
			}
		}
		return kentekens;
	}
}