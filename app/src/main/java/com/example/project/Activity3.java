package com.example.project;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class Activity3 extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Test Activity 3";

    String loginEmail;

    ArrayList<Place> initialList = new ArrayList<>();
    ArrayList<Place> placesList = new ArrayList<>();
    MyOwnAdapter myAdapter;
    SQLiteDatabase db;

    SharedPreferences sharedPreferences = null;
    public static final String DEFAULT="N/A";

    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        // Load top toolbar
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

        //Email
        sharedPreferences = getSharedPreferences("Bing", Context.MODE_PRIVATE);
        loginEmail = sharedPreferences.getString("EMAIL",DEFAULT);
        TextView emailTextView = findViewById(R.id.emailTextView);
        String favoritePlaces = "All favorite places by: " + loginEmail;
        emailTextView.setText(favoritePlaces);

        // DATABASE
        loadDataFromDatabase();
        ListView theList = findViewById(R.id.the_list);
        myAdapter = new MyOwnAdapter();
        theList.setAdapter(myAdapter);
        theList.setOnItemClickListener((list, item, position, id) -> Toast.makeText(getApplicationContext(), "Data was saved successfully ", Toast.LENGTH_LONG).show());

        // ADD A PLACE
        String title = getIntent().getStringExtra("TITLE");
        String latitude = getIntent().getStringExtra("LATITUDE");
        String longitude = getIntent().getStringExtra("LONGITUDE");
        String description = getIntent().getStringExtra("DESCRIPTION");
        String stars = getIntent().getStringExtra("STARS");
        String zoom = getIntent().getStringExtra("ZOOM");
        currentPhotoPath = getIntent().getStringExtra("PHOTO");

            if (TextUtils.isEmpty(title)) {
                return;
            }
            if (TextUtils.isEmpty(latitude)) {
                return;
            }
            if (TextUtils.isEmpty(longitude)) {
                return;
            }
            if (TextUtils.isEmpty(description)) {
                return;
            }
            if (TextUtils.isEmpty(stars)) {
                return;
            }
            if (TextUtils.isEmpty(zoom)) {
                return;
            }
            if (TextUtils.isEmpty(currentPhotoPath)) {
                return;
            }

            ContentValues newRowValues = new ContentValues();
            newRowValues.put(DbOpener.COL_TITLE, title);
            newRowValues.put(DbOpener.COL_LATITUDE, latitude);
            newRowValues.put(DbOpener.COL_LONGITUDE, longitude);
            newRowValues.put( DbOpener.COL_DESCRIPTION, description);
            newRowValues.put(DbOpener.COL_EMAIL, loginEmail);
            newRowValues.put(DbOpener.COL_STARS, stars);
            newRowValues.put(DbOpener.COL_ZOOM, zoom);
            newRowValues.put(DbOpener.COL_PHOTO, currentPhotoPath);

            long newId = db.insert(DbOpener.TABLE_NAME, null, newRowValues);
            Place newPlace = new Place(title, latitude, longitude,description,loginEmail,stars,zoom, currentPhotoPath, newId);
            placesList.add(newPlace);
            myAdapter.notifyDataSetChanged();

    } //End onCreate

    /**
     * Initialize top toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    /**
     * Top toolbar items
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.helpItem) {
            Dialog helpDialog = new Dialog(Activity3.this);
            helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            helpDialog.setContentView(R.layout.activity_2_help_dialog);
            Button okButton = helpDialog.findViewById(R.id.okButton);
            TextView helpDescription = helpDialog.findViewById(R.id.helpDescription);
            helpDescription.setText(R.string.activity3Select);
            okButton.setOnClickListener(click -> helpDialog.cancel());
            helpDialog.show();
        }

        return true;
    }

    /**
     * Navigation drawer items
     *
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



    /*
    * DATABASES
    *
    *
     */
    protected class MyOwnAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return placesList.size();
        }

        public Place getItem(int position){
            return placesList.get(position);
        }

        public View getView(int position, View old, ViewGroup parent) {
            Place thisRow = getItem(position);
            if (old == null){
                old = getLayoutInflater().inflate(R.layout.activity_3_row_layout, parent, false );
            }
            TextView rowTitle = old.findViewById(R.id.placeTitle);
            rowTitle.setText(thisRow.getTitle());

            RatingBar mRatingBar = old.findViewById(R.id.ratingBar);
            String s = thisRow.getStars();
            mRatingBar.setRating(Character.getNumericValue(s.charAt(0)));

            ImageView viewPhoto = old.findViewById(R.id.viewPhoto);
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(thisRow.getCurrentPhotoPath()), 150, 150);
            viewPhoto.setImageBitmap(thumbImage);



            TextView data = old.findViewById(R.id.data);
            String dataString = thisRow.getLatitude() + ", " + thisRow.getLongitude();
            data.setText(dataString) ;

            // Go back to activity 2
            ImageButton detailsButton =  old.findViewById(R.id.detailsButton);
            detailsButton.setOnClickListener(click -> {
                Dialog helpDialog = new Dialog(Activity3.this);
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.setContentView(R.layout.activity_2_help_dialog);
                Button okButton = helpDialog.findViewById(R.id.okButton);
                TextView helpDescription = helpDialog.findViewById(R.id.helpDescription);
                helpDescription.setText(thisRow.getTitle());
                TextView helpDescription2 = helpDialog.findViewById(R.id.helpDescription2);
                helpDescription2.setText(thisRow.getDescription());
                TextView helpDescription3 = helpDialog.findViewById(R.id.helpDescription3);
                String help3 = "Latitude: " + thisRow.getLatitude()  + " Longitude: " + thisRow.getLongitude();
                helpDescription3.setText(help3);
                TextView helpDescription4 = helpDialog.findViewById(R.id.helpDescription4);
                String help4 = "Zoom Level: " + thisRow.getZoom();
                helpDescription4.setText(help4);
                TextView helpDescription5 = helpDialog.findViewById(R.id.helpDescription5);
                String help5 = "Email: " + thisRow.getEmail();
                helpDescription5.setText(help5);
                okButton.setOnClickListener(click2 -> helpDialog.cancel());
                helpDialog.show();
            });


            // View details
            ImageButton viewButton =  old.findViewById(R.id.viewButton);
            viewButton.setOnClickListener(click -> {
                Intent intent = new Intent(getApplicationContext(), Activity2.class);
                intent.putExtra("LATITUDE", thisRow.getLatitude());
                intent.putExtra("LONGITUDE", thisRow.getLongitude());
                intent.putExtra("ZOOM", thisRow.getZoom());
                startActivity(intent);
            });

            // Delete
            ImageButton deleteButton =  old.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(click -> {
                placesList.remove(position);
                deletePlace(thisRow);
                notifyDataSetChanged();
            });

            return old;
        }
        public long getItemId(int position)
        {
            return getItem(position).getId();
        }
    }

    protected void deletePlace(Place c) {
        db.delete(DbOpener.TABLE_NAME, DbOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
    }

    private void loadDataFromDatabase() {
        DbOpener dbOpener = new DbOpener(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {DbOpener.COL_ID, DbOpener.COL_TITLE, DbOpener.COL_LATITUDE, DbOpener.COL_LONGITUDE, DbOpener.COL_DESCRIPTION, DbOpener.COL_EMAIL, DbOpener.COL_STARS, DbOpener.COL_ZOOM, DbOpener.COL_PHOTO};
        Cursor results = db.query(false, DbOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int titleColumnIndex = results.getColumnIndex(DbOpener.COL_TITLE);
        int latitudeColumnIndex = results.getColumnIndex(DbOpener.COL_LATITUDE);
        int longitudeColumnIndex = results.getColumnIndex(DbOpener.COL_LONGITUDE);
        int descriptionColumnIndex = results.getColumnIndex(DbOpener.COL_DESCRIPTION);
        int emailColumnIndex = results.getColumnIndex(DbOpener.COL_EMAIL);
        int starsColumnIndex = results.getColumnIndex(DbOpener.COL_STARS);
        int zoomColumnIndex = results.getColumnIndex(DbOpener.COL_ZOOM);
        int photoColumnIndex = results.getColumnIndex(DbOpener.COL_PHOTO);
        int idColIndex = results.getColumnIndex(DbOpener.COL_ID);

        while(results.moveToNext()) {

            String title = results.getString(titleColumnIndex);
            String latitude = results.getString(latitudeColumnIndex);
            String longitude = results.getString(longitudeColumnIndex);
            String description = results.getString(descriptionColumnIndex);
            String email = results.getString(emailColumnIndex);
            String stars = results.getString(starsColumnIndex);
            String zoom = results.getString(zoomColumnIndex);
            String photo = results.getString(photoColumnIndex);
            long id = results.getLong(idColIndex);

            if (email.equals(loginEmail)) {
                placesList.add(new Place(title, latitude, longitude, description, email, stars, zoom, photo, id));
            }

        }
        initialList = placesList;
        printCursor(results, db.getVersion());
    }

    private void printCursor(Cursor c, int version) {
        Log.v(TAG, "The database number = " + version);
        Log.v(TAG, "The number of columns in the cursor = " + c.getColumnCount());
        String[] columnNames = c.getColumnNames();
        Log.v(TAG, "The names of columns in the cursor = " + Arrays.toString(columnNames));
        Cursor  cursor = db.rawQuery("select * from " +  DbOpener.TABLE_NAME + " WHERE email='" + loginEmail + "'",null);
        int titleColumnIndex = cursor.getColumnIndex(DbOpener.COL_TITLE);
        int idColIndex = cursor.getColumnIndex(DbOpener.COL_ID);
        while(cursor.moveToNext()) {
            String title = cursor.getString(titleColumnIndex);
            long id = cursor.getLong(idColIndex);
            Log.v(TAG, "_id:" + id + " - title:" + title);
        }
        cursor.close();
    }
}
