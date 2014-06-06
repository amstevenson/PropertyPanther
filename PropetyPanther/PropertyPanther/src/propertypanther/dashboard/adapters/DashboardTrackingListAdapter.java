package propertypanther.dashboard.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.propertypanther.R;

public class DashboardTrackingListAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<HashMap<String, String>> userTracking;
	private LayoutInflater mInflater;
	
	
	
    // JSON Node names for collecting all of a users messages 
	private static final String TAG_TADDRESS = "addr_line_1";
	private static final String TAG_TCITY    = "city_name";
	private static final String TAG_TROOM    = "prop_num_rooms";
	
	public DashboardTrackingListAdapter(Context context, ArrayList<HashMap<String, String>> userTracking){
		this.context 			  = context;
		this.userTracking         = userTracking;
	}
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userTracking.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userTracking.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_all_trackedproperties , null);
        }
		
		TextView trackAddress = (TextView) convertView.findViewById(R.id.trackedAddress);
		TextView trackCity    = (TextView) convertView.findViewById(R.id.trackedCity);
		TextView trackRoom    = (TextView) convertView.findViewById(R.id.trackedRoomCount);
		
		trackAddress.setText(userTracking.get(position).get(TAG_TADDRESS));
		trackCity.setText(userTracking.get(position).get(TAG_TCITY));
		trackRoom.setText("   " + userTracking.get(position).get(TAG_TROOM));
		
		
		
		return convertView;
	}

}
