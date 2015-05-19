package com.rookies.driver.cabpooler;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rookies.driver.cabpooler.beans.DriverInfo;
import com.rookies.driver.cabpooler.util.CabPoolerHttpConnection;


public class login extends ActionBarActivity {

    private DriverInfo driverInfo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginBtn(View v)
    {
        EditText uID = (EditText) findViewById(R.id.userID);
        EditText passwd = (EditText) findViewById(R.id.password);

        UserAction uAction = new UserAction(this);
        uAction.execute(uID.getText().toString(),passwd.getText().toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class UserAction extends AsyncTask<String,String,String> {
        Context context;

        private UserAction(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            CabPoolerHttpConnection conn = new CabPoolerHttpConnection();
            driverInfo = conn.loginDriver(params[0],params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(driverInfo!=null) {
                Intent mainAct = new Intent(context, MainActivity.class);
                mainAct.putExtra("DriverInfo",driverInfo);
                startActivity(mainAct);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Invalid Login Credentials!!", Toast.LENGTH_LONG).show();
            }

        }


    }
}
