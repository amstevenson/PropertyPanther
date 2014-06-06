package propertypanther.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import propertypanther.operations.JSONParser;
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

public class UserDashboardMaintenanceRequest extends Activity {

	private String[] userRetrieved;
	private static String user_id;
	private static String request_details;
	
	// parser
	private JSONParser jsonParser = new JSONParser();
	
	// objects
	private EditText requestDetails;
	private Button   requestSubmit;
	
	// progress dialog
	private ProgressDialog pDialog;
	
	// middleware script link
	private final static String url_process_request = "http://propertypanther.info:8080/PantherAPI/maint_add.jsp";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_dashboard_maintenance_request);

		requestDetails = (EditText) findViewById(R.id.maintRequestDetails);
		requestSubmit  = (Button)   findViewById(R.id.btnmaintRequestProcess);
		
		Intent intent = getIntent();
		
		// get the user id from intent
		userRetrieved = intent.getStringArrayExtra("userRetrieved");
		user_id = userRetrieved[8];
		
		// button click listener
		requestSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				request_details = requestDetails.getText().toString();
				
				new SubmitMaintenanceRequest().execute();
				
			}
		});
	}
	
	/**
	 * Class used to retrieve property information from a Middleware script
	 * and populate the UI using an ArrayList of HashMaps
	 */
	class SubmitMaintenanceRequest extends AsyncTask<String, Boolean, String> {

	    /**
	     * Before starting background thread Show Progress Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	       
	        pDialog = new ProgressDialog(UserDashboardMaintenanceRequest.this);
	        pDialog.setMessage("Submitting your maintenance request, pleae be patient...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();
	    }
		
		
		@Override
		protected String doInBackground(String... args) {
			
			// Setup the parameters to send
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", user_id));
			params.add(new BasicNameValuePair("request_details", request_details));
			
			JSONObject json = jsonParser.makeHttpRequest(url_process_request, "GET", params);
			
			Log.d("after processing maintenance request result", json.toString());
			
			return null;
		}
		
		/*
		 * After completing background task, dismiss the progress dialog
		 * and update the UI - needs to be done when the middleware is written
		 */
		protected void onPostExecute (String file_url){

			// dismiss the dialog after getting all properties
	        pDialog.dismiss();
	        
	        CharSequence text = "Your maintenance request has been sent successfully.";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
			toast.show();
	        
			finish();
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard_maintenance_request, menu);
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
