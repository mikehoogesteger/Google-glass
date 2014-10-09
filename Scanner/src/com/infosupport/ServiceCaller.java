package com.infosupport;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ServiceCaller extends AsyncTask<Void, Void, Void> {

	private static final String TAG = "ServiceCaller";
	private static final String BASE_URL = "http://rdw.almere.pilod.nl/kentekens/";
	private String url = BASE_URL;
	private String json;

	public ServiceCaller(String kenteken) {
		url += kenteken;
	}

	@Override
	protected Void doInBackground(Void... params) {
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

			JSONObject obj;
			try {
				obj = new JSONObject(json);
				String kenteken = obj.getJSONObject("resource").getString(
						"Kenteken");
				String verzekerd = obj.getJSONObject("resource").getString(
						"WAMverzekerdgeregistreerd");
				String apk = obj.getJSONObject("resource").getString(
						"VervaldatumAPK");
				System.out.println(kenteken);
				System.out.println(verzekerd);
				System.out.println(apk);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(json);
		} catch (Exception e) {
			Log.e(TAG, "JSON error");
		}
		return null;
	}

	public String getJSON() {
		// JSONObject obj;
		// try {
		// obj = new JSONObject(json);
		//
		// String cilinders = obj.getJSONObject("resource").getString(
		// "Aantalcilinders");
		//
		// System.out.println(cilinders);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return json;
	}
}
