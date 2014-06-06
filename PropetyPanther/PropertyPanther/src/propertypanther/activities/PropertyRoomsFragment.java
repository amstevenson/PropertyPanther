package propertypanther.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import propertypanther.propertylist.adapters.PropertyListRoomsAdapter;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.propertypanther.R;

/**
 * A fragment of the activity PropertyMain that displays a list of rooms, with
 * images, and general information regarding the cost/status.
 * 
 * @author Adam Stevenson
 *
 */
public class PropertyRoomsFragment extends Fragment {

    // hash map for messages
    private ArrayList<HashMap<String, String>> propertyRooms;
    private ArrayList<Hashtable<String, Bitmap>> propertiesRoomsImages;
    
    // adapter for UI population
	private PropertyListRoomsAdapter             propListRoomsAdapter;
    
    // get bundles 
    private Bundle propertyBundle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.fragment_property_rooms, container, false);
		
		// Hashmap for collecting user messages and payments
		propertyRooms = new ArrayList<HashMap<String, String>>();
		
		// retrieve the information from the bundle
		getExtras();
		
    	//Updating parsed JSON data into ListView using HashMap 
		// setting the messages list adapter
		ListView propertyRoomsLv = (ListView) view.findViewById(R.id.listRooms);
		propListRoomsAdapter = new PropertyListRoomsAdapter(getActivity().getApplicationContext(), propertyRooms, propertiesRoomsImages);
		propertyRoomsLv.setAdapter(propListRoomsAdapter);
        
        
        
        
	    return view;
	}
	
	@SuppressWarnings("unchecked")
	private void getExtras(){
		
		// Retrieve serializable extra (rooms); suppressed warnings unchecked, so move to another method
		propertyBundle = getArguments();
		
		propertyRooms = (ArrayList<HashMap<String, String>>) propertyBundle.getSerializable("propertyRooms");
		propertiesRoomsImages = (ArrayList<Hashtable<String, Bitmap>>) propertyBundle.getSerializable("propertyRoomsImages");
		
		
		
	}
}
