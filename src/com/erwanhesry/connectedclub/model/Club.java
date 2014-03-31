package com.erwanhesry.connectedclub.model;

public class Club {
	public String name;
	public String type;
	public String description;
	public String twitterAccount;
	public String facebookAccount;
	public String photoURL;
	public String openHour;
	public double latitude;
	public double longitude;
	public long cid;
	public String phoneNumber;
	public int orderable;
	
	public Club(int id, String n, String ph, String ty, String d, String p, String t, String f, String o, double lat, double lon, int or){
		this.name = n;
		this.phoneNumber = ph;
		this.type = ty;
		this.description = d;
		this.photoURL = p;
		this.twitterAccount = t;
		this.facebookAccount = f;
		this.latitude = lat;
		this.longitude = lon;
		this.openHour = o;
		this.cid = id;
		this.orderable = or;
	}
}
