package com.infosupport;

import java.util.List;

import org.json.JSONObject;

/**
 * @author Mike The delegation interface
 */
public interface TaskDelegate {

	/**
	 * This method needs to be called when an async task is done.
	 * 
	 * @param result
	 *            The JSONObject that needs to be passed through with all the
	 *            data about a drivers license
	 */
	public void taskCompletionResult(JSONObject result);

	public void taskCompletionResult(List<JSONObject> result);
}
