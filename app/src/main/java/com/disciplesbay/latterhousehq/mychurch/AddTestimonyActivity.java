package com.disciplesbay.latterhousehq.mychurch;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.app.ConnectivityReceiver;
import com.disciplesbay.latterhousehq.mychurch.helper.HandleActivityResult;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.disciplesbay.latterhousehq.mychurch.helper.HandleActivityResult.HANDLE_IMAGE;

public class AddTestimonyActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = AddTestimonyActivity.class.getSimpleName();
    int RESULT_OK =0;
    EditText editTextName, editTextDesc;
    String name, description;
    Snackbar snackbar;
    private ProgressDialog pDialog;
    private SessionManager session;
    private String selectedImagePath;
    final int SELECT_FILE = 1;
    private ImageView loadImage;
    private ImageView testimonyImage;
    int click = 0;
    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;


    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_testimony);
        if (savedInstanceState != null) {
            name = savedInstanceState.getString("name");
            description = savedInstanceState.getString("description");
        }

        session = new SessionManager(this);

        db = new SQLiteHandler(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        testimonyImage = (ImageView) findViewById(R.id.testimonyImage);
        testimonyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testimonyImage.setImageResource(0);
            }
        });

        loadImage = (ImageView) findViewById(R.id.loadImage);
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showpicgallery();
            }
        });


        editTextDesc = (EditText) findViewById(R.id.testimony_description);
        description = editTextDesc.getText().toString();




    }

    public void sendTestimony(View view) {


        editTextDesc = (EditText) findViewById(R.id.testimony_description);
        description = editTextDesc.getText().toString();

        if (session.isLoggedIn()) {
            if (validate()) {
                postAnnouncemt(name, description);
            }
        } else {
            snackbar = Snackbar.make(findViewById(R.id.myParent), "Please Login to Continue", Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.setAction("LOGIN", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AddTestimonyActivity.this, LoginActivity.class);
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
        savedInstanceState.putString("name", name);
        savedInstanceState.putString("description", description);

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
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);


    }

    private void postAnnouncemt(final String name, final String description) {
        // Tag used to cancel the request
        String tag_string_req = "req_send_testimony";

        pDialog.setMessage("Sending your Testimony.......");
        showDialog();

        final String senderId =  db.getApikey().get("apikey");

        String url = "https://disciplesbay.com/api/testimonies";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Send Testimony response " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.equalsIgnoreCase("ok")) {
                        Toast.makeText(getApplicationContext(), "Testimony Sent", Toast.LENGTH_LONG).show();
                        finish();

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
                Log.e(TAG, "Error in sending Testimony ");
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

        strReq.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(strReq);

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


        String description = editTextDesc.getText().toString().trim();


        if (description.isEmpty() || description.length() < 10) {
            editTextDesc.setError("Your Testimony is too short");
            valid = false;
        } else {
            editTextDesc.setError(null);
        }

        return valid;
    }

    private void showpicgallery() {

        try {
            if (ActivityCompat.checkSelfPermission(AddTestimonyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddTestimonyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_FILE);
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
                            Glide.with(AddTestimonyActivity.this).load(encodedImage).into(testimonyImage);
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


}
