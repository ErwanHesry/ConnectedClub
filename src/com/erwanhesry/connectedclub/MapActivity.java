package com.erwanhesry.connectedclub;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.erwanhesry.connectedclub.adapter.DbAdapter;
import com.erwanhesry.connectedclub.model.Club;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends FragmentActivity {
	GoogleMap mapView;
	com.google.android.gms.maps.Projection projection;
	
	private static LocationManager locationManager;
	private static LocationListener locationListener;
	
	private static boolean located = false;
	
	private static DbAdapter dbA;
	
	public static Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.overridePendingTransition(R.anim.slide_top_to_bottom,
                 R.anim.slide_bottom_to_top);
		
		ctx = getApplicationContext();
		 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_map);
		
		EasyTracker.getInstance().activityStart(this);
		
		TextView notInABar = (TextView)findViewById(R.id.notInABar);
		notInABar.setTypeface(MainActivity.tf);
		
		FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
		SupportMapFragment mFRaFragment = new MapFragmentD();
		mTransaction.add(R.id.mainl, mFRaFragment);
		mTransaction.commit();

		try {
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
		
		dbA = DbAdapter.getInstance(ctx);
		final List<Club> cList = dbA.getClubs();

		locationManager = (LocationManager) this.getSystemService(getApplicationContext().LOCATION_SERVICE);
		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location arg0) {
				// TODO Auto-generated method stub
				Log.d("MapActivity", "LocationChanged");
				
				// Getting latitude of the current location
		        double latitude = arg0.getLatitude();
		 
		        // Getting longitude of the current location
		        double longitude = arg0.getLongitude();
		 
		        // Creating a LatLng object for the current location
		        LatLng latLng = new LatLng(latitude, longitude);
		 
		        if(MapFragmentD.mapView.getCameraPosition().zoom != 11 || !located){
		        	// Showing the current location in Google Map
		        	MapFragmentD.mapView.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		        
		        // Zoom in the Google Map
		        MapFragmentD.mapView.animateCamera(CameraUpdateFactory.zoomTo(11));
		        
		        	located = true;
		        }
		        
				for(Club c : cList){
					float[] distance = new float[3];
					arg0.distanceBetween(arg0.getLatitude(), arg0.getLongitude(), c.latitude, c.longitude, distance);
					if(distance[0]<25 && !LocatedInsideFragment.isInsideAClub){
						Intent intent = new Intent(MapActivity.ctx, LocatedInsideActivity.class);
						intent.putExtra(DbAdapter.KEY_ID, c.cid);
						startActivity(intent);
					}
				} 
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
		};
			 
		//Cell-ID and WiFi location updates.
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		//GPS location updates.
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	    // The rest of your onStart() code.
	    EasyTracker.getInstance().activityStart(this); // Add this method.
	  }

	  /* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MapFragmentD.mapView.setMyLocationEnabled(false);
		locationManager.removeUpdates(locationListener);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MapFragmentD.mapView.setMyLocationEnabled(true);
		//Cell-ID and WiFi location updates.
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		//GPS location updates.
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	@Override
	  public void onStop() {
	    super.onStop();
	    // The rest of your onStop() code.
	    EasyTracker.getInstance().activityStop(this); // Add this method. 
	  }

}
