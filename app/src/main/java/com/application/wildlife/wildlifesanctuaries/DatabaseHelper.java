package com.application.wildlife.wildlifesanctuaries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "feedback.db";
    public static final String TABLE_NAME = "feedbacks";
    public static final String COLUMN_NAME_USERNAME = "name";
    public static final String COLUMN_NAME_EMAIL = "email";
    public static final String COLUMN_NAME_FEEDBACKS = "col_feedbacks";
    public static final String COLUMN_NAME_RATINGS = "ratings";
    public static final String COLUMN_IMAGE_PATHS = "image_paths";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE "+TABLE_NAME+" ("+COLUMN_NAME_USERNAME+" VARCHAR(20), "+COLUMN_NAME_EMAIL+" VARCHAR(30), "+COLUMN_NAME_FEEDBACKS+" VARCHAR(100), "+COLUMN_IMAGE_PATHS+" VARCHAR(150), "+COLUMN_NAME_RATINGS+" DECIMAL(2,1));";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

    public boolean insertData(String name, String email, String feedback, float rating, String image_path) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME_USERNAME, name);
        contentValues.put(COLUMN_NAME_EMAIL, email);
        contentValues.put(COLUMN_NAME_FEEDBACKS, feedback);
        contentValues.put(COLUMN_NAME_RATINGS, rating);
        contentValues.put(COLUMN_IMAGE_PATHS, image_path);

        long l1 = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if(l1 > 0)
            return true;
        else
            return false;
    }

    public List<FeedbackModel> getFeedbacks() {

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<FeedbackModel> feedbackModels = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_NAME+";";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_USERNAME));
            String email = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EMAIL));
            String feedback = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FEEDBACKS));
            String image_path = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATHS));
            float rating = cursor.getFloat(cursor.getColumnIndex(COLUMN_NAME_RATINGS));
            FeedbackModel feedbackModel = new FeedbackModel();
            feedbackModel.setName(name);
            feedbackModel.setFeedback(feedback);
            feedbackModel.setRating(rating);
            feedbackModel.setImagePath(image_path);
            feedbackModel.setEmail(email);
            feedbackModels.add(feedbackModel);
        }
        cursor.close();
        return feedbackModels;

    }

}