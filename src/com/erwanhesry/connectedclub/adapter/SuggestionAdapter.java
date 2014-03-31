package com.erwanhesry.connectedclub.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.erwanhesry.connectedclub.LocatedInsideActivity;
import com.erwanhesry.connectedclub.MainActivity;
import com.erwanhesry.connectedclub.MapActivity;
import com.erwanhesry.connectedclub.R;
import com.erwanhesry.connectedclub.model.Suggestion;

public class SuggestionAdapter extends ArrayAdapter<Suggestion> {

	 private ArrayList<Suggestion> suggestions;
	 private Context ctx;
	 private View v;
	
	public SuggestionAdapter(Context context, int textViewResourceId, ArrayList<Suggestion> suggestions) {
		super(context, textViewResourceId, suggestions);
		this.suggestions = suggestions;
		this.ctx = context;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		v = convertView;
	    if (v == null) {
	      LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      v = vi.inflate(R.layout.suggestion_listitem, null);
	    }
		
	    final Suggestion s = suggestions.get(position);
	    if(s != null){
	    	TextView name = (TextView)v.findViewById(R.id.suggestion_name);
	    	TextView desc = (TextView)v.findViewById(R.id.suggestion_description);
	    	final ImageView phone = (ImageView)v.findViewById(R.id.call_button);
	    	final ImageView map = (ImageView)v.findViewById(R.id.map_button);
	    	
	    	if(name != null){
	    		name.setText(s.name);
	    		name.setTypeface(MainActivity.tf);
	    		name.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(s.isAClub == 1){
							DbAdapter dbA = DbAdapter.getInstance(ctx);
							long id = dbA.getClubIdByName(s.name);
							Intent intent = new Intent(ctx, LocatedInsideActivity.class);
							intent.putExtra(DbAdapter.KEY_ID, id);
							ctx.startActivity(intent);
						}
					}
				});
	    	}
	    	
	    	if(desc != null){
	    		desc.setText(s.description);
	    		desc.setTypeface(MainActivity.tf);
	    	}
	    	
	    	if(phone != null){
	    		phone.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						try {
					        Intent callIntent = new Intent(Intent.ACTION_CALL);
					        callIntent.setData(Uri.parse("tel:"+s.phone));
					        ctx.startActivity(callIntent);
					    } catch (Exception e) {
					    }
					}
				});
	    	}
	    	
	    	if(map != null){
	    		map.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String url = "geo:0,0?q="+s.name;
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW,  Uri.parse(url));
						ctx.startActivity(intent);
					}
				});
	    	}
	    	
	    }
		
		return v;
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}


}
