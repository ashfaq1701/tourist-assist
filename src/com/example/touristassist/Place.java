package com.example.touristassist;

import java.util.logging.*;
import org.json.*;

public class Place {
	private Double Lat;
	private Double Lng;
	private String Name = "";
	private Double Rating = 0.0;
	private String Vicinity = "";
	private String Reference="";
	
	public void setLat(Double lat){
		Lat=lat;
	}
	public Double getLat(){
		return Lat;
	}
	public void setLng(Double lng){
		Lng=lng;
	}
	public Double getLng(){
		return Lng;
	}
	public void setName(String name){
		Name=name;
	}
	public String getName(){
		return Name;
	}
	public String getReference(){
		return Reference;
	}
	public void setReference(String reference){
		Reference=reference;
	}
	public static Place JSONToPlaceObject(JSONObject jsonObject){
		Place place = null;
		try
		{
			place=new Place();
			JSONObject geometry=(JSONObject)jsonObject.get("geometry");
			JSONObject location=(JSONObject)geometry.get("location");
			place.setLat((Double)location.get("lat"));
			place.setLng((Double)location.get("lng"));
			place.setName(jsonObject.getString("name"));
			place.setReference(jsonObject.getString("reference"));
		}catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE,null,ex);
        }
        return place;
	}
	public String toString(){
		return "Place{ "+"Name: "+Name+", Latitide: "+Lat+", Longitude: "+Lng+", Rating: "+Rating+", Vicinity: "+Vicinity+"}";
	}
}
