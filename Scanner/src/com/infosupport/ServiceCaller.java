package com.infosupport;

import java.io.BufferedInputStream;
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
//				String kenteken = obj.getJSONObject("resource").getString(
//						"Kenteken");
//				String verzekerd = obj.getJSONObject("resource").getString(
//						"WAMverzekerdgeregistreerd");
//				String apk = obj.getJSONObject("resource").getString(
//						"VervaldatumAPK");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(json);
		} catch (Exception e) {
			Log.e(TAG, "JSON error");
		}
		
		return obj;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		delegate.taskCompletionResult(result);
		super.onPostExecute(result);
	}
}
