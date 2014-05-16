package propertypanther.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.example.propertypanther.R;

public class ListPropertiesSearch extends Activity {
	
	// Search parameters
	private String 	location;
	private float   minPrice = 200;
	private float   maxPrice = 600;
	
	private String[] userRetrieved;
	
    // Hash maps for messages and payments - in a specific order
    private ArrayList<HashMap<String, String>> userMessages;
	private ArrayList<HashMap<String, String>> userPayments;
	
	// tags for HashMaps
	private static final String TAG_MESSAGES = "messages";
	private static final String TAG_PAYMENTS = "payments";
	private static final String TAG_USERRETRIEVED = "userRetrieved";
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_search);
		
		// Hashmap for collecting user messages and payments
		userMessages = new ArrayList<HashMap<String, String>>();
		userPayments = new ArrayList<HashMap<String, String>>();
		
		retrieveUserInformation();

		// used to create a listener for the button that will trigger the search method
		Button btnSearch = (Button)findViewById(R.id.refineSearch);
		
		//for all edit texts, center the gravity
		final EditText editLocation = (EditText) findViewById(R.id.editSearchLocation);
		editLocation.setGravity(Gravity.CENTER);
		
		final EditText editMinPrice = (EditText) findViewById(R.id.editMinPrice);
		editMinPrice.setGravity(Gravity.CENTER);
		
		final EditText editMaxPrice = (EditText) findViewById(R.id.editMaxPrice);
		editMaxPrice.setGravity(Gravity.CENTER);
		
		// Min/max SeekBar listeners with implemented methods
		SeekBar seekBarMin = (SeekBar) findViewById(R.id.seekbarMinPrice);
		seekBarMin.setMax(800);
		
		seekBarMin.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar,
					int progress, boolean fromUser) {
				
				// Change progress text label with current SeekBar value
				editMinPrice.setText(String.valueOf(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do not need, but required method
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Do not need, but required method
			}
		});
		
		SeekBar seekBarMax = (SeekBar) findViewById(R.id.seekbarMaxPrice);
		seekBarMax.setMax(800);
		
		seekBarMax.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar,
					int progress, boolean fromUser) {
				
				editMaxPrice.setText(String.valueOf(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do not need, but required method
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Do not need, but required method
			}
		});
		
		// listener for the search button.
		// this takes the location, min and max parameters
		// and retrieves properties that do not break those restrictions.
		btnSearch.setOnClickListener(new Button.OnClickListener()
		{

			@Override
			public void onClick(View v) {
				
				// empty string validation
				if("".equals(editMinPrice.getText().toString())) editMinPrice.setText("200");
				if("".equals(editMaxPrice.getText().toString())) editMaxPrice.setText("600");
				
				// Take the new search parameters and store them in variables
				if("".equals(editLocation.getText().toString()))
				{
					// If the user wants to do a simple search using only the cost parameters,
					// we want to keep the location the same
				}
				if (location == null && "".equals(editLocation.getText().toString()))
				{
					Context context = getApplicationContext();
					CharSequence text = "Please enter a location, so we can process your search query.";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				else{
					location = editLocation.getText().toString();
					
					minPrice = Float.parseFloat(editMinPrice.getText().toString());
					maxPrice = Float.parseFloat(editMaxPrice.getText().toString());
	
					// define the difference between min and max, if it is less than 0, notify
					// the user.
					if ((maxPrice - minPrice) < 0)
					{
						Context context = getApplicationContext();
						CharSequence text = "The maximum price is smaller than your minimum." +
								" Please change this before pressing the search button again.";
						int duration = Toast.LENGTH_LONG;
	
						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					// the validation has been done, and the parameters have been
					// set. Therefore we can create a new instance of the activity
					// that is responsible for loading all properties.
					else {
						
						String[] propSearchParameters = new String[3];
						
						propSearchParameters[0] = location;
						propSearchParameters[1] = String.valueOf(minPrice);
						propSearchParameters[2] = String.valueOf(maxPrice);
						
						Intent intent = new Intent(getApplicationContext(), ListPropertiesActivity.class);
						
						intent.putExtra("propSearchParameters", propSearchParameters);
						
						if(TAG_USERRETRIEVED != null)
						{
							intent.putExtra(TAG_USERRETRIEVED, userRetrieved);
							intent.putExtra(TAG_MESSAGES, userMessages);
							intent.putExtra(TAG_PAYMENTS, userPayments);
						}
						
						startActivity(intent);
						
					}
				}
			}
		});
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * 
     * This method is called when the activity is made, or resumed; i.e
     * another activity references this one in an intent and starts it.
     * Any intents that are received, update the already existing (or newly
     * created) arrays / HashMaps.
     * The user, messages, and payments are used for the navigation menu purposes.
	 * 
     * @param userRetrieved The user carried across to this activity. Determines
     * if the user is logged in or not and authenticated.
     * 
     * @param userMessages If the user has messages, store them to carry across to the 
     * properties activity.
     * 
     * @param userPayments If the user has payments, store them to carry across to the 
     * properties activity.
	 */
	@SuppressWarnings("unchecked")
	private void retrieveUserInformation(){
		
		// get the location entered from the previous activity (main).
		Intent intent = this.getIntent();
		
		userRetrieved = new String[14];
		
		// store the user, in order to send to the properties page if the user is logged in
		if(intent.getStringExtra("location") !=null) location = intent.getStringExtra("location");
		else location = null;
		
		if(intent.getStringArrayExtra(TAG_USERRETRIEVED) != null)
			userRetrieved = intent.getStringArrayExtra("userRetrieved");
		else userRetrieved = null;
		
		if(intent.getSerializableExtra(TAG_MESSAGES) != null)
			userMessages = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_MESSAGES);
		else userMessages = null;
			
		if(intent.getSerializableExtra(TAG_PAYMENTS) != null)
			userPayments = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_PAYMENTS);
		else userPayments = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.property_search, menu);
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

}
