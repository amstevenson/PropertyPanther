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
 * A class that populates a user interface consisting of properties with values derived from 
 * a HashMap of details, and a Hashtable of images.
 * 
 * @author Adam Stevenson
 *
 */
public class PropertyListAdapter extends BaseAdapter {
	
	private OperationUtils operationUtils = new OperationUtils();
	private Context context;
	private ArrayList<HashMap<String, String>> propertiesList;
	private ArrayList<Hashtable<String, Bitmap>> propertiesListImages;
	private LayoutInflater mInflater;
	
    // JSON Node names for collecting all of a users properties
    private static final String TAG_PADDRESS     = "addr_line_1";
    private static final String TAG_PIMG         = "prop_image_link";
    private static final String TAG_PAVGCOST 	 = "average_price";
	
	public PropertyListAdapter(Context context, ArrayList<HashMap<String, String>> propertiesList,
			ArrayList<Hashtable<String, Bitmap>> propertiesListImages){
		this.context 			  = context;
		this.propertiesList = propertiesList;
		this.propertiesListImages = propertiesListImages;
	}
	
	@Override
	public int getCount() {

		return propertiesList.size();
	}

	@Override
	public Object getItem(int position) {

		return propertiesList.get(position);
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
            convertView = mInflater.inflate(R.layout.list_all_properties , null);
        }
		
		// change the property description values
		TextView avgPrice = (TextView) convertView.findViewById(R.id.pAvgCost);
		TextView propAddress1 = (TextView) convertView.findViewById(R.id.pProperty);
		ImageView propImage = (ImageView) convertView.findViewById(R.id.pImage);
		
		avgPrice.setText(propertiesList.get(position).get(TAG_PAVGCOST).toString());
		propAddress1.setText(propertiesList.get(position).get(TAG_PADDRESS).toString());
		
		// set the image for the property
		Bitmap propertyBitmap = propertiesListImages.get(position).get(TAG_PIMG);
			
		// resize using relevant operations function
		propertyBitmap = operationUtils.getResizedBitmap(propertyBitmap, 720, 300);
			
		// assign the bitmap
		propImage.setImageBitmap(propertyBitmap);
		
		
		return convertView;
	}
}
