/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.disciplesbay.latterhousehq.mychurch.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import com.disciplesbay.latterhousehq.mychurch.SermonsActivity;
import com.disciplesbay.latterhousehq.mychurch.data.Announcements;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.data.CartModel;
import com.disciplesbay.latterhousehq.mychurch.data.EventsData;
import com.disciplesbay.latterhousehq.mychurch.data.SermonCategory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SQLiteHandler extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "latterhouse";

    //Table names
    //apikey table name
    private static final String TABLE_API_KEY = "apikey";
    // Login table name
    private static final String TABLE_USER = "user";
    //Events Table name
    private static final String TABLE_EVENTS = "events";
     //Downloads table name
    private static final String TABLE_CARD = "card";

    //cart table name
    private static final String TABLE_CART = "cart";
    // date of birth table name
    private static final String TABLE_DB = "date_of_birth";


    //Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Api_key table column names
    private static final String KEY_API = "api";

    // date of birth column names
    private static final String KEY_DAY = "day";
    private static final String KEY_MONTHS = "month";
    private static final String KEY_YEARS = "year";

    //cart column names
    private static final String KEY_PRODUCT_NAME = "product";
    private static final String KEY_PRODUCT_URL = "url";
    private static final String KEY_PRODUCT_AMOUNT = "amount";
    private static final String KEY_PRODUCT_IMAGE = "image";

    // Login Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";


    //Events Table Column names
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_EVENT_DESCRIPTION = "event_description";
    private static final String KEY_EVENT_TIME = "time";
    private static final String KEY_TYPE = "type";
    private static final String KEY_IMAGE = "image";

    //Tag Table Column names
    private static final String KEY_TAG_NAME = "tag_name";


    //card table column names
    private static final String KEY_CARD_NUMBER = "cardNumber";
    private static final String KEY_CVV = "cvv";
    private static final String KEY_YEAR = "year";
    private static final String KEY_MONTH = "month";


    // Creating Tables

    //Api_key table create statement
    private static final String CREATE_API_TABLE = "CREATE TABLE " + TABLE_API_KEY + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_API + " TEXT,"
            + KEY_CREATED_AT + " TEXT" + ")";

    private static final String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PRODUCT_NAME + " TEXT,"
            + KEY_PRODUCT_AMOUNT + " INTEGER," + KEY_PRODUCT_URL + " TEXT,"
            + KEY_PRODUCT_IMAGE + " TEXT," + KEY_CREATED_AT + " TEXT" + ")";

    // date of birth table create statement
    private static final String CREATE_DB_TABLE = "CREATE TABLE " + TABLE_DB + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DAY + " INTEGER," + KEY_MONTHS + " INTEGER,"
            + KEY_YEARS + " INTEGER" + ")";
    // login table create statement
    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
            + KEY_CREATED_AT + " TEXT" + ")";
    // Audio table create statement
    //Event table create statement
    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " INTEGER," + KEY_EVENT_NAME + " TEXT,"
            + KEY_IMAGE + " TEXT,"
            + KEY_EVENT_DESCRIPTION + " TEXT," + KEY_EVENT_TIME + " TEXT,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    private static final String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARD
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CARD_NUMBER + " INTEGER,"
            + KEY_CVV + " INTEGER," + KEY_YEAR + " INTEGER," + KEY_MONTH + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_LOGIN_TABLE);
         db.execSQL(CREATE_EVENTS_TABLE);
        db.execSQL(CREATE_API_TABLE);
        db.execSQL(CREATE_CARD_TABLE);
        db.execSQL(CREATE_CART_TABLE);
        db.execSQL(CREATE_DB_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_API_KEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DB);
        // Create tables again
        onCreate(db);
    }


    public void addDB(int day, int year, int month) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DAY, day);
        values.put(KEY_YEARS, year);//api key
        values.put(KEY_MONTHS, month);//api key
        //inserting row
        db.insertWithOnConflict(TABLE_DB, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();

        Log.d(TAG, "card inserted " + values.toString());

    }
    public HashMap<String, String> getDB() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_DB;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("day", cursor.getString(cursor.getColumnIndex(KEY_DAY)));
            user.put("year", cursor.getString(cursor.getColumnIndex(KEY_YEARS)));
            user.put("month", cursor.getString(cursor.getColumnIndex(KEY_MONTHS)));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    public void addCart(int id, String Pname, String Pamount, String Purl, String Pimage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_PRODUCT_NAME, Pname);//product name
        values.put(KEY_PRODUCT_AMOUNT, Pamount);//product amount
        values.put(KEY_PRODUCT_URL, Purl);//product url
        values.put(KEY_PRODUCT_IMAGE, Pimage);//product image
        values.put(KEY_CREATED_AT, getDateTime());
        db.insertWithOnConflict(TABLE_CART, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();

        Log.d(TAG, "cart inserted " + values.toString());

    }

    public ArrayList<CartModel> getCarts() {
        ArrayList<CartModel> carts = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CART;

        Log.e(LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                CartModel product = new CartModel();
                product.setProductName(c.getString(c.getColumnIndex(KEY_PRODUCT_NAME)));
                product.setAmount(c.getInt(c.getColumnIndex(KEY_PRODUCT_AMOUNT)));
                product.setImageUrl(c.getString(c.getColumnIndex(KEY_PRODUCT_IMAGE)));
                product.setProductUrl(c.getString(c.getColumnIndex(KEY_PRODUCT_URL)));
                carts.add(product);
            } while (c.moveToNext());
        }
        return carts;

    }

    public int getTotalCart() {
        int total = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_CART;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_AMOUNT));

        }
        while (cursor.moveToNext()) {
            total = total + cursor.getInt(cursor.getColumnIndex(KEY_PRODUCT_AMOUNT)); ;
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Total: " + total);

        return total;
    }

    public void deleteCartItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // now delete the tag
        db.delete(TABLE_CART, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});


        db.close();

        Log.d(TAG, "Deleted cart item info from sqlite");
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();


        db.delete(TABLE_CART, null, null);

        db.close();

        Log.d(TAG, "Deleted cart details info from sqlite");
    }


    public void addCard(int id, long cardNUmber, int cvv, int year, int month) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_CARD_NUMBER, cardNUmber);//api key
        values.put(KEY_CVV, cvv);//api key
        values.put(KEY_YEAR, year);//api key
        values.put(KEY_MONTH, month);//api key
        values.put(KEY_CREATED_AT, getDateTime());//api key
        //inserting row
        db.insertWithOnConflict(TABLE_CARD, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Log.d(TAG, "card inserted " + values.toString());

    }

    public HashMap<String, String> getCard() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CARD;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("cardNumber", cursor.getString(cursor.getColumnIndex(KEY_CARD_NUMBER)));
            user.put("cvv", cursor.getString(cursor.getColumnIndex(KEY_CVV)));
            user.put("year", cursor.getString(cursor.getColumnIndex(KEY_YEAR)));
            user.put("month", cursor.getString(cursor.getColumnIndex(KEY_MONTH)));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public void deleteCard() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_CARD, null, null);
        db.close();

        Log.d(TAG, "Deleted card details info from sqlite");
    }

    /**
     * Storing user apikey
     */
    public void addApiKey(int id, String apikey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_API, apikey);//api key
        //inserting row
        long api_key_id = db.insertWithOnConflict(TABLE_API_KEY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Log.d(TAG, "api inserted " + apikey);

    }

    public HashMap<String, String> getApikey() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_API_KEY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("apikey", cursor.getString(cursor.getColumnIndex(KEY_API)));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public int updateApiKey(int id, String apiKey) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_API, apiKey);//api key

        Log.d(TAG, "api updated after login " + apiKey);
        // updating row
        return db.updateWithOnConflict(TABLE_API_KEY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)}, SQLiteDatabase.CONFLICT_REPLACE);


    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /*
     * Creating tag
     */


    /**
     * getting all tags
     */

    /*
     * Updating a tag
     */

    /*
     * Deleting a tag
     */

    /**
     * getting all audio under single tags
     */

    /**
     * getting all audio under single tags
     */

    /*
     * Creating a Audio
     */

    /*
     * Creating AUDIO_tag
     */

    /*
     * getting audio count
     */

    /*
     * Updating a audio
     */

    /*
     * Deleting a audio
     */

    /*
     * Creating a VIDEO
     */

    /*
     * Creating VIDEO_tag
     */

    /*
     * getting audio count
     */

    /*
     * Updating a audio
     */

    /*
     * Deleting a audio
     */

    /*
     * Creating Announcement
     */

    /**
     * getting all announcements
     */

    /*
     * Updating a announcement
     */

    /*
     * Deleting a announcement
     */


    public void createEvents(EventsData eventsData) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, eventsData.getId());
        values.put(KEY_EVENT_NAME, eventsData.getEventName());
        values.put(KEY_EVENT_DESCRIPTION, eventsData.getEventDesrciption());
        values.put(KEY_CREATED_AT, eventsData.getExpires());
        values.put(KEY_TYPE, eventsData.getType());
        values.put(KEY_IMAGE, eventsData.getImage());


        // insert row
        db.insertWithOnConflict(TABLE_EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        closeDB();


    }


    /**
     * getting all Events
     */
    public List<EventsData> getAllEvents() {
        List<EventsData> eventsData = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                EventsData t = new EventsData();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setEventName(c.getString(c.getColumnIndex(KEY_EVENT_NAME)));
                t.setEventDesrciption(c.getString(c.getColumnIndex(KEY_EVENT_DESCRIPTION)));
                t.setExpires(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
                t.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
                t.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));


                // adding to tags list
                eventsData.add(t);
            } while (c.moveToNext());
        }
        return eventsData;
    }

    /*
     * Updating a event
     */
    public int updateEvent(EventsData eventsData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, eventsData.getEventName());
        values.put(KEY_EVENT_DESCRIPTION, eventsData.getEventDesrciption());

        // updating row
        return db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(eventsData.getId())});
    }

    /*
     * Deleting a announcement
     */
    public void deleteEvents() {
        SQLiteDatabase db = this.getWritableDatabase();

        // now delete the announcement
        db.delete(TABLE_EVENTS, KEY_CREATED_AT + " = ?",
                new String[]{getDateTime()});
        Log.d(TAG, "deleted all events ");
        closeDB();
    }


    /**
     * get datetime
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
