package propertypanther.operations;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * All network functions are stored here, mostly related to 
 * connectivity. 
 * 
 * @author Adam Stevenson
 *
 */
public class NetworkFunctions {
		
		// check connection to the network
		public boolean isNetConnected(Context context) {
		
			// check the system service
		    ConnectivityManager cm;
		    NetworkInfo info = null;
		    try
		    {
		        cm = (ConnectivityManager) 
		              context.getSystemService(Context.CONNECTIVITY_SERVICE);
		        info = cm.getActiveNetworkInfo();
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		    if (info != null) return true;
		    
		    else return false;
	}
		
		
		
		
}


