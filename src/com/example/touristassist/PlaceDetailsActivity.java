package com.example.touristassist;

import java.io.*;
import java.net.*;
import android.app.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.content.*;
import org.json.*;

public class PlaceDetailsActivity extends Activity{
	public double lat=0;
	public double lng=0;
	public String name="";
	public String reference="";
	String address="";
	String telephone="";
	double rating=0;
	String types="";
	String vicinity="";
	String website="";
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_details);
		Intent i=getIntent();
		lat=i.getExtras().getDouble("lat");
		lng=i.getExtras().getDouble("lng");
		name=i.getExtras().getString("name");
		reference=i.getExtras().getString("reference");
	}
	public String makeUrl(){
		String url="https://maps.googleapis.com/maps/api/place/details/json?";
		url=url+"reference="+reference;
		url=url+"&sensor=false";
		url=url+"&key="+SharedResources.key;
		return url;
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
	public void viewInWindow(String content){
		JSONObject json=null;
		try {
			json=new JSONObject(content);
			JSONObject result=(JSONObject)json.get("result");
			if(!(result.getString("formatted_address").equals(null)))
				address=result.getString("formatted_address");
			if(!(result.getString("formatted_phone_number").equals(null)))
				telephone=result.getString("formatted_phone_number");
			rating=result.getDouble("rating");
			if(!(result.getJSONArray("types").equals(null)))
			{
				JSONArray typesArray=(JSONArray)result.getJSONArray("types");
				for(int i=0;i<typesArray.length();i++){
					types=types+(typesArray.getString(i))+",";
				}
				types=types.substring(0,types.length()-1);
			}
			if(!(result.getString("vicinity").equals(null)))
				vicinity=result.getString("vicinity");
			if(!(result.getString("website").equals(null)))
				website=result.getString("website");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void viewInMapListener(View v){
		
	}
}
