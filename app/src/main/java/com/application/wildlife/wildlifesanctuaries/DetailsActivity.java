package com.application.wildlife.wildlifesanctuaries;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private String wildlifeName, cityName, stateName;
    private int animalPos;
    private TextView tvDescription, tvSpeciality, tvArea;
    private ImageView ivPhoto;
    private LinearLayout llMap;
    LocationManager locationManager;
    LocationListener locationListener;
    double lat=20.5;
    double longi=78.6;
    int randomValue;
    ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        myToolbar.setTitleTextColor(Color.WHITE);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        wildlifeName = getIntent().getExtras().get("wildlifeName").toString();
        cityName = getIntent().getExtras().get("cityName").toString();
        stateName = getIntent().getExtras().get("stateName").toString();
        animalPos = getIntent().getExtras().getInt("animalPos");
        getSupportActionBar().setTitle(wildlifeName);

        //tvWildlifeName = findViewById(R.id.tvDescription);
        tvSpeciality = findViewById(R.id.tvSpeciality);
        tvArea = findViewById(R.id.tvArea);
        ivPhoto = findViewById(R.id.ivPhoto);
        tvDescription = findViewById(R.id.tvDescription);
        llMap = findViewById(R.id.llMap);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        Values values = new Values();
        String speciality[] = values.getSpeciality();
        Random random = new Random();
        for(String sp : speciality)
            if(sp.split(":")[0].trim().equals(wildlifeName.trim())) {
                tvSpeciality.setText("Speciality: \n" + sp.split(":")[1]);
                for(int i=0; i<values.getFamousFor().length; ++i) {
                    if(values.getFamousFor()[i].trim().equals(sp.split(":")[1])) {
                        randomValue = i;
                        break;
                    }
                }
                break;
            }
            else {
                if(animalPos==-1)
                    randomValue = random.nextInt(9);
                else
                    randomValue = animalPos;
                tvSpeciality.setText("Speciality: \n" + values.getFamousFor(randomValue));
            }

        ivPhoto.setImageResource(values.getImages(randomValue));

        String dist[] = values.getDist();
        float area = 0;
        for(String d : dist) {
            if(d.split(":")[0].trim().equals(wildlifeName.trim())) {
                area = Float.parseFloat(d.split(":")[1]);
                break;
            }
            else {
                area = (random.nextFloat() * 600) + 200;
            }
        }
        String ar = String.format(Locale.US, "%.2f", area);
        tvArea.setText("Area: \n" + ar + " sq. km");


        ivPhoto.setOnClickListener(this);
        tvDescription.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Geocoder geocoder = new Geocoder(DetailsActivity.this, Locale.getDefault());
                LatLng latLang;
                Address add;

                try {
                    Toast.makeText(DetailsActivity.this, wildlifeName, Toast.LENGTH_SHORT).show();
                    List<Address> addressList = geocoder.getFromLocationName(wildlifeName+", India",1);
                    if(addressList.isEmpty())
                        addressList = geocoder.getFromLocationName(cityName+", "+stateName+", India", 1);
                    add = addressList.get(0);
                    latLang = new LatLng(add.getLatitude(),add.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLang).title(wildlifeName+", India").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 15));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                longi = location.getLongitude();
                lat = location.getLatitude();
                progressDialog.dismiss();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(DetailsActivity.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1000, locationListener);
    }

    @Override
    public void onClick(View view) {

        Values values = new Values();

        switch (view.getId())
            {
                case R.id.ivPhoto:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(values.getAnmialLink(randomValue)));
                    startActivity(intent);
                    break;

                case R.id.tvDescription:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Description");
                    builder.setMessage(values.getDes(randomValue));
                    builder.show();
                    break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng latLang = new LatLng(lat,longi);
        mMap.addMarker(new MarkerOptions().position(latLang).title(wildlifeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLang));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(DetailsActivity.this, MapsActivity.class);
                intent.putExtra("wildlifeName", wildlifeName);
                intent.putExtra("wildlifeLat", lat);
                intent.putExtra("wildlifeLongi", longi);
                intent.putExtra("stateName", stateName);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
                return true;

            case R.id.actionNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            case R.id.actionHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.actionSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.actionTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;

            case R.id.feedback:
                Intent intent1  = new Intent(DetailsActivity.this, FeedbackActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
