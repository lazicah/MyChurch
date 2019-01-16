package com.disciplesbay.latterhousehq.mychurch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    SessionManager session;
    SQLiteHandler db;
    TextView username, useremail, login, date, icon;
    private int year, month, day;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        username = (TextView) findViewById(R.id.user_name);
        useremail = (TextView) findViewById(R.id.user_email);
        login = (TextView) findViewById(R.id.login_button);
        date = (TextView) findViewById(R.id.date_of_birth);
        icon = (TextView)findViewById(R.id.icon_text);



        db =  new SQLiteHandler(this);
        session = new SessionManager(this);
        showDate();
        if(session.isLoggedIn()){
            login.setText(getResources().getString(R.string.btn_logout));
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutUser();
                }
            });
            if (!db.getUserDetails().isEmpty()){
                username.setText(db.getUserDetails().get("name"));
                useremail.setText(db.getUserDetails().get("email"));
                icon.setText(db.getUserDetails().get("name").substring(0,1));
            }
        }
    }


    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDate(){
        Log.d("Date ", db.getUserDetails().toString());

        if (!db.getDB().isEmpty()){
            String days = db.getDB().get("day");
            String months = db.getDB().get("month");
            String years = db.getDB().get("year");
            date.setText(new StringBuffer().append(days).append("/")
                    .append(months).append("/").append(years));
        }else{
            date.setText("click the left icon to set date of birth");
        }


    }


    @SuppressWarnings("deprecation")
    public void setDate(View view){
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id){
        if (id == 999){
            return new DatePickerDialog(this,myDateListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            db.addDB(dayOfMonth,year,month + 1);
            date.setText(new StringBuffer().append(dayOfMonth).append("/")
                    .append(month+1).append("/").append(year));

        }
    };

}
