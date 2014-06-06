package propertypanther.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import propertypanther.operations.JSONParser;
import propertypanther.operations.RegularExpressions;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.propertypanther.R;

public class UserDashboardChangeDetails extends Activity {

	// json parser class for executing script
	JSONParser jsonparser = new JSONParser();
	
	
	private String[] userRetrieved;
	private ArrayList<HashMap<String, String>> userMessages;
	private ArrayList<HashMap<String, String>> userPayments;
	
	// regular expressions
	private RegularExpressions regularExpressions = new RegularExpressions();
	
    // Progress Dialog
    private ProgressDialog pDialog;
	
    // json middleware retrieving objects
	private JSONArray userInformation;
	private JSONObject json;
	
	// define objects
	private static String userForename;
	private static String userSurname;
	private static String userEmail;
	private static String userPhone;
	private static	String userAddrl1;
	private static	String userAddrl2;
	private static	String userPostcode;
	private static	String userCityname;
	private static String oldPassword;
	private static String newPassword;
	private static String newPasswordRepeat;
	
	//tag strings
	private static final String TAG_MESSAGES = "messages";
	private static final String TAG_PAYMENTS = "payments";
	
	private String url_process_updateuser     = "http://propertypanther.info:8080/PantherAPI/process_updateuser.jsp";
	private String url_process_updatepassword = "http://propertypanther.info:8080/PantherAPI/process_updatepassword.jsp";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_dashboard_change_details);

		retrieveUserInformation();
		
		for (int i = 0; i < userRetrieved.length; i ++) Log.d("user retrieved information to set", userRetrieved[i]);
		
		// define objects assigned id's
		Button changeUserDetails = (Button) findViewById(R.id.changeUserSubmitBtn);
		
		final EditText changeUserForename = (EditText) findViewById(R.id.changeUserForename);
		final EditText changeUserSurname = (EditText) findViewById(R.id.changeUserSurname);
		final EditText changeUserEmail = (EditText) findViewById(R.id.changeUserEmail);
		final EditText changeUserPhone = (EditText) findViewById(R.id.changeUserPhone);
		final EditText changeUserAddrl1 = (EditText) findViewById(R.id.changeUserAddrl1);
		final EditText changeUserAddrl2 = (EditText) findViewById(R.id.changeUserAddrl2);
		final EditText changeUserPostcode = (EditText) findViewById(R.id.changeUserPostcode);
		final EditText changeUserCityname = (EditText) findViewById(R.id.changeUserCityname);
		
		Button changeUserPasswordBtn = (Button) findViewById(R.id.changeUserPasswordBtn);
		final EditText changeUserPassword = (EditText) findViewById(R.id.changeCurrPassword);
		final EditText changeNewPassword = (EditText) findViewById(R.id.changeNewPassword);
		final EditText changeNewPasswordRepeat = (EditText) findViewById(R.id.changeNewRepeatPassword);
		
		// set the variables that are already stored
		changeUserForename.setText(userRetrieved[10]);
		changeUserSurname.setText(userRetrieved[4]);
		changeUserEmail.setText(userRetrieved[9]);
		changeUserPhone.setText(userRetrieved[3]);
		changeUserAddrl1.setText(userRetrieved[5]);
		changeUserAddrl2.setText(userRetrieved[6]);
		changeUserPostcode.setText(userRetrieved[7]);
		changeUserCityname.setText(userRetrieved[2]);
		
		changeUserDetails.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				userForename = changeUserForename.getText().toString();
				userSurname = changeUserSurname.getText().toString();
				userEmail = changeUserEmail.getText().toString();
				userPhone = changeUserPhone.getText().toString();
				userAddrl1 = changeUserAddrl1.getText().toString();
				userAddrl2 = changeUserAddrl2.getText().toString();
				userPostcode = changeUserPostcode.getText().toString();
				userCityname = changeUserCityname.getText().toString();
				
				
				if(userForename.length() > 0 && userSurname.length() > 0 && userEmail.length() > 0
						&& userPhone.length() > 0 && userAddrl1.length() > 0 && userAddrl2.length() > 0
						&& userPostcode.length() > 0 && userCityname.length() > 0){
					
					if(regularExpressions.emailValidation(userEmail))
						new ChangeUserDetails().execute();
					else {
						
						CharSequence text = "You have entered an incorrect email address, please change this and try again.";
						int duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(getApplicationContext(), text, duration);
						toast.show();
					}
				}
				else{
					
					CharSequence text = "You have left one field or more blank, please revise and try again.";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(getApplicationContext(), text, duration);
					toast.show();
				}
			}
		});
		
		changeUserPasswordBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// change password
				oldPassword = changeUserPassword.getText().toString();
				newPassword = changeNewPassword.getText().toString();
				newPasswordRepeat = changeNewPasswordRepeat.getText().toString();
				
				if(oldPassword.length() > 0 || newPassword.length() > 0 || newPasswordRepeat.length() > 0){
					
					CharSequence text = "You have left one of the password fields blank, please revise and try again.";
    				int duration = Toast.LENGTH_LONG;

    				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
    				toast.show();
				} else {
					
					if(userRetrieved[0].equals("true")){
						
						if(newPassword.equals(newPasswordRepeat)) new ChangePassword().execute();
						else{
							CharSequence text = "Your new passwords do not match, please revise and try again";
	        				int duration = Toast.LENGTH_LONG;

	        				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
	        				toast.show();
						}
						
					}
				}
				
			}
		});
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@SuppressWarnings("unchecked")
	private void retrieveUserInformation(){
		
		Intent intent = getIntent();
		
		// retrieve all information from intent
		userRetrieved = intent.getStringArrayExtra("userRetrieved");
		userPayments = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_PAYMENTS);
		userMessages = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_MESSAGES);
		
	}
	
	/**
	 * Class used to change the details of a user
	 */
	class ChangeUserDetails extends AsyncTask<String, Boolean, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(UserDashboardChangeDetails.this);
            pDialog.setMessage("Changing your details, please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("user_id", userRetrieved[8]));
			params.add(new BasicNameValuePair("new_password", newPassword));
			
			json = jsonparser.makeHttpRequest(url_process_updatepassword, "GET", params);
			
			try {
				userInformation = json.getJSONArray("process");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			return null;
		}
	
		protected void onPostExecute (String file_url){
			
			pDialog.dismiss();
			
			 // determine what to do next based on the authentication result
            runOnUiThread(new Runnable() {
            	public void run() {
            	
            		Log.d("json change password", userInformation.toString());
            		
            		try {
						if(userInformation.getJSONObject(0).get("transact").equals("true")){
							
							// create an instance of a user dashboard, and send across user information
							Intent intent = new Intent(getApplicationContext(),UserDashboard.class);
							
							intent.putExtra(TAG_MESSAGES, userMessages);
							intent.putExtra(TAG_PAYMENTS, userPayments);
							intent.putExtra("userRetrieved", userRetrieved);
							 
						    startActivity(intent);
						}
						else if(userRetrieved[0].equals("false")){
							
							CharSequence text = "There has been an error processing this change of details, please check your internet" +
									"connection and try again.";
							int duration = Toast.LENGTH_LONG;

							Toast toast = Toast.makeText(getApplicationContext(), text, duration);
							toast.show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            });
			
		}
		
	}
	
	/**
	 * Class used to change the details of a user
	 */
	class ChangePassword extends AsyncTask<String, Boolean, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(UserDashboardChangeDetails.this);
            pDialog.setMessage("Changing your details, please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("user_id", userRetrieved[8]));
			params.add(new BasicNameValuePair("user_forename", userForename));
			params.add(new BasicNameValuePair("user_surname", userSurname));
			params.add(new BasicNameValuePair("user_email", userEmail));
			params.add(new BasicNameValuePair("user_phone", userPhone));
			params.add(new BasicNameValuePair("addr_line_1", userAddrl1));
			params.add(new BasicNameValuePair("addr_line_2", userAddrl2));
			params.add(new BasicNameValuePair("addr_postcode", userPostcode));
			params.add(new BasicNameValuePair("city_name", userCityname));
			
			json = jsonparser.makeHttpRequest(url_process_updateuser, "GET", params);
			
			try {
				userInformation = json.getJSONArray("process");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			return null;
		}
	
		protected void onPostExecute (String file_url){
			
			pDialog.dismiss();
			
			 // determine what to do next based on the authentication result
            runOnUiThread(new Runnable() {
            	public void run() {
            	
            		Log.d("json object user information", userInformation.toString());
            		
            		try {
						if(userInformation.getJSONObject(0).get("transact").equals("true")){
							
							// create an instance of a user dashboard, and send across user information
							Intent intent = new Intent(getApplicationContext(),UserDashboard.class);
							
							intent.putExtra(TAG_MESSAGES, userMessages);
							intent.putExtra(TAG_PAYMENTS, userPayments);
							intent.putExtra("userRetrieved", userRetrieved);
							 
						    startActivity(intent);
						}
						else if(userRetrieved[0].equals("false")){
							
							CharSequence text = "There has been an error processing this change of details, please check your internet" +
									"connection and try again.";
							int duration = Toast.LENGTH_LONG;

							Toast toast = Toast.makeText(getApplicationContext(), text, duration);
							toast.show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            });
			
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_dashboard_change_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
