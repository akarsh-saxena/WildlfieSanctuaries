package com.application.wildlife.wildlifesanctuaries;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class AnimalStateActivity extends AppCompatActivity {

    ListView stateListView;
    ArrayList<String> stateList;
    ArrayAdapter<String> stateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_state);

        stateListView = findViewById(R.id.stateListView);
        stateList = new ArrayList<>();
        stateAdapter = new ArrayAdapter<>(this, R.layout.listview_item, stateList);
        stateListView.setAdapter(stateAdapter);

        String animalName = getIntent().getExtras().getString("animalName");

        final Values values = new Values();
        final String speciality[] = values.getSpeciality();

        for(String sp : speciality)
            if(sp.split(":")[1].equals(animalName))
                stateList.add(sp.split(":")[0]);

        stateAdapter.notifyDataSetChanged();

        stateListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(AnimalStateActivity.this, DetailsActivity.class);
                        intent.putExtra("wildlifeName", (String) adapterView.getItemAtPosition(i));
                        intent.putExtra("cityName", "");
                        for(String s : speciality)
                            if(s.split(":")[0].equals(adapterView.getItemAtPosition(i))) {
                                Log.d("muy", "State Found: "+adapterView.getItemAtPosition(i));
                                intent.putExtra("stateName", s.split(":")[2]);
                            }
                        intent.putExtra("animalPos", getIntent().getExtras().getInt("animalPos"));
                        startActivity(intent);
                    }
                }
        );
    }
}
