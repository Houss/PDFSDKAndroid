package com.allappsmobile.pdfsdk.app;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class MyRenderer implements Renderer{
	Context context;
	MainActivity act;

	public MyRenderer(MainActivity mainActivity) {
		act = mainActivity;
	}

	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		int[] results = new int[4];
	    gl.glGetIntegerv(GL10.GL_MAX_VIEWPORT_DIMS, results, 0);
	    for (int i = 0; i < results.length; i++) {
	        Log.e("", "results[" + i + "]: " + results[i]);
	        Global.GLmaxSize=results[0];
	    }
	    act.intent();

	}
 

}
