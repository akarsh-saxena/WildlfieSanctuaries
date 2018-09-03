package com.application.wildlife.wildlifesanctuaries;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    //TextView customerName, feedbackDescription, emailID;
    EditText etName, etEmail, etFeedback;
    Button btnSubmit;
    RatingBar feedbackRating;
    DatabaseHelper databaseHelper;
    ImageView ivCamera;
    ImageButton ibCamera;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

//        imageRecyclerView = findViewById(R.id.)

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etFeedback = findViewById(R.id.etFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        feedbackRating = findViewById(R.id.feedbackRating);
        ivCamera = findViewById(R.id.ivCamera);
        ibCamera = findViewById(R.id.ibCamera);


        databaseHelper = new DatabaseHelper(this);

        btnSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isInserted = false;
                        if(uri!=null)
                            isInserted = databaseHelper.insertData(etName.getText().toString(), etEmail.getText().toString(), etFeedback.getText().toString(), feedbackRating.getRating(), uri.getPath());
                        else
                            isInserted = databaseHelper.insertData(etName.getText().toString(), etEmail.getText().toString(), etFeedback.getText().toString(), feedbackRating.getRating(), null);

                        if(isInserted)
                            Toast.makeText(FeedbackActivity.this, "Feedback Submitted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(FeedbackActivity.this, "Try Again!!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        ibCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Camera_Check())
                        {
                            //img.setEnabled(true);
                            Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            uri = MyGeturi("My_Pics","/IMG_",".JPG");
                            in.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                            startActivityForResult(in,007);
                            Log.d("path", uri.getPath());
                        }
                        else
                            Toast.makeText(FeedbackActivity.this, "Sorry Your Device Is not compatible!!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public boolean Camera_Check()
    {
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true;
        else
            return false;
    }

    public Uri MyGeturi(String folder, String name, String ext)
    {
        return Uri.fromFile(getFile(folder,name,ext));
    }

    public File getFile(String folder, String name, String ext)
    {
        File save= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),folder);
        if(!save.exists())
            save.mkdir();
        String timestamp=new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).format(new Date());
        File f2=new File(save.getPath()+ name + timestamp + ext);
        return f2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==007 && resultCode==RESULT_OK)
        {
            Intent in= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f= new File(uri.getPath());
            in.setData(Uri.fromFile(f));
            this.sendBroadcast(in);
            ivCamera.setImageURI(uri);
        }
        else
            Toast.makeText(this, "Cancelled By User!!", Toast.LENGTH_SHORT).show();
    }

}
