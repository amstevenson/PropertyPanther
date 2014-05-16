package propertypanther.activities;

import java.util.ArrayList;
import java.util.HashMap;

import propertypanther.dashboard.adapters.DashboardPaymentsListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.propertypanther.R;

/**
 * 
 * An activity that displays all of the payments for a user in a ListView.
 * 
 * @author Adam Stevenson
 *
 */
public class UserDashboardPayments extends Activity {

    // Linked hash map for messages - in a specific order
    private ArrayList<HashMap<String, String>> userPayments;
	private DashboardPaymentsListAdapter adapter;
    
	private String TAG_PAYMENTS = "payments";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_dashboard_payments);
	
		// retrieve the payments associated with the user
		populatePayments();
		
		getActionBar().setHomeButtonEnabled(true);
	}

	@SuppressWarnings("unchecked")
	public void populatePayments(){
		
		Intent intent = getIntent();
		
		// Hashmap for collecting user messages
		userPayments = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_PAYMENTS);
		
		// setting the messages list adapter
		ListView lvPayments = (ListView) findViewById(R.id.listViewPayments);
		adapter = new DashboardPaymentsListAdapter(getApplicationContext(),
				userPayments);
		lvPayments.setAdapter(adapter);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_dashboard_payments, menu);
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
