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
import propertypanther.operations.OperationUtils;
import propertypanther.slidingmenu.adapter.NavDrawerListAdapter;
import propertypanther.slidingmenu.model.NavDrawerItem;
import propertypanther.slidingmenu.model.NavDrawerProfileItem;
import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.propertypanther.R;

/**
 * 
 * An activity to act as a dash-board for the user;
 * a central point from which they can monitor everything to do
 * with their account.
 * 
 * @author Adam Stevenson
 *
 */
public class UserDashboard extends Activity {

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
	
	// hash map for messages
    private ArrayList<HashMap<String, String>> propertiesList;
    private ArrayList<Hashtable<String, Bitmap>> propertiesListImages;
	private ArrayList<HashMap<String, String>> trackedPropertiesList;
    
    // navigation menu items
	private ArrayList<NavDrawerItem> 		navDrawerItems;
	private ArrayList<NavDrawerProfileItem> navDrawerProfileItem;
	private NavDrawerListAdapter 			navMenuAdapter;
	
	private OperationUtils operationUtils = new OperationUtils();
	
    // Hash maps for messages and payments - in a specific order
    private ArrayList<HashMap<String, String>> userMessages;
	private ArrayList<HashMap<String, String>> userPayments;
    
    // for populating the user details
	private String[] userRetrieved = new String[15];
    
	// JSON Node names for users
	private static final String TAG_USERRETRIEVED         = "userRetrieved";
	
    // JSON Node names for collecting all of a users messages 
	private static final String TAG_MESSAGES           	  = "messages";
	private static final String TAG_MESSAGEBODY           = "message_body";
    private static final String TAG_MESSAGESENT 		  = "message_sent";
    private static final String TAG_MESUSERFORENAME       = "user_forename";
    private static final String TAG_MESUSERSURNAME        = "user_surname";
    
    // JSON Node names for collecting all payments
    private static final String TAG_PAYMENTS              = "payments";
    private static final String TAG_PAYMENTDUE            = "payment_due";
    private static final String TAG_PAYMENTAMOUNT         = "payment_amount";
    private static final String TAG_PAYMENTDIFFERENCEDATE = "payment_diff_date";
    
    // JSON Node names for tracking properties
    private static final String TAG_USERID                = "id";
    
    // define objects
	private TextView txtSignedInUsername;
	private TextView txtSignedInEmail;
	private ImageView imgUserProfile;
	private TextView txtPaymentCost;
	private TextView txtPaymentFirstPayDate;
	private TextView txtPaymentFirstPayDue;
	private TextView txtLatestMessageName;
	private TextView txtLatestMessageDate;
	private TextView txtLatestMessage;
	private TextView txtLatestMessageLabel;
	private TextView txtYourAddress;
    private Button   btnChangeUserDetails;
	private ImageView addressImage;
    private TextView  txtAvgPrice;
    private TextView  txtTrackLocation;
    private TextView  txtTrackAddress;
    private TextView  txtTrackRoomCount;
    private Button    btnMaintenanceRequest;
    
    
    // 'User' JSONArray
    private JSONArray properties = null;
    
    private JSONParser jsonParser = new JSONParser();
    
    private static String url_get_properties = "http://propertypanther.info:8080/PantherAPI/property.jsp";
    private static String url_retrieve_tracked_properties = "http://propertypanther.info:8080/PantherAPI/tracking.jsp";
    
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
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_dashboard);
		
		// allocate layouts to objects
		txtSignedInUsername     = (TextView)  findViewById(R.id.dashboardSignedinUsername);
		txtSignedInEmail        = (TextView)  findViewById(R.id.dashboardSignedinEmail);
		imgUserProfile          = (ImageView) findViewById(R.id.dashboardSignedinUserImage);
		txtPaymentCost          = (TextView)  findViewById(R.id.dashboardNextPaymentCost);
		txtPaymentFirstPayDate  = (TextView)  findViewById(R.id.dashboardFirstNextPayment);
		txtPaymentFirstPayDue   = (TextView)  findViewById(R.id.dashboardFirstDaysLeft);
		txtLatestMessageName    = (TextView)  findViewById(R.id.dashboardLatestMessageName);
		txtLatestMessageDate    = (TextView)  findViewById(R.id.dashboardLatestMessageDate);
		txtLatestMessage        = (TextView)  findViewById(R.id.dashboardLatestMessage);
		txtLatestMessageLabel   = (TextView)  findViewById(R.id.dashboardMessageLabel);
		txtYourAddress          = (TextView)  findViewById(R.id.dashboardYourPropertyName);
		btnChangeUserDetails    = (Button)    findViewById(R.id.dashboardChangeUserDetails);
		addressImage            = (ImageView) findViewById(R.id.dashboardPropertyImage);
		txtAvgPrice             = (TextView)   findViewById(R.id.dashboardAveragePropertyCost);
		txtTrackLocation        = (TextView) findViewById(R.id.dashboardTrackingCity);
		txtTrackAddress         = (TextView) findViewById(R.id.dashboardTrackingAddress);
		txtTrackRoomCount       = (TextView) findViewById(R.id.dashboardTrackingRoomCount);
		btnMaintenanceRequest   = (Button)   findViewById(R.id.dashboardMaintenanceRequest);
		
		// populate user interface with retrieved values and create the navigation menu
		populateUI();
		createNavigationMenu();
		
		// whenever a user clicks on a message panel TextView, send him/her to
		// the messages activity. 
		txtLatestMessageName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(), UserDashboardMessages.class);
       			intent.putExtra(TAG_MESSAGES, userMessages);
				startActivity(intent);
				
			}
		});
		
		txtLatestMessageDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(), UserDashboardMessages.class);
       			intent.putExtra(TAG_MESSAGES, userMessages);
				startActivity(intent);
			}
		});
		
		txtLatestMessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(), UserDashboardMessages.class);
       			intent.putExtra(TAG_MESSAGES, userMessages);
				startActivity(intent);
			}
		});
		
		txtLatestMessageLabel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getApplicationContext(), UserDashboardMessages.class);
       			intent.putExtra(TAG_MESSAGES, userMessages);
				startActivity(intent);
			}
		});
		
		// whenever a user clicks on a payment panel textview, send him/her to 
		// the payments activity
		txtPaymentCost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), UserDashboardPayments.class);
       			intent.putExtra(TAG_PAYMENTS, userPayments);
				startActivity(intent);
			}
		});
		
		txtPaymentFirstPayDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), UserDashboardPayments.class);
       			intent.putExtra(TAG_PAYMENTS, userPayments);
				startActivity(intent);
			}
		});
		
		txtPaymentFirstPayDue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), UserDashboardPayments.class);
       			intent.putExtra(TAG_PAYMENTS, userPayments);
				startActivity(intent);
			}
		});
		
		btnChangeUserDetails.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent detailsIntent = new Intent(getApplicationContext(), UserDashboardChangeDetails.class);
				
				detailsIntent.putExtra("userRetrieved", userRetrieved);
    			detailsIntent.putExtra(TAG_MESSAGES, userMessages);
    			detailsIntent.putExtra(TAG_PAYMENTS, userPayments);
				
    			startActivity(detailsIntent);
    			
			}
		});
		
		txtTrackAddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent trackingIntent = new Intent(getApplicationContext(), UserDashboardTrackedProperties.class);
				
				trackingIntent.putExtra("trackedPropertiesList", trackedPropertiesList);
				
				startActivity(trackingIntent);
			}
		});
		
		txtTrackLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent trackingIntent = new Intent(getApplicationContext(), UserDashboardTrackedProperties.class);
				
				trackingIntent.putExtra("trackedPropertiesList", trackedPropertiesList);
				
				startActivity(trackingIntent);
				
			}
		});
		
		txtTrackRoomCount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent trackingIntent = new Intent(getApplicationContext(), UserDashboardTrackedProperties.class);
				
				trackingIntent.putExtra("trackedPropertiesList", trackedPropertiesList);
				
				startActivity(trackingIntent);
				
			}
		});
		
		btnMaintenanceRequest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent maintenanceIntent = new Intent(getApplicationContext(), UserDashboardMaintenanceRequest.class);
				
				maintenanceIntent.putExtra("userRetrieved", userRetrieved);
				
				startActivity(maintenanceIntent);
			}
		});
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@SuppressWarnings("unchecked")
	private void populateUI()
	{
		// Hashmap for collecting user messages
		userMessages = new ArrayList<HashMap<String, String>>();
		userPayments = new ArrayList<HashMap<String, String>>();
		propertiesList = new ArrayList<HashMap<String, String>>();
	    propertiesListImages = new ArrayList<Hashtable<String, Bitmap>>();
		trackedPropertiesList = new ArrayList<HashMap<String, String>>();
	    
 		Intent intent     = getIntent();
 		
 		if(intent.getStringArrayExtra(TAG_USERRETRIEVED) != null)
 		{
 		userRetrieved     = intent.getStringArrayExtra(TAG_USERRETRIEVED);
 		
	 		if(userRetrieved[0].equals("true") && userRetrieved !=null)
	 		{
		 		// retrieve the messages...unfortunately this requires a check, which would most probably
		 		// result in a slow down if a method was made to actually go through all the throws/assertions
	 			// and everything else, therefore it seems easier/better overall to use a 'suppressWarnings' tag
				userMessages = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_MESSAGES);
				userPayments = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_PAYMENTS);
		 		
				imgUserProfile.setImageBitmap(
					    operationUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.guest , 100, 100));
				
				txtSignedInUsername.setText(userRetrieved[10] + " " + userRetrieved[4]);
				txtSignedInEmail.setText(userRetrieved[9]);
				
				// display the information for the first payment
				txtPaymentCost.setText("£ " + userPayments.iterator().next().get(TAG_PAYMENTAMOUNT));
				txtPaymentFirstPayDate.setText(userPayments.iterator().next().get(TAG_PAYMENTDUE));
				txtPaymentFirstPayDue.setText(userPayments.iterator().next().get(TAG_PAYMENTDIFFERENCEDATE) + " Days time");
				
				// display the information for the first message
				txtLatestMessageDate.setText("( " + userMessages.iterator().next().get(TAG_MESSAGESENT).toString() +" )");
				txtLatestMessage.setText(userMessages.iterator().next().get(TAG_MESSAGEBODY).toString());
				txtLatestMessageName.setText(userMessages.iterator().next().get(TAG_MESUSERFORENAME) + " " +
										     userMessages.iterator().next().get(TAG_MESUSERSURNAME));
				
				txtYourAddress.setText(userRetrieved[5]);
				
				new LoadAllProperties().execute();
				new LoadTrackedProperties().execute();
	 		}
 		}
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
		
		// load slide menu navigation bar
		// the profile of the user: 
		profileBackgroundImage = BitmapFactory.decodeResource(getResources(),R.drawable.nav_profile_bg);
		profileImage 		   = BitmapFactory.decodeResource(getResources(),R.drawable.guest);
		profileName 		   = userRetrieved[10] + " " + userRetrieved[4];
		profileEmail 		   = userRetrieved[9];
		
		// Name of the navigation function followed by the icon
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons  = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		
		// create the layout and ListView for the navigation bar
		navDrawerLayout = (DrawerLayout) findViewById(R.id.dashb_nav_menu_drawer_layout);
		navDrawerList   = (ListView) findViewById(R.id.dashb_list_slidermenu);
		
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
		// Messages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Payments
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(4, -1)));
		// Log off
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(5, -1)));
		
		// Recycle typed array 'navmenuicons'
		navMenuIcons.recycle();
		
		// setting the nav drawer list adapter
		navMenuAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerProfileItem,
				navDrawerItems, "Properties");
		navDrawerList.setAdapter(navMenuAdapter);
		
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		navDrawerToggle = new ActionBarDrawerToggle(this, navDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open 
				R.string.app_name) {
			
			public void onDrawerClosed(View view) {
				// send this view to the back
				navDrawerLayout.bringChildToFront(getWindow().getDecorView());
				navDrawerLayout.closeDrawers();
				getWindow().getDecorView().bringToFront();
				
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
				else if(selectedOptionTitle.equals("Search Properties"))
				{
					Intent properties = new Intent(getApplicationContext(), ListPropertiesSearch.class);
					properties.putExtra("userRetrieved", userRetrieved);
					properties.putExtra(TAG_MESSAGES, userMessages);
					properties.putExtra(TAG_PAYMENTS, userPayments);
					startActivity(properties);
				}
				else if(selectedOptionTitle.equals("Messages"))
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
				else if(selectedOptionTitle.equals("Log off"))
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

			params.add(new BasicNameValuePair("id", userRetrieved[14]));
			
			try {
				// Implement success variable in script, in order to
				// allow the if statement to succeed; if the user does not select an 
				// option that has a valid amount of properties, then they will be
				// unable to see anything; they will be redirected.

				// Initialise the JSONObject to be used for UI purposes
				JSONObject json = jsonParser.makeHttpRequest(url_get_properties, "GET", params);
				
				Log.d("properties to string", json.toString());
				
				if(json!=null)
				{
		                // Getting Array of Properties
		                properties = json.getJSONArray(TAG_PROPERTIES);
					
		                
		                if(properties.getJSONObject(0) != null && properties.getJSONObject(0).has("property_id"))
		                {
				            	// looping through All Properties
				                for (int i = 0; i < properties.length(); i++) {
				                    JSONObject c = properties.getJSONObject(i);
				                    
				                    // Storing each json item in variable
				                    String pAvgCost = c.getString(TAG_PAVGCOST);
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
					                   
					           		   propertyImage = operationUtils.getResizedBitmap(propertyImage, 720, 300);
					                   
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

			if (properties!=null)
            {
				txtYourAddress.setText(propertiesList.iterator().next().get(TAG_PADDRESS));
				addressImage.setImageBitmap(propertiesListImages.iterator().next().get(TAG_PIMG));
	            txtAvgPrice.setText("£ " + userPayments.iterator().next().get(TAG_PAYMENTAMOUNT));
			}	
		}
	}
	
	/**
	 * Class used to retrieve tracked property information from a script
	 */
	class LoadTrackedProperties extends AsyncTask<String, Boolean, String> {

	    /**
	     * Before starting background thread Show Progress Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	       
	    }
		
		@Override
		protected String doInBackground(String... args) {
			
			if(trackedPropertiesList !=null) trackedPropertiesList.clear();
			
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_USERID, userRetrieved[8]));
			
			JSONObject json = null;
			
			json = jsonParser.makeHttpRequest(url_retrieve_tracked_properties, "GET", params);
			
			Log.d("Tracked properties retrieved", json.toString());
			
			try{
				// Getting Array of Properties
	            properties = json.getJSONArray(TAG_PROPERTIES);

	            
	            
	            if(properties.getJSONObject(0).has(TAG_PID))
		        {
		                Log.d("list of properties", properties.toString());
		                
		                
			            // looping through All Properties
			            for (int i = 0; i < properties.length(); i++) {
			                JSONObject c = properties.getJSONObject(i);
			                    
			                // Storing each json item in variable
			                //String pAvgCost = c.getString(TAG_PAVGCOST);
				            String pAddress = c.getString(TAG_PADDRESS);
				            String pCity = c.getString(TAG_PCITY);
				            String pPostcode = c.getString(TAG_PPOSTCODE);
				            String pDescription = c.getString(TAG_PDESCRIPTION);
				            String pId = c.getString(TAG_PID);
				            String pStatus = c.getString(TAG_PSTATUS);
				            String pNumRooms = c.getString(TAG_NUMROOMS);
				            String pDistrict = c.getString(TAG_DISTRICT);
				            String pTrackCode = c.getString(TAG_TRACK_CODE);
				                    
				            // creating new HashMaps; one for images, one for property information
				            HashMap<String, String> infoMap  = new HashMap<String, String>();
		
				            // adding each child node to prop info HashMap key => value
				            //infoMap.put(TAG_PAVGCOST, pAvgCost);
				            infoMap.put(TAG_PADDRESS, pAddress);
			             	infoMap.put(TAG_PCITY, pCity);
				            infoMap.put(TAG_PPOSTCODE, pPostcode);
				            infoMap.put(TAG_PDESCRIPTION, pDescription);
				            infoMap.put(TAG_PID, pId);
				            infoMap.put(TAG_PSTATUS, pStatus);
				            infoMap.put(TAG_NUMROOMS, pNumRooms);
				            infoMap.put(TAG_DISTRICT, pDistrict);
				            infoMap.put(TAG_TRACK_CODE, pTrackCode);
				                    
				            // adding propInfo HashList to ArrayList
				            trackedPropertiesList.add(infoMap);
			            }
		        }
	        } catch (JSONException e) {
	                	e.printStackTrace();
	        }
           
			return null;
		}
		
		/*
		 * After completing background task, dismiss the progress dialog
		 * and update the UI - needs to be done when the Middleware is written
		 */
		protected void onPostExecute (String file_url){
				
			if(trackedPropertiesList.isEmpty() || trackedPropertiesList.size() == 0){}
			else {
				txtTrackLocation.setText(trackedPropertiesList.iterator().next().get("city_name"));
				txtTrackAddress.setText(trackedPropertiesList.iterator().next().get("addr_line_1"));
				txtTrackRoomCount.setText("Rooms: " + trackedPropertiesList.iterator().next().get("prop_num_rooms"));
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// toggle nav drawer on selecting action bar app icon/title
		if (navDrawerToggle.onOptionsItemSelected(item)) return true;
		
		// Handle action bar actions click
		switch (item.getItemId()) {
		
			default:return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_dashboard, menu);
		return true;
	}
	
	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

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
	
	@Override
	protected void onResume() {
		super.onResume();
		
		new LoadAllProperties().execute();
		new LoadTrackedProperties().execute();
		
	}
}
