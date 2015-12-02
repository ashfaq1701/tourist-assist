package com.example.touristassist;

import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.io.BufferedReader;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

public class ListViewActivity extends Activity {
	GPSTracker mGPS;
	PlacesServices Services;
	LatLng latLng;
	ArrayList<Place> placeList;
	ArrayList<String> placeStrList;
	MyAsyncTask thread;
	StableArrayAdapter adapter;
	boolean nextPageTokenFlag;
	ListView listView;
	Context currentContext=this;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		placeList=new ArrayList<Place>();
		placeStrList=new ArrayList<String>();
		listView = (ListView) findViewById(R.id.listview);
		GPSTracker mGPS = new GPSTracker(this);
		if(mGPS.canGetLocation)
		{
			mGPS.getLocation();
    		latLng=new LatLng(mGPS.getLatitude(),mGPS.getLongitude());
		}
		makeURL(latLng.latitude,latLng.longitude,"",false,"");
	}
	private void executeMyThread(String urlString){
		thread=new MyAsyncTask();
		thread.execute(urlString,null,null);
	}
	private String makeURL(double latitude, double longitude,String specification,boolean nextPageFlag,String nextPageToken) {
		StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");
		if(nextPageFlag==false){
			if (specification.equals("")) {
				urlString.append("&location=");
				urlString.append(Double.toString(latitude));
				urlString.append(",");
				urlString.append(Double.toString(longitude));
				urlString.append("&radius="+SharedResources.getRadius());
				urlString.append("&sensor=false&key=" + SharedResources.key);
			} else {
				urlString.append("&location=");
				urlString.append(Double.toString(latitude));
				urlString.append(",");
				urlString.append(Double.toString(longitude));
				urlString.append("&radius="+SharedResources.getRadius());
				urlString.append("&types=" + specification);
				urlString.append("&sensor=false&key=" + SharedResources.key);
			}
			Log.d("sayem", "" + urlString.toString());
			executeMyThread(urlString.toString());
			return urlString.toString();
		}
		else{
			urlString.append("pagetoken=");
			urlString.append(nextPageToken);
			urlString.append("&sensor=false&key=" + SharedResources.key);
			executeMyThread(urlString.toString());
			return urlString.toString();
		}
	}
	class MyAsyncTask extends AsyncTask<String, Integer, String> {
		protected String doInBackground(String... urls) {
			URL url = null;
			try {
				url = new URL(urls[0]);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			URLConnection urlConnection = null;
			try {
				urlConnection = url.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 8);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line;
			String content = "";
			if (bufferedReader != null) {
				try {
					while ((line = bufferedReader.readLine()) != null) {
						content += line;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("sayem", content);
			}
			
			return content;
		}

		@Override
		protected void onPostExecute(String content) {
			super.onPostExecute(content);
			String nextPageToken="";
			JSONObject object=null;
			try {
				object = new JSONObject(content);
				JSONArray jsonArray = object.getJSONArray("results");
				for (int i = 0; i < jsonArray.length(); i++) {
					try {
						Place place = Place.JSONToPlaceObject((JSONObject) jsonArray.get(i));
						Log.d("Places Services ", "" + place);
						placeList.add(place);
					} catch (Exception e) {
					}
				}
			} catch (JSONException ex) {
				Logger.getLogger(PlacesServices.class.getName()).log(Level.SEVERE,null, ex);
			}
			if(object.has("next_page_token") && !(object.isNull("next_page_token"))){
				nextPageTokenFlag=true;
				try{
					nextPageToken=object.getString("next_page_token");
					makeURL(latLng.latitude,latLng.longitude,"",nextPageTokenFlag,nextPageToken);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			else
				nextPageTokenFlag=false;
			Log.d("ashfaq",""+placeList.size());
			SharedResources.setPlaceList(placeList);
			placeStrList=new ArrayList<String>();
			for(Place place:placeList){
				placeStrList.add(place.getName());
				Log.d("ashfaq",""+place.getName());
			}
			adapter = new StableArrayAdapter(currentContext,android.R.layout.simple_list_item_1,placeStrList);
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			      @Override
			      public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
			    	  String selectedItem=(String) parent.getItemAtPosition(position);
			    	  for(Place place:SharedResources.placeList){
			    		  if(place.getName().equals(selectedItem)){
			    			  Intent i=new Intent(getApplicationContext(),PlaceDetailsActivity.class);
			    			  i.putExtra("lat", place.getLat());
			    			  i.putExtra("lng", place.getLng());
			    			  i.putExtra("name", place.getName());
			    			  i.putExtra("reference", place.getReference());
			    			  startActivity(i);
			    			  Log.d("ashfaq","Lat: "+place.getLat()+" Lng: "+place.getLng());
			    		  }
			    	  }
			      }
			});
		}
	}
}
