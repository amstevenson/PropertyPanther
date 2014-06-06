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
import android.widget.TextView;

import com.example.propertypanther.R;

public class DashboardPaymentsListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HashMap<String, String>> userPayments;
	private LayoutInflater mInflater;
	
    // JSON Node names for collecting all of a users payments 
	private static final String TAG_PAYMENTAMOUNT       = "payment_amount";
    private static final String TAG_PAYMENTDUE    		= "payment_due";
    private static final String TAG_PAYMENTRECEIVED     = "payment_received";
    private static final String TAG_PAYMENTSTATUS 		= "payment_status";
	
	public DashboardPaymentsListAdapter(Context context, ArrayList<HashMap<String, String>> userPayments){
		this.context 			  = context;
		this.userPayments = userPayments;
	}

	@Override
	public int getCount() {

		return userPayments.size();
	}

	@Override
	public Object getItem(int position) {

		return userPayments.get(position);
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
            convertView = mInflater.inflate(R.layout.user_dashboard_list_payment , null);
        }
		
		// payment information
		TextView paymentAmount        = (TextView) convertView.findViewById(R.id.paymentListAmount);
		TextView paymentDue          = (TextView) convertView.findViewById(R.id.paymentListDue);
		TextView paymentReceived          = (TextView) convertView.findViewById(R.id.paymentListReceived);
		TextView paymentStatus       = (TextView) convertView.findViewById(R.id.paymentListStatus);
		
		// for each type of payment, determine the colour of the payment status
		if(userPayments.get(position).get(TAG_PAYMENTSTATUS).equals("PAID LATE")){
		
			paymentAmount.setText(userPayments.get(position).get(TAG_PAYMENTAMOUNT).toString());
			paymentDue.setText(userPayments.get(position).get(TAG_PAYMENTDUE).toString());
			paymentReceived.setText(userPayments.get(position).get(TAG_PAYMENTRECEIVED).toString());
			paymentStatus.setText(userPayments.get(position).get(TAG_PAYMENTSTATUS).toString());
			paymentStatus.setTextColor(Color.parseColor("#f92673"));
		}
		else if(userPayments.get(position).get(TAG_PAYMENTSTATUS).equals("PAID")){
			
			paymentAmount.setText(userPayments.get(position).get(TAG_PAYMENTAMOUNT).toString());
			paymentDue.setText(userPayments.get(position).get(TAG_PAYMENTDUE).toString());
			paymentReceived.setText(userPayments.get(position).get(TAG_PAYMENTRECEIVED).toString());
			paymentStatus.setText(userPayments.get(position).get(TAG_PAYMENTSTATUS).toString());
			paymentStatus.setTextColor(Color.parseColor("#a5e22b"));
		}
		else{
			
			paymentAmount.setText(userPayments.get(position).get(TAG_PAYMENTAMOUNT).toString());
			paymentDue.setText(userPayments.get(position).get(TAG_PAYMENTDUE).toString());
			paymentReceived.setText(userPayments.get(position).get(TAG_PAYMENTRECEIVED).toString());
			paymentStatus.setText(userPayments.get(position).get(TAG_PAYMENTSTATUS).toString());
			paymentStatus.setTextColor(Color.parseColor("#ff9722"));
		}
		
		return convertView;
	}
	
	
	
}
