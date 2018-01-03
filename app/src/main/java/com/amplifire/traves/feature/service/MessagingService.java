package com.amplifire.traves.feature.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.amplifire.traves.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "BerrykitchenMsgService";
    private Bitmap mImageBitmap;
    private NotificationCompat.Builder mNotificationCompatBuilder;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        int icon = R.drawable.ic_small;
        String tag = remoteMessage.getNotification().getTag();
        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        Map<String, String> data = remoteMessage.getData();
        sendPushNotification(tag,
                title,
                message,
                icon);

    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param message FCM message body received.
     */
    private void sendNotification(RemoteMessage message) {
        NotificationCompat.Builder notificationBuilder;
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(message.getData().get("deep_link")));
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mainIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        int notificationId = new Random().nextInt();
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(notificationId, this);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500, 500, 500, 500, 500};
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(icon)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(pattern)
                    .setWhen(getTimeMilliSec(getTimeStamp()))
//                    .setSmallIcon(icon)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), icon))
                    .setLights(Color.BLUE, 500, 500)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(message.getData().get("title"))
                    .setContentText(message.getData().get("body"));
//                    .addAction(R.drawable.ic_check, message.getData().get("button_ok_text"), mainIntent)
//                    .addAction(R.drawable.ic_cancel, message.getData().get("button_dismiss_text"), dismissIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(icons)
                    .setColor(ContextCompat.getColor(this, R.color.green))
                    .setAutoCancel(true)
                    .setWhen(getTimeMilliSec(getTimeStamp()))
                    .setLights(Color.BLUE, 500, 500)
                    .setVibrate(pattern)
                    .setSound(defaultSoundUri)
//                    .setSmallIcon(icons)
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), icons))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(message.getData().get("title"))
                    .setContentText(message.getData().get("body"));
//                    .addAction(R.drawable.ic_check, message.getData().get("button_ok_text"), mainIntent)
//                    .addAction(R.drawable.ic_cancel, message.getData().get("button_dismiss_text"), dismissIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendPushNotification(
            final String tag,
            final String title,
            final String message,
            final int icon
    ) {
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
//        notificationIntent.setData(Uri.parse(deep_link));
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mainIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(0, this);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500, 500, 500, 500, 500};

        mNotificationCompatBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setWhen(getTimeMilliSec(getTimeStamp()))
                .setVibrate(pattern)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
//                .addAction(R.drawable.ic_check, ok_button, mainIntent)
                .addAction(R.drawable.ic_cancel, "close", dismissIntent);

        mNotificationCompatBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotificationCompatBuilder.build());
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        return ts;
    }

}