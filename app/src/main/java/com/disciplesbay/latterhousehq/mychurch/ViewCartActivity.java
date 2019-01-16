package com.disciplesbay.latterhousehq.mychurch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import com.carteasy.v1.lib.Carteasy;
import com.disciplesbay.latterhousehq.mychurch.Utils.TLSSocketFactory;
import com.disciplesbay.latterhousehq.mychurch.adapters.ViewCartAdapter;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.data.Cart;
import com.disciplesbay.latterhousehq.mychurch.download.DownloadManagerService;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;


public class ViewCartActivity extends AppCompatActivity implements ViewCartAdapter.OnDeleteListener {

    private Transaction transaction1;
    private JSONObject metadata = new JSONObject();
    private JSONArray customfields = new JSONArray();
    private String paystack_public_key = "pk_test_83c9a315b069c930189220a4adf6cf2a286a14a5";
    private ArrayList<Cart> mItems;
    private Toolbar toolbar;
    private RecyclerView.LayoutManager mLayoutManager;
    private SQLiteHandler db;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private TextView NoOfItems;
    private Button continuebutton;
    private ImageView emptyCart;
    private TextView emptyCartText;
    private Button CheckOut;
    private ViewCartAdapter adapter;
    private EditText mEditCardNum;
    private EditText mEditCVC;
    private EditText mEditExpiryMonth;
    private EditText mEditExpiryYear;
    private SessionManager session;
    private View views;
    private InterstitialAd mInterstitialAd;
    private int id;
    private Carteasy cs;

    private static String[] splitName(String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex <= 0 || (dotIndex == name.length() - 1)) {
            return new String[]{name, ""};
        } else {
            return new String[]{name.substring(0, dotIndex), name.substring(dotIndex + 1)};
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);

        session = new SessionManager(this);
        views = getWindow().getDecorView().getRootView();


        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5688268523815560/2796255474");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        //Retrieve the cart details - begin
        Map<Integer, Map> data;
        cs = new Carteasy();
        data = cs.ViewAll(getApplicationContext());


        emptyCart = (ImageView) findViewById(R.id.empty_cart);
        emptyCartText = (TextView) findViewById(R.id.empty_cart_text);
        if (data == null || data.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.VISIBLE);
        }

        mItems = new ArrayList<Cart>();
        Cart cartitem = new Cart();
        if (data != null && data.size() != 0) {
            for (Map.Entry<Integer, Map> entry : data.entrySet()) {

                //Retrieve the values of the Map by starting from index 0 - zero

                cartitem = new Cart();
                //Get the sub values of the Map
                Map<String, String> innerdata = entry.getValue();
                for (Map.Entry<String, String> innerEntry : innerdata.entrySet()) {
                    System.out.println(innerEntry.getKey() + "=" + innerEntry.getValue());

                    String product = innerEntry.getKey();
                    switch (product) {
                        case "id":
                            cartitem.setProductid(innerEntry.getValue());
                            break;
                        case "productName":
                            cartitem.setName(innerEntry.getValue());
                            break;

                        case "type":
                            cartitem.setType(innerEntry.getValue());
                            break;

                        case "productUrl":
                            cartitem.setUrl(innerEntry.getValue());
                            break;
                        //case "product desc":
                        //  cartitem.setDescription(innerEntry.getValue());
                        //break;
                        //case "product qty":
                        //  cartitem.setQuantity(Integer.parseInt(innerEntry.getValue()));
                        //break;
                        //case "product size":
                        //  cartitem.setSize(innerEntry.getValue());
                        //break;
                        case "price":
                            cartitem.setPrice(Integer.parseInt(innerEntry.getValue()));
                            break;
                        //case "product color":
                        //  cartitem.setColor(innerEntry.getValue());
                        //break;
                        case "thumbnail":
                            cartitem.setmThumbnail(innerEntry.getValue());
                            break;
                    }
                }
                mItems.add(cartitem);
            }
        }

        //Set MyBag ( NoOfItems );
        NoOfItems = (TextView) findViewById(R.id.no_of_items);
        NoOfItems.setText("MY CART (" + Integer.toString(mItems.size()) + ")");


        //Retrieve the cart details - end
        mRecyclerView.setHasFixedSize(true);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ViewCartAdapter(this, mItems);
        Log.d("CARTEASY", mItems.toString());

        mRecyclerView.setAdapter(adapter);


        //Navigate the User back to the display activity
        continuebutton = (Button) findViewById(R.id.continueshopping);

        if (data != null) {
            continuebutton.setEnabled(true);
            continuebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // call this to finish the current activity
                }
            });
        }

        CheckOut = (Button) findViewById(R.id.button);
        if (data != null) {
            CheckOut.setEnabled(true);

            CheckOut.setText("Checkout " + "(NGN)" + totalAmount());
            CheckOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchase(v);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            });

        }


    }

    @Override
    public void onDelete() {
        //Set MyBag ( NoOfItems );

        NoOfItems.setText("MY CART (" + Integer.toString(mItems.size() -1) + ")");
        CheckOut.setText("Checkout " + "(NGN)" + totalAmount());
        Toast.makeText(this,  " VVVVVVVVVVVVVVVVVV ", Toast.LENGTH_SHORT).show();



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void performCharge(final View view, final String cardNUmber, final String cvv, final int month, final int year) throws JSONException {

        db = new SQLiteHandler(this);


        PaystackSdk.setPublicKey(paystack_public_key);


        //create a Charge object


        Card card = new Card(cardNUmber, month, year, cvv);
        int amnt = totalAmount() * 100;

        Charge charge = new Charge();
        charge.setCard(card); //sets the card to charge)
        charge.setAmount(amnt);
        charge.setEmail(db.getUserDetails().get("email"));
        charge.setReference(("Multiple items").toString().replaceAll("\\s+", "") + Calendar.getInstance().getTimeInMillis());
        try {
            charge.putCustomField("Charged From", "Android SDK");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        this.metadata = new JSONObject();
        this.customfields = new JSONArray();


        for (int i = 0; i < mItems.size(); i++) {
            Cart cc = mItems.get(i);
            JSONObject customObj = new JSONObject();

            try {
                customObj.put("productName", cc.getName());
                customObj.put("productId", cc.getProductid());
                customObj.put("productType", cc.getType());
                customObj.put("churchId", "1");
                customObj.put("userId", db.getUserDetails().get("uid"));
                this.customfields.put(customObj);
            } catch (JSONException e) {
                Log.d("Pay cart activity", e.toString());
            }


        }

        try {
            this.metadata.put("custom_fields", this.customfields);
            Log.d("metadata", this.metadata.toString());
        } catch (JSONException e) {
            Log.d("Payment activity", e.toString());
        }

        charge.putMetadata("metadata", this.metadata);

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
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
                    Toast.makeText(ViewCartActivity.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                    verifyOnServer(transaction.getReference(), view);
                } else {
                    Toast.makeText(ViewCartActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });


    }

    private void verifyOnServer(final String trans, final View view) {


        String ay = db.getApikey().get("apikey");
        String url = "https://disciplesbay.com/api/verify/payment?apiKey=" + ay + "&reference=" + trans;
        RequestQueue requestQueue;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Cart Activity", "Cart response " + response.toString());
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("ok")) {
                        Toast.makeText(ViewCartActivity.this, String.format("Your Payment Was Successful you will receive a mail shortly"), Toast.LENGTH_LONG).show();
                        Snackbar snackbar = Snackbar.make(views, "Your Download has been Scheduled", Snackbar.LENGTH_LONG);

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
                        cs.clearCart(ViewCartActivity.this);

                        for (int i = 0; i < mItems.size(); i++) {
                            Cart cc = mItems.get(i);
                            final String[] nameParts = splitName(cc.getUrl());
                            final String ext = nameParts[1];
                            DownloadManagerService.startMission(ViewCartActivity.this, cc.getUrl(), NewPipeSettings.getVideoDownloadFolder(ViewCartActivity.this).getAbsolutePath(), cc.getName() + "." + ext, false, 3);
                        }
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewCartActivity.this, String.format("There was a problem verifying %s on the backend: %s ", trans, error), Toast.LENGTH_LONG).show();


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
                    Toast.makeText(ViewCartActivity.this, "Card database is empty, Please save card first", Toast.LENGTH_LONG).show();

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
                Toast.makeText(ViewCartActivity.this, "Performing transaction, Would take a minute.....", Toast.LENGTH_LONG).show();

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

    private void purchase(View view) {

        if (session.isLoggedIn()) {
            showDialog();

        } else {

            Snackbar snackbar = Snackbar.make(views, "Please Login to Continue", Snackbar.LENGTH_LONG);

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
        Toast.makeText(getApplicationContext(), "Enter your Card details", Toast.LENGTH_LONG).show();

    }

    public int totalAmount() {

        int price = 0;

        for (int i = 0; i < mItems.size(); i++) {
            Cart cart = mItems.get(i);

            price = price + cart.getPrice();

        }
        return price;

    }

    public void chai(){
        //Set MyBag ( NoOfItems );

        NoOfItems.setText("MY CART (" + Integer.toString(mItems.size()) + ")");
        CheckOut.setText("Checkout " + "(NGN)" + totalAmount());
        //Toast.makeText(this,  " VVVVVVVVVVVVVVVVVV ", Toast.LENGTH_SHORT).show();

    }


}
