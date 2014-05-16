package propertypanther.slidingmenu.model;

/**
 * A class that creates an Navigation Menu Item
 * 
 * @author Adam Stevenson
 *
 */
public class NavDrawerItem {
	
	private String title;
	private int icon;
	private String count = "0";
	// boolean to set visibility of the counter
	private boolean isCounterVisible = false;
	
	/**
	 *  Default (empty) constructor
	 */
	public NavDrawerItem(){}

	/** 
	 * Constructor that creates an Navigation menu item, that has
	 * a string name, and a BitMap icon.
	 * 
	 * @param title - the title of the menu item
	 * @param icon  - the image associated with it
	 */
	public NavDrawerItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	
	/**
	 *  Constructor that creates an Navigation menu item, that has a string name,
	 *  a BitMap icon, and parameters that allow for counts. For example, 
	 *  where you have a count of the amount of new messages.
	 *  Note: parameter count is not used as of yet, but it is kept in to show that we could 
	 *  incorporate notification functionality
	 *  
	 * @param title - the title of the menu item
	 * @param icon - the image associated with it
	 * @param isCounterVisible - a boolean determining whether a counter is visible or not
	 * @param count - for when we need to notify the user of updates
	 */
	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}
	
	
	public String getTitle(){
		return this.title;
	}
	
	public int getIcon(){
		return this.icon;
	}
	
	public String getCount(){
		return this.count;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}
	
	public void setCount(String count){
		this.count = count;
	}
	
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
}
