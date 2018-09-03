package com.application.wildlife.wildlifesanctuaries;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class Feedbacks extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    FeedbacksAdapter feedbacksAdapter;
    List<FeedbackModel> feedbackModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        feedbackModelList = databaseHelper.getFeedbacks();
        feedbacksAdapter = new FeedbacksAdapter(this, feedbackModelList);
        feedbacksAdapter.notifyDataSetChanged();

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(feedbacksAdapter);

    }
}
