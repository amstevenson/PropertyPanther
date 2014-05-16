package propertypanther.activities;

import java.util.ArrayList;
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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.propertypanther.R;

/**
 * 
 * An activity responsible for uploading information inserted by the user
 * for the purpose of registering and creating a new account.
 * 
 * @author Adam Stevenson
 *
 */
public class MainActivitySignupForm extends Activity {

	// Regular expressions from relevant class
	private RegularExpressions regExpressions = new RegularExpressions();
    
	// Progress Dialog
    private ProgressDialog pDialog;
	
    // variables for async register task
    private static String user_forename;
    private static String user_surname;
    private static String user_email;
    private static String user_password;
    
    // url for middleware register script
    private static final String url_register_user = "http://propertypanther.info:8080/PantherAPI/process_register.jsp";
    
    // Json parser object
    private JSONParser jsonParser = new JSONParser();
    
    private JSONArray userRegistered;
    private JSONObject json;
    String hasCompleted = "true";
    
    // toast variables
	Context context;
	CharSequence text;
	int duration;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_signup_form);
		
		Button btnSubmit        = (Button) findViewById(R.id.signupSubmitBtn);
		
		// apply context to toast variable
		context = getApplicationContext();
		
		btnSubmit.setOnClickListener(new OnClickListener (){

			@Override
			public void onClick(View v) {
				
				TextView txtForename       = (TextView) findViewById(R.id.signupForename);
				TextView txtSurname        = (TextView) findViewById(R.id.signupSurname);
				TextView txtPassword       = (TextView) findViewById(R.id.signupPassword); 
				TextView txtRepeatPassword = (TextView) findViewById(R.id.signupPasswordRepeat);
				TextView txtEmail          = (TextView) findViewById(R.id.signupEmail);
				
				user_forename = txtForename.getText().toString();
				user_surname = txtSurname.getText().toString();
				user_email = txtEmail.getText().toString();
				user_password = txtPassword.getText().toString();
				

				
				// If any required fields are empty, request that the user revises the form and completes fully
				if("".equals(txtForename.getText().toString()) || "".equals(txtSurname.getText().toString()) 
						|| "".equals(txtPassword.getText().toString()) || "".equals(txtRepeatPassword.getText().toString())
						|| "".equals(txtEmail.getText().toString()))
				{
					text = "One or more fields have been left empty, please revise and complete fully";
					duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				// If password fields are under a certain length, notify user
				else if (txtPassword.getText().toString().length() < 5 && txtRepeatPassword.getText().toString().length() < 5)
				{
					text = "Passwords need to match and be over 5 letters long";
					duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				// If the email is in the correct format, and the password field lengths are more than 5
				// ensure that the password fields match, then the script responsible for creating the new
				// user will be executed. 
				else if(regExpressions.emailValidation(txtEmail.getText().toString()) &&
						txtPassword.getText().toString().length() > 5 && txtRepeatPassword.getText().toString().length() > 5)
				{
					if(txtPassword.getText().toString().equals(txtRepeatPassword.getText().toString())){
						
					new RegisterUser().execute();
					}
					else
					{
						text = "Your passwords do not match, please ensure they are both the same and try again";
						duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					
				}
			}
		});
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	/**
	 * Class used to register a user
	 */
	class RegisterUser extends AsyncTask<String, Boolean, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivitySignupForm.this);
            pDialog.setMessage("Registering user, please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("user_forename", user_forename));
			params.add(new BasicNameValuePair("user_surname", user_surname));
			params.add(new BasicNameValuePair("user_email", user_email));
			params.add(new BasicNameValuePair("user_pass", user_password));
			
			try {
				
				json = jsonParser.makeHttpRequest(url_register_user, "GET", params);
				
				Log.d("json to string", json.toString());
				
				userRegistered = json.getJSONArray("register");
				
				hasCompleted = userRegistered.getJSONObject(0).getString("completion").toString();

				Log.d("hasCompleted", hasCompleted);
				
			} catch (JSONException e) {

				e.printStackTrace();
			}
			
			return null;
		}
	
		protected void onPostExecute (String file_url){
			
			pDialog.dismiss();
			
			new Runnable() {
				public void run() {
					
					if(hasCompleted.equals("true")){
						
						text = "The registration process has been completed. ";
						duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
						
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						
						startActivity(intent);
						
					} else {
						
						text = "There has been an error registering, please check your internet connection and try again. ";
						duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					
				}
			};
			
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_signup_form, menu);
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
