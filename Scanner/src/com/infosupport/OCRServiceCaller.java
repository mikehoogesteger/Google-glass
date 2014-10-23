package com.infosupport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class OCRServiceCaller extends AsyncTask<String, String, String> {
	
	private File image;
	private TaskDelegate delegate;
	
	public OCRServiceCaller(TaskDelegate delegate, File image) {
		this.delegate = delegate;
		this.image = image;
	}

	@Override
	protected String doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost("http://api.ocrapiservice.com/1.0/rest/ocr");

    	FileBody bin = new FileBody(image);
    	StringBody language = null;
		try {
			language = new StringBody("en");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	StringBody apikey = null;
		try {
			apikey = new StringBody("xFCzm3UYfS");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	MultipartEntity reqEntity = new MultipartEntity();
    	reqEntity.addPart("image", bin);
    	reqEntity.addPart("language", language);
    	reqEntity.addPart("apikey", apikey);
    	httppost.setEntity(reqEntity);

    	HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	HttpEntity resEntity = response.getEntity();  
    	final StringBuilder sb = new StringBuilder();
		if (resEntity != null) {
	    	InputStream stream = null;
			try {
				stream = resEntity.getContent();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	byte bytes[] = new byte[4096];
	    	int numBytes;
	    	try {
				while ((numBytes=stream.read(bytes))!=-1) {
					if (numBytes!=0) {
						sb.append(new String(bytes, 0, numBytes));
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	@Override
	protected void onPostExecute(String result) {
		delegate.taskCompletionResult(result);
		super.onPostExecute(result);
	}

	
}
