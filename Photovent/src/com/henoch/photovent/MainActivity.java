package com.henoch.photovent;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * @author Henoch
 * Android interface for selecting or creating photovents.
 */
public class MainActivity extends Activity {
	
	private Firebase dbRef; //Photovent root database
	private List<String> eventNames; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		dbRef = new Firebase(Consts.FIREBASE_ROOT_URL);
		eventNames = new  ArrayList<String>();
	
		final ListView eventList = (ListView) this.findViewById(R.id.event_list_view);
		final ButtonListAdapter mAdapter = new ButtonListAdapter(this.getApplicationContext(), eventNames);
		eventList.setAdapter(mAdapter);
		
		//Synchronizes event names set with firebase
		dbRef.addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onChildAdded(DataSnapshot snapshot, String arg1) {
				String name = snapshot.getName();
				
				//Keep the event names in sorted order on insertion
				int index;
				
				for (index = 0; index < eventNames.size(); index++)
					if (name.compareTo(eventNames.get(index)) <= 0) break;
				
				eventNames.add(index, name);
				mAdapter.notifyDataSetChanged();
			
			}
			
			@Override
			public void onChildRemoved(DataSnapshot snapshot) {
				eventNames.remove(snapshot.getName());
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

		});
			
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * @author Henoch
	 * Adapter backing the event list listview.
	 */
	public class ButtonListAdapter extends BaseAdapter{
		
		private Context context;
		private List<String> eventNames;
		
		public ButtonListAdapter(Context c, List<String> names) {
			context = c;
			eventNames = names;
		}
		
		@Override
		public int getCount() {
			return eventNames.size();
		}

		@Override
		public Object getItem(int position) {
			return 0;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View v = inflater.inflate(R.layout.button_detail_component, parent, false);
			Button button = (Button) v.findViewById(R.id.eventButtonView);
			button.setText(eventNames.get(position));
			
			button.setOnClickListener(new BtnListener(button));
			
			return button;
		}
		
		
		/**
		 * @author Henoch
		 * Button listener for each event button in the event list.
		 */
		private class BtnListener implements OnClickListener {
			
			private String name;
			
			public BtnListener(Button b) {
				name = b.getText().toString();
			}
			
			@Override
			public void onClick(View v) {
				gotoEvent(name);
			}
			
		}

	}
	
	
	//Called when user wishes to create event
	public void createEvent(View v) {
		 	
		    EditText editText = (EditText) findViewById(R.id.eventNameInput);
		    String eventName = editText.getText().toString();
		    
		    if (eventName.isEmpty() || eventName.length() > 20) {
		    	Toast.makeText(this.getApplicationContext(), 
		    			"Event name must have 1 - 20 characters", Toast.LENGTH_SHORT).show();	
		    }else if (eventName.matches("^.*[^a-zA-Z0-9 ].*$")) {
		    	Toast.makeText(this.getApplicationContext(), 
		    			"Event name must contain only alpha-numeric characters", Toast.LENGTH_SHORT).show();	
		    } else if (eventNames.contains(eventName)) {
		    	Toast.makeText(this.getApplicationContext(), 
		    			"Event already exists", Toast.LENGTH_SHORT).show();			    
		    } else {
		    	dbRef.child(eventName).setValue("");	    
		    }
		    
	}
	
	//Called when the user wishes to go to an event
	private void gotoEvent(String eventName) {
		Intent intent = new Intent(this, EventActivity.class);
	    intent.putExtra(Consts.EVENT_NAME_KEY, eventName);
	    startActivity(intent);
	}
	
}
