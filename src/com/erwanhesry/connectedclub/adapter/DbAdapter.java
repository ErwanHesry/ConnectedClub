package com.erwanhesry.connectedclub.adapter;

import java.util.ArrayList;
import java.util.List;

import com.erwanhesry.connectedclub.model.Club;
import com.erwanhesry.connectedclub.model.Cocktails;
import com.erwanhesry.connectedclub.model.Suggestion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
	
	private static final String TAG = "DbAdapter";
	
	public static final String KEY_ID = "_id";
	
	private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cc";
    private static final String DATABASE_TABLE_CLUB = "club";
    private static final String DATABASE_TABLE_COCKTAIL = "cocktail";
    private static final String DATABASE_TABLE_SUGGESTION = "suggestion";
    private static final String DATABASE_TABLE_HAS_COCKTAIL = "has_cocktail";
	private static final String DATABASE_TABLE_HAS_SUGGESTION = "has_suggestion";
    
    public static final String KEY_ID_CLUB = "idclub";
	public static final String KEY_CLUB_NAME = "name";
	public static final String KEY_CLUB_PHONE = "club_phone";
	public static final String KEY_TYPE = "type";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_TWITTER_ACCOUNT = "twitterAccount";
	public static final String KEY_FACEBOOK_ACCOUNT = "facebookAccount";
	public static final String KEY_OPEN = "openHour";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_PHOTO_URL = "photoURL";
	public static final String KEY_ORDERABLE = "orderable";
	
	public static final String KEY_ID_COCKTAIL = "idcocktail";
	public static final String KEY_COCKTAIL_NAME = "name";
	public static final String KEY_COMPOSITION = "composition";
	public static final String KEY_PRICE = "price";
	
	public static final String KEY_ID_CLUB_JOIN = "idJoinClub";
	public static final String KEY_ID_COCKTAIL_JOIN = "idJoinCocktail";
	public static final String KEY_ID_SUGGESTION_JOIN = "idJoinSuggestion";
	
	public static final String KEY_ID_SUGGESTION = "id_suggestion";
	public static final String KEY_SUGGESTION_NAME = "name";
	public static final String KEY_SUGGESTION_DESCRIPTION = "description";
	public static final String KEY_PHONE_NUMBER = "number";
	public static final String KEY_IS_A_NUMBER = "isANumber";
	
	private static final String DATABASE_CREATE_TABLE_CLUB =
	            "CREATE TABLE " + DATABASE_TABLE_CLUB + " (" +
	            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            KEY_ID_CLUB + " INTEGER NOT NULL," +
	            KEY_ORDERABLE + " INTEGER NOT NULL," +
	            KEY_CLUB_NAME + " TEXT NOT NULL, " +
	            KEY_CLUB_PHONE + " TEXT NOT NULL, " +
	            KEY_TYPE + " TEXT NOT NULL, " +
	            KEY_DESCRIPTION + " TEXT NOT NULL, " +
	            KEY_OPEN + " TEXT NOT NULL, " +
	            KEY_PHOTO_URL + " TEXT, " +
	            KEY_TWITTER_ACCOUNT + " TEXT, " +
	            KEY_FACEBOOK_ACCOUNT + " TEXT, " +
	            KEY_LATITUDE + " REAL NOT NULL, " +
	            KEY_LONGITUDE + " REAL NOT NULL); ";
	
	private static final String DATABASE_CREATE_TABLE_COCKTAIL =
            "CREATE TABLE " + DATABASE_TABLE_COCKTAIL + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_ID_COCKTAIL + " INTEGER NOT NULL," +
            KEY_COCKTAIL_NAME + " TEXT NOT NULL, " +
            KEY_COMPOSITION + " TEXT NOT NULL, " +
            KEY_PRICE + " REAL NOT NULL); ";
	
	private static final String DATABASE_CREATE_TABLE_SUGGESTION =
            "CREATE TABLE " + DATABASE_TABLE_SUGGESTION + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_ID_SUGGESTION + " INTEGER NOT NULL," +
            KEY_SUGGESTION_NAME + " TEXT NOT NULL, " +
            KEY_SUGGESTION_DESCRIPTION + " TEXT NULL, " +
            KEY_PHONE_NUMBER + " TEXT NOT NULL, " + 
            KEY_IS_A_NUMBER + " INTEGER NOT NULL ); ";
	
	private static final String DATABASE_CREATE_TABLE_HAS_COCKTAIL =
			"CREATE TABLE " + DATABASE_TABLE_HAS_COCKTAIL + " (" +
            KEY_ID_CLUB_JOIN + " INTEGER NOT NULL, " +
            KEY_ID_COCKTAIL_JOIN + " INTEGER NOT NULL," +
            "PRIMARY KEY(" + KEY_ID_CLUB_JOIN + ", " + KEY_ID_COCKTAIL_JOIN + "), " +
            "FOREIGN KEY(" + KEY_ID_CLUB_JOIN + ") REFERENCES " + DATABASE_TABLE_CLUB + "(" + KEY_ID_CLUB + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + KEY_ID_COCKTAIL_JOIN + ") REFERENCES " + DATABASE_TABLE_COCKTAIL + "(" + KEY_ID_COCKTAIL + ") ON DELETE SET DEFAULT);";
	
	private static final String DATABASE_CREATE_TABLE_HAS_SUGGESTION =
			"CREATE TABLE " + DATABASE_TABLE_HAS_SUGGESTION + " (" +
            KEY_ID_CLUB_JOIN + " INTEGER NOT NULL, " +
            KEY_ID_SUGGESTION_JOIN + " INTEGER NOT NULL," +
            "PRIMARY KEY(" + KEY_ID_CLUB_JOIN + ", " + KEY_ID_SUGGESTION_JOIN + "), " +
            "FOREIGN KEY(" + KEY_ID_CLUB_JOIN + ") REFERENCES " + DATABASE_TABLE_CLUB + "(" + KEY_ID_CLUB + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + KEY_ID_SUGGESTION_JOIN + ") REFERENCES " + DATABASE_TABLE_SUGGESTION + "(" + KEY_ID_SUGGESTION + ") ON DELETE SET DEFAULT);";
    
    private final Context mContext;
    
    private static DbAdapter mDbAdapter = null;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    
    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
        	db.execSQL(DATABASE_CREATE_TABLE_CLUB);
        	db.execSQL(DATABASE_CREATE_TABLE_COCKTAIL);
        	db.execSQL(DATABASE_CREATE_TABLE_SUGGESTION);
        	db.execSQL(DATABASE_CREATE_TABLE_HAS_COCKTAIL);
        	db.execSQL(DATABASE_CREATE_TABLE_HAS_SUGGESTION);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        	Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_CREATE_TABLE_CLUB);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_CREATE_TABLE_COCKTAIL);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_CREATE_TABLE_SUGGESTION);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_CREATE_TABLE_HAS_COCKTAIL);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_CREATE_TABLE_HAS_SUGGESTION);
            onCreate(db);
        }
    }
    
    public DbAdapter(Context ctx)
    {
        this.mContext = ctx;
        
        open();
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        close();

        super.finalize();
    }

    public static DbAdapter getInstance(Context ctx)
    {
        if(mDbAdapter == null)
            mDbAdapter = new DbAdapter(ctx);

        return mDbAdapter;
    }
    
    // Openes database and return it
    public DbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    // Closes database
    public void close()
    {
        mDbHelper.close();
    }
    
    
    /*
     * Clubs
     */
    public boolean isClubExists(Club c)
    {
        Cursor cursor = mDb.rawQuery(    "SELECT 1 FROM " + DATABASE_TABLE_CLUB
                                        + " WHERE " + KEY_CLUB_NAME + "='" + c.name + "'",
                                        null);

        return cursor.getCount() > 0;
    }
    
    public boolean updateClub(Club c)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_CLUB_NAME, c.name);
        values.put(KEY_CLUB_PHONE, c.phoneNumber);
        values.put(KEY_TYPE, c.type);
    	values.put(KEY_DESCRIPTION, c.description);
    	values.put(KEY_PHOTO_URL, c.photoURL);
    	values.put(KEY_TWITTER_ACCOUNT, c.twitterAccount);
    	values.put(KEY_FACEBOOK_ACCOUNT, c.facebookAccount);
    	values.put(KEY_OPEN, c.openHour);
    	values.put(KEY_LATITUDE, c.latitude);
    	values.put(KEY_LONGITUDE, c.longitude);
    	values.put(KEY_ID_CLUB, c.cid);
    	values.put(KEY_ORDERABLE, c.orderable);
        return mDb.update(    DATABASE_TABLE_CLUB,
                            values,
                            KEY_ID_CLUB + "='" + c.cid + "'",
                            null) == 1;
    }
    
    public void saveClub(Club c){
    	if(!isClubExists(c)){
	    	ContentValues values = new ContentValues();
	    	values.put(KEY_CLUB_NAME, c.name);
	    	values.put(KEY_CLUB_PHONE, c.phoneNumber);
	    	values.put(KEY_TYPE, c.type);
	    	values.put(KEY_DESCRIPTION, c.description);
	    	values.put(KEY_PHOTO_URL, c.photoURL);
	    	values.put(KEY_TWITTER_ACCOUNT, c.twitterAccount);
	    	values.put(KEY_FACEBOOK_ACCOUNT, c.facebookAccount);
	    	values.put(KEY_OPEN, c.openHour);
	    	values.put(KEY_LATITUDE, c.latitude);
	    	values.put(KEY_LONGITUDE, c.longitude);
	    	values.put(KEY_ID_CLUB, c.cid);
	    	values.put(KEY_ORDERABLE, c.orderable);
	    	mDb.insert(DATABASE_TABLE_CLUB, null, values);
	
    	} else {
    		updateClub(c);
    	}
    }
    
    public boolean deleteClub(Club c)
    {
        return mDb.delete(    DATABASE_TABLE_CLUB,
                KEY_ID_CLUB + "=" + c.cid,
                null) > 0;
    }
    
    public List<Club> getClubs()
    {
        List<Club> clubs = new ArrayList<Club>();

        Cursor cursor = mDb.query(    DATABASE_TABLE_CLUB,
                                        null,null,null,null,null,
                                        KEY_ID + " DESC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Club c = new Club(  
                		cursor.getInt(cursor.getColumnIndex(KEY_ID_CLUB)),
                		cursor.getString(cursor.getColumnIndex(KEY_CLUB_NAME)),
                		cursor.getString(cursor.getColumnIndex(KEY_CLUB_PHONE)),
                		cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_PHOTO_URL)),
                        cursor.getString(cursor.getColumnIndex(KEY_TWITTER_ACCOUNT)),
                        cursor.getString(cursor.getColumnIndex(KEY_FACEBOOK_ACCOUNT)),
                        cursor.getString(cursor.getColumnIndex(KEY_OPEN)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_ORDERABLE)));

                clubs.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return clubs;
    }
    
    public Club getClubById(long id)
    {
        Cursor cursor = mDb.query(    DATABASE_TABLE_CLUB,
                                    null,
                                    KEY_ID_CLUB + "=" + id,
                                    null, null, null, null,
                                    "1");
        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        Club c = new Club(  cursor.getInt(cursor.getColumnIndex(KEY_ID_CLUB)),
        					cursor.getString(cursor.getColumnIndex(KEY_CLUB_NAME)),
        					cursor.getString(cursor.getColumnIndex(KEY_CLUB_PHONE)),
        					cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_PHOTO_URL)),
                            cursor.getString(cursor.getColumnIndex(KEY_TWITTER_ACCOUNT)),
                            cursor.getString(cursor.getColumnIndex(KEY_FACEBOOK_ACCOUNT)),
                            cursor.getString(cursor.getColumnIndex(KEY_OPEN)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                            cursor.getInt(cursor.getColumnIndex(KEY_ORDERABLE)));

        cursor.close();

        return c;
    }
    
    public long getClubIdByName(String name){
    	Cursor cursor = mDb.query(    DATABASE_TABLE_CLUB,
						                null,
						                KEY_CLUB_NAME + "='" + name +"'",
						                null, null, null, null,
						                "1");
    	if (cursor != null)
        {
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(KEY_ID));
        } else {
        	return 0;
        }
    	
    }
    
    
    /*
     * Cocktails
     */
    public boolean isCocktailExists(Cocktails c)
    {
        Cursor cursor = mDb.rawQuery(    "SELECT 1 FROM " + DATABASE_TABLE_COCKTAIL
                                        + " WHERE " + KEY_COCKTAIL_NAME + "='" + c.name + "'",
                                        null);

        return cursor.getCount() > 0;
    }
    
    public boolean updateCocktail(Cocktails c)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_CLUB_NAME, c.name);
    	values.put(KEY_COMPOSITION, c.composition);
    	values.put(KEY_PRICE, c.price);
    	values.put(KEY_ID_COCKTAIL, c.cid);

        return mDb.update(    DATABASE_TABLE_COCKTAIL,
                            values,
                            KEY_ID_COCKTAIL + "='" + c.cid + "'",
                            null) == 1;
    }
    
    public void saveCocktail(Cocktails c){
    	if(!isCocktailExists(c)){
	    	ContentValues values = new ContentValues();
	    	values.put(KEY_CLUB_NAME, c.name);
	    	values.put(KEY_COMPOSITION, c.composition);
	    	values.put(KEY_PRICE, c.price);
	    	values.put(KEY_ID_COCKTAIL, c.cid);

	    	mDb.insert(DATABASE_TABLE_COCKTAIL, null, values);
	    	
    	} else {
    		updateCocktail(c);
    	}
    }
    
    public boolean deleteCocktail(Cocktails c)
    {
        return mDb.delete(    DATABASE_TABLE_COCKTAIL,
                KEY_ID_COCKTAIL + "=" + c.cid,
                null) > 0;
    }
    
    public List<Cocktails> getCocktails()
    {
        List<Cocktails> cocktails = new ArrayList<Cocktails>();

        Cursor cursor = mDb.query(    DATABASE_TABLE_COCKTAIL,
                                        null,null,null,null,null,
                                        KEY_ID + " DESC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Cocktails c = new Cocktails(  
                		cursor.getInt(cursor.getColumnIndex(KEY_ID_COCKTAIL)),
                		cursor.getString(cursor.getColumnIndex(KEY_COCKTAIL_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_COMPOSITION)),
                        cursor.getFloat(cursor.getColumnIndex(KEY_PRICE)));

                cocktails.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return cocktails;
    }
    
    public List<Cocktails> getCocktailsAssociatedToClub(int id)
    {
        List<Cocktails> cocktails = new ArrayList<Cocktails>();
/*
        Cursor cursor = mDb.query(    DATABASE_TABLE_COCKTAIL,
                                        null,null,null,null,null,
                                        KEY_ID + " DESC");
*/      
        Cursor cursor = mDb.rawQuery("SELECT "+KEY_ID_COCKTAIL+", "+KEY_COCKTAIL_NAME+", "+KEY_COMPOSITION+", "+KEY_PRICE+"  FROM "+
        							DATABASE_TABLE_COCKTAIL+" INNER JOIN "+DATABASE_TABLE_HAS_COCKTAIL+
        							" ON "+DATABASE_TABLE_COCKTAIL+"."+KEY_ID_COCKTAIL+" = "+DATABASE_TABLE_HAS_COCKTAIL+"."+KEY_ID_COCKTAIL_JOIN+
        							" WHERE "+KEY_ID_CLUB_JOIN+" = "+id,
        							null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Cocktails c = new Cocktails(  
                		cursor.getInt(cursor.getColumnIndex(KEY_ID_COCKTAIL)),
                		cursor.getString(cursor.getColumnIndex(KEY_COCKTAIL_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_COMPOSITION)),
                        cursor.getFloat(cursor.getColumnIndex(KEY_PRICE)));

                cocktails.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return cocktails;
    }
    
    public Cocktails getCocktailById(int id){
    	Cursor cursor = mDb.query(    DATABASE_TABLE_COCKTAIL,
                null,
                KEY_ID + "=" + id,
                null, null, null, null,
                "1");
		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		
		Cocktails c = new Cocktails(  
				cursor.getInt(cursor.getColumnIndex(KEY_ID_COCKTAIL)),
				cursor.getString(cursor.getColumnIndex(KEY_COCKTAIL_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_COMPOSITION)),
                cursor.getFloat(cursor.getColumnIndex(KEY_PRICE)));

		cursor.close();
		
		return c;
    }
    
    /*
     * Suggestions
     */
    public boolean isSuggestionExists(Suggestion s)
    {
        Cursor cursor = mDb.rawQuery(    "SELECT 1 FROM " + DATABASE_TABLE_SUGGESTION
                                        + " WHERE " + KEY_SUGGESTION_NAME + "='" + s.name + "'",
                                        null);

        return cursor.getCount() > 0;
    }
    
    public boolean updateSuggestion(Suggestion s)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_SUGGESTION_NAME, s.name);
    	values.put(KEY_SUGGESTION_DESCRIPTION, s.description);
    	values.put(KEY_PHONE_NUMBER, s.phone);
    	values.put(KEY_IS_A_NUMBER, s.isAClub);

        return mDb.update(  DATABASE_TABLE_SUGGESTION,
                            values,
                            KEY_ID_SUGGESTION + "='" + s.sid + "'",
                            null) == 1;
    }
    
    public void saveSuggestion(Suggestion s){
    	if(!isSuggestionExists(s)){
	    	ContentValues values = new ContentValues();
	    	values.put(KEY_SUGGESTION_NAME, s.name);
	    	values.put(KEY_SUGGESTION_DESCRIPTION, s.description);
	    	values.put(KEY_PHONE_NUMBER, s.phone);
	    	values.put(KEY_IS_A_NUMBER, s.isAClub);
	    	values.put(KEY_ID_SUGGESTION, s.sid);

	    	mDb.insert(DATABASE_TABLE_SUGGESTION, null, values);
	    	
    	} else {
    		updateSuggestion(s);
    	}
    }
    
    public boolean deleteSuggestion(Suggestion s)
    {
        return mDb.delete(    DATABASE_TABLE_SUGGESTION,
                KEY_ID_SUGGESTION + "=" + s.sid,
                null) > 0;
    }
    
    public List<Suggestion> getSugggestion()
    {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        Cursor cursor = mDb.query(    DATABASE_TABLE_SUGGESTION,
                                        null,null,null,null,null,
                                        KEY_ID + " DESC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Suggestion s = new Suggestion(  
                		cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION_NAME)),
                		cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)),
                        cursor.getInt(cursor.getColumnIndex(KEY_IS_A_NUMBER)),
                        cursor.getInt(cursor.getColumnIndex(KEY_ID_SUGGESTION)));

                suggestions.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return suggestion list
        return suggestions;
    }
    
    public List<Suggestion> getSuggestionAssociatedToClub(int id)
    {
    	List<Suggestion> suggestions = new ArrayList<Suggestion>();
/*
        Cursor cursor = mDb.query(    DATABASE_TABLE_COCKTAIL,
                                        null,null,null,null,null,
                                        KEY_ID + " DESC");
*/      
        Cursor cursor = mDb.rawQuery("SELECT "+KEY_SUGGESTION_NAME+", "+KEY_SUGGESTION_DESCRIPTION+", "+KEY_PHONE_NUMBER+", "+KEY_IS_A_NUMBER+", "+KEY_ID_SUGGESTION+"  FROM "+
        							DATABASE_TABLE_SUGGESTION+" INNER JOIN "+DATABASE_TABLE_HAS_SUGGESTION+
        							" ON "+DATABASE_TABLE_SUGGESTION+"."+KEY_ID_SUGGESTION+" = "+DATABASE_TABLE_HAS_SUGGESTION+"."+KEY_ID_SUGGESTION_JOIN+
        							" WHERE "+KEY_ID_CLUB_JOIN+" = "+id,
        							null);

     // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Suggestion s = new Suggestion(  
                		cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION_NAME)),
                		cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)),
                        cursor.getInt(cursor.getColumnIndex(KEY_IS_A_NUMBER)),
                        cursor.getInt(cursor.getColumnIndex(KEY_ID_SUGGESTION)));

                suggestions.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return suggestion list
        return suggestions;
    }
    
    public Suggestion getSuggestionById(int id){
    	Cursor cursor = mDb.query(    DATABASE_TABLE_SUGGESTION,
                null,
                KEY_ID_SUGGESTION + "=" + id,
                null, null, null, null,
                "1");
		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		
		Suggestion s = new Suggestion(  
        		cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION_NAME)),
        		cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(KEY_IS_A_NUMBER)),
                cursor.getInt(cursor.getColumnIndex(KEY_ID_SUGGESTION)));

		cursor.close();
		
		return s;
    } 
    
    
    
    /*
     * Associations
     */
    public boolean isCocktailAssociationExists(int cl, int co)
    {
        Cursor cursor = mDb.rawQuery(    "SELECT 1 FROM " + DATABASE_TABLE_HAS_COCKTAIL
                                        + " WHERE " + KEY_ID_CLUB_JOIN + "='" + cl + "' AND " + KEY_ID_COCKTAIL_JOIN + "='" + co + "'",
                                        null);

        return cursor.getCount() > 0;
    }
    
    public void saveCocktailAssociations(int clubId, int cocktailId){
    	if(!isCocktailAssociationExists(clubId, cocktailId)){
	    	ContentValues values = new ContentValues();
	    	values.put(KEY_ID_CLUB_JOIN, clubId);
	    	values.put(KEY_ID_COCKTAIL_JOIN, cocktailId);
	    	mDb.insert(DATABASE_TABLE_HAS_COCKTAIL, null, values);
    	}
    }
    
    public boolean isSuggestionAssociationExists(int c, int s)
    {
        Cursor cursor = mDb.rawQuery(    "SELECT 1 FROM " + DATABASE_TABLE_HAS_SUGGESTION
                                        + " WHERE " + KEY_ID_CLUB_JOIN + "='" + c + "' AND " + KEY_ID_SUGGESTION_JOIN + "='" + s + "'",
                                        null);

        return cursor.getCount() > 0;
    }
    
    public void saveSuggestionAssociations(int c, int s){
    	if(!isCocktailAssociationExists(c, s)){
	    	ContentValues values = new ContentValues();
	    	values.put(KEY_ID_CLUB_JOIN, c);
	    	values.put(KEY_ID_SUGGESTION_JOIN, s);
	    	mDb.insert(DATABASE_TABLE_HAS_SUGGESTION, null, values);
    	}
    }
    
    public void flushAllAssociations()
    {
        mDb.delete( DATABASE_TABLE_HAS_SUGGESTION,
                    null,null);
        mDb.delete( DATABASE_TABLE_HAS_COCKTAIL,
                null,null);
    }
    
}
