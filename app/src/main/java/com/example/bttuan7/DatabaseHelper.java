package com.example.bttuan7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "photos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PHOTOS = "photos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMAGE_NAME = "image_name";
    public static final String COLUMN_CAPTURE_DATE = "capture_date";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_PHOTOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_IMAGE_NAME + " TEXT, " +
                    COLUMN_CAPTURE_DATE + " TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }








    public void insertPhoto(byte[] photo, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_NAME, photo);
        values.put(COLUMN_CAPTURE_DATE, timestamp);
        db.insert(TABLE_PHOTOS, null, values);
        db.close();
    }



    public List<PhotoItem> getAllPhotos() {
        List<PhotoItem> photoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_PHOTOS,
                new String[]{DatabaseHelper.COLUMN_IMAGE_NAME, DatabaseHelper.COLUMN_CAPTURE_DATE},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Integer id =  cursor.getInt(0);
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("photo"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                String captureDate = cursor.getString(2);
                photoList.add(new PhotoItem(id, bitmap, captureDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return photoList;
    }




}