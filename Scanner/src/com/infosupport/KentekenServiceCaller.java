package com.infosupport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Mike The ServiceCaller does a call to the rdw api to get data about a
 *         drivers licence.
 */
public class KentekenServiceCaller extends AsyncTask<String, JSONObject, JSONObject> {

	private static final String TAG = "KentekenServiceCaller";
	private static final String BASE_URL = "http://rdw.almere.pilod.nl/kentekens/";
	
	private String url = BASE_URL;
	private String json;
	private ArrayList<String> kentekens;
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
	public KentekenServiceCaller(ArrayList<String> kenteken, TaskDelegate delegate) {
		kentekens = kenteken;
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected JSONObject doInBackground(String... params) {
		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		
		for (String kenteken : kentekens) {
			JSONObject obj = null;
			try {
				URL url = new URL(this.url + kenteken);
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
			if (obj != null) {
				jsonObjects.add(obj);
			}
		}
		
		for (JSONObject object : jsonObjects) {
			try {
				String verzekerd = object.getJSONObject("resource").getString(
						"WAMverzekerdgeregistreerd");
				String apk = object.getJSONObject("resource").getString(
						"VervaldatumAPK");
				Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
						.parse(apk);
				
					return object;
				
			} catch (JSONException e) {
				Log.e(TAG, "Unable to recieve data from json");
			} catch (ParseException e) {
				Log.e(TAG, "Unable to parse date");
			}
		}

		return null;
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
