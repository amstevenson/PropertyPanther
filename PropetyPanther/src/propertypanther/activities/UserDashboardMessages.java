package propertypanther.activities;

import java.util.ArrayList;
import java.util.HashMap;

import propertypanther.dashboard.adapters.DashboardMessagesListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.propertypanther.R;

/**
 * An activity that displays all of the messages for a user in a ListView.
 * 
 * @author Adam Stevenson
 *
 */
public class UserDashboardMessages extends Activity {

    // Linked hash map for messages - in a specific order
    private ArrayList<HashMap<String, String>> userMessages;
	private DashboardMessagesListAdapter adapter;
    
	private String TAG_MESSAGES = "messages";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_dashboard_messages);
		
		// get the messages belonging to the user
		populateMessages();
		
		final ListView listMessages = (ListView) findViewById(R.id.listViewMessages);
		
		// Click listener for selecting an individual message and displaying it
		// in a way that allows the user to see the whole message
		listMessages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?>  parent, View view,
                    int position, long id) {
				
				String[] messageInformation = new String[10];
				
				messageInformation[0] = userMessages.get(position).get("message_body");
				messageInformation[1] = userMessages.get(position).get("message_to");
				messageInformation[2] = userMessages.get(position).get("message_from");
				messageInformation[3] = userMessages.get(position).get("authentication");
				messageInformation[4] = userMessages.get(position).get("message_id");
				messageInformation[5] = userMessages.get(position).get("message_read");
				messageInformation[6] = userMessages.get(position).get("message_type");
				messageInformation[7] = userMessages.get(position).get("message_sent");
				messageInformation[8] = userMessages.get(position).get("user_forename");
				messageInformation[9] = userMessages.get(position).get("user_surname");
				
				Intent intent = new Intent(getApplicationContext(), UserDashBoardWholeMessage.class);
				intent.putExtra("message", messageInformation);
				startActivity(intent);
			}
		});
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@SuppressWarnings("unchecked")
	public void populateMessages(){
		
		Intent intent = getIntent();
		
		// Hashmap for collecting user messages
		userMessages = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(TAG_MESSAGES);
		
		// setting the messages list adapter
		ListView lvMessages = (ListView) findViewById(R.id.listViewMessages);
		adapter = new DashboardMessagesListAdapter(getApplicationContext(),
				userMessages);
		lvMessages.setAdapter(adapter);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_dashboard_messages, menu);
		
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
