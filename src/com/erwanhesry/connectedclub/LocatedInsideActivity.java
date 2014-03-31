package com.erwanhesry.connectedclub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.erwanhesry.connectedclub.adapter.DbAdapter;
import com.erwanhesry.connectedclub.adapter.LocatedInsideFragmentAdapter;
import com.erwanhesry.connectedclub.model.Club;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.viewpagerindicator.TitlePageIndicator;

public class LocatedInsideActivity extends LocatedInsideBase {
	public static Context ctx;
	public static Club currentClub;
	public static int currentClubId;
	private static DbAdapter dbA;
	public static Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.overridePendingTransition(R.anim.slide_left_to_right,
                R.anim.slide_right_to_left);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ctx = getApplicationContext();
		setContentView(R.layout.activity_located_inside);
		
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-40344885-1");
		
		dbA = DbAdapter.getInstance(ctx);
        
        Bundle extras = getIntent().getExtras();
        long id = extras.getLong(DbAdapter.KEY_ID);
        currentClubId = (int)id;
        currentClub = dbA.getClubById(id);
        mGaTracker.sendView("/"+currentClub.name);
		
		mAdapter = new LocatedInsideFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        TextView header = (TextView)findViewById(R.id.locatedIn);
        header.setText(currentClub.name.toUpperCase());
        header.setTypeface(MainActivity.tf);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//LocatedInsideFragment.tt.cancel();
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance().activityStart(this); // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance().activityStop(this); // Add this method.
	  }

}
