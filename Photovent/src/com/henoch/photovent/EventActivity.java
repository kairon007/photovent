package com.henoch.photovent;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * @author Henoch
 * Activity for interfacing with event photos and adding user's photos to event database.
 * ImagerPagerAdapter Class and Camera capturing methods taken from 
 * http://developer.android.com/training/displaying-bitmaps/display-bitmap.html
 */
public class EventActivity extends FragmentActivity {
	
	static final int REQUEST_IMAGE_CAPTURE = 1;

	private String eventName;
	private ImagePagerAdapter mAdapter;
	private ViewPager mPager;
	private List<String> encodedImages; // List of encoded strings corresponding to photo bitmaps
	private Firebase eventDbRef; //Reference to event data
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		//Fetches event name passed from the main activity
		eventName = getIntent().getStringExtra(Consts.EVENT_NAME_KEY);
		
		setTitle(eventName);
				
		eventDbRef = new Firebase(Consts.FIREBASE_ROOT_URL + "/" + eventName);
		encodedImages = new ArrayList<String>();
		
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
		
		eventDbRef.addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
				encodedImages.add(snapshot.getValue().toString());
				mAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		

	}
	
	public void dispatchTakePictureIntent(View v) {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
		    	startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE); 
		    }
		} else {
			Toast.makeText(getApplicationContext(), 
					"Phone Camera Unavailable", Toast.LENGTH_SHORT).show();	
		}
			
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        
	        new BitmapUploaderTask(eventDbRef, this.getApplicationContext()).execute(imageBitmap);
	        
	    }
	}
	
	public class ImagePagerAdapter extends FragmentStatePagerAdapter {
		
        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
        	return encodedImages.size();
        }
        
        @Override
        public Fragment getItem(int position) {
        	return ImageDetailFragment.newInstance(encodedImages.get(position));
        }
    }
	
}
