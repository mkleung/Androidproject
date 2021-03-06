package com.example.project;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * <h1>Activity 2 listview</h1>
 * This activity is uses an asynctask that call the bing api
 * which fetches a list of coffeeshops
 *
 * @author  Pak Leung
 * @version 1.0
 */


public class Activity2_listview extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ListView coffeeListView;
    String coffeeUrl;
    ArrayList < CoffeePlace > coffeePlaces;
    PlacesAdapter myAdapter;

    TextView resultTextView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2_listview);

        resultTextView =findViewById(R.id.resultTextView);

        // Load Data from intent
        String latitude = getIntent().getStringExtra("LATITUDE");
        String longitude = getIntent().getStringExtra("LONGITUDE");

        coffeeUrl = "https://dev.virtualearth.net/REST/v1/LocalSearch/?query=coffee&userLocation=" + latitude + "," + longitude + "&key=ApzeMYSxJulF36ptSnMPfbN9Tb3ZDRj5820D3_YGcudYRWnStu_hn7ADXK2-Ddkz";

        CoffeeQuery req = new CoffeeQuery();
        req.execute(coffeeUrl);

        // ListView
        coffeePlaces = new ArrayList <> ();
        coffeeListView = findViewById(R.id.theListView);

        // Load toolbar
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        //Load NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Go Back to Activity2
        Button hideButton =  findViewById(R.id.hideButton);
        hideButton.setOnClickListener(click -> finish());

        //Populate the coffeeshop listview
        coffeeListView.setOnItemClickListener((parent, view, position, id) -> {
            CoffeePlace selectedCoffee = coffeePlaces.get(position);

            Dialog helpDialog = new Dialog(Activity2_listview.this);

            helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            helpDialog.setContentView(R.layout.activity_2_help_dialog);

            TextView helpDescription =  helpDialog.findViewById(R.id.helpDescription);
            helpDescription.setText(selectedCoffee.getName());

            TextView helpDescription2 =  helpDialog.findViewById(R.id.helpDescription2);
            helpDescription2.setText(selectedCoffee.getAddress());

            TextView helpDescription3 =  helpDialog.findViewById(R.id.helpDescription3);
            helpDescription3.setText(selectedCoffee.getTelephone());

            TextView helpDescription4 = helpDialog.findViewById(R.id.helpDescription4);
            helpDescription4.setText(selectedCoffee.getWebsite());

            ImageView imageView = helpDialog.findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);

            ImageView coffeeImageView = helpDialog.findViewById(R.id.coffeeImageView);
            coffeeImageView.setVisibility(View.VISIBLE);

            Button okButton = helpDialog.findViewById(R.id.okButton);
            okButton.setOnClickListener(click -> helpDialog.cancel());
            helpDialog.show();

        });

        // Progressbar
        progressBar = findViewById(R.id.progressBar);


    }

    // Load top toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        MenuItem itemZoomIn = menu.findItem(R.id.itemZoomIn);
        MenuItem itemZoomOut = menu.findItem(R.id.itemZoomOut);
        itemZoomIn.setVisible(false);
        itemZoomOut.setVisible(false);

        return true;
    }

    /**
     * Top Toolbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.helpItem) {
            Dialog helpDialog = new Dialog(Activity2_listview.this);
            helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            helpDialog.setContentView(R.layout.activity_2_help_dialog);
            TextView helpDescription = helpDialog.findViewById(R.id.helpDescription);
            helpDescription.setText(R.string.helpExtra);
            Button okButton = helpDialog.findViewById(R.id.okButton);
            okButton.setOnClickListener(click -> helpDialog.cancel());
            helpDialog.show();
        }
        return true;
    }

    /**
     * Navigation adapter
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainActivity:
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                break;

            case R.id.activityOne:
                Intent activityOneIntent = new Intent(getApplicationContext(), Activity1.class);
                startActivity(activityOneIntent);
                break;

            case R.id.activityTwo:
                Intent activityTwoIntent = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activityTwoIntent);
                break;

            case R.id.activityThree:
                Intent activityThreeIntent = new Intent(getApplicationContext(), Activity3.class);
                startActivity(activityThreeIntent);
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * This method is used to retrieve data from url using asynctask
     */
    @SuppressLint("StaticFieldLeak")
    private class CoffeeQuery extends AsyncTask < String, Integer, String > {

        protected void onPreExecute() {}

        protected String doInBackground(String...args) {

            try {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    String newLine = line + "\n";
                    sb.append(newLine);
                }
                String result = sb.toString(); //result is the whole string

                // convert string to JSON:
                JSONObject coffeeReport = new JSONObject(result);

                String data = coffeeReport.getString("resourceSets");
                JSONArray jsonArr = new JSONArray(data);
                JSONObject jsonObj = jsonArr.getJSONObject(0);
                String resources = jsonObj.getString("resources");
                JSONArray resourcesArr = new JSONArray(resources);
                publishProgress(50);
                for (int i = 0; i < resourcesArr.length(); i++) {
                    JSONObject jsonObject = resourcesArr.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String address = jsonObject.getString("Address");
                    JSONObject addObj = new JSONObject(address);
                    String formattedAddress = addObj.getString("formattedAddress");
                    String phoneNumber = jsonObject.getString("PhoneNumber");
                    String website = jsonObject.getString("Website");
                    coffeePlaces.add(new CoffeePlace(name, formattedAddress, phoneNumber, website));
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        public void onProgressUpdate(Integer...args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        public void onPostExecute(String fromDoInBackground) {
            progressBar.setVisibility(View.GONE);
            String coffeeSize = "Total: " + coffeePlaces.size();
            resultTextView.setText(coffeeSize);
            coffeeListView.setAdapter(myAdapter = new PlacesAdapter());
        }
    }

    /**
     * CoffeeShop listview adapter
     */
    private class PlacesAdapter extends BaseAdapter {
        public int getCount() {
            return coffeePlaces.size();
        }
        public Object getItem(int position) {
            return "This is row " + position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            CoffeePlace aPlace = coffeePlaces.get(position);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_2_listview_row_layout, parent, false);
            }
            TextView placeName = convertView.findViewById(R.id.placeName);
            placeName.setText(aPlace.getName());
            TextView placeAddress =  convertView.findViewById(R.id.placeAddress);
            placeAddress.setText(aPlace.getAddress());
            return convertView;
        }
    }
}