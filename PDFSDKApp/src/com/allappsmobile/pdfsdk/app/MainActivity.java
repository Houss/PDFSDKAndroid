package com.allappsmobile.pdfsdk.app;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView view = new GLSurfaceView(this);
   		view.setRenderer(new MyRenderer(this));
   		setContentView(view);
 
	}
	public void intent(){
		Intent intentDirectory=new Intent(MainActivity.this,ActivityDirectory.class);
	    startActivity(intentDirectory);
	    finish();
	}
}
