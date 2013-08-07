package com.allappsmobile.pdfsdk.app;

import java.io.File;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.allappsmobile.pdf_sdk.PDFDocument;

import com.polites.android.GestureImageView; 
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.font.PDFFont;

import fi.harism.curl.CurlPage;
import fi.harism.curl.CurlView;
import android.widget.RelativeLayout.LayoutParams;


public class PDFPageCurl extends SherlockActivity  
{


	String path,pdf; 

	PDFFile file;
	PDFPage filePage;

	int currentPage = 1;
	int pageCount;
	
	boolean isTwo = false;

	
	private CurlView mCurlView; 
	
	RelativeLayout relativeCurl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);  
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DDDDDD")));
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{	   	    		   	    	  	   	 
			path 	= extras.getString("path");
			pdf = extras.getString("pdf");
			currentPage = extras.getInt("currentPage");
			isTwo = extras.getBoolean("isTwo");
		} 
	
		


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
     
        
        
        file = getDocument(path+pdf);
        if(file==null)
        {
        	return;
        }
        pageCount = file.getNumPages();
        
        int index = 0;
		if (getLastNonConfigurationInstance() != null) {
			index = (Integer) getLastNonConfigurationInstance();
		}
		
		relativeCurl = (RelativeLayout) findViewById(R.id.relativeCurl);
		relativeParams(isTwo);
		mCurlView = (CurlView) findViewById(R.id.curl);
		mCurlView.setPageProvider(new PageProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);
		mCurlView.setBackgroundColor(0xFF202830);
		//mCurlView.

        
	}
	
	int globalWidth,globalHeight,setPageW,setPageH;
	
	public void relativeParams(boolean isTwoPages)
	{
		DisplayMetrics metric = new DisplayMetrics();
		PDFPageCurl.this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		globalWidth = metric.widthPixels;
		globalHeight = metric.heightPixels;
		
		Log.i("relativeParams","globalWidth = " + globalWidth);
		Log.i("relativeParams","globalHeight = " + globalHeight);
		
		float pageH = file.getPage(1, true).getHeight();
		float pageW = file.getPage(1, true).getWidth();
		
		Log.i("relativeParams","pageH = " + pageH);
		Log.i("relativeParams","pageW = " + pageW);
		
	    float factor;
		
		if(pageW>=pageH)
		{
			if(isTwoPages)
			{
				pageW = pageW*2;
				factor = (globalWidth*100)/pageW;
				Log.i("relativeParams","factor = " + factor);
				
				setPageW = (int) ((pageW*factor)/100);
				setPageH = (int) ((pageH*factor)/100);
				
				Log.i("relativeParams","setPageW = " + setPageW);
				Log.i("relativeParams","setPageH = " + setPageH);
			}
			else
			{
				factor = (globalWidth*100)/pageW;
				Log.i("relativeParams","factor = " + factor);
				
				setPageW = (int) ((pageW*factor)/100);
				setPageH = (int) ((pageH*factor)/100);
				
				Log.i("relativeParams","setPageW = " + setPageW);
				Log.i("relativeParams","setPageH = " + setPageH);
			}
		}
		else
		{
			if(isTwoPages)
			{
				pageW = pageW*2;
				factor = (globalWidth*100)/pageW;
				Log.i("relativeParams","factor = " + factor);
				
				setPageW = (int) ((pageW*factor)/100);
				setPageH = (int) ((pageH*factor)/100);
				
				Log.i("relativeParams","setPageW = " + setPageW);
				Log.i("relativeParams","setPageH = " + setPageH);
			}
			else
			{
				factor = (globalWidth*100)/pageH;
				Log.i("relativeParams","factor = " + factor);
				
				setPageW = (int) ((pageW*factor)/100);
				setPageH = (int) ((pageH*factor)/100);
				
				Log.i("relativeParams","setPageW = " + setPageW);
				Log.i("relativeParams","setPageH = " + setPageH);
			}
		
		}
		
		RelativeLayout.LayoutParams params = (LayoutParams) relativeCurl.getLayoutParams();
		params.width = setPageW;
		params.height = setPageH;
		relativeCurl.setLayoutParams(params);
	}
	
	public class PageProvider implements CurlView.PageProvider {

		// Bitmap resources.
		private int[] mBitmapIds = { R.drawable.obama, R.drawable.road_rage,
				R.drawable.taipei_101, R.drawable.world };

		@Override
		public int getPageCount() {
			return pageCount;
			//return 1;
		}

		private Bitmap loadBitmap(int width, int height, int index) 
		{

			
			
			
			Bitmap b = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			Drawable d = getResources().getDrawable(mBitmapIds[index]);

			int margin = 7;
			int border = 3;
			Rect r = new Rect(margin, margin, width - margin, height - margin);

			int imageWidth = r.width() - (border * 2);
			int imageHeight = imageWidth * d.getIntrinsicHeight()
					/ d.getIntrinsicWidth();
			if (imageHeight > r.height() - (border * 2)) {
				imageHeight = r.height() - (border * 2);
				imageWidth = imageHeight * d.getIntrinsicWidth()
						/ d.getIntrinsicHeight();
			}

			r.left += ((r.width() - imageWidth) / 2) - border;
			r.right = r.left + imageWidth + border + border;
			r.top += ((r.height() - imageHeight) / 2) - border;
			r.bottom = r.top + imageHeight + border + border;

			Paint p = new Paint();
			p.setColor(0xFFC0C0C0);
			c.drawRect(r, p);
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;

			d.setBounds(r);
			d.draw(c);

			return b;
		}
 
		@Override
		public void updatePage(CurlPage page, int width, int height, int index) 
		{

			
			filePage = file.getPage(index*2, true);
			Bitmap front = filePage.getImage((int) ((int)filePage.getWidth()*1.5), (int) ((int)filePage.getHeight()*1.5), null, false, true);
			
			PDFPage pBack =file.getPage((index*2)+1, true);
			Bitmap back = pBack.getImage((int) ((int)pBack.getWidth()*1.5), (int) ((int)pBack.getHeight()*1.5), null, false, true);

            Matrix matrix = new Matrix(); 
            matrix.preScale(-1.0f, 1.0f); 
            Bitmap mirroredBitmap = Bitmap.createBitmap(back, 0, 0, back.getWidth(), back.getHeight(), matrix, false);

			page.setTexture(front, CurlPage.SIDE_FRONT);
			page.setTexture(mirroredBitmap, CurlPage.SIDE_BACK);

		}

	}

	/**
	 * CurlView size changed observer.
	 */
	public class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) 
		{
			
			//mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
			
			
			if (isTwo) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
				//mCurlView.setMargins(.1f, .05f, .1f, .05f);
				mCurlView.setMargins(.0f, .0f, .0f, .0f);
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				//mCurlView.setMargins(.1f, .1f, .1f, .1f);
				mCurlView.setMargins(.0f, .0f, .0f, .0f);
			}
			
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mCurlView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurlView.onResume();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurlView.getCurrentIndex();
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
    			Intent intentDirectory=new Intent(PDFPageCurl.this,PDFPageCurl.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage-2);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
        	}
        	else
        	{
    			Intent intentDirectory=new Intent(PDFPageCurl.this,PDFPageCurl.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage-1);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
        	}
        	
    		//currentPage = currentPage - 1;
//			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
//			intentDirectory.putExtra("path",path);
//			intentDirectory.putExtra("pdf",pdf);
//			intentDirectory.putExtra("currentPage",currentPage);
//		    startActivity(intentDirectory);
		}
    	
    	if(item.getTitle().equals("Next"))
		{
    		//bitmapGlobal.recycle();
        	if(isTwo)
        	{
    			Intent intentDirectory=new Intent(PDFPageCurl.this,PDFPageCurl.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage+2);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
        	}
        	else
        	{
    			Intent intentDirectory=new Intent(PDFPageCurl.this,PDFPageCurl.class);
    			intentDirectory.putExtra("path",path);
    			intentDirectory.putExtra("pdf",pdf);
    			intentDirectory.putExtra("currentPage",currentPage+1);
    			intentDirectory.putExtra("isTwo",isTwo);
    		    startActivity(intentDirectory);
        	}
    		
//    		currentPage = currentPage + 1;
//			Intent intentDirectory=new Intent(PDFViewer.this,PDFViewer.class);
//			intentDirectory.putExtra("path",path);
//			intentDirectory.putExtra("pdf",pdf);
//			intentDirectory.putExtra("currentPage",currentPage);
//		    startActivity(intentDirectory);
		}
    	
		return super.onOptionsItemSelected(item);
	}
    
    
    
    
}