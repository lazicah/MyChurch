package com.disciplesbay.latterhousehq.mychurch.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.disciplesbay.latterhousehq.mychurch.DownloadActivity;
import com.disciplesbay.latterhousehq.mychurch.ProductDetailsActivity;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.data.AudioVideo;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * Created by root on 4/14/18.
 */

public class SermonsAdapter extends RecyclerView.Adapter<SermonsAdapter.ViewHolder> {


    ArrayList<AudioVideo> audioVideoArrayList;
    Activity activity;
    String paystack_public_key = "pk_test_83c9a315b069c930189220a4adf6cf2a286a14a5";
    String backend_url = "new";
    Transaction transaction1;
    JSONObject metadata = new JSONObject();
    JSONArray customfields = new JSONArray();
    EditText mEditCardNum;
    EditText mEditCVC;
    EditText mEditExpiryMonth;
    EditText mEditExpiryYear;
    private SermonsAdapter.Listener listener;
    private SQLiteHandler db;
    private SessionManager session;

    public SermonsAdapter(ArrayList<AudioVideo> audioVideoArrayList, Activity activity) {
        this.audioVideoArrayList = audioVideoArrayList;
        this.activity = activity;


    }

    @Override
    public int getItemCount() {
        return audioVideoArrayList.size();
    }

    //tell adapter how many data items are there

    public void setListener(SermonsAdapter.Listener listener) {
        this.listener = listener;
    }

    // activities will use this to register as a listener

    @Override
    public SermonsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_video_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final SermonsAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        session = new SessionManager(activity);


        final AudioVideo audioVideo = audioVideoArrayList.get(position);


        TextView textView = (TextView) cardView.findViewById(R.id.name_of);
        textView.setText(audioVideo.getName());

        ImageView thumb = (ImageView) cardView.findViewById(R.id.image);
        Glide.with(activity).load(audioVideo.getThumb()).into(thumb);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;

        try {
            date = dateFormat.parse(audioVideo.getTimestamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(date.getTime(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        TextView textView4 = (TextView) cardView.findViewById(R.id.timestamp);
        textView4.setText(timeAgo);


        TextView textView6 = (TextView) cardView.findViewById(R.id.bill);
        TextView buy = (TextView) cardView.findViewById(R.id.buy);
        final int amnt = (int) audioVideo.getPrice();

        textView6.setText(String.valueOf(amnt) + ".00");

        if (audioVideo.getPaid().equals(true)) {
            buy.setText("Purchased");
            buy.setTextColor(Color.GRAY);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(position);
                    }

                    Intent i = new Intent(activity, DownloadActivity.class);
                    i.putExtra("URL", audioVideo.getUrl());
                    i.putExtra("file_name", audioVideo.getName());
                    activity.startActivity(i);
                }
            });

        } else {

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (listener != null) {
                        listener.onClick(position);
                    }
                    /**
                     *

                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage( "\n\"" + audioVideo.getName() + "\"\n" + audioVideo.getDescription() + "\n" + "Price: "+amnt + "\nAre you sure you want to Purchase this item?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (session.isLoggedIn()) {
                                showDialog(position);
                            } else {
                                dialogInterface.dismiss();
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

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();*/

                    showDetails(view,position);


                }
            });
        }


    }

    // tell adapter how to create the view holder

    // This is the subroutine you will call after creating the charge
    // adding a card and setting the access_code

    public void performCharge(final int position, final View view, final String cardNUmber, final String cvv, final int month, final int year) throws JSONException {

        db = new SQLiteHandler(activity);

        final AudioVideo audioVideo = audioVideoArrayList.get(position);
        PaystackSdk.setPublicKey(paystack_public_key);


        //create a Charge object


        Card card = new Card(cardNUmber, month, year, cvv);
        int amnt = (int) Math.round(audioVideo.getPrice()) * 100;

        Charge charge = new Charge();
        charge.setCard(card); //sets the card to charge)
        charge.setAmount(amnt);
        charge.setEmail(db.getUserDetails().get("email"));
        charge.setReference(audioVideo.getName().toString().replaceAll("\\s+","") + Calendar.getInstance().getTimeInMillis());
        try {
            charge.putCustomField("Charged From", "Android SDK");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.metadata = new JSONObject();
        this.customfields = new JSONArray();
        JSONObject customObj = new JSONObject();
        customObj.put("productName", audioVideo.getName());
        customObj.put("productId", audioVideo.getId());
        customObj.put("productType", audioVideo.getType());
        customObj.put("churchId", "1");
        customObj.put("userId", db.getUserDetails().get("uid"));
        this.customfields.put(customObj);


        try {
            this.metadata.put("custom_fields", this.customfields);
        } catch (JSONException e) {
            Log.d("Payment activity", e.toString());
        }

        charge.putMetadata("metadata", this.metadata);

        PaystackSdk.chargeCard(activity, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                Toast.makeText(activity, " You Successfully paid for " + transaction.getReference(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(view.getContext(), DownloadActivity.class);
                i.putExtra("URL", audioVideo.getUrl());
                i.putExtra("file_name", audioVideo.getName());
                view.getContext().startActivity(i);
                verifyOnServer(transaction.getReference(), position);
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
                        performCharge(position, view, cardNUmber, cvv, month, year);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                ;

                if (transaction.getReference() != null) {
                    Toast.makeText(activity, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                    verifyOnServer(transaction.getReference(),position);
                } else {
                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void showDialog(final int position) {

        final SQLiteHandler db = new SQLiteHandler(activity);


        LayoutInflater li = LayoutInflater.from(activity);
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
                    Toast.makeText(activity, "Card database is empty, Please save card first", Toast.LENGTH_LONG).show();

            }
        });


        //Creating an alertdialog builder
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

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
                Toast.makeText(activity, "Performing transaction, Would take a minute.....", Toast.LENGTH_LONG).show();

                cardNumber = mEditCardNum.getText().toString();
                expiryMonth = Integer.parseInt(mEditExpiryMonth.getText().toString()); //any month in the future
                expiryYear = Integer.parseInt(mEditExpiryYear.getText().toString()); // any year in the future. '2018' would work also!
                cvv = mEditCVC.getText().toString();  // cvv of the test card
                try {
                    performCharge(position, view, cardNumber, cvv, expiryMonth, expiryYear);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();
            }
        });

    }

    public interface Listener {
        void onClick(int position);
    }

    //Define adapter view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }


    private void verifyOnServer(final String trans, final int position) {



        String ay = db.getApikey().get("apikey");
        String url = "https://disciplesbay.com/api/verify/payment?apiKey=" + ay + "&reference=" + trans;
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Testimony Activity", "Send Testimony response " + response.toString());
                Toast.makeText(activity, String.format("Your Payment Was Successful you will receive a mail shortly"), Toast.LENGTH_LONG).show();


                try {
                    AudioVideo audioVideo = audioVideoArrayList.get(position);
                    audioVideo.setPaid(true);



                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray status = jsonObject.getJSONArray("status");



                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, String.format("There was a problem verifying %s on the backend: %s ", trans, error), Toast.LENGTH_LONG).show();


            }
        });

        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


    private void showDetails(View view, int position){

        final AudioVideo audioVideo = audioVideoArrayList.get(position);

        Intent intent = new Intent(view.getContext(), ProductDetailsActivity.class);
        intent.putExtra("productName", audioVideo.getName());
        intent.putExtra("productDesc", audioVideo.getDescription());
        intent.putExtra("id", audioVideo.getId());
        intent.putExtra("price", audioVideo.getPrice());
        intent.putExtra("time", audioVideo.getTimestamp());
        intent.putExtra("urlProduct", audioVideo.getUrl());
        intent.putExtra("type",audioVideo.getType());
        view.getContext().startActivity(intent);

    }
}






