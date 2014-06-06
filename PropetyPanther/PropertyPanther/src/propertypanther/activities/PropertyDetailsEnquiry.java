package propertypanther.activities;

import propertypanther.operations.RegularExpressions;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.example.propertypanther.R;

/**
 * 
 * An activity that will run a script that will send details about a property
 * to a user, who has entered a valid email address. 
 * 
 * @author Adam Stevenson
 *
 */
public class PropertyDetailsEnquiry extends Activity {
	
	private RegularExpressions regExpressions = new RegularExpressions();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_details_enquiry);
		
		Button   btnSubmit    = (Button) findViewById(R.id.enquirySubmitbtn);
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// if the email validation does not return false, run the script responsible for 
				// allowing a user to enquire about a property
				EditText emailAddress = (EditText) findViewById(R.id.enquiryEmail);
				
				if(regExpressions.emailValidation(emailAddress.getText().toString()))
				{
					
					WebView propertyEnquiryView = (WebView) findViewById(R.id.enquiryWebView);
					propertyEnquiryView.loadUrl("mailto:" + emailAddress);
					
				}
			}
		});
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.property_details_enquiry, menu);
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
