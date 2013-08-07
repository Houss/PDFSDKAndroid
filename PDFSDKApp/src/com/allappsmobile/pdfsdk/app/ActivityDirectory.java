package com.allappsmobile.pdfsdk.app;



import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;


import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;


public class ActivityDirectory extends SherlockActivity 
{

	ListView listView;
	Button btnBack;
	
	private ArrayList<ItemDirectory> list = new ArrayList<ItemDirectory>();
	private AdapterDirectory adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_directory);  
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DDDDDD")));
		
		listView = (ListView)findViewById(R.id.listView);
		btnBack  = (Button)findViewById(R.id.btnBack); 
		
		String path = Environment.getExternalStorageDirectory()+"/";
		
		getDirectory(path);
		
		btnBack.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Log.i("getDirectory","btnBack");
				goBack();
			}
		});
	}
	
	ArrayList<String> listFolders = new ArrayList<String>();
	ArrayList<String> listFiles = new ArrayList<String>();
	String globalPath = "";
	
	public void getDirectory(String path)
	{
		//String setPath = path;
		globalPath = path;
		Log.i("getDirectory","globalPath = " + globalPath);
		
		String[] splitPath = path.split("/");
		int count = splitPath.length;
		Log.i("getDirectory","globalPath count = " + count);
		if(count==3)
		{
			// Root
			btnBack.setVisibility(View.GONE);
		}
		else
		{
			btnBack.setVisibility(View.VISIBLE);
		}

		File directory = new File(globalPath);
		File[] listDirectory = directory.listFiles();
		
		
		// Directory
		for(int i=0; i<listDirectory.length;i++)
		{
			File getFolder = listDirectory[i];
			if(getFolder.isDirectory())
			{
				listFolders.add(getFolder.getName());
			}
		}

		Collections.sort(listFolders,String.CASE_INSENSITIVE_ORDER);
		
		for(int i = 0; i<listFolders.size();i++)
		{
			list.add(new ItemDirectory(R.drawable.ic_folder,listFolders.get(i)));
		}
		
		// Files
		for(int i=0; i<listDirectory.length;i++)
		{
			File getFile = listDirectory[i]; 
			if(getFile.isFile())
			{
				if(getFile.getName().endsWith(".pdf"))  
				{
					listFiles.add(getFile.getName());   
				}
			}
		}

		Collections.sort(listFiles,String.CASE_INSENSITIVE_ORDER);
		
		for(int i = 0; i<listFiles.size();i++)
		{
			list.add(new ItemDirectory(R.drawable.ic_file,listFiles.get(i)));
		}

		adapter = new AdapterDirectory(ActivityDirectory.this,R.layout.row_list_item,list);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick( AdapterView<?> listView, View view,int pos, long id )
            {
				ItemDirectory itemList = list.get(pos);
				String getItemList = itemList.getTitle();
				
				if(getItemList.endsWith(".pdf"))
				{
					// Launch viewer
					Log.i("getDirectory","Launch viewer");
					if(!globalPath.endsWith("/"))
					{
						globalPath = globalPath+"/";
					}
					Intent intentDirectory=new Intent(ActivityDirectory.this,PDFViewer.class);
					//Intent intentDirectory=new Intent(ActivityDirectory.this,PDFPageCurl.class);
					intentDirectory.putExtra("path",globalPath);
					intentDirectory.putExtra("pdf",getItemList);
					intentDirectory.putExtra("currentPage",1);
				    startActivity(intentDirectory);
				}
				else
				{
					// Open folder
					Log.i("getDirectory","Open folder after onItemClick globalPath = " + globalPath);
					Log.i("getDirectory","Open folder onItemClick " + globalPath+getItemList);
					listFolders.clear();
					listFiles.clear();
					list.clear();
					if(!globalPath.endsWith("/"))
					{
						globalPath = globalPath+"/";
					}
					getDirectory(globalPath+getItemList);
					
				}
				
            }
		}
		);
	}
	
	public void goBack()
	{
		listFolders.clear(); 
		listFiles.clear();
		list.clear();
		String[] splitGlobalPath = globalPath.split("/");
		int count = splitGlobalPath.length;
		globalPath = "";
		for(int i= 0;i<count-1;i++)
		{
			globalPath += splitGlobalPath[i].toString()+"/";
		}
		
		if(!globalPath.startsWith("/"))
		{
			globalPath = "/"+globalPath;
		}
		
		Log.i("goBack","globalPath = "+ globalPath);
		getDirectory(globalPath);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_directory, menu);
		return true;
	}

}
