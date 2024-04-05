package com.vdsl.cybermart.Notify;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.play.integrity.internal.b;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vdsl.cybermart.Message.FragmentMessage;
import com.vdsl.cybermart.Message.MessageActivity;
import com.vdsl.cybermart.R;

import java.util.HashMap;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    FirebaseUser firebaseUser;

    String senderEmail;

    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String senderEmail = remoteMessage.getData().get("email");
        Log.e(TAG, "onMessageReceived: " + senderEmail );


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

            // Handle notification here, for example, show notification.
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            sendFloatNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),senderEmail);

            receiveNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void receiveNotification(String title, String body) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference notificationsRef = database.getReference("Notification");

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", firebaseUser.getUid());
        map.put("title", title);
        map.put("body", body);


        notificationsRef.push().setValue(map);

        getIdFromEmail(firebaseUser.getEmail(), new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                final DatabaseReference notifyRef = FirebaseDatabase.getInstance().getReference("notificationUser")
                        .child(id)
                        .child(title);

                notifyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            notifyRef.child("message").setValue(body);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);


        String channelId = "CyberMart";
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.thongbao);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.img_logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*NotificationChannel channel = new NotificationChannel("channel_id",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);*/
            CharSequence channelName = "CyberNotify";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setSound(soundUri, new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build());

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendFloatNotification(String title, String messageBody,String email) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // Yêu cầu quyền SYSTEM_ALERT_WINDOW nếu chưa được cấp
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Thêm cờ FLAG_ACTIVITY_NEW_TASK vào Intent
            startActivity(intent);
            return;
        }

        String channelId = "my_channel_id_01";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
                // Cấu hình kênh thông báo
                channel.setDescription("[Channel description]");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, MessageActivity.class);
        notificationIntent.putExtra("userEmail",email);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.img_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.img_logo))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.img_logo))
                        .bigLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.img_logo)))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setAutoCancel(false) // true khi chạm vào thông báo sẽ được bỏ qua, nhưng vuốt để bỏ qua
                .setTicker("Notification");


        builder.setContentIntent(contentIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        // id để tạo thông báo mới trong danh sách menu thông báo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(new Random().nextInt(), builder.build());
    }


    private void getIdFromEmail(String email, final FragmentMessage.OnIdReceivedListener listener) {
        DatabaseReference peopleReference = FirebaseDatabase.getInstance().getReference().child("Account");
        peopleReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String accountId = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        accountId = dataSnapshot1.getKey();
                        Log.d("TAG", "onDataChange: sdone" + accountId);
                        break;
                    }
                }
                listener.onIdReceived(accountId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onIdReceived(null);
            }
        });
    }

    public interface OnIdReceivedListener {
        void onIdReceived(String id);
    }

}
