package com.disciplesbay.latterhousehq.mychurch;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.YoYo;
import com.disciplesbay.latterhousehq.mychurch.Utils.TLSSocketFactory;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nineoldandroids.animation.Animator;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    String os;
    String model;
    String device;
    String product;
    String id, op;
    SessionManager session;
    private SQLiteHandler db;
    private PrefManager prefManager;
    TextView info;
    private RequestQueue queue;
    private AdView mAdView;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);

    }


    /**
     * Called when the activity is first created.
     */
    Thread splashTread;

    /**
     * UK
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        db = new SQLiteHandler(this);
        session = new SessionManager(this);
        FacebookSdk.sdkInitialize(this);
        info = (TextView) findViewById(R.id.info);
        mAdView = findViewById(R.id.adView);

        MobileAds.initialize(this,
                "ca-app-pub-5688268523815560~5407898954");

        os = System.getProperty("os.version");
        op = Build.VERSION.RELEASE;
        model = Build.DISPLAY;
        device = Build.DEVICE;
        product = Build.MODEL;
        id = Build.ID;

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            //Launch();
            StartAnimations();
        } else {
            StartAnimations();
        }

        Log.d("LAUNCH ACTIVITY", "send: " + op + model + device + product + id);


    }

    private void StartAnimations() {

        db.addApiKey(1,"PSryK5d8ab940ffb261eb69486d503c6ee6183150dcf8YXpwk");


        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        info.setText("You're welcome......");

        anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    //Splash screen pause time
                    while (waited < 5000) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashActivity.this,
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashActivity.this.finish();
                }

            }
        };
        splashTread.start();

    }

    private void Launch() {


        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        info.setText("Loading Church data......");

        anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);


        //create os json
        JSONObject jsonOs = new JSONObject();
        try {
            jsonOs.put("name", "Android");
            jsonOs.put("version", op);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //create device json object
        JSONObject jsonDevice = new JSONObject();
        try {
            jsonDevice.put("os", jsonOs);
            jsonDevice.put("id", id);
            jsonDevice.put("model", model);
            jsonDevice.put("name", product);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Create Main jSon object
        JSONObject jsonParams = new JSONObject();

        try {
            jsonParams.put("churchId", "1");
            jsonParams.put("appId", "1");
            jsonParams.put("countryId", "162");
            jsonParams.put("regionId", "2538");
            jsonParams.put("device", jsonDevice);

            Log.d("LAUNCH send", "json: " + jsonParams.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        String URL = "https://disciplesbay.com/api/launch";


        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.POST, URL, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("LAUNCH ACTIVITY", "Response: " + jsonObject.toString());

                        try {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                            String respon = jsonObject1.getString("apiKey");
                            String status = jsonObject.getString("status");

                            if (status.equalsIgnoreCase("ok")) {
                                prefManager.setFirstTimeLaunch(false);
                                Intent intent = new Intent(SplashActivity.this,
                                        MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                SplashActivity.this.finish();
                            } else {
                                info.setText("error getting data, please check your internet connection");
                            }
                            Log.d("api", respon);
                            db.addApiKey(1, respon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        info.setText("Sorry we're having some difficulty" + volleyError.toString());

                    }

                })
        {


            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

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
            queue = Volley.newRequestQueue(this, stack);
        } else {
            queue = Volley.newRequestQueue(this);
        }


        jobReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jobReq);


    }


}
