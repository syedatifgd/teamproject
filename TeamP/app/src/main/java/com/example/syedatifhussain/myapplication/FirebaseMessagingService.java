package com.example.syedatifhussain.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by SyedAtif on 5/28/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.projectsicon2)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationBody))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        mBuilder.setVibrate(pattern);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        Intent intent = new Intent(this, MeetingPanel.class);
        intent.putExtra("orgid",click_action);
        intent.putExtra("projectid",notificationTitle);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationID = (int)System.currentTimeMillis();

        NotificationManager mNotifyMgr =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationID,mBuilder.build());



    }
}
