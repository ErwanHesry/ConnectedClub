package com.erwanhesry.connectedclub.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.erwanhesry.connectedclub.MainActivity;
import com.erwanhesry.connectedclub.R;
import com.erwanhesry.connectedclub.R.id;
import com.erwanhesry.connectedclub.R.layout;
import com.erwanhesry.connectedclub.model.Cocktails;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CocktailItemAdapter  extends ArrayAdapter<Cocktails> {

	 private ArrayList<Cocktails> cocktails;
	  private Context ctx;
	  //private static ImageView image;
	  private static Bitmap bitmap;
	  private View v;
	
	public CocktailItemAdapter(Context context, int textViewResourceId, ArrayList<Cocktails> cocktails) {
		super(context, textViewResourceId, cocktails);
		this.cocktails = cocktails;
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
	      v = vi.inflate(R.layout.cocktail_listitem, null);
	    }
		
	    Cocktails cocktail = cocktails.get(position);
	    if(cocktail != null){
	    	TextView name = (TextView)v.findViewById(R.id.cocktail_name);
	    	TextView composition = (TextView)v.findViewById(R.id.cocktail_composition);
	    	TextView price = (TextView)v.findViewById(R.id.cocktail_price);
	    	ImageView icon = (ImageView)v.findViewById(R.id.cocktail_icon);
	    	
	    	if(name != null){
	    		name.setText(cocktail.name);
	    		name.setTypeface(MainActivity.tf);
	    	}
	    	
	    	if(composition != null){
	    		composition.setText(cocktail.composition);
	    		composition.setTypeface(MainActivity.tf);
	    	}
	    	
	    	if(price != null){
	    		price.setText(String.valueOf(cocktail.price)+"€");
	    		price.setTypeface(MainActivity.tf);
	    	}
	    	
	    	if(icon != null){
	    		try {
	    			String cName = cocktail.name.toLowerCase();
	    			cName = cName.replace(" ", "-");
					icon.setBackground(getBitmapFromAsset("Cocktails/"+cName+".png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					try {
						icon.setBackground(getBitmapFromAsset("Cocktails/default.png"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
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

	private Drawable getBitmapFromAsset(String strName) throws IOException
    {
        AssetManager assetManager = ctx.getAssets();

        InputStream istr = assetManager.open(strName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        Drawable d = new BitmapDrawable(ctx.getResources(), bitmap);
        istr.close();

        return d;
    }

}
