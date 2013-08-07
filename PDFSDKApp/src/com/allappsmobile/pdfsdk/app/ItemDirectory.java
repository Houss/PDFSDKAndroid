package com.allappsmobile.pdfsdk.app;

public class ItemDirectory
{
	public int icon;
	public String title;
	
	public ItemDirectory(int icon,String title)
	{
		super();
        this.icon = icon;
        this.title = title;
	}
	
	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}