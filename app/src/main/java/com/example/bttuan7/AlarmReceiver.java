package com.example.bttuan7;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the current date in the format you store the date in
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Create a DatabaseHelper instance
        DatabaseHelper db = new DatabaseHelper(context);

        // Check if a photo is taken today
        if (!db.isPhotoTakenToday(currentDate)) {
            // If no photo is taken today, send the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = new NotificationCompat.Builder(context, "photo_channel")
                    .setContentTitle("Take a Photo!")
                    .setContentText("It's time to capture a photo.")
                    .setSmallIcon(R.drawable.img_2)  // Your notification icon
                    .build();

            notificationManager.notify(1, notification);
        }
    }
}


