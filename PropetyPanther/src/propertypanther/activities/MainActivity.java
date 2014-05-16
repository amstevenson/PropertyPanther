package propertypanther.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import propertypanther.operations.JSONParser;
import propertypanther.operations.RegularExpressions;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.propertypanther.R;

/**
 * Main activity; determines:
 * 		User Login: User clicks login, and enters credentials.
 * 		Search properties: User enters location details, and is sent to the
 * 						   property details page. 
 * 
 * @author Adam Stevenson
 */
public class MainActivity extends Activity {

	// Define screen objects - 
	// listed here instead of main body, because each object is referenced twice. 
	private Button 				 btnLocation;
	private Button 				 btnLoginChoice;			
	private Button 				 btnLogin;
	private Button 				 btnProcessLoc;
	private AutoCompleteTextView txtLocation;
	private EditText 			 txtUsername;
	private EditText 			 txtPassword;
	private TextView             txtMakeAccount;
	
	private RegularExpressions regExpressions = new RegularExpressions();
	
    // JSON Node names for authenticating and collecting a users personal information
	private static final String TAG_USER          		  = "user";
    private static final String TAG_USERPERMISSIONS 	  = "user_permissions";
    private static final String TAG_CITYNAME    		  = "city_name";
    private static final String TAG_USERPHONE    		  = "user_phone";
    private static final String TAG_USERAUTHENTICATION    = "authentication";
    private static final String TAG_USERSURNAME    		  = "user_surname";
    private static final String TAG_ADDRL1 				  = "addr_line_1";
    private static final String TAG_ADDRL2 				  = "addr_line_2";
    private static final String TAG_ADDRPOSTCODE          = "addr_postcode";
    private static final String TAG_USERID   	          = "user_id";
    private static final String TAG_USEREMAIL             = "user_email";
    private static final String TAG_USERFORENAME          = "user_forename";
    private static final String TAG_USERTITLE             = "user_title";
    private static final String TAG_PASSCHANGED           = "pass_changed";	
    private static final String TAG_SESSIONKEY			  = "session_key";
    
    // JSON Node names for collecting all of a users messages 
	private static final String TAG_MESSAGES            	= "messages";
	private static final String TAG_MESSAGEBODY           = "message_body";
    private static final String TAG_MESSAGETO 			  = "message_to";
    private static final String TAG_MESSAGEFROM    		  = "message_from";
    private static final String TAG_MESSAGEID    		  = "message_id";
    private static final String TAG_MESSAGEREAD  		  = "message_read";
    private static final String TAG_MESSAGETYPE    		  = "message_type";
    private static final String TAG_MESSAGESENT 		  = "message_sent";
    
    // JSON Node names for collecting all of a users payments 
	private static final String TAG_PAYMENTS         	  = "payments";
	//private static final String TAG_PROPERTYID            = "property_id";
    private static final String TAG_PAYMENTDUE 			  = "payment_due";
    private static final String TAG_REFERENCEID    		  = "reference_id";
    private static final String TAG_PAYMENTID    		  = "payment_id";
    private static final String TAG_PAYMENTAMOUNT  		  = "payment_amount";
    private static final String TAG_PAYMENTSTATUS    	  = "payment_status";
    private static final String TAG_PAYMENTRECEIVED 	  = "payment_received";
    private static final String TAG_PAYMENTDIFFERENCEDATE = "payment_diff_date";
 
    // hash map for messages
    private ArrayList<HashMap<String, String>> userMessages;
    
    // hash map for payments
    private ArrayList<HashMap<String, String>> userPayments;
    
    // Progress Dialog
    private ProgressDialog pDialog;
    
    // 'User' JSONArray
    private JSONArray userInformation = null;
    
    // 'User' Arraylist
    private String[] userRetrieved = new String[15];
    
    private JSONParser jsonParser = new JSONParser();
    
    // Middleware link
    private static String url_authenticate_user = "http://propertypanther.info:8080/PantherAPI/user.jsp";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// we don't need an ActionBar for the first screen, so hide it.
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		
		// Hashmap for collecting user messages and payments
		userMessages = new ArrayList<HashMap<String, String>>();
		userPayments = new ArrayList<HashMap<String, String>>();
		
		// Initialise objects used for styles
		txtLocation    =  (AutoCompleteTextView) findViewById(R.id.initialSearchLocation);
		btnLocation    =  (Button) findViewById(R.id.initialSearchBtn);
		btnLoginChoice =  (Button) findViewById(R.id.initialLogChoice);				
		btnProcessLoc  =  (Button) findViewById(R.id.initialProcessSearchBtn);
		btnLogin       =  (Button) findViewById(R.id.initialloginBtn);
		txtUsername    =  (EditText) findViewById(R.id.initialUsername);
		txtPassword    =  (EditText) findViewById(R.id.initialPassword);
		txtMakeAccount =  (TextView) findViewById(R.id.initialSignupText);
		
		// Specify which font file will be used
		String font = "fonts/OpenSans-Semibold.ttf";
		Typeface tf = Typeface.createFromAsset(getAssets(), font);
		
		// Apply the changes to each object
		txtLocation.setTypeface(tf);
		btnLocation.setTypeface(tf);
		btnLoginChoice.setTypeface(tf);
		btnProcessLoc.setTypeface(tf);
		btnLogin.setTypeface(tf);
		txtUsername.setTypeface(tf);
		txtPassword.setTypeface(tf);	
		txtMakeAccount.setTypeface(tf);
		
		// Get all the locations from the text file located in res/drawable called locations.txt
		// and add to the array, finally, the strings contained define the search suggestions.
		final String[] initialFileLocations;
		initialFileLocations = loadAllLocations();
			
		// declare the string array to store 4 suggestions for the user
		final String[] refineLocations = new String[4];
			
		txtLocation.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event){
					
				if(event.getAction() == KeyEvent.ACTION_UP)
				{
					if(initialFileLocations != null)
					{
						if(txtLocation.length() > 1)
						{
							int i = 0;
							
							// cycle through each location, but only retrieve three at most
							for (int j = 0; j < initialFileLocations.length; j++)
							{	
								if(initialFileLocations[j].contains(txtLocation.getText().toString())
											&& (i < 4))
								{
									refineLocations[i] = initialFileLocations[j];		
									i ++;
								}		
							}
									
							// call the method responsible for populating the array adapter
							populateLocSuggestions(refineLocations);
						
							// after the suggestions have been listed, we need to reinitialise
							// the "refineLocations" array.
							//
							// Note: we need to do this because each event has a deterministic number
							// 		 of suggestions - If one has more than another, we will see unrelated results.
							for (i =0; i < 4; i++) refineLocations[i] = null;
						}
					}
				}
				return false;	
			}	
		});		
		
		// for some reason this does not work in the method onClick
		txtMakeAccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent signupIntent = new Intent(v.getContext(),MainActivitySignupForm.class);
				startActivity(signupIntent);
				
			}
		});
		
	}
	
	/**
	 * A method to retrieve locations from a text file, and populate an AutoCompleteTextView
	 * based on these results; the dropDown list results will display these places, dependent 
	 * on the characters inputed by the user. 
	 * Note: This method only populates the arrayList.
	 * 
	 * @param autoLocations - String Array that stores all the Towns/Cities etc of England.
	 * 
	 * @see populateLocSuggestions - The method that allocates the AutoCompleteTextView with
	 * an adapter that contains all of the locations stored in the String Array found here.
	 *
	 * @return null - if we do not have the information we need - or none at all.
	 * 
	 * @return autoLocations - if we have an array that contains all the information
	 * we need, then return this.
	 * 
	 */
	public String[] loadAllLocations(){
		
		try{
			
			// define file reader variables
			String[] autoLocations = new String[1802];
			int i = 0;
			String str = "";
			InputStream is = this.getResources().openRawResource(R.drawable.locations);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
			// read each line and add it to the array
			if(is!=null)
			{
				while((str = reader.readLine()) != null)
				{
					autoLocations[i] = str;

					i++;
				}
			}
			is.close();
			
			// return the complete array of locations
			return autoLocations;
			
		} catch (IOException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Method that allocates the AutoCompleteTextView txtLocation with dropDown list 
	 * values that allow a user to click on, and complete their location query, without
	 * having to write all of it in themselves.
	 * 
	 * @param refineLocations - String Array containing the locations
	 */
	public void populateLocSuggestions(String[] refineLocations){
		
		// The index's of the array cannot be null when they are used by the adapter
		// ...So if any are null, then we need to fix that.
		int indexCounter = 0;
		int i;
		
		for (i = 0; i < 4; i++) if(refineLocations[i] != null) indexCounter++;
		
		// now we have the amount of actual locations that have been retrieved from
		// the onKey press event of the onCreate method.
		String[] suggestedLocations = new String[indexCounter];
		
		for (i = 0; i < indexCounter; i++ ) suggestedLocations[i] = refineLocations[i];

		// Now we can populate the array adapter that offers suggestions to
		// users, based on the text inside of: AutoTextView txtLocation
		if (suggestedLocations != null)
		{
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		            android.R.layout.simple_dropdown_item_1line, suggestedLocations);
		    txtLocation.setAdapter(adapter);
		}
	}
	
	/**
	 *  The objects that run this function from the "onClick" parameter
	 *  have their id checked, then the related code is triggered.
	 */
	public void onClick (View view)
	{
		int id = view.getId();
		
		switch (id)
		{
		// Button - Search by country
		case R.id.initialSearchBtn:
			// Allow the user to enter a location; i.e postcode, town, city. 
			btnLocation.setVisibility(View.INVISIBLE);
			txtLocation.setVisibility(View.VISIBLE);
			// Make the button responsible for carrying the result over to the search
			// results form, visible.
			btnProcessLoc.setVisibility(View.VISIBLE);
			btnLoginChoice.setVisibility(View.INVISIBLE);
			txtMakeAccount.setVisibility(View.INVISIBLE);
			break;
			
		// Button - Process location
		case R.id.initialProcessSearchBtn:
			Intent processIntent = new Intent(this,ListPropertiesActivity.class);
			String mainActLocation = txtLocation.getText().toString();
			processIntent.putExtra("mainActLocation", mainActLocation);
			
			if(regExpressions.contSymOrNum(mainActLocation)){
				
				CharSequence text = "Please check your search location and revise " +
						"- it contains numbers or symbols";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();
			}
			else startActivity(processIntent);
			
			break;
		
		// Button - Login
		case R.id.initialLogChoice:
			// Display the EditText fields and buttons
			btnLocation.setVisibility(View.INVISIBLE);
			btnLoginChoice.setVisibility(View.INVISIBLE);
			txtMakeAccount.setVisibility(View.INVISIBLE);
			btnLogin.setVisibility(View.VISIBLE);
			
			txtUsername.setVisibility(View.VISIBLE);
			txtPassword.setVisibility(View.VISIBLE);
			break;
		
		case R.id.initialloginBtn:
			
			if(txtUsername.getText().length() > 0 && txtPassword.getText().length() > 0){
				
				new GetUserInformation().execute();
				
			}
			else{
				
				CharSequence text = "You have either not entered your username or password, please"
						+ " try again";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();
			}
		
			break;
		}
	}	
	
	@Override
	public void onBackPressed() {
	   
		// If the user is in a subsection of the main activity,
		// pressing the back button and having the application finish,
		// would not be what the user would want; they would instead wish
		// to be brought back to the main section.
		if(txtUsername.getVisibility() == View.VISIBLE)
		{
			// revert to main screen
			btnLocation.setVisibility(View.VISIBLE);
			btnLoginChoice.setVisibility(View.VISIBLE);
			txtMakeAccount.setVisibility(View.VISIBLE);
			
			btnLogin.setVisibility(View.INVISIBLE);
			txtUsername.setVisibility(View.INVISIBLE);
			txtPassword.setVisibility(View.INVISIBLE);
		}
		else if(txtLocation.getVisibility() == View.VISIBLE)
		{
			// revert to main screen
			btnLocation.setVisibility(View.VISIBLE);
			btnLoginChoice.setVisibility(View.VISIBLE);
			txtMakeAccount.setVisibility(View.VISIBLE);
			
			txtLocation.setVisibility(View.INVISIBLE);
			btnProcessLoc.setVisibility(View.INVISIBLE);
		}
		else finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Class used to retrieve property information from a Middleware script
	 * and populate the UI using an ArrayList of HashMaps.
	 * 
	 * @param userRetrieved If the username (email) and password match a user stored
	 * in the database, this array stores all the information for that user.
	 * 
	 * @param userMessages if the usernames authentication field (index 0) has a value of 'true',
	 * this ArrayList of HashMaps stores all the messages that belong to the user. 
	 * 
	 * @param userPayments if the usernames authentication field (index 0) has a value of 'true',
	 * this ArrayList of HashMaps stores all the payments that belong to the user. 
	 * 
	 */
	class GetUserInformation extends AsyncTask<String, Boolean, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Validating credentials, please be patient...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("email", txtUsername.getText().toString()));
			params.add(new BasicNameValuePair("pass", txtPassword.getText().toString()));
			
			if (userRetrieved[0] != null){
				for (int i = 0; i < userRetrieved.length; i++) if (userRetrieved[i] != null) userRetrieved[i] = null;
			}
			
			try {

				// Initialise the JSONObject to be used for UI purposes
				JSONObject json = jsonParser.makeHttpRequest(url_authenticate_user, "GET", params);
				
				if(json!=null)
				{
	                // Getting Array of users
	                userInformation = json.getJSONArray(TAG_USER);
	 
	                // we will only ever have one user collected for comparison purposes
		            JSONObject c = userInformation.getJSONObject(0);
		            
		            Log.d("user authen to string", c.toString());
		            
	                String uAuthentication = c.getString(TAG_USERAUTHENTICATION);
	                
	                if (uAuthentication.equals("true")){
	                	
	                		// username and password is authentic
		                    // Storing each json item in variable
			                String uPermissions = c.getString(TAG_USERPERMISSIONS);
			                String cityName;
			                String uPhone;
			                String uSurname     = c.getString(TAG_USERSURNAME);
			                String addrLine1;
			                String addrLine2;
			                String addrPostcode;
			                String uID          = c.getString(TAG_USERID);
			                String uEmail       = c.getString(TAG_USEREMAIL);
		                    String uForename    = c.getString(TAG_USERFORENAME);
			                String uTitle;
			                String passChanged  = c.getString(TAG_PASSCHANGED);
			                String sessionKey   = c.getString(TAG_SESSIONKEY);
			                String propertyID; 
			                
			                // If a new user is made, they might not have certain fields
			                if(c.has(TAG_CITYNAME) && c.has(TAG_ADDRL1) && c.has(TAG_ADDRL2)
			                		&& c.has(TAG_USERPHONE))
			                {
			                	uPhone       = c.getString(TAG_USERPHONE);
			                	cityName     = c.getString(TAG_CITYNAME);
			                	addrLine1    = c.getString(TAG_ADDRL1);
			                	addrLine2    = c.getString(TAG_ADDRL2);
			                	addrPostcode = c.getString(TAG_ADDRPOSTCODE);
			                }
			                else {
			                	uPhone       = "N/A";
			                	cityName     = "N/A";
			                	addrLine1    = "N/A";
			                	addrLine2    = "N/A";
			                	addrPostcode = "N/A";
			                } // the rest is allocated automatically
			                
			                if(c.has("user_property")) propertyID = c.getString("user_property");
			                else
			                	propertyID = "";
			                
			                if(c.has(TAG_USERTITLE)) uTitle = c.getString(TAG_USERTITLE);
			                else
			                	uTitle = "";
			                
			                // adding string values to array - will be passed across
			                // to different activities
			                userRetrieved[0]  = uAuthentication;
			                userRetrieved[1]  = uPermissions;
			                userRetrieved[2]  = cityName;
			                userRetrieved[3]  = uPhone;
			                userRetrieved[4]  = uSurname;
			                userRetrieved[5]  = addrLine1;
			                userRetrieved[6]  = addrLine2;
			                userRetrieved[7]  = addrPostcode;
			                userRetrieved[8]  = uID;
			                userRetrieved[9]  = uEmail;
			                userRetrieved[10] = uForename;
			                userRetrieved[11] = uTitle; 
			                userRetrieved[12] = passChanged;
			                userRetrieved[13] = sessionKey;
			                userRetrieved[14] = propertyID;
			                
			                // if we have a null for user retrieved, there has been a server error
			                if(userRetrieved[0] == null) userRetrieved[0] = "false";
			                
			                // retrieve all the messages associated to this user
			                if(userRetrieved[0].equals("true")){
			                	
			                	params.clear();
			                	
			                	// supply the parameters for the next database query
			        			params.add(new BasicNameValuePair("id", userRetrieved[8]));
			        			params.add(new BasicNameValuePair("session_key", userRetrieved[13]));
			                	params.add(new BasicNameValuePair("type", "detailed"));
			        			
			        			// collect a users messages, since we have many, we should populate a LinkedHashmap and send it
			        			// across to the next activity - Linked means: to be in a certain order and not random
			        			String url_user_messages = "http://propertypanther.info:8080/PantherAPI/messages.jsp";
			                	
			        			json = jsonParser.makeHttpRequest(url_user_messages, "GET", params);
			        			
			        			// define variables used for changing the format of dates
				    			SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S", Locale.ENGLISH);
				    			java.util.Date utilParsedDate;
				    			java.sql.Date sqlParsedDate;
			        			
			        			if(json!=null){
			        			
			        				if(userMessages!= null) userMessages.clear();
			        				
			        				userInformation = json.getJSONArray(TAG_MESSAGES);
			        				
				        			// looping through All messages
					                for (int i = 0; i < userInformation.length(); i++) {
					                	
					                	// retrieve each message using JSONObject 'c'
					                    c = userInformation.getJSONObject(i);
					                    
					                    Log.d("each message in turn", c.toString());
					                    
					                    if(c.get(TAG_USERAUTHENTICATION).equals("true"))
					                    {
						                    // Storing each json item in variable
						                    uAuthentication = c.getString(TAG_USERAUTHENTICATION);
						                    String mBody = c.getString(TAG_MESSAGEBODY);
							                String mTo = c.getString(TAG_MESSAGETO);
							                String mFrom = c.getString(TAG_MESSAGEFROM);
							                String mID = c.getString(TAG_MESSAGEID);
							                String mRead = c.getString(TAG_MESSAGEREAD);
							                String mType = c.getString(TAG_MESSAGETYPE);
							                String mSent = c.getString(TAG_MESSAGESENT);
				        					
				        					// information of the person who has sent the message to the user
				        					String mSentForename = c.getString(TAG_USERFORENAME);
				        					String mSentSurname  = c.getString(TAG_USERSURNAME);
				        					
							                // parse date collected into the correct format
							    			utilParsedDate = originalDateFormat.parse(mSent.toString());
							    			sqlParsedDate = new java.sql.Date(utilParsedDate.getTime());
							    			mSent = new SimpleDateFormat("dd MMM yyyy").format(sqlParsedDate);
							                
				        					// if we have a null message (or none at all).
						                    if (mBody == null) {
						                    	
						                    	mBody         = "No Message Information Available";
						                    	mRead         = "";
						                    	mType         = "";
						                    	mSent         = "";
						                    	mSentForename = "";
						                    	mSentSurname  = "";
						                    }
							    			
							                // creating new HashMap
							                HashMap<String, String> map = new HashMap<String, String>();
							 
							                // adding each child node to HashMap key => value
							                map.put(TAG_USERAUTHENTICATION, uAuthentication);
							                map.put(TAG_MESSAGEBODY, mBody);
							                map.put(TAG_MESSAGETO, mTo);
							                map.put(TAG_MESSAGEFROM, mFrom);
							                map.put(TAG_MESSAGEID, mID);
							                map.put(TAG_MESSAGEREAD, mRead);
							                map.put(TAG_MESSAGETYPE, mType);
							                map.put(TAG_MESSAGESENT, mSent);
							                map.put(TAG_USERFORENAME, mSentForename);
							                map.put(TAG_USERSURNAME, mSentSurname);
							                
							                // adding HashList to ArrayList
							                userMessages.add(map);
					                    }
					                    else {
					                    	
					                    	// if we have a null message (or none at all).
						                    	
						                    String mBody         = "No Message Information Available";
						                    String mRead         = "";
						                    String mType         = "";
						                    String mSent         = "";
						                    String mSentForename = "";
						                    String mSentSurname  = "";
						                    
							    			
							                // creating new HashMap
							                HashMap<String, String> map = new HashMap<String, String>();
							 
							                // adding each child node to HashMap key => value
							                map.put(TAG_USERAUTHENTICATION, uAuthentication);
							                map.put(TAG_MESSAGEBODY, mBody);
							                map.put(TAG_MESSAGEREAD, mRead);
							                map.put(TAG_MESSAGETYPE, mType);
							                map.put(TAG_MESSAGESENT, mSent);
							                map.put(TAG_USERFORENAME, mSentForename);
							                map.put(TAG_USERSURNAME, mSentSurname);
							                
							                // adding HashList to ArrayList
							                userMessages.add(map);
					                    }
					                }
				                }
			        			
			        		    // retrieve the payments allocated to a user - uses same parameters as messages
			        			String url_user_payments = "http://propertypanther.info:8080/PantherAPI/payments.jsp";
			        			
			        			params.clear();
			                	// supply the parameters for the next database query
			        			params.add(new BasicNameValuePair("id", userRetrieved[8]));
			        			params.add(new BasicNameValuePair("session_key", userRetrieved[13]));
			        			
			        			json = jsonParser.makeHttpRequest(url_user_payments, "GET", params);
			        			
			        			if(json !=null){
			        				
				        			userInformation = json.getJSONArray(TAG_PAYMENTS);
			        				
			        				if(userPayments != null) userPayments.clear();
			        				
			        				for (int i = 0; i < userInformation.length(); i++){
			        					
			        					c = userInformation.getJSONObject(i);
			        					
			        					Log.d("daodpwawod", c.toString());
			        					
			        					if(c.get(TAG_USERAUTHENTICATION).equals("true"))
			        					{
			        						
				        					// storing each json item in a variable
				        					uAuthentication = c.getString(TAG_USERAUTHENTICATION);
				        					String payDue = c.getString(TAG_PAYMENTDUE);
				        					String refID = c.getString(TAG_REFERENCEID);
				        					uID = c.getString(TAG_USERID);
				        					String payID = c.getString(TAG_PAYMENTID);
				        					String payAmount = c.getString(TAG_PAYMENTAMOUNT);
				        					String payStatus = c.getString(TAG_PAYMENTSTATUS);
				        					String payReceived = "---";
				        					String payDifferenceDate = "---";
				        					
				        					// if we have payments expecting to be paid, there will be no
				        					// payment received field, therefore we need to check if it exists or not
				        					if(c.has(TAG_PAYMENTRECEIVED)) {
				        						
				        						// if we have payments, change date formats, and calculate difference
				        						payReceived = c.getString(TAG_PAYMENTRECEIVED);
				        						
					        					// payment received
					        					utilParsedDate = originalDateFormat.parse(payReceived.toString());
					        					sqlParsedDate = new java.sql.Date(utilParsedDate.getTime());
					        					payReceived = new SimpleDateFormat("dd MMM yyyy").format(sqlParsedDate);
					        					
				        					}
				        						
				        					// parse date collected into the correct date format
				        					// payment due
				        					utilParsedDate = originalDateFormat.parse(payDue.toString());
				        					sqlParsedDate = new java.sql.Date(utilParsedDate.getTime());
				        					payDue = new SimpleDateFormat("dd MMM yyyy").format(sqlParsedDate);
				        					
				        					// calculate the difference in time between todays date and the due date
				        					// define variables
				        					long difference;
				        					int days;
				        					
				        					// get todays date and calculate the difference in milliseconds
				        					Calendar todayD = Calendar.getInstance();
				        					difference = utilParsedDate.getTime() - todayD.getTime().getTime();
				        					
				        					Log.d("todays time", todayD.getTime().toString());
				        					Log.d("util parsed date time", String.valueOf(utilParsedDate.getTime()));

				        					// convert difference to days and store as a string
				        					days = (int) (difference / (1000 * 60 * 60 * 24));
				        					payDifferenceDate = String.valueOf(days);
				        					
				        					// creating new HashMap
				        					HashMap<String, String> map = new HashMap<String, String>();
				        					
				        					// adding each child node to HashMap key => value
				        					map.put(TAG_USERAUTHENTICATION, uAuthentication);
				        					//map.put(TAG_PROPERTYID, propID);
				        					map.put(TAG_PAYMENTDUE, payDue);
				        					map.put(TAG_REFERENCEID, refID);
				        					map.put(TAG_USERID, uID);
				        					map.put(TAG_PAYMENTID, payID);
				        					map.put(TAG_PAYMENTAMOUNT, payAmount);
				        					map.put(TAG_PAYMENTSTATUS, payStatus);
				        					map.put(TAG_PAYMENTRECEIVED, payReceived);
				        					map.put(TAG_PAYMENTDIFFERENCEDATE, payDifferenceDate);
				        					
				        					// adding HashList to ArrayList
				        					userPayments.add(map);
			        					}
			        					else{
			        						
						                    String payAmount         = "None";
						                    String payDifferenceDate = "---";
						                    String payDue            = "0";
						                    String payStatus         = "---";
						                    String payReceived       = "---";
						                    	
					        				// creating new HashMap
					        				HashMap<String, String> map = new HashMap<String, String>();
					        					
					        				// adding each child node to HashMap key => value
					        				map.put(TAG_USERAUTHENTICATION, uAuthentication);
					        				//map.put(TAG_PROPERTYID, propID);
					        				map.put(TAG_PAYMENTDUE, payDue);
					        				map.put(TAG_USERID, uID);
					        				map.put(TAG_PAYMENTAMOUNT, payAmount);
					        				map.put(TAG_PAYMENTSTATUS, payStatus);
					        				map.put(TAG_PAYMENTRECEIVED, payReceived);
					        				map.put(TAG_PAYMENTDIFFERENCEDATE, payDifferenceDate);
					        					
					        				userPayments.add(map);
						                   
			        					}
			        				}
			        			}
				            }  
	                }
	                else
	                {	
	                	// Username and password is not authentic
	                	// store the authentication value
	                	userRetrieved[0] = uAuthentication;
	                }
				}
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
            	e.printStackTrace();
            } catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/*
		 * After completing background task, dismiss the progress dialog
		 * and determine what will be done with the results
		 */
		protected void onPostExecute (String file_url){

			// dismiss the dialog after getting all properties
            pDialog.dismiss();
           
            try{
            	
            // determine what to do next based on the authentication result
            runOnUiThread(new Runnable() {
            	public void run() {
            	
    				Log.d("userPayments", userPayments.toString());
            		
            		if(userRetrieved[0].equals("true") && userRetrieved[0] != null){
            			
            			// create an instance of a user dashboard, and send across user information
            			Intent intent = new Intent(getApplicationContext(),UserDashboard.class);
            			
            			intent.putExtra(TAG_MESSAGES, userMessages);
            			intent.putExtra(TAG_PAYMENTS, userPayments);
            			intent.putExtra("userRetrieved", userRetrieved);
            			 
 		                startActivity(intent);
            		}
            		else if(userRetrieved[0].equals("false")){
            			
        				CharSequence text = "You have entered either an incorrect username " +
        						"or password, please try again.";
        				int duration = Toast.LENGTH_LONG;

        				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        				toast.show();
            		}
            	}
            });
            } catch (NullPointerException e){
            	e.printStackTrace();
            }
		}
	}
}
		
	



	
	
	
	
	
	
	



