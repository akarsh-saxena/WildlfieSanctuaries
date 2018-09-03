package com.application.wildlife.wildlifesanctuaries;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Collections;

public class CityActivity extends AppCompatActivity {

    ArrayList<String> cityList;
    ListView cityListView;
    ArrayAdapter cityAdapter;

    RequestQueue requestQueue;

    //final String JSONData = "https://api.myjson.com/bins/js3s3";
    final String JSONData = "https://api.myjson.com/bins/tankj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        final String stateName = getIntent().getExtras().getString("stateName").toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        requestQueue = Volley.newRequestQueue(this);

        cityListView = findViewById(R.id.cityListView);
        cityList = new ArrayList<>();
        cityAdapter = new ArrayAdapter<>(this, R.layout.listview_item, cityList);
        cityListView.setAdapter(cityAdapter);

        ActionBar myToolbar = getSupportActionBar();
        myToolbar.setTitle(getTitle()+" ("+stateName+")");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, JSONData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray statesArray = response.getJSONArray("States");
                            for (int i = 0; i < statesArray.length(); ++i) {
                                JSONObject stateObject = statesArray.getJSONObject(i);
                                if (TextUtils.equals(stateObject.getString("Sname"), stateName)) {
                                    JSONArray cityArray = stateObject.getJSONArray("Cities");
                                    for (int j = 0; j < cityArray.length(); ++j) {
                                        JSONObject cityObject = cityArray.getJSONObject(j);
                                        cityList.add(cityObject.get("Cname").toString());
                                    }
                                }
                            }
                            Collections.sort(cityList);
                            cityAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CityActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        requestQueue.add(jsonObjectRequest);

        cityListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(CityActivity.this, WildlifeActivity.class);
                        intent.putExtra("stateName", stateName);
                        intent.putExtra("cityName", (String) adapterView.getItemAtPosition(i));
                        intent.putExtra("animalPos", getIntent().getExtras().getInt("animalPos"));
                        startActivity(intent);
                    }
                }
        );
    }
}
