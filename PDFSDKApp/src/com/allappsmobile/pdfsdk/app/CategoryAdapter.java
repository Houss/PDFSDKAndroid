package com.allappsmobile.pdfsdk.app;

import java.util.ArrayList;






import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<CategoryFrame>
{
	private ArrayList<CategoryFrame> categoryitems  = null;
	private Activity activity;

	
	public CategoryAdapter(Activity getActivity,int getLayout,ArrayList<CategoryFrame> categoryItems)
	{
		super(getActivity,getLayout,categoryItems);
		this.categoryitems = categoryItems;
		this.activity = getActivity;
	}
	
	public static class ViewHolder
	{
		public RelativeLayout tabCategory;
		public ImageView iconCategory;
		public TextView titleCategory;
	}
	
	public int getCount()
	{
		int count;
		if(categoryitems!=null)
		{
			count = categoryitems.size();
		}
		else
		{
			count = 0;
		}
		return count;
	}
	


    public long getItemId(int position) 
    {
        return position;
    }
	
	public View getView(int position,View convertView,ViewGroup parent)
	{
		View v = convertView;
		ViewHolder holder;
		
		if(v==null)
		{
			LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.category_layout, null);
			
			holder = new ViewHolder();
			
		    
		//    v = super.getView(position, convertView, parent);
		    holder.tabCategory   = (RelativeLayout)v.findViewById(R.id.relativeTabColor);
		    holder.iconCategory  = (ImageView)v.findViewById(R.id.imageIcon);
		    holder.titleCategory = (TextView)v.findViewById(R.id.textCategory); 
		    v.setTag(holder);
		} 
		else
		{
			holder = (ViewHolder)v.getTag(); 
		}

		final CategoryFrame categoryFrame = categoryitems.get(position);

    	holder.titleCategory.setId(position);
    	
    	holder.tabCategory.setBackgroundColor(categoryFrame.getTabColor());
    	holder.iconCategory.setImageResource(categoryFrame.getIcon());
    	holder.titleCategory.setText(categoryFrame.getTitle());

		    
		return v;
		

		
	}
}