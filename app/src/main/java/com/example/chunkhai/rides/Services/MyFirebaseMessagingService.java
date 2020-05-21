package com.example.chunkhai.rides.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.chunkhai.rides.NotificationActivity;
import com.example.chunkhai.rides.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Intent intent;

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        if(remoteMessage.getData().isEmpty()) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());


        }
        else{
            showNotification(remoteMessage.getData());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void showNotification(Map<String,String> data) {
        String title = data.get("title").toString();
        String body = data.get("body").toString();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.chunkhai.rides";

        Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification_ringtone);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            notificationChannel.setDescription("RideS");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.setSound(defaultSoundUri, attributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

    }

    public void showNotification(String title, String body){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "com.example.chunkhai.rides";
        Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification_ringtone);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            notificationChannel.setDescription("RideS");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.setSound(defaultSoundUri, attributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }
}
