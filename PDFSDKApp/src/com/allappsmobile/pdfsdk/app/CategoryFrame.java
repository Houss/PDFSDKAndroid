package com.allappsmobile.pdfsdk.app;

public class CategoryFrame
{
	private int tabColor;
	private int icon;
	private String title;
	
	public CategoryFrame(int customTabColor,int customIcon,String customTitle)
	{
		this.tabColor = customTabColor;
		this.icon = customIcon;
		this.title = customTitle;
	}

	public int getTabColor() {
		return tabColor;
	}

	public void setTabColor(int tabColor) {
		this.tabColor = tabColor;
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