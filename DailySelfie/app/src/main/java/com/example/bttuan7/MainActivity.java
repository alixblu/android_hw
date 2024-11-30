package com.example.bttuan7;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bttuan7.DatabaseHelper;
import com.example.bttuan7.ImageAdapter;
import com.example.bttuan7.PhotoItem;
import com.example.bttuan7.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 2;
    private ListView listView; // Change this to ListView
    private DatabaseHelper db;
    private ImageAdapter imageAdapter;
    private List<PhotoItem> photoItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        ImageButton btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(v -> showPhotoOptionsDialog());
        ImageView detailIcon = findViewById(R.id.imageView);
        detailIcon.setOnClickListener(v -> showPopupMenu(v));

        listView = findViewById(R.id.listView);
        imageAdapter = new ImageAdapter(this, photoItems);
        listView.setAdapter(imageAdapter);
        displayPhotos();
        createNotificationChannel();
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.getMenu().add("Delete Selected");
        popupMenu.getMenu().add("Delete All");
        popupMenu.getMenu().add("Set Alarm");
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Delete Selected":
                    deleteSelectedPhotos();
                    return true;
                case "Delete All":
                    db.deleteAllPhotos();
                    displayPhotos();
                    Toast.makeText(MainActivity.this, "All photos deleted", Toast.LENGTH_SHORT).show();
                    return true;
                case "Set Alarm":
                    showTimePickerDialog();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                (view, hourOfDay, minuteOfHour) -> {
                    setDailyReminder(hourOfDay, minuteOfHour);
                    Toast.makeText(MainActivity.this, "Daily alarm set for " + hourOfDay + ":" + minuteOfHour, Toast.LENGTH_SHORT).show();
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private void setDailyReminder(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Log.d("AlarmReceiver", "Alarm set for: " + calendar.getTime().toString());

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }

    }
    // In your MainActivity or on the first run of your app:
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Photo Notifications";
            String description = "Notifications related to photo reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("photo_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    private void deleteSelectedPhotos() {
        List<PhotoItem> selectedPhotos = new ArrayList<>();
        for (PhotoItem photo : photoItems) {
            if (photo.isSelected()) {
                selectedPhotos.add(photo);
            }
        }

        for (PhotoItem selectedPhoto : selectedPhotos) {
            db.deletePhotoById(selectedPhoto.getId());
        }

        displayPhotos();
        Toast.makeText(MainActivity.this, "Selected photos deleted", Toast.LENGTH_SHORT).show();
    }

    private void showPhotoOptionsDialog() {
        if (checkPermission(Manifest.permission.CAMERA, REQUEST_CAMERA)) {
            openCamera();
        }
    }

    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] photoBytes = stream.toByteArray();
                db.insertPhoto(photoBytes, timestamp);
                displayPhotos();
            }
        }
    }

    private void displayPhotos() {
        photoItems.clear();
        photoItems.addAll(db.getAllPhotos());
        imageAdapter.notifyDataSetChanged();
    }

}
