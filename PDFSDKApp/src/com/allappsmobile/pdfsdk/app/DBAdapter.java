package com.allappsmobile.pdfsdk.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter
{
	private static final String TAG                   =   "DBAdapter";
    private static final String DATABASE_NAME         =   "PDFSDK_DATA";
    private static final String DATABASE_TABLE1       =   "pdf_document";  
    private static final String DATABASE_TABLE2       =   "bookmarks";  
    private final static int    DATABASE_VERSION      =   1;
    
    //DATABASE_TABLE1 = "pdf_document";                  
    private static final String DATABASE_CREATE1 = 
        "create table "+DATABASE_TABLE1+" ( _id integer primary key autoincrement," +
        "id_pdf text  null," +
        "title text  null," +
        "page_count text  null " +
        ");";
    
    //DATABASE_TABLE2 = "bookmarks";                  
    private static final String DATABASE_CREATE2 = 
        "create table "+DATABASE_TABLE2+" ( _id integer primary key autoincrement," +
        "id_bookmark 	text  null," +
        "text_bookmark 	text  null," +
        "page 			text  null," +
        "pdf 			text  null," +
        "page_count 	text  null " +
        ");";
	
    
    private final Context context;     
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    
    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }   
    
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_CREATE1);                     
            db.execSQL(DATABASE_CREATE2);
            db.setMaximumSize(102400000);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            
            db.execSQL("DROP TABLE IF EXISTS PDFSDK_DATA");
            onCreate(db);
        }
        
    }
    
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    
    public DBAdapter openToRead() throws SQLException 
    {       
       try
       {                 
            db = DBHelper.getReadableDatabase();
       } 
       catch (SQLiteException ex) 
       {                  
            db = DBHelper.getReadableDatabase();            
       }               
            return this;    
    }
    
    public void close() 
    {
        DBHelper.close();                      
    }  
    
    public long saveDataPDFDocument(String title,String page_count)
    {
    	Cursor c = null;
    	
    	Log.i("saveDataPDFDocument","title = " + title);
    	Log.i("saveDataPDFDocument","page_count = " + page_count);
    	
    	try
    	{
    		c = db.rawQuery("SELECT id_pdf FROM " + DATABASE_TABLE1 + " WHERE title = '"+title+"' AND page_count = '"+page_count+"'", null);
    		if(c.getCount()>0)
    		{
    			//	UPDATE
    			
                ContentValues args1 = new ContentValues();                                         
                args1.put("title",title);
                args1.put("page_count",page_count);
                return db.update(DATABASE_TABLE1, args1," title = '"+title+"' AND page_count = '"+page_count+"' " , null); 
    		}
    		else
    		{
    			//	INSERT
    			String newID = getNewIdPDF();
    			Log.i("saveDataPDFDocument","newID = "+ newID);
    			
                ContentValues args1 = new ContentValues();    
                args1.put("id_pdf","1");
                args1.put("title",title);
                args1.put("page_count",page_count);
                return db.insert(DATABASE_TABLE1, null, args1);
    		}
    	}
    	catch(Exception ex)
    	{
    		return 0;
    	}
    }
    
//    "id_bookmark 	text  null," +
//    "text_bookmark 	text  null," +
//    "pdf 			text  null," +
//    "page_count 	text  null " +
    
    public long insertBookmark(String id_bookmark,String text_bookmark,String page,String pdf,String page_count)
    {
    	Cursor c = null;
    	
    	try
    	{
    		
    		c = db.rawQuery("SELECT text_bookmark FROM " + DATABASE_TABLE2 + " WHERE page = '"+page+"' and pdf = '"+pdf+"' and page_count = '"+page_count+"' ", null);
    		if(c.getCount()>0)
    		{
    			//
        		ContentValues args1 = new ContentValues();    
                args1.put("id_bookmark",id_bookmark);
                args1.put("text_bookmark",text_bookmark);
                args1.put("page",page);
                args1.put("pdf",pdf);
                args1.put("page_count",page_count);
                return db.update(DATABASE_TABLE2, args1, "page = '"+page+"' and pdf = '"+pdf+"' and page_count = '"+page_count+"'", null);
                //return db.update(DATABASE_TABLE1, args1," title = '"+title+"' AND page_count = '"+page_count+"' " , null); 
    		}
    		else
    		{
        		ContentValues args1 = new ContentValues();    
                args1.put("id_bookmark",id_bookmark);
                args1.put("text_bookmark",text_bookmark);
                args1.put("page",page);
                args1.put("pdf",pdf);
                args1.put("page_count",page_count);
                return db.insert(DATABASE_TABLE2, null, args1);
    		}
    	}
    	catch(Exception ex)
    	{
    		return 0;
    	}
    }
    
    public Cursor getAllBookmarks(String pdf,String page_count)
    {
    	try
    	{
    		return db.rawQuery("SELECT _id as _id," +
    				" id_bookmark, " +
    				" text_bookmark, " +
    				" page, " +
    				" pdf, " +
    				" page_count " +
    				" from "+DATABASE_TABLE2+
    				" where pdf = '"+pdf+"' and page_count = '"+page_count+"' ",
    				null);
    	}
        catch(Exception ex)
        { 
          return null;
        }
    }
    
    public boolean deleteBookmark(String pdf,String page_count,String id_bookmark)
    {
    	Log.i("deleteBookmark","pdf = " + pdf);
    	Log.i("deleteBookmark","page_count = " + page_count);
    	Log.i("deleteBookmark","id_bookmark = " + id_bookmark);
    	
    	try
    	{
        	int res = db.delete(DATABASE_TABLE2, " pdf = '"+pdf+"' and page_count = '"+page_count+"' and id_bookmark = '"+id_bookmark+"' ", null);
        	Log.i("deleteBookmark","res = " + res);
        	if(res==1)
        	{
        		return true;
        	}
        	else
        	{
        		return false;
        	}
    	}
    	catch(Exception ex)
    	{
    		Log.i("deleteBookmark","Error = " + ex.getMessage());
    		return false;
    	}
    	
    }
    
    public Cursor getNextIDBookmark(String pdf,String page_count)
    {
    	try
    	{
    		return db.rawQuery("SELECT id_bookmark FROM "+DATABASE_TABLE2+" WHERE pdf = '"+pdf+"' AND page_count = '"+page_count+"' order by cast(id_bookmark as Integer) desc limit 1 ", null);
    	}
    	catch(Exception ex)
    	{
    		return null;
    	}
    }
    
    public long saveDataFavorites(String id_fav,String favorite,String id_pdf)
    {
    	Cursor c = null;
    	
//        "id_fav text  null," +
//        "favorite text  null," +
//        "id_pdf text  null " +
    	
    	try
    	{
    		c = db.rawQuery("SELECT id_fav FROM " + DATABASE_TABLE2 + " WHERE favorite = '"+favorite+"' AND id_pdf = '"+id_pdf+"'", null);
    		if(c.getCount()>0)
    		{
    			//	UPDATE
                ContentValues args1 = new ContentValues();                                         
                args1.put("favorite",favorite);
                args1.put("id_pdf",id_pdf);
                return db.update(DATABASE_TABLE2, args1," favorite = '"+favorite+"' AND id_pdf = '"+id_pdf+"' " , null); 
    		}
    		else
    		{
    			//	INSERT
                ContentValues args1 = new ContentValues();    
                args1.put("id_fav",id_fav);
                args1.put("favorite",favorite);
                args1.put("id_pdf",id_pdf);
                return db.insert(DATABASE_TABLE2, null, args1);
    		}
    	}
    	catch(Exception ex)
    	{
    		return 0;
    	}
    }
    
    public String getNewIdPDF()
    {
    	try
    	{
    		Cursor c = null;
    		
    		c = db.rawQuery("SELECT id_pdf FROM " + DATABASE_TABLE1 + " ORDER BY CAST(id_pdf as Integer) desc limit 1 ", null);
    		if(c.getCount()>0)
    		{
    			if(c.moveToLast())
    			{
    				String getLast = c.getString(0);
    				Log.i("getNewIdPDF","id_pdf = " + getLast); 
    				int newId = Integer.parseInt(getLast);
    				newId = newId+1;
    				return String.valueOf(newId);
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
    }
    
//    public long insertBookmark(String id_bookmark,String text_bookmark,String page,String pdf,String page_count)
//    {
//    	try
//    	{
//    		ContentValues args1 = new ContentValues();    
//            args1.put("id_bookmark",id_bookmark);
//            args1.put("text_bookmark",text_bookmark);
//            args1.put("page",page);
//            args1.put("pdf",pdf);
//            args1.put("page_count",page_count);
//            return db.insert(DATABASE_TABLE2, null, args1);
//    	}
//    	catch(Exception ex)
//    	{
//    		return 0;
//    	}
//    }
    
    public boolean hasBookmarkPage(int get_page,String pdf,int get_page_count)
    {
    	try
    	{
    		String page = String.valueOf(get_page);
    		String page_count = String.valueOf(get_page_count);
    		
    		Log.v("hasBookmarkPage","page = " + page);
    		Log.v("hasBookmarkPage","page_count = " + page_count);
    		Log.v("hasBookmarkPage","pdf = " + pdf);
    		
    		//query: 1;	test;	2;	AllappsMOBILE_2013.pdf;	0
//			" id_bookmark, " +
//			" text_bookmark, " +
//			" page, " +
//			" pdf, " +
//			" page_count " +

    		
    		Cursor c = null;
    		c = db.rawQuery("SELECT text_bookmark FROM " + DATABASE_TABLE2 + " WHERE page = '"+page+"' and pdf = '"+pdf+"' and page_count = '"+page_count+"' ", null);
    		Log.v("hasBookmarkPage","count = " + c.getCount());
    		
    		if(c.getCount()>0)
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	catch(Exception ex)
    	{
    		return false;
    	}
    }
    
    public String getNewIdFavorite()
    {
    	try
    	{
    		Cursor c = null;
    		
    		c = db.rawQuery("SELECT id_fav FROM " + DATABASE_TABLE2 + " ORDER BY CAST(id_fav as Integer) desc limit 1 ", null);
    		if(c.getCount()>0)
    		{
    			if(c.moveToFirst())
    			{
    				String getLast = c.getString(0);
    				int newId = Integer.parseInt(getLast);
    				newId = newId+1;
    				return String.valueOf(newId);
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
    }
    
    
}