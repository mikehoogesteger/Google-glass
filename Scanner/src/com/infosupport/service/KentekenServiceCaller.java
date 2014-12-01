package com.infosupport.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.infosupport.activity.TaskDelegate;

/**
 * The KentekenServiceCaller does a call to the RDW api to get data about a
 * drivers licence.
 * 
 * @author Mike
 * @version 2.1
 */
public class KentekenServiceCaller extends
		AsyncTask<String, JSONObject, List<JSONObject>> {

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
	 * @param kentekens
	 *            is the drivers licence
	 * @param delegate
	 *            is the class that will be called when the task is done
	 */
	public KentekenServiceCaller(ArrayList<String> kentekens,
			TaskDelegate delegate) {
		this.kentekens = kentekens;
		this.delegate = delegate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<JSONObject> doInBackground(String... params) {
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

		if (jsonObjects.size() != 0) {
			System.out.println("Returning: " + jsonObjects.size());
			return jsonObjects;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(List<JSONObject> result) {
		if (result != null) {
			delegate.taskCompletionResult(result);
		}
	}
}
