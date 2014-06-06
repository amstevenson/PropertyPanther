package propertypanther.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import propertypanther.operations.JSONParser;
import propertypanther.operations.NetworkFunctions;
import propertypanther.propertylist.adapters.PropertyListAdapter;
import propertypanther.slidingmenu.adapter.NavDrawerListAdapter;
import propertypanther.slidingmenu.model.NavDrawerItem;
import propertypanther.slidingmenu.model.NavDrawerProfileItem;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.propertypanther.R;

/**
 * This activity is concerned with displaying property results based on a
 * "location" string that has been defined, populated and sent across.
 * Extra functionality for this activity includes a Navigation Menu and
 * an ActionBar item that allows a search to be refined.
 * 
 * @author Adam Stevenson
 */
public class ListPropertiesActivity extends Activity {

	// Nav menu variables
	private DrawerLayout 		  navDrawerLayout;
	private ListView 			  navDrawerList;
	private ActionBarDrawerToggle navDrawerToggle;
	
	// nav drawer title
	private CharSequence 		  navDrawerTitle;
	
	// used to store app title
	private CharSequence 		  navTitle;
	
	// slide menu items
	private Bitmap 				  profileBackgroundImage;
	private Bitmap 				  profileImage;
	private String 				  profileName;
	private String 				  profileEmail;
	private String[] 			  navMenuTitles;
	private TypedArray 			  navMenuIcons;
	
	// User information for navigation menus
	private String[]              userRetrieved = new String[14];
	
	private ArrayList<NavDrawerItem> 		navDrawerItems;
	private ArrayList<NavDrawerProfileItem> navDrawerProfileItem;
	private NavDrawerListAdapter 			adapter;
	private PropertyListAdapter             propListAdapter;
	
    // Hash maps for messages and payments - in a specific order
    private ArrayList<HashMap<String, String>> userMessages;
	private ArrayList<HashMap<String, String>> userPayments;
	
	// tags for HashMaps
	private static final String TAG_MESSAGES = "messages";
	private static final String TAG_PAYMENTS = "payments";
	
	// Search parameters
	private String 	location;
	private float   minPrice = 200;
	private float   maxPrice = 600;
	
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Setup JSON Parser class
    private JSONParser jsonParser = new JSONParser();
    
    // Setup NetworkFunctions class
    private NetworkFunctions netFunctions = new NetworkFunctions();

    // JSON Node names
    private static final String TAG_PROPERTIES   = "properties";
    private static final String TAG_PAVGCOST 	 = "average_price";
    private static final String TAG_PADDRESS     = "addr_line_1";
    private static final String TAG_PCITY        = "city_name";
    private static final String TAG_PPOSTCODE    = "addr_postcode";
    private static final String TAG_PDESCRIPTION = "prop_details";
    private static final String TAG_PID          = "property_id";
    private static final String TAG_PSTATUS      = "prop_status";
    private static final String TAG_TRACK_CODE   = "prop_track_code";
    private static final String TAG_DISTRICT     = "addr_district";
    private static final String TAG_NUMROOMS     = "prop_num_rooms";
    private static final String TAG_PIMG         = "prop_image_link";
    
    // Properties JSONArray
    private JSONArray properties = null;
   
    private ArrayList<HashMap<String, String>> propertiesList;
    private ArrayList<Hashtable<String, Bitmap>> propertiesListImages;
    
    // Middleware link
    private static String url_get_properties = "http://propertypanther.info:8080/PantherAPI/property.jsp";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_properties);
		
		// get the location entered from the previous activity (main).
		Intent intent = getIntent();
		location      = intent.getStringExtra("mainActLocation");
		
		// determine if we have any user information
		retrieveUserInformation();
		
		// Create and setup the navigation Menu that will allow the user to go back
		// and forth between different activities.
		createNavigationMenu();
		
		// Hashmap for the ListView
		propertiesList = new ArrayList<HashMap<String, String>>();
		propertiesListImages = new ArrayList<Hashtable<String, Bitmap>>();
		
		if(netFunctions.isNetConnected(this))
		{
			// get all of the properties
			if(URLUtil.isValidUrl(url_get_properties)) new LoadAllProperties().execute();
		}
		
		ListView lv = (ListView) findViewById(R.id.propList);
		
        // on selecting single product, send the user
		// to the screen that deals with listing more
		// detailed information
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
               
            	if (properties !=null)
	            	{
	            	try {
	            		// we need to ensure that we get the correct property.
	            		// if the user does multiple searches, the order of the ID's
	            		// that have been allocated by the adapter, may conflict with the
	            		// position of the ListView's OnClickListener, therefore for extra validation
	            		// we need to ensure that we get the correct property.
	            		
	            		// If we simply used .remove on the JSONArray index 0 each time a new
	            		// search was conducted, this would not be required, but that method
	            		// uses an API stipulation of 19.
	            		
	            		// we do not want a final ListView outside the scope of this listener,
	            		// so we need a new ListView parameter
	            		ListView chosenPropertyIndex = (ListView) findViewById(R.id.propList);
	            		
	            		String objectToString = chosenPropertyIndex.getItemAtPosition(position).toString();
	            		
	            		int selectionIndex = 0;
	            		
	            		try
	            		{
	            			// for each property compare against the selected ListView object.
	            			// if they match we have our correct index.
		            		for(int i = 0; i < properties.length(); i ++)
		            		{
		            			
			            		String chosenProperty = properties.getJSONObject(i).get(TAG_PID).toString();
		            			
		            			if(objectToString.contains("property_id=" + chosenProperty)) selectionIndex = i;
		            		}
	            		} catch (IndexOutOfBoundsException e) {
	            			e.printStackTrace();
	            		}
	            		
		                // create a json object that retrieves the specific property
		                JSONObject jsonSelected = properties.getJSONObject(selectionIndex);
		                String[] selectedProperty = new String[9];
		                selectedProperty[0] = jsonSelected.getString(TAG_PID);
		                selectedProperty[1] = jsonSelected.getString(TAG_PADDRESS);
		                selectedProperty[3] = jsonSelected.getString(TAG_PAVGCOST);
		                selectedProperty[4] = jsonSelected.getString(TAG_NUMROOMS);
		                selectedProperty[5] = jsonSelected.getString(TAG_PDESCRIPTION);
		                selectedProperty[6] = jsonSelected.getString(TAG_TRACK_CODE);
		                selectedProperty[7] = jsonSelected.getString(TAG_DISTRICT);
		                selectedProperty[8] = jsonSelected.getString(TAG_PPOSTCODE);
		                
		                // Starting new intent
		                Intent in = new Intent(getApplicationContext(),
		                        PropertyMain.class);
		                // sending selected property information to next activity
		                in.putExtra("selectedProperty", selectedProperty);
		                in.putExtra("userRetrieved", userRetrieved);
		                
		                // starting new activity and expecting some response back
		                startActivity(in);
	            	}
	            	catch (JSONException e){
	            		e.printStackTrace();
	            	}
            	}
            }
        });
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// toggle nav drawer on selecting action bar app icon/title
		if (navDrawerToggle.onOptionsItemSelected(item)) return true;
		
		// Handle action bar actions click
		switch (item.getItemId()) {
		
		case R.id.refSearch:
			
			Intent intent = new Intent (getApplicationContext(), ListPropertiesSearch.class);
			
			intent.putExtra("location", location);
			
			startActivity(intent);
			
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
	
	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = navDrawerLayout.isDrawerOpen(navDrawerList);
		
		menu.findItem(R.id.refSearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		navTitle = title;
		getActionBar().setTitle(navTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		// before onRestore state, sync the toggle
		navDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Any changes to configurations needs to be registered
		navDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	/**
	 * 
	 * Using a custom made adapter - in conjunction with classes that allow us to
	 * create either navigation or profile items - this method creates a menu that
	 * allows for flexible movement between activities, in addition to allowing the
	 * user to log out and check if they are online.
	 * 
	 * @param NavDrawerProfileItem - an array list that all of the personal information
	 * that relates to the user, as long as they are logged in.
	 * @param NavDrawerItems - the items that allow the user to navigate between forms.
	 * 
	 * @see NavDrawerListAdapter - Custom made adapter. It was made to ensure that the Navigation
	 * Menu could be created to meet the requirements - and look like - the design.
	 * 
	 * @see NavDrawerItem - Data Model class for making a new Navigation Item - For Example: Home, Logout.
	 * 
	 * @see NavDrawerProfileItem - Data Model class for making a new profile item - For Example: Adam Stevenson,
	 * adam.m.stevenson@students.plymouth.ac.uk.
	 * 
	 */
	public void createNavigationMenu(){
		
		navTitle= navDrawerTitle = getTitle();
		
		// -- Setup and configure the navigation bar --
		// Name of the navigation function followed by the icon
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons  = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		
		// create the layout and ListView for the navigation bar
		navDrawerLayout = (DrawerLayout) findViewById(R.id.nav_menu_drawer_layout);
		navDrawerList   = (ListView) findViewById(R.id.list_slidermenu);
		
		// if a user is logged in
		if(userRetrieved != null){
			// authenticate
			if(userRetrieved[0].equals("true"))
			{
				// load slide menu navigation bar
				// the profile of the user: 
				profileBackgroundImage = BitmapFactory.decodeResource(getResources(),R.drawable.nav_profile_bg);
				profileImage 		   = BitmapFactory.decodeResource(getResources(),R.drawable.guest);
				profileName 		   = userRetrieved[10] + " " + userRetrieved[4];
				profileEmail 		   = userRetrieved[9];
				
				// navigation user profile
				navDrawerProfileItem = new ArrayList<NavDrawerProfileItem>();
				navDrawerProfileItem.add(new NavDrawerProfileItem(profileBackgroundImage, profileImage, profileName, profileEmail));
				
				// navigation function items
				navDrawerItems = new ArrayList<NavDrawerItem>();
				// Empty space container (else the first item will stack on top of the user profile)
				navDrawerItems.add(new NavDrawerItem());
				// Home
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
				// Properties
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
				// My Dashboard
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
				// Messages
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
				// Payments
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(4, -1)));
				// Log off
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(5, -1)));
			}
		}
		// if we are not logged in, only display guest icons and name
		else{
			
			// load slide menu navigation bar
			// the profile of the user: 
			profileBackgroundImage = BitmapFactory.decodeResource(getResources(),R.drawable.nav_profile_bg);
			profileImage 		   = BitmapFactory.decodeResource(getResources(),R.drawable.guest);
			profileName 		   = "Guest";
			profileEmail 		   = "GuestUser@guest.com";
			
			// navigation user profile
			navDrawerProfileItem = new ArrayList<NavDrawerProfileItem>();
			navDrawerProfileItem.add(new NavDrawerProfileItem(profileBackgroundImage, profileImage, profileName, profileEmail));
			
			// navigation function items
			navDrawerItems = new ArrayList<NavDrawerItem>();
			// Empty space container (else the first item will stack on top of the user profile)
			navDrawerItems.add(new NavDrawerItem());
			// Home
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
			// Properties
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		}
		
		// Recycle typed array 'navmenuicons'
		navMenuIcons.recycle();
		
		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerProfileItem,
				navDrawerItems, "Properties");
		navDrawerList.setAdapter(adapter);
		
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		navDrawerToggle = new ActionBarDrawerToggle(this, navDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open 
				R.string.app_name) {
			
			public void onDrawerClosed(View view) {
				// send this view to the back
				navDrawerList.bringToFront();
				
				getActionBar().setTitle(navTitle);
				
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView) {
				// bring this view to the front
				navDrawerLayout.bringToFront();
				
				getActionBar().setTitle(navDrawerTitle);
				
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		
		navDrawerLayout.setDrawerListener(navDrawerToggle);
		
		// now that the navigation bar has been created and configured,
		// we can set a listener for the listview that gets populated by
		// parameters, in order to determine where to send the user upon
		// clicking on an item.
		navDrawerList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				NavDrawerItem selectedOption;
				String 		  selectedOptionTitle;
				
				// determine the title of the selected option
				// which will be used to navigate the user to a different location
				selectedOption      = navDrawerItems.get(position);
				selectedOptionTitle = selectedOption.getTitle();

				
				if(selectedOptionTitle.equals("Home"))
				{
					TextView selectedOptionTextView;
					selectedOptionTextView = (TextView) findViewById(R.id.title);
					
					selectedOptionTextView.setTypeface(null,Typeface.BOLD);
					
	                // Starting new intent
	                Intent home = new Intent(getApplicationContext(),
	                        MainActivity.class);
	                
	                startActivity(home);
				}
				else if (selectedOptionTitle.equals("Search Properties"))
				{

					Intent intent = new Intent (getApplicationContext(), ListPropertiesSearch.class);
					
					intent.putExtra("location", location);
					if(userRetrieved != null)
					{
						intent.putExtra("userRetrieved", userRetrieved);
						intent.putExtra("messages", userMessages);
						intent.putExtra("payments", userPayments);
					}
					
					startActivity(intent);
				}
				else if (selectedOptionTitle.equals("My Dashboard"))
				{

					Intent dashboard = new Intent (getApplicationContext(), UserDashboard.class);
					
					if(userRetrieved != null)
					{
						dashboard.putExtra("userRetrieved", userRetrieved);
						dashboard.putExtra("messages", userMessages);
						dashboard.putExtra("payments", userPayments);
					}
					
					startActivity(dashboard);
				}
				else if (selectedOptionTitle.equals("Messages"))
				{

					Intent messages = new Intent(getApplicationContext(), UserDashboardMessages.class);
	       			messages.putExtra(TAG_MESSAGES, userMessages);
					startActivity(messages);
				}
				else if(selectedOptionTitle.equals("Payments"))
				{
					Intent payments = new Intent(getApplicationContext(), UserDashboardPayments.class);
	       			payments.putExtra(TAG_PAYMENTS, userPayments);
					startActivity(payments);
					
				}
				else if (selectedOptionTitle.equals("Log off"))
				{

					Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);

					for(int i = 0; i < userRetrieved.length; i++) userRetrieved[i] = null;
					userMessages.clear();
					userPayments.clear();
					
					startActivity(mainAct);
					
					finish();
				}
			}
		});
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_properties, menu);
		
		return true;
	}
	
	/**
	 * Class used to retrieve property information from a Middleware script
	 * and populate the UI using an ArrayList of HashMaps.
	 * It is responsible for collecting all information about properties, from
	 * a given location.
	 * 
	 */
	class LoadAllProperties extends AsyncTask<String, Boolean, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           
    		// if the location is null, it means the activity has been called from 
    		// ListPropertiesSearch activity, so we have to get the string extras and allocate
            // if we do not do this, we get a leaked window error
    		if(location == null)
    		{
    			String[] propSearchParameters = new String[3];
    			
    			Intent searchIntent = getIntent();
    			
    			propSearchParameters = searchIntent.getStringArrayExtra("propSearchParameters");
    			
    			location = propSearchParameters[0];
    			minPrice = Float.valueOf(propSearchParameters[1]);
    			maxPrice = Float.valueOf(propSearchParameters[2]);
    			
    			Log.d("location: ", location);
    			Log.d("min price: ", String.valueOf(minPrice));
    			Log.d("max price: ", String.valueOf(maxPrice));
    			
    		}
            
            pDialog = new ProgressDialog(ListPropertiesActivity.this);
            pDialog.setMessage("Loading properties for " + location+". Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// In order to not simply add the same properties to the end of the ListView
			// each time this class is used, we need to clear the mappings of each 
			// property contained in the ArrayList propertiesList and also remove the
			// ListView objects associated with the previous search. 
			// Note: the "remove" method of the JSONArray properties, would do all of this simply,
			// but we need API 19 for that. 
			if (propertiesList.size() > 0)
			{
				try
				{
					// Removal of HashMaps from ArrayList
					for(int i = 0; i < propertiesList.size(); i ++) propertiesList.remove(i);
					propertiesList.remove(0);
					
				} catch (IndexOutOfBoundsException e){
					e.printStackTrace();
				}
			}
				
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			location = location.toUpperCase();
			params.add(new BasicNameValuePair("location", location));
			
			try {
				// Implement success variable in script, in order to
				// allow the if statement to succeed; if the user does not select an 
				// option that has a valid amount of properties, then they will be
				// unable to see anything; they will be redirected.

				// Initialise the JSONObject to be used for UI purposes
				JSONObject json = jsonParser.makeHttpRequest(url_get_properties, "GET", params);
				
				if(json!=null)
				{
	                // Getting Array of Properties
	                properties = json.getJSONArray(TAG_PROPERTIES);
	 
	                Log.d("list of properties", properties.toString());
	                
		            	// looping through All Properties
		                for (int i = 0; i < properties.length(); i++) {
		                    JSONObject c = properties.getJSONObject(i);
		                    
		                    // Storing each json item in variable
		                    String pAvgCost = c.getString(TAG_PAVGCOST);
		                    
		                    // So far, there is a search method to define what
		                    // properties are selected; using location, min/max 
		                    // cost variables. Therefore we only want locations to
		                    // be selected if they are within these cost constraints.
		                    // (this is done here to simply reuse this class later on)
		                    // The default is to get a typical students search on the first
		                    // load. I.e > 200 and < 600
		                    if((Float.parseFloat(pAvgCost.toString()) > minPrice &&
		                    		Float.parseFloat(pAvgCost.toString()) < maxPrice) ||
		                    		Float.parseFloat(pAvgCost.toString()) == minPrice ||
		                    		Float.parseFloat(pAvgCost.toString()) == maxPrice)
		                    {
			                    String pAddress = c.getString(TAG_PADDRESS);
			                    String pCity = c.getString(TAG_PCITY);
			                    String pPostcode = c.getString(TAG_PPOSTCODE);
			                    String pDescription = c.getString(TAG_PDESCRIPTION);
			                    String pId = c.getString(TAG_PID);
			                    String pStatus = c.getString(TAG_PSTATUS);
			                    String pNumRooms = c.getString(TAG_NUMROOMS);
			                    String pDistrict = c.getString(TAG_DISTRICT);
			                    String pTrackCode = c.getString(TAG_TRACK_CODE);
			                    String pImg       = "http://dev.propertypanther.info/images/properties/";
			                    Bitmap propertyImage;
			                    
			                    // For each property, allocate an image
			                    // default image is not found
			                    Drawable imageNotFound = getResources().getDrawable(R.drawable.image_not_found);
		                    	propertyImage = ((BitmapDrawable) imageNotFound).getBitmap();
			                    
			                    URL url = new URL(pImg + pId + ".jpg");
			                    
			                    // if we have a connection, get an image, otherwise it has not been found
			                    try{
			                    if(url.openConnection().getInputStream() != null)
			                    	propertyImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			                    } catch (IOException e){
			                    	e.printStackTrace();
			                    }
			                    
			                    // creating new HashMaps; one for images, one for property information
			                    HashMap<String, String> infoMap  = new HashMap<String, String>();
			                    Hashtable<String, Bitmap> imageMap = new Hashtable<String, Bitmap>(); 
			                    
			                    // adding each child node to prop info HashMap key => value
			                    infoMap.put(TAG_PAVGCOST, pAvgCost);
			                    infoMap.put(TAG_PADDRESS, pAddress);
			                    infoMap.put(TAG_PCITY, pCity);
			                    infoMap.put(TAG_PPOSTCODE, pPostcode);
			                    infoMap.put(TAG_PDESCRIPTION, pDescription);
			                    infoMap.put(TAG_PID, pId);
			                    infoMap.put(TAG_PSTATUS, pStatus);
			                    infoMap.put(TAG_NUMROOMS, pNumRooms);
			                    infoMap.put(TAG_DISTRICT, pDistrict);
			                    infoMap.put(TAG_TRACK_CODE, pTrackCode);
			                    infoMap.put(TAG_PIMG, pImg);
			                    
			                    // adding propInfo HashList to ArrayList
			                   propertiesList.add(infoMap);
			                   
			                   // adding each child node to prop image Hashmap key => value
			                   imageMap.put(TAG_PIMG, propertyImage);
			                   
			                   // adding property image HashList to ArrayList
			                   propertiesListImages.add(imageMap);
			                   
		                    }
	                }
				}
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
            	e.printStackTrace();
            } catch (MalformedURLException e) {
				e.printStackTrace();
			} 
			return null;
		}
		
		/*
		 * After completing background task, dismiss the progress dialog
		 * and update the UI - needs to be done when the middleware is written
		 */
		protected void onPostExecute (String file_url){

			// dismiss the dialog after getting all properties
            pDialog.dismiss();
            
			if (properties!=null)
            {
				
	            // updating UI from Background Thread
	            runOnUiThread(new Runnable() {
	            	public void run() {
	            		
	            		// setting the messages list adapter
	            		ListView lvProperties = (ListView) findViewById(R.id.propList);
	            		propListAdapter = new PropertyListAdapter(getApplicationContext(), propertiesList, propertiesListImages);
	            		lvProperties.setAdapter(propListAdapter);
	            	}
	            });
			}	
		}
	}
	
    @Override
    public void onPause(){

        super.onPause();
        if(pDialog != null)
            pDialog.dismiss();
    }
    
    @Override
    public void onResume(){
    	
    	super.onResume();
    	
    	// To populate the navigation menu, we need user information
    	// So by collecting them via intents, we can do this.
    	retrieveUserInformation();
    }
    
    /**
     * 
     * This method is called when the activity is made, or resumed; i.e
     * another activity references this one in an intent and starts it.
     * Any intents that are received, update the already existing (or newly
     * created) arrays / HashMaps.
     * 
     * @param userRetrieved The user carried across to this activity. Determines
     * if the user is logged in or not and authenticated; if so then the navigation
     * menu becomes different - more options are unlocked.
     * 
     * @param userMessages If the user clicks on the 'messages' option in the navigation menu
     * we can carry across this users information to populate that section of the dashboard
     * 
     * @param  If the user clicks on the 'payments' option in the navigation menu
     * we can carry across this users information to populate that section of the dashboard
     * 
     */
    @SuppressWarnings("unchecked")
	public void retrieveUserInformation(){
    	
    	Intent retrieveUserInfo = getIntent();
    	
		if(retrieveUserInfo.getStringArrayExtra("userRetrieved") != null)
			userRetrieved = retrieveUserInfo.getStringArrayExtra("userRetrieved");
		else userRetrieved = null;
		
		// if the user is authenticated, collect both the messages and payments
		if(userRetrieved!= null){
			if(userRetrieved[0].equals("true"))
			{
				if(retrieveUserInfo.getSerializableExtra(TAG_MESSAGES) != null)
					userMessages = (ArrayList<HashMap<String, String>>) retrieveUserInfo.getSerializableExtra(TAG_MESSAGES);
				else userMessages = null;
					
				Log.d("user messages on list properties side", userMessages.toString());
				
				if(retrieveUserInfo.getSerializableExtra(TAG_PAYMENTS) != null)
					userPayments = (ArrayList<HashMap<String, String>>) retrieveUserInfo.getSerializableExtra(TAG_PAYMENTS);
				else userPayments = null;
				
			}
		}
    }
}


