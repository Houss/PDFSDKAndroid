package com.allappsmobile.pdfsdk.app;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;


import android.app.Activity; 
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

public class ActivityThumbs extends SherlockActivity 
{
	ThumbsArray thumbs;
	String path,pdf;
	int currentPage = 1;
	boolean isTwo = false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thumbs); 
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DDDDDD")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{	   	    		   	    	  	   	 
			path 	= extras.getString("path");
			pdf = extras.getString("pdf");
			currentPage = extras.getInt("currentPage");
			isTwo = extras.getBoolean("isTwo");
		}
		
		thumbs = new ThumbsArray();   
		
		ArrayList<ItemThumbnail> image_details = thumbs.getListData(path+pdf);
		
		final ListView lv1 = (ListView) findViewById(R.id.custom_list); 
		lv1.setAdapter(new ThumbnailAdapter(this, image_details));
		lv1.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) 
			{
				Object o = lv1.getItemAtPosition(position);
				//Toast.makeText(ActivityThumbs.this, "Selected :" + " " + position,Toast.LENGTH_LONG).show();
				int pos = position+1;
				
    			Intent intentDirectory=new Intent(ActivityThumbs.this,PDFViewer.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",pos);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
			}
		});
	}
	
	@Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
	{
		String getItem = item.getTitle().toString();
		if(getItem.equals("PDF SDK App"))
		{
		    ActivityThumbs.this.finish();
			Intent intentDirectory=new Intent(ActivityThumbs.this,PDFViewer.class);
			intentDirectory.putExtra("path",path);
			intentDirectory.putExtra("pdf",pdf);
			intentDirectory.putExtra("currentPage",currentPage);
			intentDirectory.putExtra("isTwo",isTwo);
		    startActivity(intentDirectory);
		}
		return true;
	}
}