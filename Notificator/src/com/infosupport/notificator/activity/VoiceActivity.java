package com.infosupport.notificator.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;


public class VoiceActivity extends Activity {
	private static final int SPEECH_REQUEST = 0;
	private static final String TAG = "VoiceActivity";

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart()");
		displaySpeechRecognizer();
		super.onStart();
	}
	
	private void displaySpeechRecognizer() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	        Intent data) {
	    if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
	        List<String> results = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);
	        String spokenText = results.get(0);
	        OutputActivity.setDescription(spokenText);
	        Log.i(TAG, spokenText);
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	    finish();
	}

}
