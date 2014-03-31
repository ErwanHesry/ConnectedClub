package com.erwanhesry.connectedclub;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.erwanhesry.connectedclub.adapter.CocktailItemAdapter;
import com.erwanhesry.connectedclub.adapter.DbAdapter;
import com.erwanhesry.connectedclub.adapter.SuggestionAdapter;
import com.erwanhesry.connectedclub.adapter.TweetItemAdapter;
import com.erwanhesry.connectedclub.model.Cocktails;
import com.erwanhesry.connectedclub.model.Suggestion;
import com.erwanhesry.connectedclub.model.Tweets;
import com.erwanhesry.connectedclub.utility.Utils;
import com.google.analytics.tracking.android.EasyTracker;

public final class LocatedInsideFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private static final String TAG = "LocatedInsideFragment";
    private static ArrayList<Tweets> tweetsArray;
    private static ArrayList<Cocktails> cocktailsArray;
    private static ArrayList<Cocktails> billArray;
    private static ArrayList<Suggestion> suggestions;
    private LinearLayout tweetLayout;
    private ScrollView cocktailScroll;
    private LinearLayout cocktailLayout;
    private RelativeLayout cocktailRelative;
    private ViewGroup underListView;
    private ScrollView suggestionScroll;
    private LinearLayout suggestionLayout;
    public static Boolean isCheckoutCartDisplayed = false;
    public static LayoutInflater mInflater;
    public static Boolean isInsideAClub = false;
    public static TimerTask tt;
    private static DbAdapter dbA;
    private LinearLayout overlayContent;
    private Boolean userHasBooked = false;
    private TextView dayPicker, timePicker;
    private String day, time, name;
    private LinearLayout laCarte;
    private Boolean dayPicked = false;
    private Boolean timePicked = false;
    private ViewGroup overlayLayout;
    
    public static LocatedInsideFragment newInstance(String content) {
    	LocatedInsideFragment fragment = new LocatedInsideFragment();
    	
        StringBuilder builder = new StringBuilder();
        builder.append(content).append(" ");
        builder.deleteCharAt(builder.length() - 1);
        fragment.mContent = builder.toString();
		
        return fragment;
    }

    private String mContent = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }

        isInsideAClub = true;
        dbA = DbAdapter.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mInflater = inflater;
    	
    	
    	if(mContent.equals(getActivity().getString(R.string.profile))) {
        	
        	/*
        	 * Establishment infos
        	 */
        	
        	ScrollView view = new ScrollView(getActivity());
        	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        	view.setLayoutParams(params);
        	
        	getInfoView(view, inflater);
        	           
        	return view;
        	
        } else if(mContent.equals(getActivity().getString(R.string.activity))){
        	
        	/*
        	 * Twitter feed
        	 */
        	
        	tweetsArray = new ArrayList<Tweets>();
        	tweetLayout = new LinearLayout(getActivity()); 
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
            params.weight = 1;
            tweetLayout.setLayoutParams(params);  
            if(Utils.isOnline(getActivity())){
            	getTwitterFeed(LocatedInsideActivity.currentClub.twitterAccount);
            }
        	return tweetLayout;
        	
        } else if(mContent.equals(getActivity().getString(R.string.preOrder)) || mContent.equals(getActivity().getString(R.string.lacarte))) {
        	
        	/*
        	 * Cocktail list
        	 */
        	
        	LocatedInsideActivity.mGaTracker.sendView("/"+LocatedInsideActivity.currentClub.name+"/"+getActivity().getString(R.string.preOrder));
        	
        	cocktailsArray = new ArrayList<Cocktails>();
            billArray = new ArrayList<Cocktails>();
            
            List<Cocktails> cList = dbA.getCocktailsAssociatedToClub(LocatedInsideActivity.currentClubId);
            for(Cocktails c : cList){
            	cocktailsArray.add(c);
            }
            Collections.reverse(cocktailsArray);
            //Relative 
            cocktailRelative = new RelativeLayout(getActivity());
            
            //Scroll under relative
         	cocktailScroll = new ScrollView(getActivity());
         	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            cocktailScroll.setLayoutParams(params);
            cocktailRelative.setLayoutParams(params);
            
            //Scroll content
            cocktailLayout = new LinearLayout(getActivity());
            params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
            params.weight = 1;
            cocktailLayout.setLayoutParams(params);
            cocktailLayout.setOrientation(LinearLayout.VERTICAL);
            
            //get cocktail scroll content
            getCocktails();

            //Adding subviews
        	cocktailScroll.addView(cocktailLayout);
            cocktailRelative.addView(cocktailScroll);	
            
            //getOverlayPreOrder(inflater);
        	
        	return cocktailRelative;

        } else if(mContent.equals(getActivity().getString(R.string.suggestions))) {
        	
        	/*
        	 * Suggestions
        	 */
        	suggestions = new ArrayList<Suggestion>();
        	
        	List<Suggestion> sList = dbA.getSuggestionAssociatedToClub(LocatedInsideActivity.currentClubId);
            for(Suggestion s : sList){
            	suggestions.add(s);
            }
        	
        	suggestionScroll = new ScrollView(getActivity());
         	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.weight = 1;
            suggestionScroll.setLayoutParams(params);
            
            suggestionLayout = new LinearLayout(getActivity());
            params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
            params.weight = 1;
            suggestionLayout.setLayoutParams(params);
            suggestionLayout.setOrientation(LinearLayout.VERTICAL);
            
            getSuggestions();
        	
            suggestionScroll.addView(suggestionLayout);
        	return suggestionScroll;
        	
        } else {
        	LinearLayout layout = new LinearLayout(getActivity());         	
        	return layout;
        }
        
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
    
    /*
     * Methods to get content
     * 
     */
    
    public void getInfoView(ScrollView view, LayoutInflater inflater){
    	ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.profile, view);
    	 
        // background image
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bottles);
        ImageView header = (ImageView)layout.findViewById(R.id.background);
        header.setImageBitmap(bm);
        
        // contact image
        ImageView contactImage = (ImageView)layout.findViewById(R.id.contact);
        MainActivity.imageLoader.DisplayImage(LocatedInsideActivity.currentClub.photoURL, contactImage, true);
        
        TextView title = (TextView)layout.findViewById(R.id.establishmentName);
        title.setTypeface(MainActivity.roboto);
        title.setText(LocatedInsideActivity.currentClub.name);
        
        TextView type = (TextView)layout.findViewById(R.id.establishmentType);
        type.setTypeface(MainActivity.roboto);
        type.setText(LocatedInsideActivity.currentClub.type);
        
        TextView desc = (TextView)layout.findViewById(R.id.establishmentDesc);
        desc.setTypeface(MainActivity.roboto);
        desc.setText(LocatedInsideActivity.currentClub.description);
        
        TextView fb = (TextView)layout.findViewById(R.id.facebookLink);
        fb.setTypeface(MainActivity.roboto);
        
        if(LocatedInsideActivity.currentClub.facebookAccount != null){
            fb.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/"+LocatedInsideActivity.currentClub.facebookAccount));
					//startActivity(browserIntent);
					try {
					    getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/"+LocatedInsideActivity.currentClub.facebookAccount)));
					} catch (Exception e) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+LocatedInsideActivity.currentClub.facebookAccount)));
					}
				}
			});
        } else {
        	fb.setVisibility(View.GONE);
        }
        
        TextView tw = (TextView)layout.findViewById(R.id.twitterLink);
        tw.setTypeface(MainActivity.roboto);
        
        if(LocatedInsideActivity.currentClub.twitterAccount != null){
            tw.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/"+LocatedInsideActivity.currentClub.twitterAccount));
					startActivity(browserIntent);
				}
			});
        } else {
        	tw.setVisibility(View.GONE);
        }
        
        ImageView call = (ImageView)layout.findViewById(R.id.call_button);
        call.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
			        Intent callIntent = new Intent(Intent.ACTION_CALL);
			        callIntent.setData(Uri.parse("tel:"+LocatedInsideActivity.currentClub.phoneNumber));
			        startActivity(callIntent);
			    } catch (Exception e) {
			    }
			}
		});
        
        JSONObject myjson;
		try {
            myjson = new JSONObject(LocatedInsideActivity.currentClub.openHour);
			JSONArray the_json_array = myjson.getJSONArray("openhour");
			int size = the_json_array.length();
			for (int i = 0; i < size; i++) {
				JSONObject results = the_json_array.getJSONObject(i); 
				
				TextView day = (TextView)layout.findViewById(R.id.monday);
				day.setTypeface(MainActivity.roboto);
				day = (TextView)layout.findViewById(R.id.mondayHour);
				day.setTypeface(MainActivity.roboto);
				
				if(((String)results.get("1")).equals("0")){
					day.setText(getString(R.string.closed));
				} else {
					day.setText((String)results.get("1"));
				}
				
				day = (TextView)layout.findViewById(R.id.tuesday);
				day.setTypeface(MainActivity.roboto);
				day = (TextView)layout.findViewById(R.id.tuesdayHour);
				day.setTypeface(MainActivity.roboto);
				if(((String)results.get("2")).equals("0")){
					day.setText(getString(R.string.closed));
				} else {
					day.setText((String)results.get("2"));
				}
				
				day = (TextView)layout.findViewById(R.id.wednesday);
				day.setTypeface(MainActivity.roboto);
				day = (TextView)layout.findViewById(R.id.wednesdayHour);
				day.setTypeface(MainActivity.roboto);
				if(((String)results.get("3")).equals("0")){
					day.setText(getString(R.string.closed));
				} else {
					day.setText((String)results.get("3"));
				}
				
				day = (TextView)layout.findViewById(R.id.thursday);
				day.setTypeface(MainActivity.roboto);
				day = (TextView)layout.findViewById(R.id.thursdayHour);
				day.setTypeface(MainActivity.roboto);
				if(((String)results.get("4")).equals("0")){
					day.setText(getString(R.string.closed));
				} else {
					day.setText((String)results.get("4"));
				}
				
				day = (TextView)layout.findViewById(R.id.friday);
				day.setTypeface(MainActivity.roboto);
				day = (TextView)layout.findViewById(R.id.fridayHour);
				day.setTypeface(MainActivity.roboto);
				if(((String)results.get("5")).equals("0")){
					day.setText(getString(R.string.closed));
				} else {
					day.setText((String)results.get("5"));
				}
				
				day = (TextView)layout.findViewById(R.id.saturday);
				day.setTypeface(MainActivity.roboto);
				day = (TextView)layout.findViewById(R.id.saturdayHour);
				day.setTypeface(MainActivity.roboto);
				if(((String)results.get("6")).equals("0")){
					day.setText(getString(R.string.closed));
				} else {
					day.setText((String)results.get("6"));
				}
				
				day = (TextView)layout.findViewById(R.id.sunday);
				day.setTypeface(MainActivity.roboto);
				day = (TextView)layout.findViewById(R.id.sundayHour);
				day.setTypeface(MainActivity.roboto);
				if(((String)results.get("7")).equals("0")){
					day.setText(getString(R.string.closed));
				} else {
					day.setText((String)results.get("7"));
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
    
    public void getTwitterFeed(final String placeName){
    	tweetsArray = new ArrayList<Tweets>();
    	new DownloadTweeter().execute(placeName);
    }
    
    public void getCocktails(){
    	cocktailLayout.removeAllViews();
     	
     	// Header listview cocktails
        ViewGroup header = (ViewGroup)mInflater.inflate(R.layout.listview_header, cocktailLayout, false);
		TextView headerName = (TextView)header.findViewById(R.id.header_title);
		headerName.setTypeface(MainActivity.tf);
		headerName.setText(R.string.cocktails);
		cocktailLayout.addView(header);
        
		// Get items
		ListView content = new ListView(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
		params.weight = 1;
		CocktailItemAdapter cAdapter = new CocktailItemAdapter(getActivity(), R.layout.cocktail_listitem, cocktailsArray);
		content.setLayoutParams(params);
		content.setAdapter(cAdapter);
		content.setDivider(new ColorDrawable(R.color.separator));
		if(LocatedInsideActivity.currentClub.orderable == 1){
			content.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int position, long id) {
					// TODO Auto-generated method stub
					//if(userHasBooked){
						Cocktails c = cocktailsArray.get(position);
						billArray.add(c);
						TextView elements = (TextView)underListView.findViewById(R.id.underlistView_elements);
						if(billArray.size() == 1)
							elements.setText(billArray.size()+" "+getString(R.string.element));
						else 
							elements.setText(billArray.size()+" "+getString(R.string.elements));
						Toast.makeText(getActivity(), c.name+" "+getString(R.string.added), Toast.LENGTH_SHORT).show();
					//} else {
					//	Toast.makeText(getActivity(),getString(R.string.nobooking), Toast.LENGTH_SHORT).show();
					//}
				}
			});
			content.setOnItemLongClickListener(new OnItemLongClickListener() {
	
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View v,
						int position, long id) {
					// TODO Auto-generated method stub
					Log.d(TAG,"item long click");
					return true;
				}
			});
		}
		Utils.setCocktailListViewHeightBasedOnChildren(content);
		cocktailLayout.addView(content);
		
		if(LocatedInsideActivity.currentClub.orderable == 1){
	    	// Bill
	    	underListView = (ViewGroup)mInflater.inflate(R.layout.underlistview, cocktailLayout, false);
			headerName = (TextView)underListView.findViewById(R.id.underlistView_title);
			headerName.setTypeface(MainActivity.tf);
			TextView elements = (TextView)underListView.findViewById(R.id.underlistView_elements);
	    	elements.setTypeface(MainActivity.tf);
	    	if(billArray.size() > 0){
	    		elements.setText(billArray.size()+" "+getString(R.string.elements));
	    	}
	    	
	    	underListView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//if(userHasBooked){
						if(billArray.size()>0){
							Log.d(TAG,"tap to checkout");
							isCheckoutCartDisplayed = true;
							getCartCheckout();
						}
					//} else {
					//	Toast.makeText(getActivity(),getString(R.string.nobooking), Toast.LENGTH_SHORT).show();
					//}
				}
			});
	    	cocktailLayout.addView(underListView);
		}
    }
    
    public void getCartCheckout(){
    	LocatedInsideActivity.mGaTracker.sendView("/"+LocatedInsideActivity.currentClub.name+"/"+getActivity().getString(R.string.preOrder)+"/CheckoutView");
    	
    	cocktailLayout.removeAllViews();
    	
    	if(billArray.size()>0){
	    	// Header listview cocktails
	        ViewGroup header = (ViewGroup)mInflater.inflate(R.layout.listview_header, cocktailLayout, false);
			TextView headerName = (TextView)header.findViewById(R.id.header_title);
			headerName.setTypeface(MainActivity.tf);
			headerName.setText(R.string.billAndCheckout);
			ImageView headerLine = (ImageView)header.findViewById(R.id.header_line);
			headerLine.setBackgroundColor(Color.parseColor("#e9d5ac"));
			cocktailLayout.addView(header);
	        
			// Get items
			ListView content = new ListView(getActivity());
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
			params.weight = 1;
			CocktailItemAdapter cAdapter = new CocktailItemAdapter(getActivity(), R.layout.cocktail_listitem, billArray);
			content.setLayoutParams(params);
			content.setAdapter(cAdapter);
			content.setDivider(new ColorDrawable(R.color.separator));
			
			content.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View v,
						int position, long id) {
					// TODO Auto-generated method stub
					Cocktails c = billArray.get(position);
					billArray.remove(position);
					Toast.makeText(getActivity(), c.name+" "+getString(R.string.deleted), Toast.LENGTH_SHORT).show();
					getCartCheckout();
					return false;
				}
			});
			
			Utils.setCocktailListViewHeightBasedOnChildren(content);
			cocktailLayout.addView(content);
			
			float total = (float)0.0;
			String cocktailList = "";
			for(Cocktails c : billArray){
				total += c.price;
				cocktailList += c.name+" - ";
			}
			
			if(cocktailList.length()>26)
				cocktailList = cocktailList.substring(0, 26)+" ...";
			
			ViewGroup underListView = (ViewGroup)mInflater.inflate(R.layout.underlistview_checkout, cocktailLayout, false);
			headerName = (TextView)underListView.findViewById(R.id.underlistView_title);
			headerName.setTypeface(MainActivity.tf);
			TextView elements = (TextView)underListView.findViewById(R.id.underlistView_elements);
	    	elements.setTypeface(MainActivity.tf);
	    	elements.setText(cocktailList);
	    	
	    	TextView totalPrice = (TextView)underListView.findViewById(R.id.underlistView_total_price);
	    	totalPrice.setTypeface(MainActivity.tf);
	    	totalPrice.setText(String.valueOf(total)+"€");
	    	
	    	TextView number = (TextView)underListView.findViewById(R.id.underlistView_total_number);
	    	number.setTypeface(MainActivity.tf);
	    	number.setText(billArray.size()+" "+getString(R.string.preparation)); 
	    	
	    	TextView payName = (TextView)underListView.findViewById(R.id.payment_name);
	    	payName.setTypeface(MainActivity.tf);
	    	
	    	TextView paySubtitle = (TextView)underListView.findViewById(R.id.payment_subtitle);
	    	paySubtitle.setTypeface(MainActivity.tf);
	    	
	    	TextView continueName = (TextView)underListView.findViewById(R.id.continue_name);
	    	continueName.setTypeface(MainActivity.tf);
	    	
	    	TextView continueSubtitle = (TextView)underListView.findViewById(R.id.continue_subtitle);
	    	continueSubtitle.setTypeface(MainActivity.tf);
	    	
	    	LinearLayout continueLayout = (LinearLayout)underListView.findViewById(R.id.continue_layout);
	    	continueLayout.setOnClickListener(new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isCheckoutCartDisplayed = false;
					getCocktails();
				}
			});
	    	
	    	LinearLayout pay = (LinearLayout)underListView.findViewById(R.id.pay);
	    	pay.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getOverlayPreOrder(mInflater);
				}
			});
	    	
	    	cocktailLayout.addView(underListView);
    	} else {
    		getCocktails();
    	}
    }
    
    public void getOverlayPreOrder(LayoutInflater inflater){
    	LocatedInsideActivity.mGaTracker.sendView("/"+LocatedInsideActivity.currentClub.name+"/"+getActivity().getString(R.string.preOrder)+"/OverlayView");
    	
    	//generate overlay
        dayPicked = false;
        timePicked = false;
        
        overlayLayout = (ViewGroup)inflater.inflate(R.layout.overlay, cocktailRelative);
        
        overlayContent = (LinearLayout)overlayLayout.findViewById(R.id.overlayLinear);
        overlayContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Do nothing
			}
		});
        
        LinearLayout callButton = (LinearLayout)overlayLayout.findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
			        Intent callIntent = new Intent(Intent.ACTION_CALL);
			        callIntent.setData(Uri.parse("tel:"+LocatedInsideActivity.currentClub.phoneNumber));
			        startActivity(callIntent);
			    } catch (Exception e) {
			    }
			}
		});
        
        LinearLayout cancelButton = (LinearLayout)overlayLayout.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cocktailRelative.removeView(overlayContent);
			}
		});
        
        TextView text = (TextView)overlayLayout.findViewById(R.id.overlayTitle);
        text.setTypeface(MainActivity.tf);
        TextView textO = (TextView)overlayLayout.findViewById(R.id.overlayNoBooking);
        textO.setTypeface(MainActivity.tf);
        textO.setText(textO.getText()+" "+LocatedInsideActivity.currentClub.name);
        TextView textC = (TextView)overlayLayout.findViewById(R.id.call_button);
        text.setTypeface(MainActivity.tf);
        text = (TextView)overlayLayout.findViewById(R.id.cancel_button);
        text.setTypeface(MainActivity.tf);
        EditText eText = (EditText)overlayLayout.findViewById(R.id.nameInput);
        eText.setTypeface(MainActivity.tf);
        
        dayPicker = (TextView)overlayLayout.findViewById(R.id.datePicker);
        dayPicker.setTypeface(MainActivity.tf);
        if(day!=null){
        	dayPicker.setText(day);
        	dayPicked = true;
        }
        dayPicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment();
			    newFragment.show(getChildFragmentManager(), "datePicker");
			}
		});
        
        timePicker = (TextView)overlayLayout.findViewById(R.id.timePicker);
        timePicker.setTypeface(MainActivity.tf);
        if(time!=null){
        	timePicker.setText(time);
        	timePicked = true;
        }
        timePicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new TimePickerFragment();
			    newFragment.show(getChildFragmentManager(), "timePicker");
			}
		});  
        
        if(dayPicked && timePicked){
			textC.setText(R.string.preOrder);
			ImageView v = (ImageView)overlayLayout.findViewById(R.id.call_image);
			v.setVisibility(View.GONE);
			textO.setVisibility(View.GONE);
			callButton = (LinearLayout)overlayLayout.findViewById(R.id.callButton);
	        callButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LocatedInsideActivity.mGaTracker.sendView("/"+LocatedInsideActivity.currentClub.name+"/"+getActivity().getString(R.string.preOrder)+"/SendPreOrder");
					new sendPreOrder().execute("setNewPreOrder");
				}
			});
		}
        
        if(name != null){
        	eText.setText(name);
        }
    }
    
    public void getSuggestions(){
    	
    	// Header listview cocktails
        ViewGroup header = (ViewGroup)mInflater.inflate(R.layout.listview_header, suggestionLayout, false);
		TextView headerName = (TextView)header.findViewById(R.id.header_title);
		headerName.setTypeface(MainActivity.tf);
		headerName.setText(R.string.barRestaurant);
		suggestionLayout.addView(header);
		
		ListView content = new ListView(getActivity());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
		params.weight = 1;
		SuggestionAdapter cAdapter = new SuggestionAdapter(getActivity(), R.layout.suggestion_listitem, suggestions);
		content.setLayoutParams(params);
		content.setAdapter(cAdapter);
		content.setDivider(new ColorDrawable(R.color.separator));
		Utils.setSuggestionListViewHeightBasedOnChildren(content);
		suggestionLayout.addView(content);
    }
    
    class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
			// Create a new instance of TimePickerDialog and return it
			return dialog;
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			time = hourOfDay+":"+minute;
			timePicker.setText(time);
			timePicked = true;
			if(dayPicked && timePicked){
				TextView t = (TextView)overlayLayout.findViewById(R.id.call_button);
				t.setText(R.string.preOrder);
				ImageView v = (ImageView)overlayLayout.findViewById(R.id.call_image);
				v.setVisibility(View.GONE);
				TextView textO = (TextView)overlayLayout.findViewById(R.id.overlayNoBooking);
				textO.setVisibility(View.GONE);
				LinearLayout callButton = (LinearLayout)overlayLayout.findViewById(R.id.callButton);
		        callButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new sendPreOrder().execute("setNewPreOrder");
					}
				});
			}
		}
	}
    
    class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
			// Create a new instance of DatePickerDialog and return it
			return dialog;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int dayd) {
			// Do something with the date chosen by the user
			if(Locale.getDefault().getDisplayLanguage().equals("fr")){
				day = dayd+"/"+month+"/"+year;
			}
			else {
				day = month+"/"+dayd+"/"+year;
			}
			dayPicker.setText(day);
			dayPicked = true;
			if(dayPicked && timePicked){
				TextView t = (TextView)overlayLayout.findViewById(R.id.call_button);
				t.setText(R.string.preOrder);
				ImageView v = (ImageView)overlayLayout.findViewById(R.id.call_image);
				v.setVisibility(View.GONE);
				TextView textO = (TextView)overlayLayout.findViewById(R.id.overlayNoBooking);
				textO.setVisibility(View.GONE);
				LinearLayout callButton = (LinearLayout)overlayLayout.findViewById(R.id.callButton);
		        callButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new sendPreOrder().execute("");
					}
				});
			}
		}
	}
    
    /*
     * AsyncTask classes 
     * 
     */
	
	class DownloadTweeter extends AsyncTask<String, Void, ListView>{

		@Override
		protected ListView doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			/*
			String searchUrl = "http://search.twitter.com/search.json?q="+arg0[0]; 
	    	
	    	HttpClient client = new  DefaultHttpClient();
			HttpGet get = new HttpGet(searchUrl); 
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			String responseBody = null;
			try{
				responseBody = client.execute(get, responseHandler);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			JSONObject myjson;
			try {
				myjson = new JSONObject(responseBody);
				JSONArray the_json_array = myjson.getJSONArray("results");
				int size = the_json_array.length();
				for (int i = 0; i < size; i++) {
					JSONObject results = the_json_array.getJSONObject(i); 
					
					String profileImageURL = (String) results.get("profile_image_url");
					profileImageURL = profileImageURL.replace("_normal","");
					
					String tweetText = (String) results.get("text");
					tweetText = android.text.Html.fromHtml(tweetText).toString();
					
					Tweets currentTweet = new Tweets(
							(String) results.get("from_user_name"), 
							(String) results.get("from_user"),
							tweetText, 
							profileImageURL, 
							(String) results.get("created_at"));
					tweetsArray.add(currentTweet);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			
		    Query query = new Query(arg0[0]);
		    QueryResult result;
			try {
				result = MainActivity.twitterFeed.search(query);
				for (twitter4j.Status status : result.getTweets()) {
					Tweets currentTweet = new Tweets(
							status.getUser().getScreenName(),
							status.getUser().getName(),
							status.getText(),
							status.getUser().getOriginalProfileImageURL(),
							status.getCreatedAt().toString()
							);
					tweetsArray.add(currentTweet);
			    }
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ListView content = new ListView(getActivity());
			content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			content.setAdapter(new TweetItemAdapter(getActivity(), R.layout.twitter_listitem, tweetsArray));
			content.setDividerHeight(0);
	        
			return content;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ListView result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			result.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Log.d(TAG,"item click");
				}
			});
			
			result.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Log.d(TAG,"item long click");
					return true;
				}
			});
			
			tweetLayout.addView(result);
		}
	
    }
    
	class sendPreOrder extends AsyncTask<String, Void, Boolean>{

		
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			EditText et = (EditText)overlayLayout.findViewById(R.id.nameInput);
			name = et.getText().toString();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			TelephonyManager tMgr =(TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			String mail = "Welcome "+LocatedInsideActivity.currentClub.name
									+"\n\nHere is a new pre-order for the booking\nat "
									+time+" the "+day
									+"\n\nCocktails ordered:\n";
			float totalPrice = 0;
			for(Cocktails c : billArray){
				totalPrice += c.price;
				mail += c.name+" -- "+c.price+"\n";
			}
			mail += "Total: "+totalPrice;
			mail += "\n\nName: "+name;
			mail += "\nPhone number:"+tMgr.getLine1Number();
			
			mail += "\n\nThanks, The HaveACocktail team.";

			Log.d("sendPreOrder","mail: \n"+mail);
			
			String apiURL = "http://connectedclub.erwanhesry.com/api/set.php";
			HttpClient client = new  DefaultHttpClient();
			HttpPost post = new HttpPost(apiURL); 
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// Get clubs
			String responseBody = null;
			try{
				List nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair("cc_function", params[0]));
				nameValuePairs.add(new BasicNameValuePair("mail_content", mail));
				nameValuePairs.add(new BasicNameValuePair("club_id", ""+LocatedInsideActivity.currentClub.cid));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				responseBody = client.execute(post, responseHandler);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			return true;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			cocktailRelative.removeView(overlayContent);
			isCheckoutCartDisplayed = false;
			billArray = new ArrayList<Cocktails>();
			getCocktails();
		}		
	}
}

