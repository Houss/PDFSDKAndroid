package com.allappsmobile.pdfsdk.app;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;

import android.graphics.Bitmap;
import android.util.Log;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.font.PDFFont;

public class ThumbsArray
{
	public ThumbsArray()
	{
		
	}
	
	public ArrayList<ItemThumbnail> getListData(String path)
	{
		try
		{
			PDFFile document = getDocument(path);
			int count = document.getNumPages();

			
			ArrayList<ItemThumbnail> results =  new ArrayList<ItemThumbnail>();

			for(int i=0;i<count;i++)
			{
				Log.v("ThumbsArray",i + " of " + count); 

				ItemThumbnail  	item 		= new ItemThumbnail(); 
				item.setPdfPath(path);
				item.setPageNumber(i+1);
		
				results.add(item); 

			}
			
			return results;
			
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	public PDFFile getDocument(String path)
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
	
    private void initContrils() 
    {
        // TODO Auto-generated method stub
        //boolean showImages = getIntent().getBooleanExtra(PDFViewer.EXTRA_SHOWIMAGES, true);
        PDFImage.sShowImages = false;
        
        
        //boolean antiAlias = getIntent().getBooleanExtra(PDFViewer.EXTRA_ANTIALIAS, true);
        PDFPaint.s_doAntiAlias = false;
        
        
        //boolean useFontSubstitution = getIntent().getBooleanExtra(PDFViewer.EXTRA_USEFONTSUBSTITUTION, false);
        PDFFont.sUseFontSubstitution= false;
        
       // boolean keepCaches = getIntent().getBooleanExtra(PDFViewer.EXTRA_KEEPCACHES, false);
        HardReference.sKeepCaches= false;

     }
}