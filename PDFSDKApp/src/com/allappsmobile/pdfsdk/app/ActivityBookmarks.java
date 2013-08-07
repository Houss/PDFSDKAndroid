package com.allappsmobile.pdfsdk.app;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.view.Menu;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityBookmarks extends SherlockFragmentActivity
{

	int getPageCount;
	String[] getPagesArray; 
	
	ImageButton btnAddBookmark;
	
	private final static int DIALOG_BOOKMARK = 1;
	
	DBAdapter data;
	ListView listBookmarks; 
	int currentPage;

	String path,pdf; 
	boolean isTwo = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf_bookmarks);
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		data = new DBAdapter(this);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{	   	    		   	    	  	   	 
			path 	= extras.getString("path");
			pdf = extras.getString("pdf");
			currentPage = extras.getInt("currentPage");
			isTwo = extras.getBoolean("isTwo");
			getPageCount = extras.getInt("pageCount");
		} 
		
		String[] split = pdf.split(".pdf");
		//getSupportActionBar().setTitle(split[0].toString());
		String setPagesCount
		
		;
		if(getPageCount==1)
		{
			setPagesCount = "1 Page";
		}
		else
		{
			setPagesCount = getPageCount+" Pages";
		}
		getSupportActionBar().setSubtitle(split[0].toString()+"; "+setPagesCount);
		
	    btnAddBookmark = (ImageButton)findViewById(R.id.btnDelete);
	    listBookmarks = (ListView)findViewById(R.id.listBookmarks);
		
		btnAddBookmark.setOnClickListener(new View.OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				 showDialog(DIALOG_BOOKMARK);
			}
		});
		
		loadBookmarks();

	}
	
    int savePage;
    String saveText;
	int pageA,pageB;
	
	@Override
    protected Dialog onCreateDialog(int id) 
    {
		switch (id)
    	{
    		case DIALOG_BOOKMARK:
    		LayoutInflater factory = LayoutInflater.from(this);
			final View viewAddBookmark = factory.inflate(R.layout.layoud_add_bookmark, null);
			final RadioGroup radioGroup = (RadioGroup)viewAddBookmark.findViewById(R.id.radioGroup);
			final EditText editBookmark  = (EditText)viewAddBookmark.findViewById(R.id.editBookmark);
			final RadioButton radio0  = (RadioButton)viewAddBookmark.findViewById(R.id.radio0);
			final RadioButton radio1  = (RadioButton)viewAddBookmark.findViewById(R.id.radio1);
			
			final int setPageA,setPageB;
			
			if(isTwo)
			{
				if(currentPage<getPageCount)
				{
					setPageA = currentPage;
					setPageB = (currentPage+1);
					
					radio0.setText("Page "+setPageA);
					radio1.setText("Page "+setPageB); 
					radio1.setVisibility(View.VISIBLE);
				}
				else
				{
					setPageA = currentPage;
					setPageB = 0;
					radio0.setText("Page "+setPageA);
					radio1.setVisibility(View.GONE);
				}
			}
			else
			{
				setPageA = currentPage;
				setPageB = 0;
				radio0.setText("Page "+setPageA);
				radio1.setVisibility(View.GONE);
			}
			

			


			return new AlertDialog.Builder(this)
			.setTitle("Add bookmark")
			.setView(viewAddBookmark)
			.setPositiveButton("Add", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					if(editBookmark.length()==0)
					{
			    		Toast.makeText(ActivityBookmarks.this, "Capture bookmark", Toast.LENGTH_SHORT).show();
			    		return;
					}
					else
					{
						saveText = editBookmark.getText().toString();
					}
					
					if(radio1.isShown())
					{
						if(radio0.isChecked())
						{
							savePage = setPageA;
						}
						if(radio1.isChecked())
						{
							savePage = setPageB;
						}
					}
					else
					{
						savePage = setPageA;
					}

					Log.i("bookmark","savePage = " + savePage);
					Log.i("bookmark","saveText = " + saveText);
					
					insertBookmark(saveText,savePage);
	            	editBookmark.setText("");
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			{
                public void onClick(DialogInterface dialog, int whichButton) 
                {
                	editBookmark.setText("");
                }
            })
            .create();
    	}
		return null;
    }
	
	public void insertBookmark(String text,int page)
	{
		try
		{
			String setPage = String.valueOf(page);
			String new_id = nextID();
			
			data.open();
			long id;
			id = data.insertBookmark(new_id, text, setPage, pdf, String.valueOf(getPageCount));
            if(id == -1)
            {
            	Toast.makeText(ActivityBookmarks.this, "Error", Toast.LENGTH_SHORT).show();
            }
            else
            {  
            	Toast.makeText(ActivityBookmarks.this, "Saved", Toast.LENGTH_SHORT).show();
            	loadBookmarks();
            }
			
		}
		catch(Exception ex)
		{
    		Toast.makeText(ActivityBookmarks.this, "Error " + ex.getMessage(), Toast.LENGTH_SHORT).show();
    		return;
		}
		finally
		{
			data.close();
		}
	}
	
	int count;
	String[] idregs,getText,getPage;
	public void loadBookmarks()
	{
		Cursor c =null;
		try
		{
			this.count = 0;
			int index = 0;
			
			data.openToRead();
			c = data.getAllBookmarks(pdf, String.valueOf(getPageCount));
			
			if(c!=null)
			{
				this.count = c.getCount();
				idregs = new String[this.count];
				getText= new String[this.count];
				getPage= new String[this.count];
				
				if(c.moveToFirst())
				{
					do
					{
						Log.i("query",c.getString(1)+";"+c.getString(2)+";"+c.getString(3)+";"+c.getString(4)+";"+c.getString(5));
						idregs[index] = c.getString(1);
						getText[index] = c.getString(2);
						getPage[index] = c.getString(3);
						
						index++;
					}
					while(c.moveToNext());
				}
				
				BookmarkAdapter = new BookmarkAdapter();
				listBookmarks.setAdapter(BookmarkAdapter);
				
			}
			else
			{
				// No bookmarks
			}
			
		}
		catch(SQLiteException ex1) {}
		catch(Exception ex){}
		finally
        { 
			data.close();
        }
	}
	
    Context context;
    BookmarkAdapter BookmarkAdapter;
	public class BookmarkAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		
		public BookmarkAdapter()
		{
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		

        public int getCount() 
        {
            return count;
        }

        public Object getItem(int position) 
        {
            return position;
        }

        public long getItemId(int position) 
        {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	context = getApplicationContext();
            ViewHolder holder;
            if (convertView == null) 
            {
            	holder = new ViewHolder();
            	convertView = mInflater.inflate(R.layout.layout_list_bookmarks, null); 
            	holder.btnDelete = (ImageButton)convertView.findViewById(R.id.btnDelete);
            	holder.lblText = (TextView)convertView.findViewById(R.id.lblBookmark);
            	holder.lblPage = (TextView)convertView.findViewById(R.id.lblPage);
            	holder.linearBookmark = (LinearLayout)convertView.findViewById(R.id.linearBookmark);
            	convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            
            //holder.linearBookmark.setOnCreateContextMenuListener(PDFBookmarks.this);
        
            holder.btnDelete.setId(position);
            holder.lblText.setId(position);
            holder.lblPage.setId(position);
            holder.linearBookmark.setId(position);
            
       //     holder.linearBookmark.setBackgroundResource(R.drawable.btn_default_holo_light);
            
            holder.linearBookmark.setOnClickListener(new View.OnClickListener() 
            {
            	@Override
				public void onClick(View v) 
				{
            		LinearLayout layout = (LinearLayout)v;
            		final int id = layout.getId();
            		String returnPage = getPage[id];
            		final int valuePage = Integer.parseInt(returnPage);
            		
            		Log.i("linearBookmark setOnClickListener","valuePage = " + valuePage);
            		
            		AlertDialog.Builder alertbox1 = new AlertDialog.Builder(ActivityBookmarks.this);
            		alertbox1.setCancelable(false);
            		alertbox1.setMessage("Go to page "+valuePage+" ?");
            		alertbox1.setPositiveButton("Go!", new DialogInterface.OnClickListener()
            		{
            			public void onClick(DialogInterface arg0, int arg1)
                        {
                			Intent intentDirectory=new Intent(ActivityBookmarks.this,PDFViewer.class);
                			intentDirectory.putExtra("path",path);
                			intentDirectory.putExtra("pdf",pdf);
                			intentDirectory.putExtra("currentPage",valuePage);
                			intentDirectory.putExtra("isTwo",isTwo);
                		    startActivity(intentDirectory);
                        }
                    });    
            		alertbox1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
                    {                    
                        public void onClick(DialogInterface arg0, int arg1) 
                        {
                          //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                        }
                    });
            		alertbox1.show();
            		
				}
            });
            
            holder.btnDelete.setOnClickListener(new View.OnClickListener() 
            {
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					ImageButton but =(ImageButton)v;
					final int id = but.getId();
					
					AlertDialog.Builder alertbox2 = new AlertDialog.Builder(ActivityBookmarks.this);
                    alertbox2.setCancelable(false);
                    alertbox2.setMessage("Delete this bookmark?");
                    alertbox2.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                    {
                    	public void onClick(DialogInterface arg0, int arg1)
                        { 
                    		int cnt = 0;
                            String aux = idregs[id];
                            
                            data.open();
                            boolean delete = data.deleteBookmark(pdf, String.valueOf(getPageCount),aux);
                            if(!delete)
                            {
                            	Toast.makeText(ActivityBookmarks.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {  
                            	Toast.makeText(ActivityBookmarks.this, "Delete", Toast.LENGTH_SHORT).show();
                            	loadBookmarks();
                            }
                            data.close();
                        }
                    });    
                    alertbox2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
                    {                    
                        public void onClick(DialogInterface arg0, int arg1) 
                        {
                          //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertbox2.show();
				
				}
			});
            
            holder.lblText.setText(getText[position]);
            holder.lblPage.setText("Page "+getPage[position]);
            
            holder.id = position;
            return convertView;

        }
		
	}
	
	class ViewHolder
	{
		ImageButton btnDelete;
		TextView lblText,lblPage;
		LinearLayout linearBookmark;
		int id;
	}
	
	public String nextID()
	{
		Cursor c = null;
		try
		{
			data.openToRead();
			c = data.getNextIDBookmark(pdf, String.valueOf(getPageCount));
			if(c.getCount()>0)
			{
				if(c.moveToFirst())
				{
					String id_old = c.getString(0);
					int parse = Integer.parseInt(id_old);
					parse++;
					String new_id = String.valueOf(parse);
					Log.i("nextID","new_id = " + new_id);
					return new_id;
				}
				else
				{
					return "1";
				}
			}
			else
			{
				return "1";
			}
			
		}
		catch(Exception ex)
		{
			return null;
		}
		finally
		{
			data.close();
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
	{
		String getItem = item.getTitle().toString();
		if(getItem.equals("PDF SDK App"))
		{
			
			Intent intentDirectory=new Intent(ActivityBookmarks.this,PDFViewer.class);
			intentDirectory.putExtra("path",path);
			intentDirectory.putExtra("pdf",pdf);
			intentDirectory.putExtra("currentPage",currentPage);
			intentDirectory.putExtra("isTwo",isTwo);
		    startActivity(intentDirectory);
		}
		return true;
	}
}