package com.test.notificationdemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "FirebaseInstanceId";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, remoteMessage.getData().get("price"));
        Intent filter = new Intent();
        filter.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        filter.putExtra("price", remoteMessage.getData().get("price"));
        filter.putExtra("time", remoteMessage.getData().get("time"));
        filter.putExtra("name", remoteMessage.getData().get("name"));
        filter.putExtra("code", remoteMessage.getData().get("id"));
        Intent filterApp = new Intent();
        filterApp.setAction("FirebaseDateRe");
        filterApp.putExtra("price", remoteMessage.getData().get("price"));
        filterApp.putExtra("time", remoteMessage.getData().get("time"));
        filterApp.putExtra("name", remoteMessage.getData().get("name"));
        filterApp.putExtra("code", remoteMessage.getData().get("id"));
        sendBroadcast(filter);
        sendBroadcast(filterApp);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


}
