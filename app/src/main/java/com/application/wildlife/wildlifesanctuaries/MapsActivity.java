package com.application.wildlife.wildlifesanctuaries;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String wildlifeName, cityName, stateName;
    RequestQueue requestQueue;
    TextView tvWalk, tvCar, tvCycle, tvTimeDist, tvOrigin, tvDestination;
    LinearLayout llWalk, llCar, llCycle;
    String distWalk, distCar, distCycle;
    ProgressDialog progressDialog;
    Button btnGetDirections;
    double lat=20.5;
    double longi=78.6;
    LinearLayout llMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        lat = getIntent().getExtras().getDouble("wildlifeLat");
        longi = getIntent().getExtras().getDouble("wildlifeLongi");
        wildlifeName = getIntent().getExtras().get("wildlifeName").toString();
        stateName = getIntent().getExtras().get("stateName").toString();
        cityName = getIntent().getExtras().get("cityName").toString();

        getSupportActionBar().setTitle(wildlifeName);

        tvWalk = findViewById(R.id.tvWalk);
        tvCar = findViewById(R.id.tvCar);
        tvCycle = findViewById(R.id.tvCycle);
        llWalk = findViewById(R.id.llWalk);
        llCar = findViewById(R.id.llCar);
        llCycle = findViewById(R.id.llCycle);
        tvTimeDist = findViewById(R.id.tvTimeDist);
        btnGetDirections = findViewById(R.id.btnGetDirections);
        tvOrigin = findViewById(R.id.tvOrigin);
        tvDestination = findViewById(R.id.tvDestination);
        llMap = findViewById(R.id.llMap);

        this.registerForContextMenu(llMap);

        btnGetDirections.setOnClickListener(this);
        llWalk.setOnClickListener(this);
        llCar.setOnClickListener(this);
        llCycle.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                final String origin = location.getLatitude() + "," + location.getLongitude();
                String wN = wildlifeName.replace(" ", "+");
                String cN = cityName.replace(" ", "+");
                String sN = stateName.replace(" ", "+");
                final String dest = wN + "," + cN + "," + sN + ",India";
                tvDestination.setText("Destination: " + wildlifeName + "," + cityName + "," + stateName + ",India");

                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    List<Address> add = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    tvOrigin.setText("Origin: " + add.get(0).getSubAdminArea() + ", " + add.get(0).getAdminArea() + ", " + add.get(0).getCountryName());
                    LatLng latLang = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLang).title(add.get(0).getSubAdminArea() + ", " + add.get(0).getAdminArea() + ", " + add.get(0).getCountryName()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang, 15));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(this, "HELLO", Toast.LENGTH_SHORT).show();
                String urlWalking = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + dest + "&mode=walking&key=AIzaSyDKPWqssYRY-vBHAvH3WIvDJSlUDIwScRc";
                String urlDriving = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + dest + "&mode=driving&key=AIzaSyDKPWqssYRY-vBHAvH3WIvDJSlUDIwScRc";
                String urlBicycling = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + dest + "&mode=bicycling&key=AIzaSyDKPWqssYRY-vBHAvH3WIvDJSlUDIwScRc";
                JsonObjectRequest objectRequestWalking =
                        new JsonObjectRequest(urlWalking, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("rows");
                                    String status = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).get("status").toString();
                                    if (TextUtils.equals(status, "ZERO_RESULTS")) {
                                        tvWalk.setText("No data");
                                        return;
                                    }
                                    JSONObject object = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
                                    String distance = object.getString("text");
                                    JSONObject object1 = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration");
                                    String duration = object1.getString("text");
                                    tvWalk.setText(duration);
                                    distWalk = distance;
                                    /*AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);
                                    builder.setIcon(R.mipmap.ic_launcher);
                                    builder.setTitle("Origin: "+origin+" Dest: "+dest);
                                    builder.setMessage("Distance: "+distance+"\nDuration:  "+duration+"\nVia: "+mode);
                                    builder.show();*/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MapsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        });
                requestQueue.add(objectRequestWalking);

                JsonObjectRequest objectRequestDriving =
                        new JsonObjectRequest(urlDriving, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("rows");
                                    String status = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).get("status").toString();
                                    if (TextUtils.equals(status, "ZERO_RESULTS")) {
                                        tvCar.setText("No data");
                                        return;
                                    }
                                    JSONObject object = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
                                    String distance = object.getString("text");
                                    JSONObject object1 = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration");
                                    String duration = object1.getString("text");
                                    tvCar.setText(duration);
                                    distCar = distance;
                                    /*AlertDialog.Builder builder =new AlertDialog.Builder(MapsActivity.this);
                                    builder.setIcon(R.mipmap.ic_launcher);
                                    builder.setTitle("Origin: "+origin+" Dest: "+dest);
                                    builder.setMessage("Distance: "+distance+"\nDuration:  "+duration+"\nVia: "+mode);
                                    builder.show();*/

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MapsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        });
                requestQueue.add(objectRequestDriving);

                JsonObjectRequest objectRequestBicycling =
                        new JsonObjectRequest(urlBicycling, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("rows");
                                    String status = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).get("status").toString();
                                    if (TextUtils.equals(status, "ZERO_RESULTS")) {
                                        tvCycle.setText("No data");
                                        progressDialog.dismiss();
                                        return;
                                    }
                                    JSONObject object = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
                                    String distance = object.getString("text");
                                    JSONObject object1 = jsonArray.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration");
                                    String duration = object1.getString("text");
                                    tvCycle.setText(duration);
                                    distCycle = distance;
                                    /*AlertDialog.Builder builder =new AlertDialog.Builder(MapsActivity.this);
                                    builder.setIcon(R.mipmap.ic_launcher);
                                    builder.setTitle("Origin: "+origin+" Dest: "+dest);
                                    builder.setMessage("Distance: "+distance+"\nDuration:  "+duration+"\nVia: "+mode);
                                    builder.show();*/
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MapsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                requestQueue.add(objectRequestBicycling);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);

    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,1,0,"Normal");
        menu.add(0,2,0,"Hybrid");
        menu.add(0,3,0,"Satellite ");
        menu.add(0,4,0,"Terrain");
        menu.add(0,5,0,"None");

    }*/
    /*public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if (item.getItemId() == 2) {

            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        } else if (item.getItemId() == 3) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if(item.getItemId()==4)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else if(item.getItemId()==5)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
        else
            return false;

        return true;
    }*/




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
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.llWalk:
                if(TextUtils.isEmpty(distWalk))
                    tvTimeDist.setText(tvWalk.getText());
                else
                    tvTimeDist.setText(tvWalk.getText() + " (" + distWalk + ")");
                break;

            case R.id.llCar:
                if(TextUtils.isEmpty(distCar))
                    tvTimeDist.setText(tvCar.getText());
                else
                    tvTimeDist.setText(tvCar.getText() + " (" + distCar + ")");
                break;

            case R.id.llCycle:
                if(TextUtils.isEmpty(distCycle))
                    tvTimeDist.setText(tvCycle.getText());
                else
                    tvTimeDist.setText(tvCycle.getText() + " (" + distCycle + ")");
                break;

            case R.id.btnGetDirections:
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.booking_resource, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.actionHotel:
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.makemytrip");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }else
                {
                    Uri uri = Uri.parse("market://details?id=com.makemytrip");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=com.makemytrip")));
                    }
                }
                break;

            case R.id.actionCab:
                Intent launchIntent1 = getPackageManager().getLaunchIntentForPackage("com.olacabs.customer");
                if (launchIntent1 != null) {
                    startActivity(launchIntent1);//null pointer check in case package name was not found
                }else
                {
                    Uri uri = Uri.parse("market://details?id=com.olacabs.customer");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=com.olacabs.customer")));
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
