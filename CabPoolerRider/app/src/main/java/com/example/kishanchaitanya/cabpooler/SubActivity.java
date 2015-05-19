package com.example.kishanchaitanya.cabpooler;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class SubActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private Toolbar toolbar;

    GridView myGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myGrid = (GridView) findViewById(R.id.gridView);

        myGrid.setAdapter(new DataGridAdapter(this));
        myGrid.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
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
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,MyDialog.class);
        ViewHolder holder = (ViewHolder) view.getTag();
        Country temp = (Country) holder.myGridImage.getTag();
        intent.putExtra("countryImage",temp.imageId);
        intent.putExtra("countryName",temp.countryName);
        startActivity(intent);
    }
}

class Country{
    int imageId;
    String countryName;
    Country(int imageId,String countryName){
        this.imageId=imageId;
        this.countryName=countryName;
    }
}

class ViewHolder{
    ImageView myGridImage;
    ViewHolder(View v){
        myGridImage = (ImageView) v.findViewById(R.id.imageView);
    }
}

class DataGridAdapter extends BaseAdapter{

    ArrayList<Country> list;
    Context context;
    DataGridAdapter(Context context){
        this.context=context;
        list = new ArrayList<Country>();
        Resources res= context.getResources();
        String[] tempCountryNames = res.getStringArray(R.array.help_menu);
        int[] countryImages = {R.drawable.help_center, R.drawable.jobs, R.drawable.termsofservice, R.drawable.privacy};
        for (int i=0;i<4;i++){
           Country tempCountry = new Country(countryImages[i],tempCountryNames[i]);
            list.add(tempCountry);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder=null;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_item,parent,false);
            holder=new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Country temp = list.get(position);
        holder.myGridImage.setImageResource(temp.imageId);
        holder.myGridImage.setTag(temp);
        return row;
    }
}


