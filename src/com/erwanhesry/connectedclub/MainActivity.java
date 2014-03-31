package com.erwanhesry.connectedclub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
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

import com.erwanhesry.connectedclub.adapter.DbAdapter;
import com.erwanhesry.connectedclub.model.Club;
import com.erwanhesry.connectedclub.model.Cocktails;
import com.erwanhesry.connectedclub.model.Suggestion;
import com.erwanhesry.connectedclub.utility.ImageLoader;
import com.erwanhesry.connectedclub.utility.Utils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private final int SPLASH_DISPLAY_LENGHT = 1500;
	
	public static Typeface tf;
	public static Typeface roboto;
	public static Context ctx;
	public static ImageLoader imageLoader;
	public static float scale;
	private static DbAdapter dbA;
	private TextView activity;
	public TextView tB;
	public TextView tBc;
	
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    
    public static Twitter twitter;
    public static Twitter twitterFeed;
	private static RequestToken requestToken;
	private static SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		tf = Typeface.createFromAsset(getAssets(), "font/OpenSans-Regular.ttf");
		roboto = Typeface.createFromAsset(getAssets(), "font/Roboto-Thin.ttf");
		scale = getResources().getDisplayMetrics().density;
		ctx = getApplicationContext();
		imageLoader = new ImageLoader(ctx);
		setContentView(R.layout.activity_main);
		
		mSharedPreferences = getApplicationContext().getSharedPreferences("ConnectedClubPref", 0);
		
		activity = (TextView)findViewById(R.id.textActivity);
		activity.setTypeface(tf);
		
		dbA = DbAdapter.getInstance(getApplicationContext());
		
		//Twitter
		mSharedPreferences = getSharedPreferences(Utils.PREFERENCE_NAME, MODE_PRIVATE);
		
		Uri uri = getIntent().getData();
		if (uri != null && uri.toString().startsWith(Utils.CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(Utils.IEXTRA_OAUTH_VERIFIER);
            try { 
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
                Editor e = mSharedPreferences.edit();
                e.putString(Utils.PREF_KEY_TOKEN, accessToken.getToken()); 
                e.putString(Utils.PREF_KEY_SECRET, accessToken.getTokenSecret()); 
                e.commit();
	        } catch (Exception e) { 
	                Log.e("Main Twitter", e.getMessage()); 
	                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show(); 
			}
        }		
		
		if(Utils.isOnline(ctx)){
			new DownloadClubsFromDataBase().execute("mobileGetClubs");
		} else {
			new Handler().postDelayed(new Runnable(){
	            public void run() {
	                // Create an Intent that will start the Menu-Activity. 
	                Intent mainIntent = new Intent(MainActivity.this,MapActivity.class);
	            	//Intent mainIntent = new Intent(MainActivity.this,LocatedInsideActivity.class);
	                MainActivity.this.startActivity(mainIntent);
	                MainActivity.this.finish();
	            }
	        }, SPLASH_DISPLAY_LENGHT);
		}
		
		tB = (TextView)findViewById(R.id.Twitter);
		tB.setVisibility(View.GONE);
		tB.setTypeface(roboto);
		tB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isConnected()){
					askOAuth();
				}
			}
		});
		
		tBc = (TextView)findViewById(R.id.TwitterCancel);
		tBc.setVisibility(View.GONE);
		tBc.setTypeface(roboto);
		tBc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mainIntent = new Intent(MainActivity.this,MapActivity.class);
            	//Intent mainIntent = new Intent(MainActivity.this,LocatedInsideActivity.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (isConnected()) {
			String oauthAccessToken = mSharedPreferences.getString(Utils.PREF_KEY_TOKEN, "");
			String oAuthAccessTokenSecret = mSharedPreferences.getString(Utils.PREF_KEY_SECRET, "");

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			Configuration conf = confbuilder
								.setOAuthConsumerKey(Utils.CONSUMER_KEY)
								.setOAuthConsumerSecret(Utils.CONSUMER_SECRET)
								.setOAuthAccessToken(oauthAccessToken)
								.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
								.build();
			twitterFeed = new TwitterFactory(conf).getInstance();
		} 
	}
	
	public static boolean isConnected() {
		return mSharedPreferences.getString(Utils.PREF_KEY_TOKEN, null) != null;
	}

	private void askOAuth() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if(currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	    
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(Utils.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(Utils.CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();

		try {
			requestToken = twitter.getOAuthRequestToken(Utils.CALLBACK_URL);
			Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG).show();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	class DownloadClubsFromDataBase extends AsyncTask<String, Void, Boolean>{
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			activity.setText(R.string.gettingClubs);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub			
			String apiURL = "http://connectedclub.erwanhesry.com/api/get.php";
			HttpClient client = new  DefaultHttpClient();
			HttpPost post = new HttpPost(apiURL); 
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// Get clubs
			String responseBody = null;
			try{
				List nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair("cc_function", params[0]));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				responseBody = client.execute(post, responseHandler);
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
					int id = Integer.parseInt((String) results.get("id"));
					String name = (String) results.get("name");
					String phone = (String) results.get("phone");
					String twitterAccount = (String) results.get("twitter");
					String facebookAccount = (String) results.get("facebook");
					String type = (String) results.get("type");
					String description = (String) results.get("description");
					String photoURL = (String) results.get("pictURL");
					String openHour = (String) results.get("openHour");
					int orderable = Integer.parseInt((String) results.get("orderable"));
					
					double lat = Double.parseDouble((String) results.get("latitude"));
					double lon = Double.parseDouble((String) results.get("longitude"));
					
					Club c = new Club(id, name, phone, type, description, photoURL, twitterAccount, facebookAccount, openHour, lat, lon, orderable);
					dbA.saveClub(c);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			new DownloadSuggestionsFromDataBase().execute("mobileGetSuggestions");
		}	
		
	}
	
	class DownloadSuggestionsFromDataBase extends AsyncTask<String, Void, Boolean>{
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub			
			String apiURL = "http://connectedclub.erwanhesry.com/api/get.php";
			HttpClient client = new  DefaultHttpClient();
			HttpPost post = new HttpPost(apiURL); 
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// Get clubs
			String responseBody = null;
			try{
				List nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair("cc_function", params[0]));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				responseBody = client.execute(post, responseHandler);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			JSONObject myjson;
			try {	
				myjson = new JSONObject(responseBody);
				JSONArray the_json_array = myjson.getJSONArray("suggestions");
				int size = the_json_array.length();
				for (int i = 0; i < size; i++) {
					JSONObject results = the_json_array.getJSONObject(i); 
					int id = Integer.parseInt((String) results.get("id"));
					String name = (String) results.get("name");
					String desc = (String) results.get("description");
					String phone = (String) results.get("phone");
					int isAClub = Integer.parseInt((String) results.get("isAClub"));
					
					Suggestion s = new Suggestion(name, desc, phone, isAClub, id);
					dbA.saveSuggestion(s);
				}
				
				the_json_array = myjson.getJSONArray("associations");
				size = the_json_array.length();
				for (int i = 0; i < size; i++) {
					JSONObject results = the_json_array.getJSONObject(i);
					int clubId = Integer.parseInt((String) results.get("clubId"));
					int suggestionId = Integer.parseInt((String) results.get("suggestionId"));
					dbA.saveSuggestionAssociations(clubId, suggestionId);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			new DownloadCocktailsFromDataBase().execute("mobileGetCocktails");
		}	
	}
	
	class DownloadCocktailsFromDataBase extends AsyncTask<String, Void, Boolean>{
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			activity.setText(R.string.gettingCocktails);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub			
			String apiURL = "http://connectedclub.erwanhesry.com/api/get.php";
			HttpClient client = new  DefaultHttpClient();
			HttpPost post = new HttpPost(apiURL); 
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// Get clubs
			String responseBody = null;
			try{
				List nameValuePairs = new ArrayList();
				nameValuePairs.add(new BasicNameValuePair("cc_function", params[0]));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				responseBody = client.execute(post, responseHandler);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			JSONObject myjson;
			try {	
				myjson = new JSONObject(responseBody);
				JSONArray the_json_array = myjson.getJSONArray("cocktails");
				int size = the_json_array.length();
				for (int i = 0; i < size; i++) {
					JSONObject results = the_json_array.getJSONObject(i); 
					int id = Integer.parseInt((String) results.get("id"));
					String name = (String) results.get("name");
					String comp = (String) results.get("composition");
					float price = Float.parseFloat((String) results.get("price"));
					
					Cocktails c = new Cocktails(id, name, comp, price);
					dbA.saveCocktail(c);
				}
				
				the_json_array = myjson.getJSONArray("associations");
				//dbA.flushAllAssociations();
				size = the_json_array.length();
				for (int i = 0; i < size; i++) {
					JSONObject results = the_json_array.getJSONObject(i);
					int clubId = Integer.parseInt((String) results.get("clubId"));
					int cocktailId = Integer.parseInt((String) results.get("cocktailId"));
					dbA.saveCocktailAssociations(clubId, cocktailId);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			
			activity.setText(R.string.finish);
			if(isConnected()){
				// Create an Intent that will start the Menu-Activity. 
	            Intent mainIntent = new Intent(MainActivity.this,MapActivity.class);
	        	//Intent mainIntent = new Intent(MainActivity.this,LocatedInsideActivity.class);
	            MainActivity.this.startActivity(mainIntent);
	            MainActivity.this.finish();
			} else {
				tB.setVisibility(View.VISIBLE);
				tBc.setVisibility(View.VISIBLE);
			}
		}	
	}
	
}
