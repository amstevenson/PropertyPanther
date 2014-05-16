package propertypanther.dashboard.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.propertypanther.R;

public class DashboardMessagesListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HashMap<String, String>> userMessages;
	private LayoutInflater mInflater;
	
	
	
    // JSON Node names for collecting all of a users messages 
	private static final String TAG_MESSAGEBODY         = "message_body";
    private static final String TAG_MESSAGETYPE    		= "message_type";
    private static final String TAG_MESSAGESENT 		= "message_sent";
    private static final String TAG_MESUSERFORENAME       = "user_forename";
    private static final String TAG_MESUSERSURNAME        = "user_surname";
	
	public DashboardMessagesListAdapter(Context context, ArrayList<HashMap<String, String>> userMessages){
		this.context 			  = context;
		this.userMessages = userMessages;

	}

	@Override
	public int getCount() {

		return userMessages.size();
	}

	@Override
	public Object getItem(int position) {

		return userMessages.get(position);
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
            convertView = mInflater.inflate(R.layout.user_dashboard_list_message , null);
        }
		
		// message information
		ImageView messageImage        = (ImageView) convertView.findViewById(R.id.messageImage);
		TextView messageName          = (TextView) convertView.findViewById(R.id.messageName);
		TextView messageDate          = (TextView) convertView.findViewById(R.id.messageDate);
		TextView messageContent       = (TextView) convertView.findViewById(R.id.messageContent);
		
		// for each type of message, determine the colour of the TAG_MESSAGEFROM
		if(userMessages.get(position).get(TAG_MESSAGETYPE).equals("ALERT")){
		
	        messageImage.setImageResource(R.drawable.mail); 
			messageName.setText(userMessages.get(position).get(TAG_MESUSERFORENAME).toString() + " "
							    + userMessages.get(position).get(TAG_MESUSERSURNAME));
			messageName.setTextColor(Color.parseColor("#f92673"));
			
			messageDate.setText(userMessages.get(position).get(TAG_MESSAGESENT).toString());
			messageContent.setText(userMessages.get(position).get(TAG_MESSAGEBODY));
		}
		else if(userMessages.get(position).get(TAG_MESSAGETYPE).equals("MAINTENANCE")){
			
	        messageImage.setImageResource(R.drawable.mail); 
			messageName.setText(userMessages.get(position).get(TAG_MESUSERFORENAME).toString() + " "
				    + userMessages.get(position).get(TAG_MESUSERSURNAME));
			messageName.setTextColor(Color.parseColor("#ff9722"));
			messageDate.setText(userMessages.get(position).get(TAG_MESSAGESENT).toString());
			messageContent.setText(userMessages.get(position).get(TAG_MESSAGEBODY));
		}
		else{
			
	        messageImage.setImageResource(R.drawable.mail); 
			messageName.setText(userMessages.get(position).get(TAG_MESUSERFORENAME).toString() + " "
				    + userMessages.get(position).get(TAG_MESUSERSURNAME));
			messageName.setTextColor(Color.parseColor("#a5e22b"));
			messageDate.setText(userMessages.get(position).get(TAG_MESSAGESENT).toString());
			messageContent.setText(userMessages.get(position).get(TAG_MESSAGEBODY));
		} 
		
		return convertView;
	}
	
	
	
}
