package propertypanther.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.propertypanther.R;

/**
 * 
 * An activity that shows a MapView of a property. 
 * 
 * @author Adam Stevenson
 *
 */
public class PropertyDetailsMapView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_details_map_view);

		String[] retrievedProperty;
		
		Intent intent = getIntent();
		
		retrievedProperty = intent.getStringArrayExtra("retrievedProperty");
		
		// Use a WebView to call up either google maps or Chrome, to allow users to
		// view their chosen property.
		// We could make a GoogleMap object for this, register a key with Google, and go about it
		// in this way, but at the moment the database does not return the longitude/latitude for each
		// property (the LatLng object requires this), so therefore this could only be done by including a TextFile that holds ALL the information
		// that pertains to each address in the UK, which would be rather excessive. 
		WebView propertyMapView = (WebView) findViewById(R.id.propertyMapView);
		propertyMapView.loadUrl("http://www.google.co.uk/maps/place/" + retrievedProperty[8]);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.property_details_map_view, menu);
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
