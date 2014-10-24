package com.infosupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class OCRServiceCaller extends AsyncTask<String, JSONObject, JSONObject> {
	
	private final String UPLOADURL = "http://demo.openalpr.com:8010/upload";
	private final String GETURL = "http://demo.openalpr.com:8010/status?nonce=123456789";
	
	private File image;
	private TaskDelegate delegate;
	
	public OCRServiceCaller(TaskDelegate delegate, File image) {
		this.delegate = delegate;
		this.image = image;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		postData(UPLOADURL, image);
		int job_status = 1;
		JSONObject json = null;
		while (job_status < 3) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			json = doGet(GETURL);
			try {
				job_status = Integer.parseInt(json.get("job_status").toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		System.out.println(result.toString() + "FROM POSTEXECUTE");
		delegate.taskCompletionResult(result);
		super.onPostExecute(result);
	}
	
	public static void postData(String urlString, File file) {
		int status = 0;
	    try {
	        PostMethod postMessage = new PostMethod(urlString);
	        Part[] parts = {
	        		new FilePart("lpimage", file),
	                new StringPart("country", "eu"),
	                new StringPart("nonce", "123456789")
	        };
	        postMessage.setRequestEntity(new MultipartRequestEntity(parts, postMessage.getParams()));
	        HttpClient client = new HttpClient();

	        status = client.executeMethod(postMessage);
	    } catch (HttpException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        //  TODO Auto-generated catch block
	        e.printStackTrace();
	    }  
	    System.out.println(status);
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	
}
