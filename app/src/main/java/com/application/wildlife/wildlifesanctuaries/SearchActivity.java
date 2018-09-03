package com.application.wildlife.wildlifesanctuaries;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnState, btnAnimal, btnLocation;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnState = findViewById(R.id.btnState);
        btnLocation = findViewById(R.id.btnLocation);
        btnAnimal = findViewById(R.id.btnAnimal);
        btnState.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        btnAnimal.setOnClickListener(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    //    Log.d("myadd", "long, lat: "+location.getLatitude()+" "+location.getLongitude());
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    //    Log.d("myadd", addressList.get(0).getLocality());
                    Toast.makeText(SearchActivity.this, "You are in: "+ addressList.get(0).getSubAdminArea()+", "+addressList.get(0).getAdminArea(), Toast.LENGTH_SHORT).show();
                    //stateName = addressList.get(0).getAdminArea();
                    Intent intent = new Intent(SearchActivity.this, CityActivity.class);
                    intent.putExtra("animalPos", -1);
                    intent.putExtra("stateName", addressList.get(0).getAdminArea());
                    startActivity(intent);
                    //   Log.d("myadd", String.valueOf(addressList));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnState:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.btnLocation:
                getLocation();
                break;

            case R.id.btnAnimal:
                Intent intent1 = new Intent(this, SearchAnimalActivity.class);
                startActivity(intent1);
                break;

        }
    }

    public void getLocation() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
    }

}