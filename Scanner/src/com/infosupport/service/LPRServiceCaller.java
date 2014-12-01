package com.infosupport.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONException;
import org.json.JSONObject;

import com.infosupport.activity.TaskDelegate;

import android.os.AsyncTask;
import android.util.Log;

/**
 * The LPRServiceCaller does a call to the OpenANPR api to find the licenses in
 * a picture.
 * 
 * @author Mike
 * @version 2.1
 */
public class LPRServiceCaller extends AsyncTask<String, JSONObject, JSONObject> {

	private static final String TAG = "OCRServiceCaller";
	private static final String UPLOADURL = "http://demo.openalpr.com:8010/upload";
	private static final String GETURL = "http://demo.openalpr.com:8010/status?nonce=123456789";

	public static final int READY = 3;

	private File image;
	private TaskDelegate delegate;

	public LPRServiceCaller(TaskDelegate delegate, File image) {
		this.delegate = delegate;
		this.image = image;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		postData(UPLOADURL, image);
		int job_status = 1;
		JSONObject json = null;
		while (job_status < READY) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			json = doGet(GETURL);
			try {
				job_status = Integer
						.parseInt(json.get("job_status").toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		delegate.taskCompletionResult(result);
	}

	public static void postData(String urlString, File file) {
		try {
			PostMethod postMessage = new PostMethod(urlString);
			Part[] parts = { new FilePart("lpimage", file),
					new StringPart("country", "eu"),
					new StringPart("nonce", "123456789") };
			postMessage.setRequestEntity(new MultipartRequestEntity(parts,
					postMessage.getParams()));
			HttpClient client = new HttpClient();
			client.executeMethod(postMessage);
		} catch (IOException e) {
			Log.w(TAG,
					"IOException, waarschijnlijk geen internet connectie aanwezig...");
		}
	}

	public static JSONObject doGet(String urlString) {
		String json = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				json = output;
				System.out.println(json + "from doGet");
			}
			conn.disconnect();
		} catch (IOException e) {
			Log.w(TAG,
					"IOException, waarschijnlijk geen internet connectie aanwezig...");
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			Log.e(TAG, "Kon geen JsonObject maken van het response");
		}
		return jsonObject;
	}

}
