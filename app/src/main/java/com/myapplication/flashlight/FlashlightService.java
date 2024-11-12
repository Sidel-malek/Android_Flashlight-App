package com.myapplication.flashlight;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class FlashlightService extends Service {

    public FlashlightService(){}
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        CameraManager cameraManager = null;
        cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        String cameraId = null;

        try {
            cameraId = cameraManager.getCameraIdList()[0];

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId,true);
            }


            Toast.makeText(this, "Flashlight On", Toast.LENGTH_SHORT).show();


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("torch_channel", "Torch Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            Intent stopIntent = new Intent(this, FlashlightService.class);
            stopIntent.setAction("STOP_FLASHLIGHT");
            PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            @SuppressLint("ResourceAsColor")
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "torch_channel")
                    .setContentTitle("Flashlight is On")
                    .setContentText("Tap stop  to turn off the flashlight")
                    .setSmallIcon(R.drawable.ic_flashlight)
                    .setOngoing(true)
                    .setColor(R.color.purple_200)
                    .addAction(R.drawable.ic_flashlight, "Stop", stopPendingIntent) ;
            Notification notification = builder.build();


            if (intent != null && "STOP_FLASHLIGHT".equals(intent.getAction())) {
                // Action d'arrêt de la lampe de poche
                stopSelf(); // Arrêter le service
                return START_NOT_STICKY;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(110, notification ,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);

            }

            else {startForeground(110, notification);}

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }





    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onDestroy() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        String cameraId = null;
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                cameraManager.setTorchMode(cameraId,false);
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }
        }
        Toast.makeText(this, "Flashlight OFF", Toast.LENGTH_SHORT).show();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1); // Assuming notification ID is 1
        super.onDestroy();
    }



}
