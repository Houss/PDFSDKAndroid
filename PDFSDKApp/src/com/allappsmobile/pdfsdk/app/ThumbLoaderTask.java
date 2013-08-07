package com.allappsmobile.pdfsdk.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.font.PDFFont;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

class ThumbLoaderTask extends AsyncTask<String, Void, Bitmap>
{
	private final WeakReference<ImageView> imageViewReference;
	static Bitmap globalImage;
	
	public ThumbLoaderTask(ImageView imageView) 
	{
		imageViewReference = new WeakReference<ImageView>(imageView);
	}
	
	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) 
	{
		// params comes from the execute() call: params[0] is the url. is the page count
		return getPageBitmap(params[0]);
	}
	
	@Override
	// Once the image is downloaded, associates it to the imageView
	protected void onPostExecute(Bitmap bitmap)
	{
		if (isCancelled()) 
		{
			bitmap = null;
		}
		
		if (imageViewReference != null) 
		{
			ImageView imageView = imageViewReference.get();
			if (imageView != null)
			{
				if (bitmap != null)
				{
					imageView.setImageBitmap(bitmap);
				}
				else
				{
					imageView.setImageDrawable(imageView.getContext().getResources()
							.getDrawable(R.drawable.list_placeholder));
				}
			}
		}
	}
	
	static Bitmap getPageBitmap(String params)
	{
		try
		{	
			//String path = Environment.getExternalStorageDirectory().getPath()+"/2.pdf";
			
			String[] split = params.split(";");
			String valuePage = split[0].toString();
			String path = split[1].toString();
			
			Integer mPage = Integer.valueOf(valuePage);
			
			PDFFile document = getDocument(path);
			//int count = document.getNumPages();
			
			Log.i("getPageBitmap","pageBitmap page no. = " + mPage);

			
			//Bitmap page = null;
			globalImage = document.getPage(mPage,true).getImage(150, 150, null, false, true); 
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			o.inDither = false;
			o.inPurgeable=true;
			o.inInputShareable=true;
			o.inTempStorage=new byte[32*1024];
			o.inSampleSize=8;
			//globalImage = BitmapFactory.Options(o);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			globalImage.compress(CompressFormat.PNG, 10, bos);
			return globalImage;
		}	
		catch(Exception ex)
		{
			return null;
		}
	}
	
	static PDFFile getDocument(String path)
	{
		try
		{
			initContrils(); 
			File file = new File(path);
    		RandomAccessFile raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
            
            return new PDFFile(byteBuffer);
		}
		catch(Exception ex)
		{
			Log.i("getDocument","PDFFile = " + ex.getMessage());
			return null;
		}
	}
	
	static void initContrils() 
    {
        // TODO Auto-generated method stub
        //boolean showImages = getIntent().getBooleanExtra(PDFViewer.EXTRA_SHOWIMAGES, true);
        PDFImage.sShowImages = true;
        
        
        //boolean antiAlias = getIntent().getBooleanExtra(PDFViewer.EXTRA_ANTIALIAS, true);
        PDFPaint.s_doAntiAlias = false;
        
        
        //boolean useFontSubstitution = getIntent().getBooleanExtra(PDFViewer.EXTRA_USEFONTSUBSTITUTION, false);
        PDFFont.sUseFontSubstitution= false;
        
       // boolean keepCaches = getIntent().getBooleanExtra(PDFViewer.EXTRA_KEEPCACHES, false);
        HardReference.sKeepCaches= true;

     }



}