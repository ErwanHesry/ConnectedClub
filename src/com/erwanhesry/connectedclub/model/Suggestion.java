package com.erwanhesry.connectedclub.model;

public class Suggestion {
	public String name;
	public String description;
	public String phone;
	public int isAClub;
	public int sid;
	
	public Suggestion(String n, String d, String p, int i, int s){
		this.name = n;
		this.description = d;
		this.phone = p;
		this.isAClub = i;
		this.sid = s;
	}
}
