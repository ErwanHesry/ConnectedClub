package com.erwanhesry.connectedclub;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erwanhesry.connectedclub.adapter.DbAdapter;
import com.erwanhesry.connectedclub.model.Club;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragmentD extends SupportMapFragment {
	private static final String TAG = "MapFragmentD";
	public static final LatLng Chapelier = new LatLng(48.73230, -3.45772);
	public static GoogleMap mapView;
	LocationManager locationManager;
	private static DbAdapter dbA;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	@Override
	public View onCreateView(LayoutInflater mInflater, ViewGroup arg1,
			Bundle arg2) {
		return super.onCreateView(mInflater, arg1, arg2);
	}

	@Override
	public void onInflate(Activity arg0, AttributeSet arg1, Bundle arg2) {
		super.onInflate(arg0, arg1, arg2);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		dbA = DbAdapter.getInstance(getActivity());
		List<Club> cList = dbA.getClubs();
		
		mapView = getMap();
		mapView.getUiSettings().setAllGesturesEnabled(true);
		mapView.getUiSettings().setCompassEnabled(true);
		mapView.setMapType(mapView.MAP_TYPE_HYBRID);
		mapView.setMyLocationEnabled(true);
		
		mapView.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				long id = dbA.getClubIdByName(arg0.getTitle());

				Intent intent = new Intent(MapActivity.ctx, LocatedInsideActivity.class);
				intent.putExtra(DbAdapter.KEY_ID, id);
				startActivity(intent);
				
				return false;
			}
		});
		
		for(Club c : cList){
			Marker marker = mapView.addMarker(new MarkerOptions()
	        .position(new LatLng(c.latitude, c.longitude))
	        .title(c.name)
	        .snippet(c.type)
	        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
		}
		/*	
		Marker chapelier = mapView.addMarker(new MarkerOptions()
        .position(Chapelier)
        .title("Le Chapelier")
        .snippet("Bar")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
		*/
	}
	
}
