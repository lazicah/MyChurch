package com.disciplesbay.latterhousehq.mychurch;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.disciplesbay.latterhousehq.mychurch.Utils.TLSSocketFactory;
import com.disciplesbay.latterhousehq.mychurch.adapters.CaptionedImageAdapter;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.app.ConnectivityReceiver;
import com.disciplesbay.latterhousehq.mychurch.data.Testimony;
import com.disciplesbay.latterhousehq.mychurch.helper.HandleActivityResult;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;
import com.baoyz.widget.PullRefreshLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.disciplesbay.latterhousehq.mychurch.helper.HandleActivityResult.HANDLE_IMAGE;

public class TestimoniesActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    int color;
    Snackbar snackbar;
    CaptionedImageAdapter captionedImageAdapter;
    ArrayList<Testimony> testimonyArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PullRefreshLayout pullRefreshLayout;
    private SQLiteHandler db;
    private SessionManager session;
    private boolean isLiked = false;
    private String selectedImagePath;
    private ProgressDialog pDialog;
    private ImageView testimonyImage;
    FloatingActionButton fab;
    String description;
    final int SELECT_FILE = 1;
    EditText test;
    ImageView snd;

    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        checkConnection();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        pullRefreshLayout.setRefreshing(true);


        if (savedInstanceState != null) {

            description = savedInstanceState.getString("description");
        }

        fab = (FloatingActionButton) findViewById(R.id.send);

        session = new SessionManager(getApplicationContext());


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showsendDialog();
                Toast.makeText(TestimoniesActivity.this," clicked" , Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState != null) {
            ArrayList values = savedInstanceState.getStringArrayList("value");
            if (values != null) {
                captionedImageAdapter = new CaptionedImageAdapter(this, values);
            }
        }

        db = new SQLiteHandler(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.test_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fab.isShown()){
                    fab.hide();
                }
                else if (dy < 0 && !fab.isShown()){
                    fab.show();
                }
            }
           // @Override
            //public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

              //  if (newState == RecyclerView.SCROLL_STATE_IDLE){
                //    fab.show();
                //}
                //super.onScrollStateChanged(recyclerView, newState);
            //}
        });
        update();
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });


    }


    private void loadUrlData() {



        HashMap<String, String> apikey = db.getApikey();
        String ay = apikey.get("apikey");
        String url = "https://disciplesbay.com/api/testimonies?apiKey=" + ay ;



        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Testimony Activity", "Send Testimony response " + response.toString());

                testimonyArrayList.clear();

                String reactionId = "";
                String deleteId = "";
                String likess = "";
                String testId = "";


                if (pullRefreshLayout.isEnabled()){
                    pullRefreshLayout.setRefreshing(false);

                }




                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonTestimony = jsonObject.getJSONArray("response");
                    for (int i = 0; i < jsonTestimony.length(); i++) {
                        JSONObject jo = jsonTestimony.getJSONObject(i);

                        String name = jo.getString("senderName");
                        String id = jo.getString("senderId");

                        String description = jo.getString("text");

                        int color = getRandomMaterialColor("400");



                        JSONObject da = jo.getJSONObject("date");
                        String date = da.getString("normal");

                        try{

                            if (jo.has("userReaction") && jo.isNull("userReaction")){
                                testId = jo.getString("id");

                                reactionId = "0";
                                deleteId = "0";
                            }else {
                                JSONObject userRct = jo.getJSONObject("userReaction");

                                testId = jo.getString("id");
                                reactionId = userRct.getString("reactionId");
                                deleteId = userRct.getString("id");


                            }
                        }catch (JSONException e) {

                            e.printStackTrace();
                        }



                        if (reactionId.equalsIgnoreCase("")) {
                            isLiked = true;
                        } else {
                            isLiked = false;
                        }

                        try{

                            if (jo.has("reactions") && jo.isNull("reactions")){
                                likess = "0";
                            }else {
                                JSONArray reactions = jo.getJSONArray("reactions");

                                    JSONObject ro = reactions.getJSONObject(0);
                                    likess = ro.getString("count");




                            }
                        }catch (JSONException e) {

                            e.printStackTrace();
                        }


                        String url = jo.getString("images");

                            testimonyArrayList.add(new Testimony(url,testId,id, name, description, date, color, likess, isLiked,deleteId));





                    }

                    Collections.reverse(testimonyArrayList);
                    captionedImageAdapter = new CaptionedImageAdapter(TestimoniesActivity.this, testimonyArrayList);
                    recyclerView.setAdapter(captionedImageAdapter);
                    captionedImageAdapter.notifyDataSetChanged();

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int duration = Snackbar.LENGTH_LONG;
                CharSequence text = "Error Loading";
                View view = (View) findViewById(R.id.testimony_activity);

                Snackbar.make(view, text, duration);
            }
        });

        RequestQueue requestQueue;
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
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


        return super.onOptionsItemSelected(item);
    }


    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }



    @Override
    protected void onResume() {
        super.onResume();


        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */


    private void postAnnouncemt( final String description) {
        // Tag used to cancel the request
        String tag_string_req = "req_send_testimony";

        int color = getRandomMaterialColor("400");

        testimonyArrayList.add(new Testimony("","","", db.getUserDetails().get("username"),description,getDateTime(),color,"",false,""));


        pDialog.setMessage("Sending your Testimony.......");
        showDialog();

        final String senderId =  db.getApikey().get("apikey");

        String url = "https://disciplesbay.com/api/testimonies";
        RequestQueue requestQueue ;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Testimony Sender", "Send Testimony response " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.equalsIgnoreCase("ok")) {
                        Toast.makeText(getApplicationContext(), "Testimony Sent", Toast.LENGTH_LONG).show();


                    } else {

                        String errorMsg = jObj.getString("response");
                        Toast.makeText(getApplicationContext(),
                                "Error in Sending Testimony", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error in Sending Testimony", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Testimony sender", "Error in sending Testimony ");
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to testimony url
                Map<String, String> params = new HashMap<String, String>();
                params.put("testimony", description);
                params.put("apiKey", senderId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
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
            requestQueue = Volley.newRequestQueue(this, stack);
        } else {
            requestQueue = Volley.newRequestQueue(this);
        }



        strReq.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(strReq);

    }

    private void sendTestimony(View view, String description ) {





        if (session.isLoggedIn()) {

                postAnnouncemt(description);

        } else {
            snackbar = Snackbar.make(findViewById(R.id.app), "Please Login to Continue", Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.setAction("LOGIN", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TestimoniesActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();


        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("description", description);

    }



    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
            snackbar.dismiss();


        } else {
            message = "Not connected to internet";
            color = Color.WHITE;
            snackbar = Snackbar.make(findViewById(R.id.app), message, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }


    }



    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);


    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public boolean validate() {
        boolean valid = true;


        String description = test.getText().toString().trim();


        if (description.isEmpty() || description.length() < 10) {
            test.setError("Your Testimony is too short");
            valid = false;
        } else {
            test.setError(null);
        }

        return valid;
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

    private void showsendDialog() {


        LayoutInflater li = LayoutInflater.from(TestimoniesActivity.this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.content_add_testimony, null);

        testimonyImage = (ImageView) confirmDialog.findViewById(R.id.testimonyImage);
        snd = (ImageView) confirmDialog.findViewById(R.id.pick_image);
        ll = (LinearLayout) confirmDialog.findViewById(R.id.imagelinear);
        MaterialEditText testimony = (MaterialEditText) confirmDialog.findViewById(R.id.test);
        Button sendM = (Button) confirmDialog.findViewById(R.id.sendMessage);
        TextView clear = (TextView) confirmDialog.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testimonyImage.setImageResource(0);
                ll.setVisibility(View.GONE);
            }
        });



        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpicgallery();
            }
        });

        final String tesst = testimony.getText().toString();
        sendM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTestimony(v,tesst);
            }
        });



        //Creating an alertdialog builder
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);


        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);

        alertDialog.getWindow().setLayout(width, height);

        //Displaying the alert dialog
        alertDialog.show();




    }


    private void showpicgallery() {

        try {
            if (ActivityCompat.checkSelfPermission(TestimoniesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(TestimoniesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 2. pick image only
                intent.setType("image/*");
                // 3. start activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECT_FILE);}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && reqCode == SELECT_FILE && data != null) {
            Log.d("IMageLoader", "IMage loadedddddd true");
            int dataType = new HandleActivityResult().handleResult(reqCode, resCode, data);
            if (dataType == HANDLE_IMAGE) {
                selectedImagePath = getAbsolutePath(Uri.parse(data.getData().toString()));
                ll.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext())
                        .load(selectedImagePath)
                        .fitCenter()
                        .error(R.drawable.avatar1)
                        .into(testimonyImage);

            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case SELECT_FILE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    // 3. start activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, SELECT_FILE);}
                } else {
                    Toast.makeText(this, "Permission was not granted and You can't continue", Toast.LENGTH_LONG);
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    public void compressImage() {

        if (selectedImagePath != null) {
            Glide
                    .with(this)
                    .load(selectedImagePath)
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 100)
                    .fitCenter()
                    .atMost()
                    .override(300, 300)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onLoadStarted(Drawable ignore) {
                            // started async load
                        }

                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> ignore) {
                            String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                            Glide.with(TestimoniesActivity.this).load(encodedImage).into(testimonyImage);
                            Intent resultIntent = new Intent();
                            setResult(Activity.RESULT_OK, resultIntent);
                        }

                        @Override
                        public void onLoadFailed(Exception ex, Drawable ignore) {
                            Log.d("ex", ex.getMessage());
                        }
                    });
        }

    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = ((Activity) this).managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    private void update(){
        final Handler handler = new Handler();
        final int delay = 20000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                loadUrlData();

                Toast.makeText(TestimoniesActivity.this,"I don load again", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }





}
