package propertypanther.propertylist.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import propertypanther.operations.OperationUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.propertypanther.R;

/**
 * 
 * A class that populates a user interface consisting of rooms with values derived from 
 * a HashMap of details, and a Hashtable of images.
 * 
 * @author Adam Stevenson
 *
 */
public class PropertyListRoomsAdapter extends BaseAdapter {

	private OperationUtils operationUtils = new OperationUtils();
	private Context context;
	private ArrayList<HashMap<String, String>> propertiesListRooms;
	private ArrayList<Hashtable<String, Bitmap>> propertiesListRoomsImages;
	private LayoutInflater mInflater;
	
    // JSON Node names for collecting all of a users messages 
    // JSON Node names for collecting all of a properties rooms
	private static final String TAG_ROOMSTATUS      = "room_status";
    private static final String TAG_ROOMDETAILS     = "room_details";
    private static final String TAG_ROOMPRICE  		= "room_price";
    private static final String TAG_ROOMIMAGE       = "room_image";
	
	public PropertyListRoomsAdapter(Context context, ArrayList<HashMap<String, String>> propertiesListRooms,
			ArrayList<Hashtable<String, Bitmap>> propertiesListRoomsImages){
		this.context 			  = context;
		this.propertiesListRooms = propertiesListRooms;
		this.propertiesListRoomsImages = propertiesListRoomsImages;
	}
	
	@Override
	public int getCount() {

		return propertiesListRooms.size();
	}

	@Override
	public Object getItem(int position) {

		return propertiesListRooms.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_all_rooms , null);
        }
		
		// change the property description values
		TextView roomStatus = (TextView) convertView.findViewById(R.id.rStatus);
		TextView roomPrice = (TextView) convertView.findViewById(R.id.rPrice);
		TextView roomDetails = (TextView) convertView.findViewById(R.id.rDetails);
		ImageView roomImage = (ImageView) convertView.findViewById(R.id.roomImage);
		
		roomStatus.setText(propertiesListRooms.get(position).get(TAG_ROOMSTATUS).toString());
		roomPrice.setText(propertiesListRooms.get(position).get(TAG_ROOMPRICE).toString());
		roomDetails.setText(propertiesListRooms.get(position).get(TAG_ROOMDETAILS).toString());
		
		// set the image for the room
		Bitmap roomBitmap = propertiesListRoomsImages.get(position).get(TAG_ROOMIMAGE);
			
		// resize using relevant operations function
		roomBitmap = operationUtils.getResizedBitmap(roomBitmap, 720, 300);
			
		// assign the bitmap
		roomImage.setImageBitmap(roomBitmap);
		
		
		return convertView;
	}
	
	
}
