package com.infosupport.activity;

import java.util.List;

import org.json.JSONObject;

/**
 * The delegation interface is called after a service call is done. This is done
 * to communicate with the main thread.
 * 
 * @author Mike
 * @version 2.0
 */
public interface TaskDelegate {

	/**
	 * This method needs to be called when an async task is done that returns a
	 * single JSONObject.
	 * 
	 * @param result
	 *            The JSONObject that needs to be passed through with all the
	 *            data about a drivers license
	 */
	public void taskCompletionResult(JSONObject result);

	/**
	 * This method needs to be called when an async task is done that returns
	 * multiple JSONObjects.
	 * 
	 * @param result
	 *            The JSONObject list that needs to be passed through with all
	 *            the data about a drivers license
	 */
	public void taskCompletionResult(List<JSONObject> result);
}
