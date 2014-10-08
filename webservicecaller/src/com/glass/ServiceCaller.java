package com.glass;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

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
			String json = new String(b);
			System.out.println(json);

		} catch (Exception e) {
		}
		return null;
	}
}
