package com.infosupport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Mike The ServiceCaller does a call to the rdw api to get data about a
 *         drivers licence.
 */
public class ServiceCaller extends AsyncTask<String, JSONObject, JSONObject> {

	private static final String TAG = "ServiceCaller";
	private static final String BASE_URL = "http://rdw.almere.pilod.nl/kentekens/";
	private String url = BASE_URL;
	private String json;
	private TaskDelegate delegate;

	/**
	 * This constructor sets the kenteken and gives a delegation that will be
	 * called when the asyncronical call is done.
	 * 
	 * @param kenteken
	 *            is the drivers licence
	 * @param delegate
	 *            is the class that will be called when the task is done
	 */
	public ServiceCaller(String kenteken, TaskDelegate delegate) {
		url += kenteken;
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject obj = null;

		try {
			URL url = new URL(this.url);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = in.read()) != -1) {
				b.append((char) ch);
			}
			json = new String(b);

			try {
				obj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e(TAG, "JSON error", e);
			}
		} catch (IOException e) {
			Log.e(TAG, "Connection error / no internet", e);
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(JSONObject result) {
		delegate.taskCompletionResult(result);
		super.onPostExecute(result);
	}
}
