package com.allappsmobile.pdfsdk.app;

import java.util.ArrayList;






import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater; 
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterDirectory extends ArrayAdapter<ItemDirectory>
{
	private ArrayList<ItemDirectory> listItems  = null;
	private Activity activity;
	
	public AdapterDirectory(Activity getActivity,int getLayout,ArrayList<ItemDirectory> categoryItems)
	{
		super(getActivity,getLayout,categoryItems); 
		this.listItems = categoryItems;
		this.activity = getActivity; 
	}
	
	public static class ViewHolder
	{
		public ImageView iconCategory;
		public TextView titleCategory;
	}
	
	public int getCount()
	{
		int count;
		if(listItems!=null)
		{
			count = listItems.size();
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
			v = inflater.inflate(R.layout.list_item, null);
			
			holder = new ViewHolder();
			
		    holder.iconCategory  = (ImageView)v.findViewById(R.id.imageItem);
		    holder.titleCategory = (TextView)v.findViewById(R.id.lblTitle); 
		    v.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)v.getTag(); 
		}
		
		final ItemDirectory setItem = listItems.get(position);
		
    	holder.iconCategory.setImageResource(setItem.getIcon());
    	holder.titleCategory.setText(setItem.getTitle());
    	return v;
	}
}
