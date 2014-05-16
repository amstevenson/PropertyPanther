package propertypanther.operations;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * 
 * A compilations of helpful functions that mostly focus on adjusting objects to different sizes.
 * 
 * 
 * @author Adam Stevenson
 *
 */
public class OperationUtils {

	/**
	 * 
	 * A function that returns a stored drawable in a specific size to avoid crashes
	 * regarding memory leaks, memory size overloads...
	 * 
	 * @param res android resources
	 * @param resId the unique identifier of the drawable
	 * @param reqWidth the required width - determined by the user
	 * @param reqHeight the required height - determined by the user
	 * @return bitmap in a size determined by the user
	 */
	public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
		
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	/**
	 * 
	 * recreates a bitmap with a new height/width determined by the user. This can be used
     * in situations where an API stipulation of less than 19 is required; since the set functions for a bitmap
     * regarding the overall functionality of this method require this value.
     * This should be called whenever an image is downloaded from the Internet, or a local machine, to reduce memory leaks/overloads
     * caused by images that have very high dimensions.
	 * 
	 * @param bm the bitmap passed to the method
	 * @param newHeight the new height of the recreated bitmap
	 * @param newWidth the new height of the recreated bitmap
	 * @return a bitmap recreated with parameters determined by the user. 
	 */
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // Create a matrix for the manipulation
	    Matrix matrix = new Matrix();
	    // Resize the bitmap
	    matrix.postScale(scaleWidth, scaleHeight);
	    // recreate the bitmap with a new size
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	
}
