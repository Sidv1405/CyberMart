package com.vdsl.cybermart.Notify;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class OreoNotificationService extends Service {

    private static final String CHANNEL_ID ="push_notification_id" ;

    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelNotify();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle tasks here
        return START_NOT_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannelNotify() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"CyberMart", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        manager.createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public Notification.Builder getNotification(String title, String body, PendingIntent pendingIntent, Uri uri,String icon){
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(uri)
                .setAutoCancel(true);
    }
}
