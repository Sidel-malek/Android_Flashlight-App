package com.myapplication.flashlight;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button OnFlash , OffFlash;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximitySensorListner ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OnFlash = findViewById(R.id.flashOn);
        OffFlash = findViewById(R.id.flashOff);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            Toast.makeText(this, "Proximity Sensor is not available !", Toast.LENGTH_SHORT).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                proximitySensorListner = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        if (sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                            startService(new Intent(getApplicationContext(),
                                    FlashlightService.class));
                        } else {
                            stopService(new Intent(getApplicationContext(),
                                    FlashlightService.class));
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {

                    }
                };
                sensorManager.registerListener(proximitySensorListner, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        OnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(getApplicationContext(),
                        FlashlightService.class));
            }
        });

        OffFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getApplicationContext(),
                        FlashlightService.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proximitySensorListner != null) {
            sensorManager.unregisterListener(proximitySensorListner);
        }
    }
}
