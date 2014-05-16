package propertypanther.slidingmenu.model;

import android.graphics.Bitmap;

/**
 * An individual profile item for the navigation menu(s) that are present
 * within the application. This essentially comprises the header, and details information
 * relating to the users forename and surname, email address, and image. 
 * 
 * @author Adam Stevenson
 *
 */
public class NavDrawerProfileItem {

	private Bitmap profileBackgroundImage;
	private Bitmap profileImage;
	private String profileName;
	private String profileEmail;
	
	// boolean to set visibility of the counter
	private boolean isCounterVisible = false;
	
	/**
	 * Default (empty) constructor
	 */
	@SuppressWarnings("unused")
	private NavDrawerProfileItem(){}
	
	/**
	 * constructor for creating a profile item for the user.
	 * 
	 * @param profileBackgroundImage - the background image (shown at the top of the navigation menu)
	 * @param profileImage - the avatar image for the user
	 * @param profileName 
	 * @param profileEmail 
	 */
	public NavDrawerProfileItem(Bitmap profileBackgroundImage, Bitmap profileImage, String profileName, String profileEmail){
		this.profileBackgroundImage = profileBackgroundImage;
		this.profileImage = profileImage;
		this.profileName = profileName;
		this.profileEmail = profileEmail;
	}
	
	public Bitmap getProfileBackgroundImage(){
		return this.profileBackgroundImage;
	}
	
	public Bitmap getProfileImage() {
		return this.profileImage;
	}
	
	public String getProfileName(){
		return this.profileName;
	}
	
	public String getProfileEmail(){
		return this.profileEmail;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setProfileBackgroundImage(Bitmap profileBackgroundImage){
		this.profileBackgroundImage = profileBackgroundImage;
	}
	
	public void setProfileImage(Bitmap profileImage){
		this.profileImage = profileImage;
	}
	
	public void setProfileName(String profileName){
		this.profileName = profileName;
	}
	
	public void setProfileEmail(String profileEmail){
		this.profileEmail = profileEmail;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
	
	
}
