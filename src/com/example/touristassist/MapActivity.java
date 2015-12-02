package com.example.touristassist;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends Activity{
	static GoogleMap gmap=null;
	static LatLng latLng=null;
	PlacesServices Services;
	static boolean currentLocationFlag=false;
	static boolean allLocationFlag=false;
	static boolean specificLocationFlag=false;
	static ArrayList<Place> placeList=null;
	boolean nextPageTokenFlag=false;
	MyAsyncTask thread;
	
	public static void setCurrentLocationFlag(){
		currentLocationFlag=true;
	}
	
	public static void setAllLocationFlag(){
		allLocationFlag=true;
	}
	
	public static void setSpecificLocationFlag(){
		specificLocationFlag=true;
	}
	
	public static void setPlacesList(ArrayList<Place> placesList){
		placeList=placesList;
	}
	
	public static GoogleMap getMap(){
		return gmap;
	}
	public static LatLng getLatLng(){
		return latLng;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		gmap=((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		GPSTracker mGPS = new GPSTracker(this);
		Services=new PlacesServices(SharedResources.key);
		if(mGPS.canGetLocation)
		{
			mGPS.getLocation();
    		latLng=new LatLng(mGPS.getLatitude(),mGPS.getLongitude());
		}
		if(currentLocationFlag){
			gmap.clear();
			gmap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , 16.0f));
			Marker currentMarker=gmap.addMarker(new MarkerOptions()
			.position(latLng)
			.title("My Location")
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			currentLocationFlag=false;
		}
		if(allLocationFlag){
			gmap.clear();
			placeList=new ArrayList<Place>();
			nextPageTokenFlag=false;
			//placeList=SharedResources.placeList;
			
			/*Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;
			
			double equatorLength = 40075004; // in meters
		    double widthInPixels = width;
		    double metersPerPixel = equatorLength / 256;
		    int zoomLevel = 1;
		    while ((metersPerPixel * widthInPixels) > SharedResources.Radius) {
		        metersPerPixel /= 2;
		        ++zoomLevel;
		    }*/
			
			if(mGPS.canGetLocation)
			{
				mGPS.getLocation();
	    		latLng=new LatLng(mGPS.getLatitude(),mGPS.getLongitude());
			}
			gmap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , 11.0f));
			Marker currentMarker=gmap.addMarker(new MarkerOptions()
			.position(latLng)
			.title("My Location")
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			makeURL(mGPS.getLatitude(),mGPS.getLongitude(),"",false,"");
			allLocationFlag=false;
			//gmap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , zoomLevel));
		}
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
	class MyAsyncTask extends AsyncTask<String, Integer, Integer> {
		protected Integer doInBackground(String... urls) {
			String nextPageToken="";
			LatLng latLng=null;
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
			if(object.has("next_page_token") && !object.isNull("next_page_token")){
				nextPageTokenFlag=true;
				try{
					nextPageToken=object.getString("next_page_token");
					latLng=MapActivity.getLatLng();
					makeURL(latLng.latitude,latLng.longitude,"",nextPageTokenFlag,nextPageToken);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			else
				nextPageTokenFlag=false;
			return 0;
		}

		@Override
		protected void onPostExecute(Integer i) {
			super.onPostExecute(i);
			if(placeList!=null){
    			Log.d("sayem:", "Places Detected");
    			for(Place place:placeList){
    				LatLng locLatLng=new LatLng(place.getLat(),place.getLng());
    				Marker marker=gmap.addMarker(new MarkerOptions()
    				.position(locLatLng)
    				.title(place.getName()));
    			}
    		}
			Log.d("ashfaq",""+placeList.size());
			SharedResources.setPlaceList(placeList);
		}
	}
}
