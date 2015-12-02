package com.example.touristassist;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
	
	GoogleMap gMap;
	LatLng latLng;
	EditText setRadius;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button findLocation=(Button) findViewById(R.id.find_location);
		setRadius=(EditText) findViewById(R.id.set_radius);
		Button radiusDone=(Button) findViewById(R.id.radius_done);
		Button listView=(Button) findViewById(R.id.view_list);
		Button mapView=(Button) findViewById(R.id.view_map);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void findLocationListener(View v){
		MapActivity.setCurrentLocationFlag();
		Intent i=new Intent(getApplicationContext(),MapActivity.class);
		startActivity(i);
	}
	
	public void radiusDoneListener(View v){
		SharedResources.setRadius(Integer.parseInt(setRadius.getText().toString()));
	}
	
	public void listViewListener(View v){
		Intent i=new Intent(getApplicationContext(),ListViewActivity.class);
		startActivity(i);
	}
	
	public void mapViewListener(View v){
		MapActivity.setAllLocationFlag();
		Intent i=new Intent(getApplicationContext(),MapActivity.class);
		startActivity(i);
	}

}
