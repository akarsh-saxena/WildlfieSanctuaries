package com.application.wildlife.wildlifesanctuaries;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView stateListView;
    ArrayList<String> stateList;
    ArrayAdapter<String> stateAdapter;
    RequestQueue requestQueue;
    JSONObject parentObject;
    LocationManager locationManager;
    LocationListener locationListener;

    //final String JSONData = "https://api.myjson.com/bins/js3s3";
    final String JSONData = "https://api.myjson.com/bins/tankj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        myToolbar.setTitleTextColor(Color.WHITE);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        stateListView = findViewById(R.id.stateListView);
        stateList = new ArrayList<>();
        stateAdapter = new ArrayAdapter<>(this, R.layout.listview_item, stateList);
        stateListView.setAdapter(stateAdapter);
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, JSONData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            parentObject = response;
                            JSONArray statesArray = response.getJSONArray("States");
                            for (int i = 0; i < statesArray.length(); ++i) {
                                JSONObject statesObject = statesArray.getJSONObject(i);
                                stateList.add(statesObject.get("Sname").toString());
                                Collections.sort(stateList);
                                stateAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });


        //Collections.sort(stateList);
        //stateAdapter.notifyDataSetChanged();

        requestQueue.add(jsonObjectRequest);

        stateListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this, CityActivity.class);
                        intent.putExtra("stateName", (String) adapterView.getItemAtPosition(i));
                        intent.putExtra("animalPos", -1);
                        startActivity(intent);
                    }
                }
        );

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                //    Log.d("myadd", "long, lat: "+location.getLatitude()+" "+location.getLongitude());
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                //    Log.d("myadd", addressList.get(0).getLocality());
                    Toast.makeText(MainActivity.this, "You are in: "+ addressList.get(0).getSubAdminArea()+", "+addressList.get(0).getAdminArea(), Toast.LENGTH_SHORT).show();
                    //stateName = addressList.get(0).getAdminArea();
                    Intent intent = new Intent(MainActivity.this, CityActivity.class);
                    intent.putExtra("stateName", addressList.get(0).getAdminArea());
                    intent.putExtra("animalPos", -1);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_location:
                Toast.makeText(this, "Displaying nearby sanctuaries", Toast.LENGTH_SHORT).show();
                getLocation();
                return true;

            case R.id.action_feedbacks:
                Toast.makeText(this, "Displaying feedbacks", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Feedbacks.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getLocation() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
    }
}
