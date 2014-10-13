package com.infosupport;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class Immersion3DActivity extends Activity {
//	private static final String TAG = "Immersion3DActivity";
	MyGLView view3D; // 3D

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView view = new GLSurfaceView(this);
		view.setRenderer(new TetrahedronRenderer(true));
		setContentView(view);
	}
}
