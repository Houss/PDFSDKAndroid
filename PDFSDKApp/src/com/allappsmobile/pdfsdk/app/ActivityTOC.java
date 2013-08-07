package com.allappsmobile.pdfsdk.app;

import java.util.ArrayList;

import com.allappsmobile.pdf_sdk.port.DocMgr;
import com.allappsmobile.pdf_sdk.port.DocOutline;
import com.sun.pdfview.PDFFile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityTOC extends Picker 
{
	String path,pdf;
	
    private DocMgr docMgr;
	private State curState;
	   
    public static final String KEY_PAGE_NO = "PageNo";
    public static final String KEY_OUTLINE = "Outline";
    
    private DocOutline mOutline;
    
    private ArrayList<Integer> mTypes;
    private Bitmap mBranchIcon, mLeafIcon;
    private String mTitle;
	int currentPage = 1;
	boolean isTwo = false;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DDDDDD")));
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{	   	    		   	    	  	   	 
			path 	= extras.getString("path");
			pdf = extras.getString("pdf");
			currentPage = extras.getInt("currentPage");
			isTwo = extras.getBoolean("isTwo");
		}
		
		mTypes = new ArrayList<Integer>();
		mBranchIcon = BitmapFactory.decodeResource(getResources(), R.drawable.branch);
        mLeafIcon 	= BitmapFactory.decodeResource(getResources(), R.drawable.leaf);
		
        setTitle("Picker");
        setHeadIcon(R.drawable.back);
        
        String full_path=path+pdf;
        
        Log.i("TOCActivity","full_path = " + full_path);
        
        docMgr = new DocMgr();
        docMgr.openDoc(full_path);
        mOutline = docMgr.getOutline();
		mTitle = getString(R.string.outline);

		if(mOutline==null) 
		{
			
		}
		else
		{
			mOutline.setRootName("Outline");
	        exploreCurOutline();
		}


        
	}
	
    private void exploreCurOutline() {
        mTitle = mOutline.getBranchName();
        setHeadText(mTitle);
        buildOutlineList();
        setListAdapter(new listAdapter(this, mItems, mIcons));
    }
    
    @Override
    public void onClick(View v) 
    {
    	if(mOutline!=null)
		{
	        mOutline.moveBackUp();
	        exploreCurOutline();
		}
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)  
    {
    	if(mOutline!=null)
		{
	        super.onListItemClick(l, v, position, id);
	        if (mOutline.getChildrenCount(position) > 0) {
	            mOutline.moveTo(position);
	            exploreCurOutline();
	        } else {
	            int pageNum = mOutline.getPageNo(position);
	            Toast.makeText(ActivityTOC.this, "Go to page " + pageNum,Toast.LENGTH_SHORT).show();
	           
    			Intent intentDirectory=new Intent(ActivityTOC.this,PDFViewer.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",pageNum);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
	        }
		}
    }
    
    public void buildOutlineList() {
        mItems.clear();
        mIcons.clear();
        mTypes.clear();

        mOutline.getChildren(mItems, mTypes);
        for (Integer type : mTypes) {
            if (type == 0) {
                mIcons.add(mBranchIcon);
            } else {
                mIcons.add(mLeafIcon);
            }
        }
    }
	
    private class State 
    {
        public void OpenDoc(String name) {}
        public StringBuffer getPageContent(int pNo) 
        {
            return null;
        }
        public DocOutline getOutline() {
            return null;
        }
        public int getNumPages() {
            return 0;
        }
        public int getPageNo() {
            return 0;
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}