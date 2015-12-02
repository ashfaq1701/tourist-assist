package com.example.touristassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.json.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.content.*;
import android.graphics.Color;

public class ShowPathActivity extends Activity{
	GoogleMap pathMap=null;
	LatLng latLng=null,selectedLatLng=null;
	double selectedLat=0,selectedLng=0;
	String name="",reference="";
	MyAsyncTask thread;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_path);
		Intent i=getIntent();
		selectedLat=i.getExtras().getDouble("lat");
		selectedLng=i.getExtras().getDouble("lng");
		name=i.getExtras().getString("name");
		reference=i.getExtras().getString("reference");
		selectedLatLng=new LatLng(selectedLat,selectedLng);
		pathMap=((MapFragment) getFragmentManager().findFragmentById(R.id.path_map)).getMap();
		GPSTracker mGPS = new GPSTracker(this);
		if(mGPS.canGetLocation)
		{
			mGPS.getLocation();
    		latLng=new LatLng(mGPS.getLatitude(),mGPS.getLongitude());
		}
		pathMap.clear();
		pathMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , 11.0f));
		pathMap.addMarker(new MarkerOptions()
		.position(latLng)
		.title("My Location")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		pathMap.addMarker(new MarkerOptions()
		.position(selectedLatLng)
		.title(name));
		executeMyThread();
		
	}
	private void executeMyThread(){
		thread=new MyAsyncTask();
		thread.execute(null,null,null);
	}
	public String makeUrl(){
	    StringBuilder urlString = new StringBuilder();
	    urlString.append("http://maps.googleapis.com/maps/api/directions/json");
	    urlString.append("?origin="); 
	    urlString.append(Double.toString(latLng.latitude));
	    urlString.append(",");
	    urlString.append(Double.toString(latLng.longitude));
	    urlString.append("&destination="); 
	    urlString.append(Double.toString(selectedLatLng.latitude));
	    urlString.append(",");
	    urlString.append(Double.toString(selectedLatLng.longitude));
	    urlString.append("&sensor=false&mode=driving");
	    return urlString.toString();
	}
	public String getContent(String urlStr){
		URL url = null;
		try {
			url = new URL(urlStr);
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
	private List<LatLng> decodePoly(String encoded){

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0;
	    int length = encoded.length();
	    int latitude = 0;
	    int longitude = 0;

	    while(index < length){
	        int b;
	        int shift = 0;
	        int result = 0;

	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);

	        int destLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        latitude += destLat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);

	        int destLong = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        longitude += destLong;

	        poly.add(new LatLng((latitude / 1E5),(longitude / 1E5) ));
	    }
	    return poly;
	}
	public void drawPath(String result){
	    try{
	        final JSONObject json = new JSONObject(result);
	        JSONArray routeArray = json.getJSONArray("routes");
	        JSONObject routes = routeArray.getJSONObject(0);

	        JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	        String encodedString = overviewPolylines.getString("points");
	        Log.d("test: ", encodedString);
	        List<LatLng> list = decodePoly(encodedString);

	        LatLng last = null;
	        for (int i = 0; i < list.size()-1; i++) {
	            LatLng src = list.get(i);
	            LatLng dest = list.get(i+1);
	            last = dest;
	            Log.d("Last latLng:", last.latitude + ", " + last.longitude );
	            Polyline line = pathMap.addPolyline(new PolylineOptions().add( 
	                    new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
	                    .width(2)
	                    .color(Color.BLUE));
	        }

	        Log.d("Last latLng:", last.latitude + ", " + last.longitude );
	    }catch (JSONException e){
	        e.printStackTrace();
	    }
	}
	class MyAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void...voids) {
			// TODO Auto-generated method stub
			return getContent(makeUrl());
		}
		protected void onPostExecute(String content) {
			drawPath(content);
		}
	}

}
