package com.infosupport.queue;

import java.io.File;

public class QueueItem {

	private String description;
	private String location;
	private File image;
	private String id;
	
	public QueueItem() {
		this.description = null;
		this.location = null;
		this.image = null;
		this.id = null;
	}
	
	public QueueItem(String description, String location, File image, String id) {
		this.description = description;
		this.location = location;
		this.image = image;
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public File getImage() {
		return image;
	}
	
	public void setImage(File image) {
		this.image = image;
	}
	
	public String getRandomNr() {
		return id;
	}
	
	public void setRandomNr(String id) {
		this.id = id;
	}
	
}
