package com.example.iot;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class Chanel extends Application {
    public static final String CHANEL_1_ID="CHANEL_1";
    public static final String CHANEL_2_ID="CHANEL_2";

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            NotificationChannel chanel1 = new NotificationChannel(CHANEL_1_ID, "chanel1", NotificationManager.IMPORTANCE_HIGH);
            chanel1.enableLights(true);
            chanel1.setLightColor(Color.RED);
            chanel1.enableVibration(true);
            chanel1.setDescription("Ovo su neuspesno obavljene operacije nad bazom");

            NotificationChannel chanel2 = new NotificationChannel(CHANEL_2_ID, "chanel2", NotificationManager.IMPORTANCE_HIGH);
            chanel1.enableLights(true);
            chanel1.setLightColor(Color.GREEN);
            chanel1.enableVibration(true);
            chanel1.setDescription("Ovo su uspesno obavljene operacije nad bazom");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(chanel1);
            manager.createNotificationChannel(chanel2);
        }
    }
}
