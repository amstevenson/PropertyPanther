package propertypanther.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.propertypanther.R;

/**
 * 
 * An activity that displays the information of one selected message, from the
 * activity responsible for listing all of the messages
 * 
 * @author Adam Stevenson
 *
 */
public class UserDashBoardWholeMessage extends Activity {

	// Linked hash map for messages - in a specific order
    private String[] userMessage;
    
    private static final String TAG_MESSAGE = "message";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_dash_board_whole_message);
		
		populateUI();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_dash_board_whole_message, menu);
		return true;
	}

	public void populateUI(){
		
		Intent intent = getIntent();
		
		userMessage = new String[10];
		
		// Hashmap for collecting user messages
		userMessage = intent.getStringArrayExtra(TAG_MESSAGE);
		
		if(userMessage !=null) for(int i = 0; i < userMessage.length; i++) Log.d("message " + i, userMessage[i]);
		
		TextView indivMessageDate = (TextView) findViewById(R.id.indivMessageDate);
		indivMessageDate.setText(userMessage[7]);
		
		TextView indivMessageContent = (TextView) findViewById(R.id.indivMessageContent);
		indivMessageContent.setText(userMessage[0]);
		
		TextView indivMessageName = (TextView) findViewById(R.id.indivMessageName);
		indivMessageName.setText(userMessage[8] + " " + userMessage[9]);
		
		if(userMessage[6].equals("MAINTENANCE")) indivMessageName.setTextColor(Color.parseColor("#ff9722"));
		else if(userMessage[6].equals("ALERT")) indivMessageName.setTextColor(Color.parseColor("#f92673"));
		else indivMessageName.setTextColor(Color.parseColor("#a5e22b"));
		
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
