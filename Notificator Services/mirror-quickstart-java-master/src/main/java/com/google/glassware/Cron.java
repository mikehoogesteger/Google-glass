package com.google.glassware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.mirror.model.Location;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;

public class Cron implements Runnable {

	public static Credential credential;

	private static boolean started = false;

	public static void message(String string) {
		System.out.println(string);
	}

	@Override
	public void run() {
		while (true) {
			if (started) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				callService();
			}
		}
	}

	private void callService() {
		URL url = null;
		try {
			url = new URL("http://localhost:8181/OCRService/queue");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}

		try {
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 203) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			} else if (conn.getResponseCode() == 200) {
				String description = getData("description");
				String location = getData("location");
				makeTimelineItem(description, location, new URL("http://localhost:8181/OCRService/queue/image").openStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		conn.disconnect();
	}

	public static boolean isStarted() {
		return started;
	}

	public static void setStarted(boolean started) {
		Cron.started = started;
	}
	
	public String getData(String type) {
		URL url = null;
		try {
			url = new URL("http://localhost:8181/OCRService/queue/" + type);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
		String result = "";
		String output = null;
		
		try {
			while ((output = br.readLine()) != null) {
				result += output;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		conn.disconnect();
 
		return result;
	}
	
	public void makeTimelineItem(String description, String location, InputStream image) {
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setText(description);
		timelineItem.setLocation(new Location().setAddress(location));
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
		
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		menuItemList.add(new MenuItem().setAction("REPLY"));
		menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
		menuItemList.add(new MenuItem().setAction("TOGGLE_PINNED"));
		menuItemList.add(new MenuItem().setAction("NAVIGATE"));
		menuItemList.add(new MenuItem().setAction("DELETE"));

		timelineItem.setMenuItems(menuItemList);
		
		String contentType = "image/jpeg";
		
		try {
			MirrorClient.insertTimelineItem(credential, timelineItem,
					contentType, image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
