package com.erwanhesry.connectedclub.model;

public class Cocktails {
	public String name;
	public String composition;
	public float price;
	public long cid;
	
	public Cocktails(int id, String name, String composition, float price){
		this.name = name;
		this.composition = composition;
		this.price = price;
		this.cid = id;
	}
	
}
