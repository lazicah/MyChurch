package com.disciplesbay.latterhousehq.mychurch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.carteasy.v1.lib.Carteasy;
import com.disciplesbay.latterhousehq.mychurch.Utils.TLSSocketFactory;
import com.disciplesbay.latterhousehq.mychurch.Utils.Utility;
import com.disciplesbay.latterhousehq.mychurch.adapters.CartAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.data.CartModel;
import com.disciplesbay.latterhousehq.mychurch.download.DownloadManagerService;
import com.disciplesbay.latterhousehq.mychurch.fragment.DialogFullscreenFragment;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;


public class ProductDetailsActivity extends AppCompatActivity {
    private int inter = 0;
    private ImageView eventImg, i, g;

    public static final int DIALOG_QUEST_CODE = 300;


    String paystack_public_key = "pk_test_83c9a315b069c930189220a4adf6cf2a286a14a5";
    String backend_url = "new";
    JSONObject metadata = new JSONObject();
    JSONArray customfields = new JSONArray();
    String name;
    String descriptions;
    String value;
    int id;
    String image;
    String productUrl;
    double price;
    SQLiteHandler db;
    SessionManager session;
    View v;
    Transaction transaction1;
    EditText mEditCardNum;
    EditText mEditCVC;
    EditText mEditExpiryMonth;
    EditText mEditExpiryYear;
    TextView productName, seller, amount, description, views;
    private int statusBarColor;
    private FloatingActionMenu menuLabelsRight;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    ImageView imageView;
    private InterstitialAd mInterstitialAd;
    ProgressDialog progressDialog;


    Carteasy carteasy;
    String thumb,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        db = new SQLiteHandler(this);
        session = new SessionManager(this);
        i = (ImageView) findViewById(R.id.like);

        imageView = (ImageView) findViewById(R.id.product_image);
        progressDialog = new ProgressDialog(this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5688268523815560/9618606704");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        carteasy = new Carteasy();
        v  = getWindow().getDecorView().getRootView();



        menuLabelsRight = (FloatingActionMenu) findViewById(R.id.menu_labels_right);
        fab1 = (FloatingActionButton) findViewById(R.id.cart);
        fab2 = (FloatingActionButton) findViewById(R.id.buy);

        productName = (TextView) findViewById(R.id.product_name);
        seller = (TextView) findViewById(R.id.seller);
        amount = (TextView) findViewById(R.id.amount);
        description = (TextView) findViewById(R.id.description);
        views = (TextView) findViewById(R.id.views);

        Intent intent = getIntent();
        name = intent.getExtras().getString("productName");
        descriptions = intent.getExtras().getString("productDesc");
        String time = intent.getExtras().getString("time");
        price = intent.getExtras().getDouble("price");
        id = intent.getExtras().getInt("id");
        productUrl = intent.getExtras().getString("urlProduct");
        thumb = intent.getExtras().getString("thumb");
        type = intent.getExtras().getString("type");

        int cost = (int) price;


        value = String.valueOf(cost);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;

        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);


        final int amnt = (int) price;

        amount.setText(String.valueOf(amnt) + ".00");
        productName.setText(name);
        description.setText(descriptions);
        seller.setText(timeAgo);
        Glide.with(this).load(thumb).fitCenter().into(imageView);


        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inter == 0){
                    inter = 1;
                    i.setImageResource(R.drawable.ic_star);
                }else {
                    inter = 0;
                    i.setImageResource(R.drawable.ic_star_e);
                }
            }
        });


    }

    public void addCart(View view) {

        carteasy.add(String.valueOf(id),"id",id);
        carteasy.add(String.valueOf(id),"productName",name);
        carteasy.add(String.valueOf(id),"productUrl", productUrl);
        carteasy.add(String.valueOf(id),"price",value);
        carteasy.add(String.valueOf(id),"thumbnail",thumb );
        carteasy.add(String.valueOf(id),"type", type);
        carteasy.commit(this);
        carteasy.persistData(this,true);



        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }



        Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_LONG).show();

    }

    public void purchase(View view) {
        showDialogFullscreen();

        /*if (session.isLoggedIn()) {
            showDialog();

        } else {

            Snackbar snackbar = Snackbar.make(view, "Please Login to Continue", Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.setAction("LOGIN", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    view.getContext().startActivity(intent);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();


        }
        Toast.makeText(getApplicationContext(), "Enter your Card details", Toast.LENGTH_LONG).show();*/

    }


    public void performCharge(final View view, final String cardNUmber, final String cvv, final int month, final int year) throws JSONException {


        //create a Charge object
        PaystackSdk.setPublicKey(paystack_public_key);
        progressDialog.setMessage("Performing Transaction.......");
        progressDialog.show();


        Card card = new Card(cardNUmber, month, year, cvv);
        int amnt = (int) Math.round(price) * 100;

        Charge charge = new Charge();
        charge.setCard(card); //sets the card to charge)
        charge.setAmount(amnt);
        charge.setEmail(db.getUserDetails().get("email"));
        charge.setReference(name.toString().replaceAll("\\s+", "") + Calendar.getInstance().getTimeInMillis());
        try {
            charge.putCustomField("Charged From", "Android SDK");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.metadata = new JSONObject();
        this.customfields = new JSONArray();
        JSONObject customObj = new JSONObject();
        customObj.put("productName", name);
        customObj.put("productId", id);
        customObj.put("productType", "video");
        customObj.put("churchId", "1");
        customObj.put("userId", db.getUserDetails().get("uid"));
        this.customfields.put(customObj);


        try {
            this.metadata.put("custom_fields", this.customfields);
        } catch (JSONException e) {
            Log.d("Payment activity", e.toString());
        }

        charge.putMetadata("metadata", this.metadata);

        PaystackSdk.chargeCard(ProductDetailsActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                progressDialog.setMessage("Payment Successful now verifying..........");
                Toast.makeText(ProductDetailsActivity.this, " You Successfully paid for " + transaction.getReference(), Toast.LENGTH_SHORT).show();
                verifyOnServer(transaction.getReference(), view);
               }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here

                transaction1 = transaction;
                if (error instanceof ExpiredAccessCodeException) {

                    try {
                        performCharge(view, cardNUmber, cvv, month, year);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                ;

                if (transaction.getReference() != null) {
                    Toast.makeText(ProductDetailsActivity.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                    verifyOnServer(transaction.getReference(),view);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void showDialog() {


        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.video_view, null);

        mEditCardNum = (EditText) confirmDialog.findViewById(R.id.edit_card_number);
        mEditCVC = (EditText) confirmDialog.findViewById(R.id.edit_cvc);
        mEditExpiryMonth = (EditText) confirmDialog.findViewById(R.id.edit_expiry_month);
        mEditExpiryYear = (EditText) confirmDialog.findViewById(R.id.edit_expiry_year);

        Button loadCard = (Button) confirmDialog.findViewById(R.id.loadCard);
        loadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!db.getCard().isEmpty()) {
                    mEditCardNum.setText(db.getCard().get("cardNumber"));
                    mEditCVC.setText(db.getCard().get("cvv"));
                    mEditExpiryYear.setText(db.getCard().get("year"));
                    mEditExpiryMonth.setText(db.getCard().get("month"));
                } else
                    Toast.makeText(ProductDetailsActivity.this, "Card database is empty, Please save card first", Toast.LENGTH_LONG).show();

            }
        });


        //Creating an alertdialog builder
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //Displaying the alert dialog
        alertDialog.show();

        Button dismiss = (Button) confirmDialog.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {

            String cardNumber = "";
            int expiryMonth = 0;
            int expiryYear = 0; // any year in the future. '2018' would work also!
            String cvv = "";

            @Override
            public void onClick(View view) {
                Toast.makeText(ProductDetailsActivity.this, "Performing transaction, Would take a minute.....", Toast.LENGTH_LONG).show();
                hideKeyboard(ProductDetailsActivity.this);
                cardNumber = mEditCardNum.getText().toString();
                expiryMonth = Integer.parseInt(mEditExpiryMonth.getText().toString()); //any month in the future
                expiryYear = Integer.parseInt(mEditExpiryYear.getText().toString()); // any year in the future. '2018' would work also!
                cvv = mEditCVC.getText().toString();  // cvv of the test card
                try {
                    performCharge(view, cardNumber, cvv, expiryMonth, expiryYear);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();
            }
        });

    }



    private void showDialogFullscreen() {

        final String[] nameParts = splitName(productUrl);
        final String ext = nameParts[1];

        CartModel cartMode = new CartModel();
        cartMode.setProductName(name);
        cartMode.setAmount((int) price);
        cartMode.setImageUrl(image);

        String data = Utility.getGsonParser().toJson(cartMode);

        Bundle b = new Bundle();
        b.putString("data", data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFullscreenFragment newFragment = new DialogFullscreenFragment();
        newFragment.setRequestCode(DIALOG_QUEST_CODE);
        newFragment.setArguments(b);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        newFragment.setOnCallbackResult(new DialogFullscreenFragment.CallbackResult() {
            @Override
            public void sendResult(int requestCode, Object obj) {
                if (requestCode == DIALOG_QUEST_CODE) {

                    Toast.makeText(ProductDetailsActivity.this,"Your Download has been scheduled",Toast.LENGTH_SHORT).show();
                    DownloadManagerService.startMission(ProductDetailsActivity.this, productUrl, NewPipeSettings.getVideoDownloadFolder(ProductDetailsActivity.this).getAbsolutePath(), name + "." + ext, false, 3);

                }
            }
        });

    }


    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    private void verifyOnServer(final String trans, final View view) {

        final String[] nameParts = splitName(productUrl);
        final String ext = nameParts[1];


        String ay = db.getApikey().get("apikey");
        String url = "https://disciplesbay.com/api/verify/payment?apiKey=" + ay + "&reference=" + trans;
        RequestQueue requestQueue;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Product Activity", "Verify response " + response.toString());



                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("ok")){
                        progressDialog.setMessage("Payment Verified......");
                        progressDialog.dismiss();
                        Toast.makeText(ProductDetailsActivity.this, String.format("Your Payment Was Successful you will receive a mail shortly"), Toast.LENGTH_LONG).show();
                        Snackbar snackbar = Snackbar.make(v, "Your Download has been Scheduled", Snackbar.LENGTH_LONG);

                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        snackbar.setAction("OPEN", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), DownloadsActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();

                        DownloadManagerService.startMission(ProductDetailsActivity.this, productUrl, NewPipeSettings.getVideoDownloadFolder(ProductDetailsActivity.this).getAbsolutePath(), name + "." + ext, false, 3);

                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProductDetailsActivity.this, String.format("There was a problem verifying %s on the backend: %s ", trans, error), Toast.LENGTH_LONG).show();


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

        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private static String[] splitName(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex <= 0 || (dotIndex == name.length() - 1)) {
            return new String[]{name, ""};
        } else {
            return new String[]{name.substring(0, dotIndex), name.substring(dotIndex + 1)};
        }
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
