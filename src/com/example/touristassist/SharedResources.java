package com.example.touristassist;

import java.util.*;

public class SharedResources {

	public static ArrayList<Place> placeList;
	public static int Radius=10000;
	public static String key="AIzaSyC7EH42vtNX7ThYY0EYKQXhJdMWlQPbzxo";
	public static boolean isCompleted=false;
	
	public static void setRadius(int radius){
		Radius=radius;
	}
	
	public static int getRadius(){
		return Radius;
	}
	
	public static void setPlaceList(ArrayList<Place> placesList){
		placeList=placesList;
	}
}
