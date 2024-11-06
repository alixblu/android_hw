package com.example.bttuan7;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Add this import to resolve 'CAMERA' and 'READ_EXTERNAL_STORAGE'
import android.Manifest;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 2;
    private View listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(v -> showPhotoOptionsDialog());

        ImageView detailIcon = findViewById(R.id.imageView);
        detailIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenu().add("Delete Selected");
            popupMenu.getMenu().add("Delete All");
            popupMenu.getMenu().add("Alarm");

            // Set listener for menu items
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

    // Method to show dialog with photo options
    private void showPhotoOptionsDialog() {
        if (checkPermission(Manifest.permission.CAMERA, REQUEST_CAMERA)) {
            openCamera();
        }
    }

    // Check and request permission if needed
    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
    }

    // Open the camera to take a photo
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CAMERA) {
                openCamera();
            }
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is required to take a photo. Please enable it in Settings.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayPhotos() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<PhotoItem> photoItems = db.getAllPhotos();
        listView = findViewById(R.id.listView);
        ImageAdapter imageAdapter = new ImageAdapter(this, photoItems);
    }
}



