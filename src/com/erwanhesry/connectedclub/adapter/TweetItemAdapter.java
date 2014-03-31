package com.erwanhesry.connectedclub.adapter;

import java.util.ArrayList;

import com.erwanhesry.connectedclub.MainActivity;
import com.erwanhesry.connectedclub.R;
import com.erwanhesry.connectedclub.R.id;
import com.erwanhesry.connectedclub.R.layout;
import com.erwanhesry.connectedclub.R.string;
import com.erwanhesry.connectedclub.model.Tweets;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetItemAdapter extends ArrayAdapter<Tweets> {
	  private ArrayList<Tweets> tweets;
	  private Context ctx;
	  //private static ImageView image;
	  private static Bitmap bitmap;
	  private View v;

	  public TweetItemAdapter(Context context, int textViewResourceId, ArrayList<Tweets> tweets) {
	    super(context, textViewResourceId, tweets);
	    this.tweets = tweets;
	    this.ctx = context;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    v = convertView;
	    if (v == null) {
	      LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      v = vi.inflate(R.layout.twitter_listitem, null);
	    }

	    Tweets tweet = tweets.get(position);
	    if (tweet != null) {
	      TextView username = (TextView) v.findViewById(R.id.username);
	      TextView message = (TextView) v.findViewById(R.id.message);
	      TextView date = (TextView) v.findViewById(R.id.date);
	      ImageView image = (ImageView) v.findViewById(R.id.avatar);
	      
	      if (username != null) {
	        username.setText(tweet.username);
	        username.setTypeface(MainActivity.tf);
	      }

	      if(message != null) {
	        message.setText(tweet.message);
	        message.setTypeface(MainActivity.tf);
	      }
	      
	      if(date != null) {
	    	String tweetDate = tweet.date;
	    	tweetDate = tweetDate.substring(8, tweetDate.length());
	    	tweetDate = tweetDate.substring(0, tweetDate.length()-15);
	    	tweetDate = ctx.getString(R.string.the)+" "+tweetDate;
	        date.setText(tweetDate);
	        date.setTypeface(MainActivity.tf);
	      }
			
	      if(image != null) {
	    	//new DownloadTweeterProfilePicture().execute(tweet.image_url);
	    	MainActivity.imageLoader.DisplayImage(tweet.image_url, image, true);
	      }
	    }

	    return v;
	  }
	  
	  
	  /*
	  class DownloadTweeterProfilePicture extends AsyncTask<String, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(params[0]).getContent());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return getCroppedBitmap(bitmap);
		}


		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);			
			image.setImageBitmap(result);
			image.invalidate();
		}
		
		public Bitmap getCroppedBitmap(Bitmap bitmap) {
		    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		            bitmap.getHeight(), Config.ARGB_8888);
		    Canvas canvas = new Canvas(output);

		    final int color = 0xff424242;
		    final Paint paint = new Paint();
		    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		    paint.setAntiAlias(true);
		    canvas.drawARGB(0, 0, 0, 0);
		    paint.setColor(color);
		    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
		            bitmap.getWidth() / 2, paint);
		    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		    canvas.drawBitmap(bitmap, rect, rect, paint);
		    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
		    //return _bmp;
		    return output;
		}
	  
	  }
	  */
	}