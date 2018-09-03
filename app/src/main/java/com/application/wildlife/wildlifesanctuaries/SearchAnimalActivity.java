package com.application.wildlife.wildlifesanctuaries;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SearchAnimalActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spinnerAnimal;
    Button btnSearch;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_animal);

        Values values = new Values();
        arrayAdapter = new ArrayAdapter(this, R.layout.listview_item, values.getSpinnerAnimals());

        spinnerAnimal = findViewById(R.id.spinnerAnimal);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerAnimal.setAdapter(arrayAdapter);
        btnSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view == btnSearch) {
            int pos = spinnerAnimal.getSelectedItemPosition();
            Intent intent = new Intent(SearchAnimalActivity.this, AnimalStateActivity.class);
            intent.putExtra("animalPos", pos);
            intent.putExtra("animalName", spinnerAnimal.getItemAtPosition(pos).toString());
            startActivity(intent);
        }
    }
}
