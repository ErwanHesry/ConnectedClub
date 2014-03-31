package com.erwanhesry.connectedclub.model;

public class Tweets {
	  public String username;
	  public String twitterUsername;
	  public String message;
	  public String image_url;
	  public String date;

	  public Tweets(String username, String twitterUsername, String message, String url, String date) {
	    this.username = username;
	    this.twitterUsername = twitterUsername;
	    this.message = message;
	    this.image_url = url;
	    this.date = date;
	  }
	}