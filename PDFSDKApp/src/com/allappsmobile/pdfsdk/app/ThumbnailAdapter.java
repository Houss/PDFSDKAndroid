package com.allappsmobile.pdfsdk.app;

import java.util.ArrayList;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThumbnailAdapter extends BaseAdapter
{
	private ArrayList<ItemThumbnail> listData;
	
	private LayoutInflater layoutInflater;
	
	public ThumbnailAdapter(Context context,ArrayList<ItemThumbnail> listData)
	{
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}
	
/*
	@Override
	public int getCount()
	{
		return listData.size();
	}

*/
	public int getCount()
	{
		int count;
		if(listData!=null)
		{
			count = listData.size();
		}
		else
		{
			count = 0;
		}
		return count;
	}
	
	
	@Override
	public Object getItem(int position)
	{
		return listData.get(position);
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;
		if(convertView == null)
		{
			convertView = layoutInflater.inflate(R.layout.list_row_thumb, null);
			holder = new ViewHolder();
			holder.imageThumb = (ImageView)convertView.findViewById(R.id.imageThumb);
			holder.textThumb = (TextView)convertView.findViewById(R.id.textThumb);
			convertView.setTag(holder);
		}
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		ItemThumbnail thumbsItem = (ItemThumbnail) listData.get(position);
		
		holder.textThumb.setText("Page "+ thumbsItem.getPageNumber());
		
		if(holder.imageThumb!=null)
		{
			String valuePage = String.valueOf(thumbsItem.getPageNumber());
			String valuePath = thumbsItem.getPdfPath();
			String param = valuePage+";"+valuePath;
			
			new ThumbLoaderTask(holder.imageThumb).execute(param);
		}
		
		return convertView; 
		
	}
	
	static class ViewHolder 
	{
		ImageView imageThumb;
		TextView textThumb;
	}

}