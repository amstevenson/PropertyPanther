package propertypanther.slidingmenu.adapter;

import java.util.ArrayList;

import propertypanther.slidingmenu.model.NavDrawerItem;
import propertypanther.slidingmenu.model.NavDrawerProfileItem;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.propertypanther.R;

/**
 * An adapter class that is populated by an ArrayList of menu and profile items. 
 * 
 * @author Adam Stevenson
 * 
 */
public class NavDrawerListAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<NavDrawerProfileItem> navDrawerProfileItem;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private String activityName;
	private LayoutInflater mInflater;
	private int toggleBackgroundColour = 0;
	
	/**
	 * Constructor that creates an an adapter view using the profileItem and DrawerItems 
	 * objects stored in their respective ArrayLists.
	 * 
	 * @param context - the context of the current state of the application/project.
	 * @param navDrawerProfileItem - the user-centered profile name, email, image object. 
	 * @param navDrawerItems - the Naviagion menu items.
	 * @param activityName - The name of the activity from which this constructor was first called from.
	 */
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerProfileItem> navDrawerProfileItem,
			ArrayList<NavDrawerItem> navDrawerItems, String activityName){
		this.context 			  = context;
		this.navDrawerProfileItem = navDrawerProfileItem;
		this.navDrawerItems       = navDrawerItems;
		this.activityName         = activityName;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.navslider_list_item , null);
        }
         
		// User information
		ImageView profileBackground = (ImageView) convertView.findViewById(R.id.profileBackground);
		ImageView profilePicture    = (ImageView) convertView.findViewById(R.id.profilePicture);
		TextView profileName        = (TextView) convertView.findViewById(R.id.profileName);
		TextView profileEmail       = (TextView) convertView.findViewById(R.id.profileEmail);
		
		// Nav item
        ImageView navIcon   = (ImageView) convertView.findViewById(R.id.icon);
        TextView navTitle   = (TextView) convertView.findViewById(R.id.title);
        TextView navCounter = (TextView) convertView.findViewById(R.id.counter);
        
        // Set user profile
        if(position == 0)
        {
        	profileBackground.setImageBitmap(navDrawerProfileItem.get(0).getProfileBackgroundImage());
	        profilePicture.setImageBitmap(navDrawerProfileItem.get(0).getProfileImage());
	        profileName.setText(navDrawerProfileItem.get(0).getProfileName());
	        profileEmail.setText(navDrawerProfileItem.get(0).getProfileEmail());
        }
        else
        {
        	if(toggleBackgroundColour == 0)
        	{
        		// toggle the background to meet design
        		convertView.setBackgroundResource(R.drawable.nav_list_selector_blue);
        		
        		// make the profile objects invisible
	        	profileBackground.setVisibility(View.GONE);
	        	profilePicture.setVisibility(View.GONE);
	        	profileName.setVisibility(View.GONE);
	        	profileEmail.setVisibility(View.GONE);
	        	
	            // Set nav function
	            navIcon.setImageResource(navDrawerItems.get(position).getIcon());        
	            navTitle.setText(navDrawerItems.get(position).getTitle());
	            
	            // displaying count
	            // check if visibility is true or false
	            if(navDrawerItems.get(position).getCounterVisibility()){
	            	navCounter.setText(navDrawerItems.get(position).getCount());
	            }
	            else
	            {
	            	// hide the counter view
	            	navCounter.setVisibility(View.GONE);
	            }
	            
	            toggleBackgroundColour += 1;
        	}
        	else
        	{
        		// toggle the background to meet design
        		convertView.setBackgroundResource(R.drawable.nav_list_selector_purple);
        		
        		// make the profile objects invisible
	        	profileBackground.setVisibility(View.GONE);
	        	profilePicture.setVisibility(View.GONE);
	        	profileName.setVisibility(View.GONE);
	        	profileEmail.setVisibility(View.GONE);
	        	
	            // Set nav function
	            navIcon.setImageResource(navDrawerItems.get(position).getIcon());        
	            navTitle.setText(navDrawerItems.get(position).getTitle());
	            
	            // displaying count
	            // check whether it set to visible or not
	            // this will mostly be used for notification purposes
	            // such as "upcoming events"..."messages"...etc
	            if(navDrawerItems.get(position).getCounterVisibility()){
	            	navCounter.setText(navDrawerItems.get(position).getCount());
	            }
	            else
	            {
	            	// hide the counter view
	            	navCounter.setVisibility(View.GONE);
	            }
	            
	            toggleBackgroundColour -= 1;
	            
	        	// finally, set the TypeFace of the TextView that relates to the activity
	        	// from which the navigation slider is used, to bold. To show that we are currently
	        	// on that activity.
	        	if(navDrawerItems.get(position).getTitle().equals(activityName))
	        	{
	        		navTitle.setTypeface(null,Typeface.BOLD);
	        	}
        	}
        	
        }
        return convertView;
	}
}
