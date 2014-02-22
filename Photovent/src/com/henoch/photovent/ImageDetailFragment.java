package com.henoch.photovent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author Google
 * Taken from http://developer.android.com/training/displaying-bitmaps/display-bitmap.html
 */
public class ImageDetailFragment extends Fragment {
		
		private static final String IMAGE_DATA_EXTRA = "encodedBitmapString";
    	private ImageView mImageView;
    	private String encodedString; //to be decoded into bitmap and displayed in mImageView

    public static ImageDetailFragment newInstance(String encoded) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, encoded);
        f.setArguments(args);
        
        return f;
    }

    // Empty constructor, required as per Fragment docs
    public ImageDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encodedString = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // image_detail_fragment.xml contains just an ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.pagerImageView);
        return v;
    }
    
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BitmapDecoderTask.decodeBitmap(mImageView, encodedString); //Downlaod image into imageview
    }
}