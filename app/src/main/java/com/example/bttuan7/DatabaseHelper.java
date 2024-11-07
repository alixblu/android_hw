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

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_PHOTOS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_IMAGE_NAME + " BLOB, " +
            COLUMN_CAPTURE_DATE + " TEXT" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void deleteAllPhotos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTOS, null, null);  // This deletes all rows in the table
        db.close();
    }

    public boolean isPhotoTakenToday(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PHOTOS + " WHERE " + COLUMN_CAPTURE_DATE + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{date + "%"}); // Match date only (ignore time part)
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0; // Return true if there's at least one photo taken today
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }
    public void deletePhotoById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTOS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
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
        Cursor cursor = db.query(TABLE_PHOTOS,
                new String[]{COLUMN_ID, COLUMN_IMAGE_NAME, COLUMN_CAPTURE_DATE},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_NAME));
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                String captureDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAPTURE_DATE));
                photoList.add(new PhotoItem(id, bitmap, captureDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return photoList;
    }
}
