package com.example.kishanchaitanya.cabpooler;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kishanchaitanya.cabpooler.beans.UserInfo;


public class payment extends ActionBarActivity {

    private Toolbar toolbar;
    private UserInfo uInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uInfo = (UserInfo)intent.getSerializableExtra("UserInfo");
        setContentView(R.layout.activity_payment);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, setting.class));
            return true;
        }

        if(id==android.R.id.home){
             Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("UserInfo",uInfo);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSplitBtn(View v){
        EditText num1 = (EditText) findViewById(R.id.paymentAmount);
        EditText num2 = (EditText) findViewById(R.id.numberOf);

        int val1 = Integer.parseInt(num1.getText().toString());
        int val2 = Integer.parseInt(num2.getText().toString());

        TextView changeText = (TextView) findViewById(R.id.textView2);

        if(val1 != 0 && val2 != 0) {
            int val3 = val1 / val2;
            String result = Integer.toString(val3);
            changeText.setText("The split amount is $" + result);
        }

        else if (val1 == 0 && val2 == 0){
            changeText.setText("Incomplete fields");
        }

        else{
            changeText.setText("Wrong values entered");
        }
    }

}
