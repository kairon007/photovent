package com.henoch.photovent;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author Google
 * Decodes encoded string into corresponding bitmap. The general structure and most of the code
 * is taken from http://developer.android.com/training/displaying-bitmaps/process-bitmap.html and
 * http://android-developers.blogspot.ca/2010/07/multithreading-for-performance.html
 */
public class BitmapDecoderTask extends AsyncTask<Void, Void, Bitmap> {
    
	private final WeakReference<ImageView> imageViewReference;
    private String encodedImage;
   
    private BitmapDecoderTask(String URL, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        encodedImage = URL;
    }
   
    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Void... params) {
    	
    	try {
			
    		byte [] encodeByte = Base64.decode(encodedImage);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte,  0, encodeByte.length);
			return bitmap;
		
    	} catch (IOException e) {
			Log.i("IO", "Bitmap decode error");
			e.printStackTrace();
		}
    	
    	return null;
    
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            BitmapDecoderTask bitmapDecoderTask = getBitmapDecoderTask(imageView);
            if (imageView != null && this == bitmapDecoderTask) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    
         
	private static class DecodedDrawable extends ColorDrawable {
	    private final WeakReference<BitmapDecoderTask> bitmapDownloaderTaskReference;

	    public DecodedDrawable(BitmapDecoderTask bitmapDownloaderTask) {
	        super(Color.BLACK);
	        bitmapDownloaderTaskReference =
	            new WeakReference<BitmapDecoderTask>(bitmapDownloaderTask);
	    }

	    public BitmapDecoderTask getBitmapDecoderTask() {
	        return bitmapDownloaderTaskReference.get();
	    }
	}
	
	
    public static void decodeBitmap(ImageView imageView, String encoded) {
   	 
    	if (cancelPotentialTask(encoded, imageView)) {
             BitmapDecoderTask task = new BitmapDecoderTask(encoded, imageView);
             DecodedDrawable downloadedDrawable = new DecodedDrawable(task);
             imageView.setImageDrawable(downloadedDrawable);
             task.execute();
         }
  
    }
    
    private static boolean cancelPotentialTask(String bmp, ImageView imageView) {
        BitmapDecoderTask bitmapDownloaderTask = getBitmapDecoderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.encodedImage;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(bmp))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param imageView
     * @return Task associated with given imageView, if it exists.
     */
    private static BitmapDecoderTask getBitmapDecoderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DecodedDrawable) {
                DecodedDrawable downloadedDrawable = (DecodedDrawable)drawable;
                return downloadedDrawable.getBitmapDecoderTask();
            }
        }
        return null;
    }

}


