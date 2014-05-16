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
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.propertypanther.R;

/**
 * Displays fragments relating to the property that has been 
 * click on in the activity ListPropertiesActivity. 
 * 
 * @see PropertyDetailsFragment
 * @see PropertyRoomsFragment
 * 
 * @author Adam Stevenson
 * 
 */
public class PropertyMain extends Activity {

	private String[] retrievedProperty;
	private String[] userRetrieved;
	
    // hash map for messages
    private ArrayList<HashMap<String, String>> propertyRooms;
    private ArrayList<Hashtable<String, Bitmap>> propertyRoomsImages;
    
    // Progress Dialog
    private ProgressDialog pDialog;
    
    // 'User' JSONArray
    private JSONArray rooms = null;
    
    private JSONParser jsonParser = new JSONParser();
	
    private static final String url_get_rooms = "http://propertypanther.info:8080/PantherAPI/room.jsp";
    
    // JSON Node names for collecting all of a properties rooms
	private static final String TAG_ROOMS          	= "rooms";
	private static final String TAG_ROOMSTATUS      = "room_status";
    private static final String TAG_PROPID 			= "property_id";
    private static final String TAG_ROOMDETAILS     = "room_details";
    private static final String TAG_ROOMID    		= "room_id";
    private static final String TAG_ROOMPRICE  		= "room_price";
    private static final String TAG_ROOMIMAGE  		= "room_image";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selected_property);
		setTitle("Results");
 		
		// HashMap for collecting user messages and payments
		propertyRooms = new ArrayList<HashMap<String, String>>();
		propertyRoomsImages = new ArrayList<Hashtable<String, Bitmap>>();
		
 		Intent intent     = getIntent();
 		retrievedProperty = intent.getStringArrayExtra("selectedProperty");
 		userRetrieved     = intent.getStringArrayExtra("userRetrieved");
 		
		// retrieve the information for each room for the property
		new LoadAllRooms().execute();
 		
		//ActionBar
		ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
		ActionBar.Tab property = actionbar.newTab().setText(getString(R.string.nav_property));
		ActionBar.Tab gallery  = actionbar.newTab().setText(getString(R.string.nav_rooms));
			
		// create the fragments and assign necessary data
		Bundle propertyBundle = new Bundle();
		propertyBundle.putStringArray("retrievedProperty", retrievedProperty);
		propertyBundle.putStringArray("userRetrieved", userRetrieved);
		propertyBundle.putSerializable("propertyRooms", propertyRooms);
		propertyBundle.putSerializable("propertyRoomsImages", propertyRoomsImages);
		
		Fragment thisProperty = new PropertyDetailsFragment();
		thisProperty.setArguments(propertyBundle);
		
		Fragment thisPropertyRooms = new PropertyRoomsFragment();
		thisPropertyRooms.setArguments(propertyBundle);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fragment_container, thisProperty);
		ft.commit();
		
		FragmentTransaction ft2 = getFragmentManager().beginTransaction();
		ft2.add(R.id.fragment_container, thisPropertyRooms);
		ft2.commit();
		
		// TabListener and binding for each fragment
		property.setTabListener(new MyTabsListener(thisProperty,
				getApplicationContext()));
		gallery.setTabListener(new MyTabsListener(thisPropertyRooms,
				getApplicationContext()));
		
		// add the tabs to the actionbar
		actionbar.addTab(property);
		actionbar.addTab(gallery);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.property_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        
	    	finish();
	    	
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	/**
	 * Class used to retrieve property information from a Middleware script
	 * and populate the UI using an ArrayList of HashMaps
	 */
	class LoadAllRooms extends AsyncTask<String, Boolean, String> {

	    /**
	     * Before starting background thread Show Progress Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	       
	        pDialog = new ProgressDialog(PropertyMain.this);
	        pDialog.setMessage("Loading rooms for the address: " + retrievedProperty[1] + ". Please be patient");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
	    }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// if this script is used more than once, and there is already a populated HashMap, clean it.
			// Note: the "remove" method of the JSONArray properties, would do all of this simply,
			// but we need API 19 for that. 
			if (propertyRooms.size() > 0)
			{
				try
				{
					// Removal of HashMaps from ArrayList
					for(int i = 0; i < propertyRooms.size(); i ++) propertyRooms.remove(i);
					propertyRooms.remove(0);
					
				} catch (IndexOutOfBoundsException e){
					e.printStackTrace();
				}
			}
				
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", retrievedProperty[0].toString()));
			
			try {
				// Implement success variable in script, in order to
				// allow the if statement to succeed; if the user does not select an 
				// option that has a valid amount of properties, then they will be
				// unable to see anything; they will be redirected.

				// Initialise the JSONObject to be used for UI purposes
				JSONObject json = jsonParser.makeHttpRequest(url_get_rooms, "GET", params);
				
				if(json!=null)
				{
	                // Getting Array of Properties
	                rooms = json.getJSONArray(TAG_ROOMS);
	 
	                Log.d("rooms", rooms.toString());
	                
		            	// looping through All Properties
		                for (int i = 0; i < rooms.length(); i++) {
		                    JSONObject c = rooms.getJSONObject(i);
		                    
		                    // Storing each json item in variable
		                    String roomStatus = c.getString(TAG_ROOMSTATUS);
			                String propID = c.getString(TAG_PROPID);
			                String roomDetails = c.getString(TAG_ROOMDETAILS);
			                String roomID = c.getString(TAG_ROOMID);
			                String roomPrice = c.getString(TAG_ROOMPRICE);

		                    String rImg       = "http://dev.propertypanther.info/images/rooms/";
		                    Bitmap roomImage;
		                    
		                    // For each room, allocate an image
		                    // default image is not found
		                    Drawable imageNotFound = getResources().getDrawable(R.drawable.image_not_found);
	                    	roomImage = ((BitmapDrawable) imageNotFound).getBitmap();
		                    
		                    URL url = new URL(rImg + roomID + ".jpg");
		                    
		                    // if we have a connection, get an image, otherwise it has not been found
		                    try{
		                    if(url.openConnection().getInputStream() != null)
		                    	roomImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		                    } catch (IOException e){
		                    	e.printStackTrace();
		                    }
		                    
			                // creating new HashMap
			                HashMap<String, String> map = new HashMap<String, String>();
		                    Hashtable<String, Bitmap> imageMap = new Hashtable<String, Bitmap>(); 
			                
			                // adding each child node to HashMap key => value
			                map.put(TAG_ROOMSTATUS, roomStatus);
			                map.put(TAG_PROPID, propID);
			                map.put(TAG_ROOMDETAILS, roomDetails);
			                map.put(TAG_ROOMID, roomID);
			                map.put(TAG_ROOMPRICE, roomPrice);
			                imageMap.put(TAG_ROOMIMAGE, roomImage);   
			                
			                // adding HashList to ArrayList
			                propertyRooms.add(map);    
			                propertyRoomsImages.add(imageMap);
	                }
				}
	        } catch (JSONException e) {
	            e.printStackTrace();
	        } catch (NullPointerException e){
	        	e.printStackTrace();
	        } catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
	        
		}
	}
	
	class MyTabsListener implements ActionBar.TabListener{

	    public Fragment fragment;
	    public Context context;

	    public MyTabsListener(Fragment fragment, Context context) {
	        this.fragment = fragment;
	        this.context = context;
	    }

	    @Override
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	       // if reselected, nothing changes
	    }

	    @Override
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // fragment is selected - populate the view
	               
	    	ft.replace(R.id.fragment_container, fragment);
	    }

	    @Override
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	    	// remove fragment when unselected
	        ft.remove(fragment);
	    }
	}
	
}




