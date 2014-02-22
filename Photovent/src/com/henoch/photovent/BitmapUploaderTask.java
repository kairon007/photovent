package com.henoch.photovent;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * @author Henoch
 * Asynchronous task for decoding bitmap into string and uploading it to database
 */
public class BitmapUploaderTask extends AsyncTask<Bitmap, Void , Boolean> {
		
	private Firebase dbRef; //Reference to an event in photovent's firebase
	private Context appContext; //Used for displaying error messages to user via Toasts
	
	public BitmapUploaderTask(Firebase db, Context context) {
		dbRef = db;
		appContext = context;
	}
	
	
	//Returns true if the bitmap is encoded and uploaded successfully
	@Override
	protected Boolean doInBackground(Bitmap... param) {
		Bitmap bitmap = param[0];
		
		try {
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
			byte [] arr = baos.toByteArray();
			
			String encodedString = Base64.encodeBytes(arr);
			
			Firebase bitmapLocation = dbRef.push();
			bitmapLocation.setValue(encodedString);		
			
			return true;
		} catch (Exception e) {
			//An error occurred when uploading or encoding string
			return false;
		}
	
	}
	
	@Override
	protected void onPostExecute(Boolean succesfull) {
		
		if (!succesfull) {
			Toast.makeText(appContext, "Could not upload photo", 
					Toast.LENGTH_SHORT).show();
		}
			
	}

}
