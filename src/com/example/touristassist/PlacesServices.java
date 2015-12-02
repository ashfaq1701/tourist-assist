package com.example.touristassist;

import java.util.*;
import java.util.logging.*;
import org.json.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.os.AsyncTask;
import android.util.Log;
import java.io.*;
import java.net.*;

public class PlacesServices {
	private String API_KEY;
	ArrayList<Place> placeList=null;
	public MyAsyncTask thread;
	
	public PlacesServices(String apikey) {
		API_KEY = apikey;
	}
	
	public void findPlaces(double latitude, double longitude,String specification,boolean nextPageFlag,String nextPageToken) {
		placeList=new ArrayList<Place>();
		makeURL(latitude, longitude, specification,false,"");
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
				urlString.append("&radius=10000");
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
			boolean nextPageTokenFlag=false;
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
			return 0;
		}

		@Override
		protected void onPostExecute(Integer i) {
			super.onPostExecute(i);
			Log.d("ashfaq",""+placeList.size());
			SharedResources.setPlaceList(placeList);
		}

		protected void onProgressUpdate(Integer... progress) {

		}
	}

}