package com.allappsmobile.pdfsdk.app;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.polites.android.GestureImageView; 
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.decrypt.PDFPassword;
import com.sun.pdfview.font.PDFFont;


public class PDFViewer extends SherlockActivity 
{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryFrame> categoryList = new ArrayList<CategoryFrame>();

	String path,pdf; 

	PDFFile file;
	PDFPage filePage;
	
	GestureImageView image;
	Bitmap imagePage;
	
	int currentPage = 1;
	int pageCount;
	
	boolean isTwo = false;
	
	private final static int DIALOG_PAGENUM = 1;
	private final static int DIALOG_BOOKMARK = 2;
	
	DBAdapter data;
	
	private String pdffilename;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf_viewer);  
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DDDDDD")));
		
		data = new DBAdapter(this);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{	   	    		   	    	  	   	 
			path 	= extras.getString("path");
			pdf = extras.getString("pdf");
			currentPage = extras.getInt("currentPage");
			isTwo = extras.getBoolean("isTwo");
		} 
	
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer); 
        image = (GestureImageView) findViewById(R.id.image); 
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        int colorLabel = Color.parseColor("#9B9B9B");
        
        int icon01 = R.drawable.ic_one_page;
        int icon02 = R.drawable.ic_two_pages;
        int icon03 = R.drawable.ic_go_page;
        int icon04 = R.drawable.ic_thumbs;
        int icon05 = R.drawable.ic_toc;
        int icon06 = R.drawable.ic_favorites;
        int icon07 = R.drawable.ic_folder;
        
        CategoryFrame category01 = new CategoryFrame(colorLabel, icon01, "One Page");
        CategoryFrame category02 = new CategoryFrame(colorLabel, icon02, "Two Pages");
        CategoryFrame category03 = new CategoryFrame(colorLabel, icon03, "Go to page");
        CategoryFrame category04 = new CategoryFrame(colorLabel, icon04, "Thumbnails");
        CategoryFrame category05 = new CategoryFrame(colorLabel, icon05, "Table of Content");
        CategoryFrame category06 = new CategoryFrame(colorLabel, icon06, "Bookmarks");
        CategoryFrame category07 = new CategoryFrame(colorLabel, icon07, "Directory");
		
        categoryList.add(category01);
        categoryList.add(category02);
        categoryList.add(category03);
        categoryList.add(category04);
        categoryList.add(category05);
        categoryList.add(category06);
        categoryList.add(category07);
        
        categoryAdapter = new CategoryAdapter(PDFViewer.this,R.layout.drawer_list_item,categoryList);
        mDrawerList.setAdapter(categoryAdapter); 

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
     // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	//getSupportActionBar().setTitle(mTitle);
            	supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	//getSupportActionBar().setTitle(mDrawerTitle);
            	supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        if (savedInstanceState == null) {
            selectItem(0);  
        } 
        
        image.setImageDrawable(PDFViewer.this.getResources().getDrawable(R.drawable.ic_page_null));
        
        setContent(null);
        
       // file = getDocument(path+pdf);
        if(file==null)
        {
        	return;
        }
        pageCount = file.getNumPages();
        showPage(currentPage,isTwo);
        
	}
	
	public PDFFile getDocument(String getPath)
	{
		try
		{
			initContrils(); 
			File file = new File(getPath);
    		RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
            
            return new PDFFile(byteBuffer);
		}
		catch(Exception ex)
		{
			Log.e("PDFDocument","Error " + ex.getMessage());
			return null;
		}
	}
	
	static void initContrils() 
    {
        PDFImage.sShowImages = true;
        PDFPaint.s_doAntiAlias = true;
        PDFFont.sUseFontSubstitution= false;
        HardReference.sKeepCaches= true;
    }
	
	// Navigation Drawer ----------------------------------------------------------------------------
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener 
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            
            //	1	One Page
            if(position==0)	
            {
            	isTwo = false;
    			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
            }
            
            //	2	Two Pages
            if(position==1)	
            {
            	isTwo = true;
    			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
            }
            
            //	3	Go to page
            if(position==2)	
            {
            	showDialog(DIALOG_PAGENUM); 
            }
            
            //	4	Thumbnails
            if(position==3)	
            {
    			Intent intentDirectory=new Intent(PDFViewer.this,ActivityThumbs.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
            }
            
            //	5	Table of Content
            if(position==4)	
            {
    			Intent intentDirectory=new Intent(PDFViewer.this,ActivityTOC.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
            }
            
            //	6	Bookmarks
            if(position==5)	
            {
    			Intent intentDirectory=new Intent(PDFViewer.this,ActivityBookmarks.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage);
    			intentDirectory.putExtra("isTwo",isTwo);
    			intentDirectory.putExtra("pageCount",pageCount);
    		    startActivity(intentDirectory);
            }
            
            //	7	Directory
            if(position==6)	
            {
    			Intent intentDirectory=new Intent(PDFViewer.this,ActivityDirectory.class);
    		    startActivity(intentDirectory);
            }
            
        }
    }
    
    private void selectItem(int position)
    {
    	// update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(getPagesArray[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    @Override
    public void setTitle(CharSequence title) 
    {
       // mTitle = title;
        //getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
	// PDF ----------------------------------------------------------------------------
    @Override
	public boolean onCreateOptionsMenu(Menu menu) 
    {
    	menu.add(0,1,1,"Previous")
    	.setIcon(R.drawable.ic_previous)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 
    	
    	menu.add(0,2,2,"Next")
    	.setIcon(R.drawable.ic_next)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 
    	
    	menu.add(0,3,3,"Add Bookmark")
    	.setIcon(R.drawable.ic_add)
    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
    	if(item.getTitle().equals("Add Bookmark"))
    	{
    		showDialog(DIALOG_BOOKMARK);
    	}
    	//currentPage
    	if(item.getTitle().equals("Previous"))
		{
    		//bitmapGlobal.recycle();
        	if(isTwo)
        	{
//        		if((currentPage>=1)&&(currentPage<=pageCount))
//        		{
        			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
        			intentDirectory.putExtra("path",path);
        			intentDirectory.putExtra("pdf",pdf);
        			intentDirectory.putExtra("currentPage",currentPage-2);
        			intentDirectory.putExtra("isTwo",isTwo);
        		    startActivity(intentDirectory);
//        		}

        	}
        	else
        	{
//        		if((currentPage>=1)&&(currentPage<=pageCount))
//        		{
        			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
        			intentDirectory.putExtra("path",path);
        			intentDirectory.putExtra("pdf",pdf);
        			intentDirectory.putExtra("currentPage",currentPage-1);
        			intentDirectory.putExtra("isTwo",isTwo);
        		    startActivity(intentDirectory);
//        		}
        	}
        	
		}
    	
    	if(item.getTitle().equals("Next"))
		{
    		//bitmapGlobal.recycle();
        	if(isTwo)
        	{
//        		if((currentPage>=1)&&(currentPage<=pageCount))
//        		{
        			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
        			intentDirectory.putExtra("path",path);
        			intentDirectory.putExtra("pdf",pdf);
        			intentDirectory.putExtra("currentPage",currentPage+2);
        			intentDirectory.putExtra("isTwo",isTwo);
        		    startActivity(intentDirectory);
//        		}

        	}
        	else
        	{
//        		if((currentPage>=1)&&(currentPage<=pageCount))
//        		{
        			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
        			intentDirectory.putExtra("path",path);
        			intentDirectory.putExtra("pdf",pdf);
        			intentDirectory.putExtra("currentPage",currentPage+1);
        			intentDirectory.putExtra("isTwo",isTwo);
        		    startActivity(intentDirectory);
//        		}

        	}
		}
    	
		return super.onOptionsItemSelected(item);
	}
    
    public void showPage(int page,boolean getIsTwo)
    {
    	if(getIsTwo)	// IsTwo
    	{
    		renderTwoPages(page);
    	}
    	else	// IsOne
    	{
    		renderOnePage(page);
    	}
    	
    	/*
    	try
    	{
    		Log.i("showPage","Start");
    		
    		Log.i("showPage","page = " + page);

    		if(imagePage!=null)
    		{
    			imagePage.recycle();
    			imagePage=null;
    		}


    		filePage = file.getPage(page, true);

    		double wi =  filePage.getWidth();
	        double hei = filePage.getHeight();
	        double zoom = 1.5;
	        RectF clip = null;
	        imagePage = filePage.getImage((int)(wi*zoom), (int)(hei*zoom), clip, true, true);
	        image.setImageBitmap(imagePage);
    	}
    	catch(Exception ex)
    	{
    		image.setImageDrawable(PDFViewer.this.getResources().getDrawable(R.drawable.ic_page_null)); 
    	}
    	*/
    }
    double setW,setH;
    public void renderOnePage(int Page)
    {
    	try
    	{
    		filePage = file.getPage(Page, true);
    		
    		if(filePage.getWidth()>900)
    		{
    			setW = filePage.getWidth();
    			setH = filePage.getHeight();
    		}
    		else
    		{
    			if(filePage.getWidth()<400)
        		{
        			setW = filePage.getWidth()*2;
        			setH = filePage.getHeight()*2;
        		}
    			else
    			{
        			setW = filePage.getWidth()*1.5;
        			setH = filePage.getHeight()*1.5;
    			}
    		}
    		
    		
    		//imagePage = filePage.getImage((int)filePage.getWidth(), (int)filePage.getHeight(), null, false, true);
    		imagePage = filePage.getImage((int)setW, (int)setH, null, false, true);
    		image.setImageBitmap(hasBookmark(imagePage,Page));
    	//	image.setImageBitmap(imagePage);
    	}
    	catch(Exception ex)
    	{
    		
    	}
    }
    
    Bitmap bitmapGlobal;
    
    public void renderTwoPages(int Page) 
    {
    	try
    	{
    		if(Page==1)
    		{
        		filePage = file.getPage(Page, true);
        		
        		if(filePage.getWidth()>900)
        		{
        			setW = filePage.getWidth();
        			setH = filePage.getHeight();
        		}
        		else
        		{
        			if(filePage.getWidth()<400)
            		{
            			setW = filePage.getWidth()*2;
            			setH = filePage.getHeight()*2;
            		}
        			else
        			{
            			setW = filePage.getWidth()*1.5;
            			setH = filePage.getHeight()*1.5;
        			}
        		}
        		
        		//bitmapGlobal = page.getImage((int)page.getWidth(), (int)page.getHeight(), null, false, true);
        		bitmapGlobal = filePage.getImage((int)setW, (int)setH, null, false, true);
        		bitmapGlobal = hasBookmark(bitmapGlobal,Page);
        		image.setImageBitmap(bitmapGlobal);
    		}
    		else
    		{
    			// Add two pages
    			if(isPair(Page))
    			{
    				image.setImageBitmap(fromLeftToRight(Page));
    			}
    			else
    			{
    				image.setImageBitmap(fromRightToLeft(Page));
    			}
    		}
    	}
    	catch(Exception ex){}
    }
    
    public Bitmap fromLeftToRight(int Page)
    {
    	try
    	{
    		// Get left page and add next right page
    		PDFPage pageLeft = file.getPage(Page, true);
    		Bitmap bitmapLeftPage = pageLeft.getImage((int)pageLeft.getWidth(), (int)pageLeft.getHeight(), null, false, true);
    		bitmapLeftPage = hasBookmark(bitmapLeftPage,Page);
    		
    		int nextPage = Page+1;
    		PDFPage pageRight = file.getPage(nextPage, true); 
    		Bitmap bitmapRightPage = pageRight.getImage((int)pageRight.getWidth(), (int)pageRight.getHeight(), null, false, true);
    		bitmapRightPage = hasBookmark(bitmapRightPage,nextPage);
    		
    		
    		int width, height; 
    		
    		width  = bitmapLeftPage.getWidth() + bitmapRightPage.getWidth(); 
    		height = bitmapRightPage.getHeight(); 
    		
    		bitmapGlobal = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
    		Canvas comboImage = new Canvas(bitmapGlobal); 
    		
            comboImage.drawBitmap(bitmapLeftPage, 0f, 0f, null); 
            comboImage.drawBitmap(bitmapRightPage, bitmapLeftPage.getWidth(), 0f , null); 

            bitmapLeftPage.recycle();
            bitmapRightPage.recycle();
            return bitmapGlobal;

    	}
    	catch(Exception ex)
    	{
    		return null;
    	}
    }
    
    public Bitmap fromRightToLeft(int Page)
    {
    	try
    	{
    		// Get left page and add next right page
    		int previousPage = Page-1;
    		PDFPage pageLeft = file.getPage(previousPage, true);
    		Bitmap bitmapLeftPage = pageLeft.getImage((int)pageLeft.getWidth(), (int)pageLeft.getHeight(), null, false, true);
    		bitmapLeftPage = hasBookmark(bitmapLeftPage,previousPage);
    		
    		PDFPage pageRight = file.getPage(Page, true);
    		Bitmap bitmapRightPage = pageRight.getImage((int)pageRight.getWidth(), (int)pageRight.getHeight(), null, false, true);
    		bitmapRightPage = hasBookmark(bitmapRightPage,Page);
    		
    		int width, height; 
    		
    		width  = bitmapLeftPage.getWidth() + bitmapRightPage.getWidth(); 
    		height = bitmapRightPage.getHeight(); 
    		
    		bitmapGlobal = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
    		Canvas comboImage = new Canvas(bitmapGlobal); 
    		
            comboImage.drawBitmap(bitmapLeftPage, 0f, 0f, null); 
            comboImage.drawBitmap(bitmapRightPage, bitmapLeftPage.getWidth(), 0f , null); 

            bitmapLeftPage.recycle();
            bitmapRightPage.recycle();
            return bitmapGlobal;

    	}
    	catch(Exception ex)
    	{
    		return null;
    	}
    }

    public boolean isPair(int Page)
    {
    	if (Page % 2 == 0) 
    	{
    		  // even
    		return true;
    	} else 
    	{
    		  // odd
    		return false;
    	}
    }
    
    //Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_resource);
    
    public Bitmap hasBookmark(Bitmap getPageBitmap,int page)
    {
    	try
    	{
    		data.openToRead();
    		boolean isBookmark = data.hasBookmarkPage(page, pdf, pageCount);
    		Log.i("hasBookmark","isBookmark = " + isBookmark);
    		
    		Bitmap pageBookmark = null;
    		Bitmap flagOn = BitmapFactory.decodeResource(PDFViewer.this.getResources(), R.drawable.ic_flag_on);
    		Bitmap flagOff = BitmapFactory.decodeResource(PDFViewer.this.getResources(), R.drawable.ic_flag_off);
    		
    		if(isBookmark)
    		{
    			pageBookmark = Bitmap.createBitmap(getPageBitmap.getWidth(), getPageBitmap.getHeight(),Bitmap.Config.ARGB_8888); 
    			Canvas canvas = new Canvas(pageBookmark);
    			canvas.drawBitmap(getPageBitmap, new Matrix(), null);
    			canvas.drawBitmap(flagOn, 10,0, null);
    			return pageBookmark;
    			
    		}
    		else
    		{
    			pageBookmark = Bitmap.createBitmap(getPageBitmap.getWidth(), getPageBitmap.getHeight(),Bitmap.Config.ARGB_8888); 
    			Canvas canvas = new Canvas(pageBookmark);
    			canvas.drawBitmap(getPageBitmap, new Matrix(), null);
    			canvas.drawBitmap(flagOff, 10,0, null);
    			return pageBookmark;
    		}
    	}
    	catch(Exception ex)
    	{
    		return getPageBitmap;
    	}
    	finally
    	{
    		data.close(); 
    	}
    }
    
    int savePage;
    String saveText;
	int pageA,pageB;
    
    @Override
    protected Dialog onCreateDialog(int id) 
    {
    	switch (id)
    	{
    		case DIALOG_PAGENUM:
    			LayoutInflater factory = LayoutInflater.from(this); 
    	        final View pagenumView = factory.inflate(R.layout.dialog_pagenumber, null);
    			final EditText edPagenum = (EditText)pagenumView.findViewById(R.id.pagenum_edit); 
    			edPagenum.setText("");
    			return new AlertDialog.Builder(this)
	            .setTitle("Jump to page")
	            .setView(pagenumView)
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	            		String strPagenum = edPagenum.getText().toString(); 
	            		int pageNum=1;
	            		try 
	            		{
	            			pageNum = Integer.parseInt(strPagenum);
	            		}
	            		catch (NumberFormatException ignore) {}
	            		if ((pageNum>=1) && (pageNum <= pageCount)) 
	            		{
	            			currentPage = pageNum;
	                		try
	                		{
	                    		currentPage = currentPage + 1;
	                			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
	                			intentDirectory.putExtra("path",path);
	                			intentDirectory.putExtra("pdf",pdf);
	                			intentDirectory.putExtra("currentPage",currentPage);
	                		    startActivity(intentDirectory);
	                		}
	                		catch(Exception ex){}
	            			//startRenderThread(mPage, mZoom);
	            		}
	                }
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                }
	            })
	            .create();
    		case DIALOG_BOOKMARK:
    			LayoutInflater factorys = LayoutInflater.from(this);
    			final View viewAddBookmark = factorys.inflate(R.layout.layoud_add_bookmark, null);
    			final RadioGroup radioGroup = (RadioGroup)viewAddBookmark.findViewById(R.id.radioGroup);
    			final EditText editBookmark  = (EditText)viewAddBookmark.findViewById(R.id.editBookmark);
    			final RadioButton radio0  = (RadioButton)viewAddBookmark.findViewById(R.id.radio0);
    			final RadioButton radio1  = (RadioButton)viewAddBookmark.findViewById(R.id.radio1);
    			
    			final int setPageA,setPageB;
    			
    			if(isTwo)
    			{
    				if(currentPage<pageCount) 
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
    			    		Toast.makeText(PDFViewer.this, "Capture bookmark", Toast.LENGTH_SHORT).show();
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
			id = data.insertBookmark(new_id, text, setPage, pdf, String.valueOf(pageCount));
            if(id == -1)
            {
            	Toast.makeText(PDFViewer.this, "Error", Toast.LENGTH_SHORT).show();
            }
            else
            {  
            	Toast.makeText(PDFViewer.this, "Saved", Toast.LENGTH_SHORT).show();
            	//loadBookmarks();
            	if(isTwo)
            	{
            			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
            			intentDirectory.putExtra("path",path);
            			intentDirectory.putExtra("pdf",pdf);
            			intentDirectory.putExtra("currentPage",currentPage);
            			intentDirectory.putExtra("isTwo",isTwo);
            		    startActivity(intentDirectory);
            	}
            	else
            	{
            			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
            			intentDirectory.putExtra("path",path);
            			intentDirectory.putExtra("pdf",pdf);
            			intentDirectory.putExtra("currentPage",currentPage);
            			intentDirectory.putExtra("isTwo",isTwo);
            		    startActivity(intentDirectory);
            	}
            }
			
		}
		catch(Exception ex)
		{
    		Toast.makeText(PDFViewer.this, "Error " + ex.getMessage(), Toast.LENGTH_SHORT).show();
    		return;
		}
		finally
		{
			data.close();
		}
	}
	
	public String nextID()
	{
		Cursor c = null;
		try
		{
			data.openToRead();
			c = data.getNextIDBookmark(pdf, String.valueOf(pageCount));
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
	
	private void setContent(String password) {
        try { 
        	String setName = path+pdf;
    		parsePDF(setName, password);

    	}
        catch (PDFAuthenticationFailureException e) {
        	setContentView(R.layout.pdf_file_password);
           	final EditText etPW= (EditText) findViewById(R.id.etPassword);
           	Button btOK= (Button) findViewById(R.id.btOK);
        	Button btExit = (Button) findViewById(R.id.btExit);
            btOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String pw = etPW.getText().toString();
		        	setContent(pw);
				}
			});
            btExit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				//	finish();
				}
			});
        }
	}
	
    private void parsePDF(String filename, String password) throws PDFAuthenticationFailureException {
        long startTime = System.currentTimeMillis();
    	try {
        	File f = new File(filename);
        	long len = f.length();
        	if (len == 0) {
        		//mGraphView.showText("file '" + filename + "' not found");
        	}
        	else {
        		//mGraphView.showText("file '" + filename + "' has " + len + " bytes");
    	    	openFile(f, password);
        	}
    	}
        catch (PDFAuthenticationFailureException e) {
        	throw e; 
		} catch (Throwable e) {
			e.printStackTrace();
			//mGraphView.showText("Exception: "+e.getMessage());
		}
        long stopTime = System.currentTimeMillis();
       // mGraphView.fileMillis = stopTime-startTime;
	}
    
    public void openFile(File files, String password) throws IOException {
        // first open the file for random access
        RandomAccessFile raf = new RandomAccessFile(files, "r");

        // extract a file channel
        FileChannel channel = raf.getChannel();

        // now memory-map a byte-buffer
        ByteBuffer bb =
                ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
        // create a PDFFile from the data
        if (password == null)
        	file = new PDFFile(bb);
        else
        	file = new PDFFile(bb, new PDFPassword(password));
	        
       // mGraphView.showText("Anzahl Seiten:" + mPdfFile.getNumPages());
    }
    
}