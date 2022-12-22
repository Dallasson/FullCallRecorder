package com.simple.fullcallrecorder;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.ContactsContract;

public class BaseApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    private void createNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    Extras.NOTIFICATION_ID,
                    Extras.NOTIFICATION_STR,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
         }
    }
}
