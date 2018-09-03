package com.application.wildlife.wildlifesanctuaries;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.application.wildlife.wildlifesanctuaries.R;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;
    VideoView splashVideoView;
    String uriPath = "android.resource://com.application.akarsh.wildlifesanctuaries/" + R.raw.wildlife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashVideoView = findViewById(R.id.splashVideoView);

        Uri uri = Uri.parse(uriPath);
        splashVideoView.setVideoURI(uri);
        splashVideoView.start();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, SearchActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
