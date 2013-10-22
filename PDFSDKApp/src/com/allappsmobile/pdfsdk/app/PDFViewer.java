package com.allappsmobile.pdfsdk.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.polites.android.GestureImageView; 
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.font.PDFFont;


public class PDFViewer extends SherlockActivity 
{
	int actionBarHeight;
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
	
	int w;
	int h;
	
	double factor;
	
	boolean isTwo = false;
	boolean landscape;
	
	private final static int DIALOG_PAGENUM = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf_viewer);  
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DDDDDD")));
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int wwidth = displaymetrics.widthPixels;
        int hheight = displaymetrics.heightPixels;
        
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            landscape=true;
            w=(int) (displaymetrics.heightPixels);
            h=(int) ((displaymetrics.widthPixels-actionBarHeight));
        }else{
            landscape=false;
            h=(int) ((displaymetrics.heightPixels-actionBarHeight));
            w=(int) (displaymetrics.widthPixels);
        } 
        
		Log.i("Cambio a Landscape --->", " " + landscape);
		Log.i("w--->", " " + w);
		Log.i("h--->", " " + h);
        
        if(w<h){
        	factor = (Global.GLmaxSize+h)/2;
            factor = factor/h;
        }else{
        	factor = (Global.GLmaxSize+w)/2;
            factor = factor/w;
        }

		
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
        
        
        file = getDocument(path+pdf);
        if(file==null)
        {
        	return;
        }
        

        pageCount = file.getNumPages();
        showPage(currentPage,isTwo);
        
	}
	
	@Override
	public void onLowMemory() {
	    super.onLowMemory();
	    System.out.println("onLowMemory");
	    // Your memory releasing code
	}
	
/*	@Override
	public void onPause(){
	   // onCreate(new Bundle());
	}*/
	
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
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
    	//currentPage
    	if(item.getTitle().equals("Previous"))
		{
    		//bitmapGlobal.recycle();
        	if(isTwo)
        	{
        		//image.setImageDrawable(PDFViewer.this.getResources().getDrawable(R.drawable.ic_page_null));
        		//showPage(currentPage-2,isTwo);
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
        		//image.setImageDrawable(PDFViewer.this.getResources().getDrawable(R.drawable.ic_page_null));
        		//showPage(currentPage-1,isTwo);
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
        		//image.setImageDrawable(PDFViewer.this.getResources().getDrawable(R.drawable.ic_page_null));
        		//showPage(currentPage+2,isTwo);
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
        		//image.setImageDrawable(PDFViewer.this.getResources().getDrawable(R.drawable.ic_page_null));
        		//showPage(currentPage+1,isTwo);
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
    	
    	if(item.getTitle().equals("PDF SDK App"))
        {
            if (mDrawerList.getVisibility() == View.VISIBLE) {
                // Its visible
                mDrawerLayout.closeDrawers();
            } else {
                // Either gone or invisible
                mDrawerLayout.openDrawer(mDrawerList);
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

    
    Bitmap bitmapGlobal;
    
    double setW,setH;
    public void renderOnePage(int Page)
    {
    		
    	filePage = file.getPage(Page, true);

		setW = w*factor;
		setH = h*factor;  
		
		Log.i("fACTOR--->", " " + factor);
		Log.i("w--->", " " + setW);
		Log.i("h--->", " " + setH);
		
		System.gc();
        if(bitmapGlobal != null) {
        	bitmapGlobal.recycle();
        	bitmapGlobal = null;
       }

   
        
        Log.i("DEBUG--->", " Start Render");
        System.gc();
		bitmapGlobal = filePage.getImage((int)setW, (int)setH, null, false, true);	
        Log.i("DEBUG--->", " End Render");
        
        image.setImageBitmap(bitmapGlobal);
        HardReference.cleanup();

    	
    }
    

    
    public void renderTwoPages(int Page) 
    {
    	try
    	{   
    		Log.i("w--->", " " + w);
    		Log.i("h--->", " " + h);
    		
    		if(Page==1)
    		{
    			
        		setW = w*(factor-.5);
        		setH = h*(factor-.5);  
        		
        		PDFPage page = file.getPage(Page, true);
        		
        		double divideby = 2;
        		if(landscape){
        			divideby = 1.25;
        		}
        		
        		System.gc();
        		PDFPage pageRight = file.getPage(Page, true);
        		System.gc();
        		Bitmap bitmapRightPage = pageRight.getImage((int)(setW/divideby), (int)(setH/divideby), null, false, true);
        		
        		bitmapGlobal = Bitmap.createBitmap((int)(bitmapRightPage.getWidth()*2), (int)(bitmapRightPage.getHeight()), Bitmap.Config.ARGB_8888); 
        		Canvas comboImage = new Canvas(bitmapGlobal); 
   
        		comboImage.drawBitmap(bitmapRightPage, bitmapRightPage.getWidth(), 0f , null); 
                image.setImageBitmap(bitmapGlobal);
                HardReference.cleanup();

                bitmapRightPage.recycle();

    		}
    		else
    		{
    			// Add two pages
    			if(isPair(Page))
    			{
    				image.setImageBitmap(fromLeftToRight(Page));
    				HardReference.cleanup();
    			}
    			else
    			{
    				image.setImageBitmap(fromRightToLeft(Page));
    				HardReference.cleanup();
    			}
    		}
    	}
    	catch(Exception ex){}
    }
    
    public Bitmap fromLeftToRight(int Page)
    {
    	try
    	{
    		Log.i("DEBUG--->", " Left to Right");
    		
    		setW = w*(factor-.5);
    		setH = h*(factor-.5);  

    		double divideby = 2;
    		if(landscape){
    			divideby = 1.25;
    		}
    		
    		// Get left page and add next right page
    		PDFPage pageLeft = file.getPage(Page, true);
    		//Bitmap bitmapLeftPage = pageLeft.getImage((int)w, (int)h, null, false, true);
    		Bitmap bitmapLeftPage = pageLeft.getImage((int)(setW/divideby), (int)(setH/divideby), null, false, true);
    		
    		int nextPage = Page+1;
    		PDFPage pageRight = file.getPage(nextPage, true); 
    		//Bitmap bitmapRightPage = pageRight.getImage((int)w, (int)h, null, false, true);
    		Bitmap bitmapRightPage = pageRight.getImage((int)(setW/divideby), (int)(setH/divideby), null, false, true);

   		
    		bitmapGlobal = Bitmap.createBitmap((int)(bitmapRightPage.getWidth()*2), (int)(bitmapRightPage.getHeight()), Bitmap.Config.ARGB_8888); 
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
    		
    		Log.i("DEBUG--->", " Right to Left -Page->" + Page);
  
    		setW = w*(factor-.5);
    		setH = h*(factor-.5);  
 
    		double divideby = 2;
    		if(landscape){
    			divideby = 1.25;
    		}
    		
    		
    		// Get left page and add next right page
    		int previousPage = Page-1;
    		PDFPage pageLeft = file.getPage(previousPage, true);
    		//Bitmap bitmapLeftPage = pageLeft.getImage((int)w, (int)h, null, false, true);
    		Bitmap bitmapLeftPage = pageLeft.getImage((int)(setW/divideby), (int)(setH/divideby), null, false, true);
    		
    		PDFPage pageRight = file.getPage(Page, true);
    		//Bitmap bitmapRightPage = pageRight.getImage((int)w, (int)h, null, false, true);
    		Bitmap bitmapRightPage = pageRight.getImage((int)(setW/divideby), (int)(setH/divideby), null, false, true);
    		
    		
    		bitmapGlobal = Bitmap.createBitmap((int)(bitmapRightPage.getWidth()*2), (int)(bitmapRightPage.getHeight()), Bitmap.Config.ARGB_8888); 
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
    	}
    	return null;
    }
    
}