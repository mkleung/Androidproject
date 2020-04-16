
package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class Activity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "Activity 1";

    ProgressBar progressBar1;
    Toolbar toolbar;
    EditText LongEditText;
    EditText LatEditText;

    String latitude;
    String longitude;

    ListView listView;
    TextView latInput;
    TextView longInput;
    List<Search_History> historyList = new ArrayList<>();

    Button SearchBtn;
    Button RandomLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        listView = findViewById(R.id.searchListView);
        latInput = findViewById(R.id.latInput);
        longInput = findViewById(R.id.longInput);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        progressBar1 = findViewById(R.id.progressbar1);
        progressBar1.setVisibility(View.VISIBLE);

        SharedPreferences shprefs = getSharedPreferences("Activity 1", Context.MODE_PRIVATE);
        latitude = shprefs.getString("Latitude","");
        longitude = shprefs.getString("Longitude","");

        LatEditText = findViewById(R.id.LatEditText);
        LatEditText.setText(latitude);
        LongEditText = findViewById(R.id.LongEditText);
        LongEditText.setText(longitude);

        SearchBtn = findViewById(R.id.SearchBtn);
        RandomLoc = findViewById(R.id.RandomLoc);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout navdrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                navdrawer, toolbar, R.string.open, R.string.close);
        navdrawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SearchBtn.setOnClickListener(click -> {
                Intent activity2 = new Intent(getApplicationContext(), Activity2.class);
                activity2.putExtra("LATITUDE", LatEditText.getText().toString());
                activity2.putExtra("LONGITUDE", LongEditText.getText().toString());
                startActivity(activity2);
                latitude = LatEditText.getText().toString();
                longitude = LongEditText.getText().toString();
                Search_History list = new Search_History(latitude, longitude);
                historyList.add(list);
                Activity1_listview adapter = new Activity1_listview(historyList, getApplicationContext());
                listView.setAdapter(adapter);
        });


        RandomLoc.setOnClickListener(click -> {

            randomCoordinates random = new randomCoordinates();
                random.execute("https://api.3geonames.org/?randomland=yes");
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences shprefs = getSharedPreferences("Activity 1", Context.MODE_PRIVATE);

        LatEditText = findViewById(R.id.LatEditText);
        LongEditText = findViewById(R.id.LongEditText);

        SharedPreferences.Editor editor = shprefs.edit();
        editor.putString("Latitude", LatEditText.getText().toString());
        editor.putString("Longitude", LongEditText.getText().toString());
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch(item.getItemId()) {
            case R.id.search:
                Toast toast1 = Toast.makeText(this, "You are now going to Activity 2", Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast1.show();
                Intent activity2 = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activity2);
                break;

            case R.id.favourite:
                Toast toast2 = Toast.makeText(this, "You are now going to Activity 3", Toast.LENGTH_LONG);
                toast2.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast2.show();
                Intent activity3 = new Intent(getApplicationContext(), Activity3.class);
                startActivity(activity3);
                break;

            case R.id.help:
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Help");
                alertDialog.setMessage("Please enter a latitude and longitude value and click the search button");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;

        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {


        switch(item.getItemId())
        {
            case R.id.search:
                Intent activity2 = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activity2);
                break;

            case R.id.favourite:
                Intent activity3 = new Intent(getApplicationContext(), Activity3.class);
                startActivity(activity3);
                break;

            case R.id.help:
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Help");
                alertDialog.setMessage("Please enter a latitude and longitude value and click the search button");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
        }
        DrawerLayout navdrawer = findViewById(R.id.drawer_layout);
        navdrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private class randomCoordinates extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... args) {
            try {

                // Load url
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream response = urlConnection.getInputStream();

                // Parse xml
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                // Start parsing
                String parameter = null;
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("latt")) {
                            latitude = xpp.nextText();
                        }
                        else if (xpp.getName().equals("longt")) {
                            longitude = xpp.nextText();
                        }
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Error");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... args) {
            progressBar1.setVisibility(View.VISIBLE);
            progressBar1.setProgress(args[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            LatEditText.setText(latitude);
            LongEditText.setText(longitude);
            Toast.makeText(getApplicationContext(),"Coordinates Generated: "+latitude+", "+longitude,Toast.LENGTH_LONG);
        }

    }

}