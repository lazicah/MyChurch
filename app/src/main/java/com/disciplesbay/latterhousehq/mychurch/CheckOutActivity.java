package com.disciplesbay.latterhousehq.mychurch;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.disciplesbay.latterhousehq.mychurch.adapters.CartAdapter;
import com.disciplesbay.latterhousehq.mychurch.helper.SQLiteHandler;
import com.disciplesbay.latterhousehq.mychurch.helper.SessionManager;

public class CheckOutActivity extends AppCompatActivity {

    SQLiteHandler db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Checkout");
        db = new SQLiteHandler(this);
        session = new SessionManager(this);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        SQLiteHandler db = new SQLiteHandler(this);

        ListView listView = (ListView)findViewById(R.id.cartList);
        CartAdapter cartAdapter = new CartAdapter(this, db.getCarts());
        listView.setAdapter(cartAdapter);

        TextView total = (TextView) findViewById(R.id.totalCart);

        if (db.getCarts().isEmpty()){
            total.setText("0.00");
        }else total.setText(String.valueOf(db.getTotalCart()) + ".00");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.audio_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_checkout) {
            db.clearCart();
            finish();


        }

        return super.onOptionsItemSelected(item);
    }
}
