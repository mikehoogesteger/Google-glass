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

public class ServiceCaller extends AsyncTask<String, JSONObject, JSONObject> {

	private static final String TAG = "ServiceCaller";
	private static final String BASE_URL = "http://rdw.almere.pilod.nl/kentekens/";
	private String url = BASE_URL;
	private String json;
	private TaskDelegate delegate;

	public ServiceCaller(String kenteken, TaskDelegate delegate) {
		url += kenteken;
		this.delegate = delegate;
	}

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
				Log.e(TAG, "JSON error");
				e.printStackTrace();
			}
		} catch (IOException e) {
			Log.e(TAG, "Connection eror");
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		delegate.taskCompletionResult(result);
		super.onPostExecute(result);
	}
}
