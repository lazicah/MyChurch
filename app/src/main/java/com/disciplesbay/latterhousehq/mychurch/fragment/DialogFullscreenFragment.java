package com.disciplesbay.latterhousehq.mychurch.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.disciplesbay.latterhousehq.mychurch.BuildConfig;
import com.disciplesbay.latterhousehq.mychurch.LoginActivity;
import com.disciplesbay.latterhousehq.mychurch.PaymentActivity;
import com.disciplesbay.latterhousehq.mychurch.R;
import com.disciplesbay.latterhousehq.mychurch.Utils.TLSSocketFactory;
import com.disciplesbay.latterhousehq.mychurch.Utils.Utility;
import com.disciplesbay.latterhousehq.mychurch.adapters.CartAdapter;
import com.disciplesbay.latterhousehq.mychurch.app.AppController;
import com.disciplesbay.latterhousehq.mychurch.app.ConnectivityReceiver;
import com.disciplesbay.latterhousehq.mychurch.data.CartModel;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;
import com.google.android.gms.ads.AdView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DialogFullscreenFragment extends DialogFragment implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener {

    // To get started quickly, change this to your heroku deployment of
    // https://github.com/PaystackHQ/sample-charge-card-backend
    // Step 1. Visit https://github.com/PaystackHQ/sample-charge-card-backend
    // Step 2. Click "Deploy to heroku"
    // Step 3. Login with your heroku credentials or create a free heroku account
    // Step 4. Provide your secret key and an email with which to start all test transactions
    // Step 5. Copy the url generated by heroku (format https://some-url.herokuapp.com) into the space below
    String backend_url = "new";
    // Set this to a public key that matches the secret key you supplied while creating the heroku instance
    String paystack_public_key = "pk_test_83c9a315b069c930189220a4adf6cf2a286a14a5";
    //pk_test_882c095e1651994e3fd22fb7236422d32b9b9121



    ProgressDialog dialog;

    SessionManager session;



    MaterialEditText amountEntered;
    MaterialEditText tagEntered;
    private TextView mTextReference;
    private Charge charge;
    private Transaction transaction;
    private JSONObject metadata;
    private JSONArray customfields;
    private SQLiteHandler db ;
    CartModel cartModel;

    ListView productList;

    ArrayList<CartModel> cartModels = new ArrayList<>();

    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY, CardType.UNKNOWN };

    private SupportedCardTypesView mSupportedCardTypesView;

    protected CardForm mCardForm;

    public CallbackResult callbackResult;

    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    private int request_code = 0;
    private View root_view;


    CartAdapter cartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        root_view = inflater.inflate(R.layout.dialog_payment, container, false);

        if (BuildConfig.DEBUG && (backend_url.equals(""))) {
            throw new AssertionError("Please set a backend url before running the sample");
        }
        if (BuildConfig.DEBUG && (paystack_public_key.equals(""))) {
            throw new AssertionError("Please set a public key before running the sample");
        }

        Bundle bundle = getArguments();
        String data = bundle.getString("data");

        cartModel = Utility.getGsonParser().fromJson(data,CartModel.class);
        cartModels.add(cartModel);

        cartAdapter = new CartAdapter(getActivity(),cartModels);

        PaystackSdk.setPublicKey(paystack_public_key);
        dialog = new ProgressDialog(getContext());

        //initialize sdk
        PaystackSdk.initialize(getApplicationContext());
        db = new SQLiteHandler(getContext());

        session = new SessionManager(getContext());

        mSupportedCardTypesView = root_view.findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

        mCardForm = root_view.findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .maskCardNumber(true)
                .maskCvv(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .actionLabel("Enter your transaction Details")
                .setup(getActivity());
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);

        productList = (ListView) root_view.findViewById(R.id.products);
        productList.setAdapter(cartAdapter);

        cartModel = new CartModel();








        ((Button) root_view.findViewById(R.id.pay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (session.isLoggedIn()) {

                    if (mCardForm.isValid()) {

                        Toast.makeText(getActivity(), "Valid", Toast.LENGTH_SHORT).show();
                        try {
                            startAFreshCharge(true);
                        } catch (Exception e) {
                            //   PaymentActivity.this.mTextError.setText(String.format("An error occurred while charging card: %s %s", e.getClass().getSimpleName(), e.getMessage()));

                        }

                    }else {
                        mCardForm.validate();
                        Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
                    }




                } else {
                     Snackbar snackbar = Snackbar.make(root_view, "Please Login to Continue", Snackbar.LENGTH_LONG);

                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.setAction("LOGIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();


                }
            }
        });




        return root_view;
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }

    @Override
    public void onCardFormSubmit() {
        if (mCardForm.isValid()) {

            Toast.makeText(getContext(), "Valid", Toast.LENGTH_SHORT).show();
        } else {
            mCardForm.validate();
            Toast.makeText(getContext(), "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    public void setRequestCode(int request_code) {
        this.request_code = request_code;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    public interface CallbackResult {
        void sendResult(int requestCode, Object obj);
    }

    private void startAFreshCharge(boolean local) throws JSONException {
        // initialize the charge
        charge = new Charge();
        charge.setCard(loadCardFromForm());


        dialog.setMessage("Performing transaction... please wait");
        dialog.show();
        dialog.setCancelable(false);


        int amount = Integer.parseInt(amountEntered.getText().toString()) * 100;
        String email = db.getUserDetails().get("email");

        this.metadata = new JSONObject();
        this.customfields = new JSONArray();
        JSONObject customObj = new JSONObject();

        try {
            customObj.put("churchId", "1");
            customObj.put("userId", db.getUserDetails().get("uid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.customfields.put(customObj);

        try {
            this.metadata.put("custom_fields", this.customfields);
        } catch (JSONException e) {
            Log.d("Payment activity", e.toString());
        }

        if (local) {
            // Set transaction params directly in app (note that these params
            // are only used if an access_code is not set. In debug mode,
            // setting them after setting an access code would throw an exception
            charge.putMetadata("metadata", this.metadata);
            charge.setAmount(amount);
            charge.setEmail(email);
            charge.setReference(tagEntered.getText().toString().replaceAll("\\s+","") + Calendar.getInstance().getTimeInMillis());
            try {
                charge.putCustomField("Charged From", "Android SDK");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chargeCard();
        } else {
            // Perform transaction/initialize on our server to get an access code
            // documentation: https://developers.paystack.co/reference#initialize-a-transaction
            //new fetchAccessCodeFromServer().execute(backend_url + "/new-access-code");
        }
    }

    /**
     * Method to validate the form, and set errors on the edittexts.
     */
    private Card loadCardFromForm() {
        //validate fields
        Card card;

        String cardNum = mCardForm.getCardNumber();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        String cvc = mCardForm.getCvv();
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        String sMonth = mCardForm.getExpirationMonth();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = mCardForm.getExpirationYear();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        return card;
    }



    private void chargeCard() {
        transaction = null;
        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(Transaction transaction) {

                transaction = transaction;
                dialog.setMessage("Trying to Verify Your Transaction... please wait");
                Log.d("REFERENCE", transaction.getReference());
                Toast.makeText(getActivity(), transaction.getReference(), Toast.LENGTH_LONG).show();


                verifyOnServer(transaction.getReference());
            }

            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(Transaction transaction) {
                transaction = transaction;
                Toast.makeText(getActivity(), transaction.getReference(), Toast.LENGTH_LONG).show();

            }




            @Override
            public void onError(Throwable error, Transaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                transaction = transaction;
                if (error instanceof ExpiredAccessCodeException) {
                    try {
                        startAFreshCharge(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    chargeCard();
                    return;
                }

                dismissDialog();

                if (transaction.getReference() != null) {
                    Toast.makeText(getActivity(), transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    //mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()));

                } else {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    // mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }

            }

        });
    }

    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void updateTextViews() {
        if (transaction.getReference() != null) {
            mTextReference.setText(String.format("Reference: %s", transaction.getReference()));
        } else {
            mTextReference.setText("No transaction");
        }
    }


    private boolean isEmpty(String s) {
        return s == null || s.length() < 1;
    }


    private void verifyOnServer(final String trans) {



        String ay = db.getApikey().get("apikey");
        String url = "https://disciplesbay.com/api/verify/payment?apiKey=" + ay + "&reference=" + trans;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Testimony Activity", "Send Testimony response " + response.toString());
                dialog.setMessage("Successful");
                dismissDialog();




                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String statius = jsonObject.getString("status");
                    if (statius.equalsIgnoreCase("ok")){
                        if (callbackResult != null) {
                            callbackResult.sendResult(request_code, cartModel);
                        }
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
            requestQueue = Volley.newRequestQueue(getContext(), stack);
        } else {
            requestQueue = Volley.newRequestQueue(getContext());
        }

        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }


}