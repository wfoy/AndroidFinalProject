package com.example.androidfinalproject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationEntry extends AppCompatActivity {

    String title;

    private SupportMapFragment mapFragment;
    private UiSettings mUiSettings;
    private GoogleMap map;
    private double latitude;
    private double longitude;
    private double defLat = 40.4455;
    private double defLong = -79.9429983;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_entry);
        title = getIntent().getStringExtra("Title"); //set title
        EditText editText = (EditText)findViewById(R.id.note_title);
        editText.setText(title, TextView.BufferType.EDITABLE);

        latitude = defLat;
        longitude = defLong;

        String word = getIntent().getStringExtra("Words");//set text if already exists
        if (word != null) {
            String[] coords = word.split(" ");
            latitude = Double.parseDouble(coords[0]);
            longitude = Double.parseDouble(coords[1]);
            if (latitude != defLat && longitude != defLong) {
                EditText tlat = (EditText) findViewById(R.id.lat_text);
                EditText tlong = (EditText) findViewById(R.id.long_text);
                tlat.setText(coords[0], TextView.BufferType.EDITABLE);
                tlong.setText(coords[1], TextView.BufferType.EDITABLE);
            }
        }
        EditText note_word = (EditText)findViewById(R.id.lat_text);
        note_word.requestFocus();

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                        mUiSettings = map.getUiSettings();
                        mUiSettings.setScrollGesturesEnabled(true);
                        mUiSettings.setZoomGesturesEnabled(true);
                        mUiSettings.setZoomControlsEnabled(true);
                    }
                });
            } else {
                Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            LatLng currentLoc = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(currentLoc).title("Memory Marker"));
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Intent data = new Intent();
            EditText ttl = (EditText)findViewById(R.id.note_title);
            EditText tlat = (EditText)findViewById(R.id.lat_text);
            EditText tlong = (EditText)findViewById(R.id.long_text);
            data.putExtra("Title", ttl.getText().toString());
            data.putExtra("Rem", title);
            setResult(RESULT_OK, data);
            validCoordinates();
            String slat = Double.toString(latitude);
            String slong = Double.toString(longitude);
            save(ttl.getText().toString(), slat + " " + slong);
            finish();
            return true;
        }
        else if (id == R.id.action_update && validCoordinates())
        {
            LatLng currentLoc = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(currentLoc).title("Memory Marker"));
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
            Toast.makeText(this, "Location changed to " + Double.toString(latitude) + ", " + Double.toString(longitude), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validCoordinates() {
        EditText tlat = (EditText)findViewById(R.id.lat_text);
        EditText tlong = (EditText)findViewById(R.id.long_text);
        String slat = tlat.getText().toString();
        String slong = tlong.getText().toString();
        try {
            latitude = Double.parseDouble(slat);
            longitude = Double.parseDouble(slong);
        }
        catch (Exception e) {
            latitude = defLat;
            longitude = defLong;
            return false;
        }
        if (latitude > 90 || latitude < -90 || longitude >=180 || longitude < -180) {
            latitude = defLat;
            longitude = defLong;
            return false;
        }

        return true;
    }

    /** Writes textToSave to the file denoted by fileName. **/
    private void save(String fileName, String textToSave) {
        try {
            OutputStreamWriter out =
                    new OutputStreamWriter(openFileOutput(fileName, 0));
            out.write(textToSave);
            out.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
