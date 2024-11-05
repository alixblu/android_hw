package com.example.bttuan7;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView detailIcon = findViewById(R.id.imageView); // Sử dụng findViewById

        detailIcon.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenu().add("Delete Selected");
            popupMenu.getMenu().add("Delete All");
            popupMenu.getMenu().add("Alarm");

            // Thiết lập listener cho các item trong menu
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getTitle().toString()) {
                    case "Delete Selected":
                        return true;
                    case "Delete All":
                        return true;
                    case "Alarm":
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        });

        displayPhotos();
    }


    private void displayPhotos() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<PhotoItem> photoItems = db.getAllPhotos();
        listView = findViewById(R.id.listView);
        ImageAdapter imageAdapter = new ImageAdapter(this, photoItems);
//        listView.setAdapter(imageAdapter);
    }






}
