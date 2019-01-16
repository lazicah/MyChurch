package com.disciplesbay.latterhousehq.mychurch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.disciplesbay.latterhousehq.mychurch.Utils.MyBaseIndicatorBanner;
import com.disciplesbay.latterhousehq.mychurch.Utils.TLSSocketFactory;
import com.disciplesbay.latterhousehq.mychurch.adapters.AnnounceAdapter;
import com.disciplesbay.latterhousehq.mychurch.adapters.HorizontalAdapter;
import com.disciplesbay.latterhousehq.mychurch.adapters.MainMessage;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.app.ConnectivityReceiver;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.data.BannerItem;
import com.disciplesbay.latterhousehq.mychurch.data.EventsData;
import com.disciplesbay.latterhousehq.mychurch.data.VideoDetails;
import com.disciplesbay.latterhousehq.mychurch.download.DownloadManagerService;
import com.disciplesbay.latterhousehq.mychurch.helper.DataProvider;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;
import com.disciplesbay.latterhousehq.mychurch.Utils.SimpleImageBanner;
import com.disciplesbay.latterhousehq.mychurch.helper.ViewFindUtils;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.flyco.banner.transform.FadeSlideTransformer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {


    private static final int FLIP_DURATIO = 3000;

    private View decorView;
    public static final boolean DEBUG = !BuildConfig.BUILD_TYPE.equals("release");
    public int count = 0;
    public String apiKey;
    int tempInt = 0;
    HorizontalAdapter horizontalAdapter;
    ArrayList<AudioVideo> audioVideoList = new ArrayList<>();
    ArrayList<AudioVideo> auidoonly = new ArrayList<>();
    ArrayList<VideoDetails> videoDetailsArrayList = new ArrayList<>();
    ArrayList<EventsData> eventsDataArrayList = new ArrayList<>();
    String TAG = "ChannelActivity";
    //UC1NF71EwP41VdjAU1iXdLkw
    // UC9CYT9gSNLevX5ey2_6CK0Q
    String URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UCu82nJA0mkgJN6kTirBxhTw&maxResults=10&key=AIzaSyCKs-K5xcP9az3SiWTRT5guGWwiku_yEqI";
    NavigationView navigationView;
    Snackbar snackbar;
    View view;
    RecyclerView recyclerView2;
    RecyclerView recyclerView1;
    AnnounceAdapter announceAdapter;
    boolean isPaid;
    private RecyclerView recyclerView;
    private TextView txtName;
    private TextView txtEmail, login;
    private Button btnLogout;
    private TextView userImage;
    private SQLiteHandler db = new SQLiteHandler(this);
    private SessionManager session;
    private AdView mAdView;
    private String greetName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setSubtitle("LatterHouse HQ");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




        checkConnection();
        decorView = getWindow().getDecorView();
        printHashKey(this);

        showVideo();
        greetUser();
        loadVideo();
        loadEvents();
        sib_the_most_comlex_usage();




        //Videos
        recyclerView = (RecyclerView) findViewById(R.id.l_videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        horizontalAdapter = new HorizontalAdapter(MainActivity.this, videoDetailsArrayList);


        //SermonCategories
        recyclerView2 = (RecyclerView) findViewById(R.id.sermons);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView1 = (RecyclerView) findViewById(R.id.events_recycler);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if(!db.getAllEvents().isEmpty()){
            announceAdapter = new AnnounceAdapter(MainActivity.this, db.getAllEvents());
            recyclerView1.setAdapter(announceAdapter);
        }



        //navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {

            setHeader();
        }
        if (!session.isLoggedIn()) {


            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);
            navigationView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            view = navigationView.inflateHeaderView(R.layout.nav_header_temp);


            login = (TextView) view.findViewById(R.id.login);

            //login
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        TextView moreVideos = (TextView) findViewById(R.id.latest_videos);
        moreVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
                startActivity(intent);
            }
        });

        TextView buyMessages = (TextView) findViewById(R.id.buyMessages);
        buyMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SermonsActivity.class);
                startActivity(intent);
            }
        });





    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;

        if (id == R.id.nav_me) {
            intent = new Intent(this, DownloadsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_events) {
            intent = new Intent(this, EventsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_testimonies) {
            intent = new Intent(this, TestimoniesActivity.class);
            startActivity(intent);

        //} else if (id == R.id.nav_branches) {
            //intent = new Intent(this, BranchesActivity.class);
            //startActivity(intent);
          //  DownloadManagerService.startMission(MainActivity.this, "http://d8.o2tvseries.com/Shooter/Season%2003/Shooter%20-%20S03E07%20(TvShows4Mobile.Com).mp4", NewPipeSettings.getVideoDownloadFolder(this).getAbsolutePath(), "Shooter Season 3e7.mp4", false, 3);

        } else if (id == R.id.nav_messages) {
            intent = new Intent(this, SermonsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_giving) {
            intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_contact) {
            intent = new Intent(this, ContactActivity.class);
            startActivity(intent);

        //}else if (id == R.id.nav_books) {
          //  intent = new Intent(this, BooksActivity.class);
            //startActivity(intent);

        }else if (id == R.id.action_checkout) {
            intent = new Intent(this, ViewCartActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showVideo() {

        final ProgressBar videoBar = (ProgressBar) findViewById(R.id.youtubeLoader);
        videoBar.setVisibility(View.VISIBLE);


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                videoBar.setVisibility(View.INVISIBLE);


                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 1; i < jsonArray.length(); i++) {


                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonVideoId = jsonObject1.getJSONObject("id");
                        JSONObject jsonsnippet = jsonObject1.getJSONObject("snippet");
                        JSONObject jsonObjectdefault = jsonsnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        VideoDetails videoDetails = new VideoDetails();

                        String videoid = jsonVideoId.getString("videoId");

                        Log.e(TAG, " New Video Id" + videoid);
                        videoDetails.setURL(jsonObjectdefault.getString("url"));
                        videoDetails.setVideoName(jsonsnippet.getString("title"));
                        videoDetails.setVideoDesc(jsonsnippet.getString("description"));
                        videoDetails.setVideoId(videoid);

                        videoDetailsArrayList.add(videoDetails);
                    }
                    recyclerView.setAdapter(horizontalAdapter);
                    horizontalAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    private void greetUser() {

        session = new SessionManager(getApplicationContext());


        if (session.isLoggedIn()) {

            Calendar c = Calendar.getInstance();
            int hours = c.get(Calendar.HOUR_OF_DAY);

            String name = db.getUserDetails().get("name");

            if (name != null){
                greetName = name.split(" ")[0];}




            ImageView greetingImage = (ImageView) findViewById(R.id.greetingimage);
            TextView greetings = (TextView) findViewById(R.id.greeting);


            if (hours >= 0 && hours <= 12) {
                greetings.setText("Good Morning " + greetName);
                greetingImage.setImageResource(R.drawable.morning);
            } else if (hours >= 12 && hours <= 16) {
                greetings.setText("Good Afternoon " + greetName);
                greetingImage.setImageResource(R.drawable.afternoon);
            } else if (hours >= 16 && hours <= 21) {
                greetings.setText("Good Evening " + greetName);
                greetingImage.setImageResource(R.drawable.evening);
            } else if (hours >= 21 && hours <= 24) {
                greetings.setText("Good Night " + greetName);
                greetingImage.setImageResource(R.drawable.night);
            }

        } else {

            Calendar c = Calendar.getInstance();
            int hours = c.get(Calendar.HOUR_OF_DAY);
            ImageView greetingImage = (ImageView) findViewById(R.id.greetingimage);
            TextView greetings = (TextView) findViewById(R.id.greeting);


            if (hours >= 0 && hours <= 12) {
                greetings.setText("Good Morning ");
                greetingImage.setImageResource(R.drawable.morning);
            } else if (hours >= 12 && hours <= 16) {
                greetings.setText("Good Afternoon ");
                greetingImage.setImageResource(R.drawable.afternoon);
            } else if (hours >= 16 && hours <= 21) {
                greetings.setText("Good Evening ");
                greetingImage.setImageResource(R.drawable.evening);
            } else if (hours >= 21 && hours <= 24) {
                greetings.setText("Good Night ");
                greetingImage.setImageResource(R.drawable.night);
            }

        }
    }


    private void loadEvents() {


        final ProgressBar videoBar = (ProgressBar) findViewById(R.id.eventLoader);
        if (db.getAllEvents().isEmpty()) {
            videoBar.setVisibility(View.VISIBLE);
        }else videoBar.setVisibility(View.INVISIBLE);


        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/events?apiKey=" + ay;
        RequestQueue requestQueue;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Event Response: " + response.toString());

                videoBar.setVisibility(View.INVISIBLE);


                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonTestimony = jsonObject.getJSONArray("response");
                    for (int i = 0; i < jsonTestimony.length(); i++) {
                        JSONObject jo = jsonTestimony.getJSONObject(i);
                        int id = jo.getInt("id");
                        //int type = jo.getInt("type");
                        String title = jo.getString("title");
                        String body = jo.getString("body");
                        String expires = jo.getString("expires");
                        String url = jo.getString("thumbnail");

                        String loc= jo.getString("location");
                        if (loc == null){
                            EventsData eventsData = new EventsData(url,"none",title, body, id, expires, 0);
                            db.createEvents(eventsData);
                            eventsDataArrayList.add(eventsData);
                        }

                        EventsData eventsData = new EventsData(url,loc,title, body, id, expires, 0);
                        db.createEvents(eventsData);
                        eventsDataArrayList.add(eventsData);


                    }
                    announceAdapter = new AnnounceAdapter(MainActivity.this, eventsDataArrayList);
                    recyclerView1.setAdapter(announceAdapter);


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error getting events", Toast.LENGTH_LONG).show();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new
                        HurlStack();
            }
            requestQueue = Volley.newRequestQueue(this, stack);
        } else {
            requestQueue = Volley.newRequestQueue(this);
        }

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
            snackbar.dismiss();


        } else {
            message = "Some features will not Work until connection to internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        greetUser();

        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);


    }

    public void setHeader() {
        // Fetching user details from SQLite
        SQLiteHandler db = new SQLiteHandler(this);
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        txtName = (TextView) view.findViewById(R.id.user_name);
        txtEmail = (TextView) view.findViewById(R.id.user_email);
        btnLogout = (Button) view.findViewById(R.id.logout);
        userImage = (TextView) view.findViewById(R.id.icon_text);

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);
        userImage.setText(name.substring(0, 1));

        // Logout button click event



    }

    public void getApiKey() {

        String deviceId = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

        String url = "https://disciplesbay.com/api/key?churchId=1&deviceId=" + deviceId + "&appId=1&version=v1.01";


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response2) {
                try {

                    JSONObject jsonObject = new JSONObject(response2);
                    apiKey = jsonObject.getString("response");

                    Log.d("api", apiKey);
                    db.addApiKey(1, apiKey);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private ArrayList<AudioVideo> loadAudio() {

        final String type = "audio";


        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/media?apiKey=" + ay + "&type=" + type;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonTestimony = jsonObject.getJSONArray("response");
                    for (int i = 0; i < jsonTestimony.length(); i++) {
                        JSONObject jo = jsonTestimony.getJSONObject(i);
                        double price = jo.getDouble("sellingPrice");
                        String url = jo.getString("fileUrl");
                        String name = jo.getString("title");
                        String description = jo.getString("description");
                        String thumb = jo.getString("thumbnail");

                        String time = jo.getString("mediaUploadedDate");
                        String payStatus = jo.getString("userPaymentStatus");

                        if (payStatus.equalsIgnoreCase("paid")) {
                            isPaid = true;
                        } else {
                            isPaid = false;
                        }

                        AudioVideo audioVideo =new AudioVideo(1, url, name, time, description,thumb, price, isPaid, type);
                        auidoonly.add(audioVideo);


                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
        return auidoonly;
    }

    private void loadVideo() {

        String type = "all";
        final String type2 = "audio";
         final ArrayList<AudioVideo> all = new ArrayList<>();

        final ProgressBar videoBar = (ProgressBar) findViewById(R.id.sermonLoader);
        videoBar.setVisibility(View.VISIBLE);



        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/media?" + "apiKey=" + ay + "&type=all" ;
        String url2 = "https://disciplesbay.com/api/test?" + "type=" + type2;
        RequestQueue requestQueue ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                videoBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Media Response: " + response.toString());

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonTestimony = jsonObject.getJSONArray("response");
                    for (int i = 0; i < jsonTestimony.length(); i++) {
                        JSONObject jo = jsonTestimony.getJSONObject(i);
                        int id = jo.getInt("id");
                        double price = jo.getDouble("sellingPrice");
                        String type = jo.getString("mediaType");
                        String url = jo.getString("file");
                        String thumb = jo.getString("thumbnail");
                        String name = jo.getString("title");
                        String description = jo.getString("description");
                        JSONObject bu = jo.getJSONObject("date");
                        String time = bu.getString("normal");
                        String payStatus = jo.getString("userPaymentStatus");
                        if (payStatus.equalsIgnoreCase("paid")) {
                            isPaid = true;
                        } else {
                            isPaid = false;
                        }

                        audioVideoList.add(new AudioVideo(id, url, name, time, description,thumb, price, isPaid, type));
                        MainMessage mainMessage = new MainMessage(audioVideoList, MainActivity.this);
                        recyclerView2.setAdapter(mainMessage);





                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new
                        HurlStack();
            }
            requestQueue = Volley.newRequestQueue(this, stack);
        } else {
            requestQueue = Volley.newRequestQueue(this);
        }









        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        requestQueue.add(stringRequest);

    }

    private void sib_the_most_comlex_usage() {
        SimpleImageBanner sib = ViewFindUtils.find(decorView, R.id.sib_the_most_comlex_usage);
        sib
                /** methods in BaseIndicatorBanner */
               .setIndicatorStyle(MyBaseIndicatorBanner.STYLE_CORNER_RECTANGLE)//set indicator style
//              .setIndicatorWidth(6)                               //set indicator width
//              .setIndicatorHeight(6)                              //set indicator height
//              .setIndicatorGap(8)                                 //set gap btween two indicators
//              .setIndicatorCornerRadius(3)                        //set indicator corner raduis
                .setSelectAnimClass(ZoomInEnter.class)
                .setIndicatorSelectColor(Color.parseColor("#ffffff"))//set indicator select anim
                /** methods in BaseBanner */
//              .setBarColor(Color.parseColor("#88000000"))         //set bootom bar color
              .barPadding(10, 5, 5, 5)                             //set bottom bar padding
//              .setBarShowWhenLast(true)                           //set bottom bar show or not when the position is the last
              .setTextColor(Color.parseColor("#ffffff"))          //set title text color
                .setTextSize(16f)                                 //set title text size
//              .setTitleShow(true)                                 //set title show or not
//              .setIndicatorShow(true)                             //set indicator show or not
//              .setDelay(2)                                        //setDelay before start scroll
//              .setPeriod(10)                                      //scroll setPeriod
                .setSource(DataProvider.getList())                  //data source list
                .setTransformerClass(FadeSlideTransformer.class)//set page transformer

                .startScroll();


              //start scroll,the last method to call


        /**
         *

        sib.setOnItemClickL(new SimpleImageBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {

                ArrayList<BannerItem> list = DataProvider.getList();

                String text = list.get(position).title;
                String time = list.get(position).time;
                String url = list.get(position).imgUrl;

                Intent intent = new Intent(MainActivity.this, TrendingActivity.class);
                intent.putExtra("topic", text);
                intent.putExtra("time",time);
                intent.putExtra("imgUrl",url);
                startActivity(intent);

            }
        });*/




    }



    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("SEEEEEEEEEEEEEEEEEEE", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("SEEEEEEEEEEEEEEE", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("SEEEEEEEEEEEEEEEEEEEE", "printHashKey()", e);
        }
    }




}
