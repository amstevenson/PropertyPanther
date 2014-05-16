package propertypanther.activities;

import java.util.ArrayList;
import java.util.HashMap;

import propertypanther.dashboard.adapters.DashboardTrackingListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.propertypanther.R;

public class UserDashboardTrackedProperties extends Activity {

	// Linked hash map for messages - in a specific order
    private ArrayList<HashMap<String, String>> trackedPropertiesList;
	private DashboardTrackingListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_dashboard_tracked_properties);

		trackedPropertiesList = new ArrayList<HashMap<String, String>>();
		
		// retrieve the tracked properties associated with the user
		populateTrackingDetails();
				
		getActionBar().setHomeButtonEnabled(true);
	}

	@SuppressWarnings("unchecked")
	private void populateTrackingDetails(){
		
		Intent intent = getIntent();
		
		// Hashmap for collecting user messages
		trackedPropertiesList = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("trackedPropertiesList");
		
		// setting the messages list adapter
		ListView lvTracking = (ListView) findViewById(R.id.listViewTrackedProperties);
		adapter = new DashboardTrackingListAdapter(getApplicationContext(),
				trackedPropertiesList);
		lvTracking.setAdapter(adapter);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_dashboard_tracked_properties,
				menu);
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
