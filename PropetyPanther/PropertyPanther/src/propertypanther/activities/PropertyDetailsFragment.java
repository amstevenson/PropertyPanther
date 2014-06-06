package propertypanther.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import propertypanther.operations.JSONParser;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.propertypanther.R;
import com.example.propertypanther.R.drawable;

/**
 * A fragment of the activity PropertyMain. It is responsible for
 * allowing the user to view details of a selected property, and provide
 * information about how to apply.
 * 
 * @author Adam Stevenson
 *
 */
public class PropertyDetailsFragment extends Fragment {

	// script to add tracking
    private static final String url_track_property = "http://propertypanther.info:8080/PantherAPI/tracking_add.jsp";
	
    // used for retriving property and user information
    private String[] retrievedProperty;
    private String[] userRetrieved;
    
    // tags for json middleware script
    private static final String TAG_PROPERTYID = "property_id";
    private static final String TAG_USERID     = "user_id";
    
    // json information
    //private JSONArray trackingInformation;
   // private JSONObject json;
    
    // parser class
    private JSONParser jsonParser = new JSONParser();
    
    // progress dialog
    private ProgressDialog pDialog;
    
    // Toggle for the options of the dialog box that appears - enquire / tracking for a property
    private int trackingToggler = 0;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final View view = inflater.inflate(R.layout.fragment_property_details, container, false);
		
		// retrieve the array containing the elements of the property
		Bundle propertyBundle      = getArguments();
		retrievedProperty = propertyBundle.getStringArray("retrievedProperty");
		userRetrieved     = propertyBundle.getStringArray("userRetrieved");
		
		// define fragment objects
		TextView propertyName              = (TextView) view.findViewById(R.id.propertySelected);
		TextView propertyDescription       = (TextView) view.findViewById(R.id.propertyDescription);
		TextView propertyDetailsLabel      = (TextView) view.findViewById(R.id.propertyDetailsLabel);
		TextView propertyNumRooms 		   = (TextView) view.findViewById(R.id.propertyDetailsNumRooms);
		TextView propertyAvgCost 		   = (TextView) view.findViewById(R.id.propertyAvgCost);
		TextView propertyDistrict 		   = (TextView) view.findViewById(R.id.propertyDistrict);
		TextView propertyAvailableRooms    = (TextView) view.findViewById(R.id.propertyRoomsVacant);
		TextView propertyMapViewLabel      = (TextView) view.findViewById(R.id.propertyMapViewLabel);
		ImageView propertyMapView          = (ImageView) view.findViewById(R.id.propertyMapViewImage);
		
		final Button   propertyInterested  = (Button)   view.findViewById(R.id.propertyDetailsInterestedBtn);
		
		// change property address information to match those belonging to the retrieved property 
		propertyName.setText(retrievedProperty[1]);
		propertyDescription.setText(retrievedProperty[5]);
		propertyNumRooms.setText("•" + " " + retrievedProperty[4] + " bedroom property");
		propertyAvgCost.setText("•" + " £" + retrievedProperty[3] +" average P/M");
		propertyDistrict.setText("•" + " " + retrievedProperty[7] + " property");
		propertyAvailableRooms.setText("•" + " " + "Added soon");
		
		// Specify which font file will be used
		String font = "fonts/OpenSans-Light.ttf";
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), font);
		
		// Apply the style changes to each object
		propertyName.setTypeface(tf);
		propertyDescription.setTypeface(tf);
		propertyDetailsLabel.setTypeface(tf);
		propertyNumRooms.setTypeface(tf);
		propertyAvgCost.setTypeface(tf);
		propertyDistrict.setTypeface(tf);
		propertyAvailableRooms.setTypeface(tf);
		propertyMapViewLabel.setTypeface(tf);
		
		if(userRetrieved == null) trackingToggler = 0;
		else trackingToggler = 1;
		
		Log.d("tracking toggler value", String.valueOf(trackingToggler));
		
		propertyInterested.setOnClickListener(new OnClickListener() {
		
		// dialog to allow the user to either track or enquire about a property
		@Override
		public void onClick(View v) {
				
			
			if (trackingToggler != 0){
			Dialog d = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT)
               .setTitle("What would you like to do?")
               .setNegativeButton("Cancel", null)
               .setItems(new String[]{"Make an Enquiry", "Track this Property"}, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dlg, int position) 
                {
                    if ( position == 0 )
                    {  
                    	Intent intent = new Intent(getActivity(),PropertyDetailsEnquiry.class);
                    	startActivity(intent);
                    	
                    }
                    else if(position == 1)
                    {
                	   ImageView tickImage = (ImageView)getActivity().findViewById(R.id.tickInterested);
                	   
                       tickImage.setVisibility(View.VISIBLE);
                       
                       propertyInterested.setBackgroundResource(drawable.button_interested_true);
                       
                       // Middleware script, to add tracking to properties
                       new TrackThisProperty().execute();
                       
                    }
                }
            })
            .create();
            d.show();
            
			}
			else {
				
				Dialog d = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT)
	               .setTitle("What would you like to do?")
	               .setNegativeButton("Cancel", null)
	               .setItems(new String[]{"Make an Enquiry"}, new DialogInterface.OnClickListener(){
	                @Override
	                public void onClick(DialogInterface dlg, int position) 
	                {
	                    if ( position == 0 )
	                    {  
	                    	Intent intent = new Intent(getActivity(),PropertyDetailsEnquiry.class);
	                    	startActivity(intent);
	                    }
	                }
	            })
	            .create();
	            d.show();
	            
			}
		}});
		
				
		propertyMapView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent mapIntent = new Intent(getActivity(), PropertyDetailsMapView.class);
				mapIntent.putExtra("retrievedProperty", retrievedProperty);
				startActivity(mapIntent);
				
			}
		});
		
		
		
	    return view;
	}
	
	/**
	 * Class used to retrieve property information from a Middleware script
	 * and populate the UI using an ArrayList of HashMaps
	 */
	class TrackThisProperty extends AsyncTask<String, Boolean, String> {

	    /**
	     * Before starting background thread Show Progress Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	       
	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage("Processing request, please be patient...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
	    }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PROPERTYID, retrievedProperty[0]));
			params.add(new BasicNameValuePair(TAG_USERID, userRetrieved[8]));
			
			JSONObject json = jsonParser.makeHttpRequest(url_track_property, "GET", params);
			
			Log.d("after tracking log", json.toString());
			
			//trackingInformation = json.getJSONArray("") 
			
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
	
}
