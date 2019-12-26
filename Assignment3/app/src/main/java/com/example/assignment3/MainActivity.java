package com.example.assignment3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private Button saveDataButton;
    private Button showDataButton;
    private Button exportDataButton;

    private TextView accelerometerTextView;
    private TextView gpsTextView;
    private TextView wifiTextView;
    private TextView microphoneTextView;
    private TextView gyroscopeTextView;
    private DatabaseHelper myDB;

    private SensorManager sensorManager;
    private Sensor sensor_accelerator;
    private Sensor sensor_gyroscope;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CHANGE_WIFI_STATE},
                    1);
        }

        accelerometerTextView = (TextView) findViewById(R.id.accelerometer_textview);
        gpsTextView = (TextView) findViewById(R.id.gps_textview);
        wifiTextView = (TextView) findViewById(R.id.wifi_textview);
        microphoneTextView = (TextView) findViewById(R.id.microphone_textview);
        gyroscopeTextView = (TextView) findViewById(R.id.gyroscope_textview);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor_accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensor_accelerator, SensorManager.SENSOR_DELAY_NORMAL);

//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                gpsTextView.setText("");
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                gpsTextView.setText("X: " + latitude + "\n" + "Y: " + longitude);
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);


        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> apList = wifiManager.getScanResults();
        Toast.makeText(MainActivity.this, Integer.toString(apList.size()), Toast.LENGTH_LONG).show();
        wifiTextView.setText("");
        for(int i = 0; i < apList.size(); i++){
            wifiTextView.append(apList.get(i).SSID.concat("\n"));
        }

        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        Timer checkTimer = new Timer();
        checkTimer.scheduleAtFixedRate(new RecorderValueInnerClass(mediaRecorder), 0, 500);
        mediaRecorder.setOutputFile("/dev/null/");
        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }

        exportDataButton = findViewById(R.id.export_data_btn);

        exportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "SensorData.csv";
                String filePath = baseDir + File.separator + fileName;
                FileWriter mFileWriter;
                File f = new File(filePath);

                CSVWriter writer = null;
                if(f.exists() && !f.isDirectory()){
                    try {
                        mFileWriter = new FileWriter(filePath, true);
                        writer = new CSVWriter(mFileWriter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        writer = new CSVWriter(new FileWriter(filePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String[] data = {accelerometerTextView.getText().toString(), gpsTextView.getText().toString(), wifiTextView.getText().toString(), microphoneTextView.getText().toString(), gyroscopeTextView.getText().toString()};
                writer.writeNext(data);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        myDB = new DatabaseHelper(MainActivity.this);

        saveDataButton = findViewById(R.id.save_data_btn);
        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDB.getAllData().getCount() != 0){
                    myDB.updateData("Accelerometer", accelerometerTextView.getText().toString());
                    myDB.updateData("GPS", gpsTextView.getText().toString());
                    myDB.updateData("Wifi", wifiTextView.getText().toString());
                    myDB.updateData("Mic", microphoneTextView.getText().toString());
                    myDB.updateData("Gyroscope", gyroscopeTextView.getText().toString());

                }
                else {
                    myDB.insertData("Accelerometer", accelerometerTextView.getText().toString());
                    myDB.insertData("GPS", gpsTextView.getText().toString());
                    myDB.insertData("Wifi", wifiTextView.getText().toString());
                    myDB.insertData("Mic", microphoneTextView.getText().toString());
                    myDB.insertData("Gyroscope", gyroscopeTextView.getText().toString());
                }

//                else{
//                    myDB.deleteData("Accelerometer");
//                    myDB.deleteData("GPS");
//                    myDB.deleteData("Wifi");
//                    myDB.deleteData("Mic");
//                    myDB.deleteData("Gyroscope");
//                    myDB.insertData("Accelerometer", accelerometerTextView.getText().toString());
//                    myDB.insertData("GPS", gpsTextView.getText().toString());
//                    myDB.insertData("Wifi", wifiTextView.getText().toString());
//                    myDB.insertData("Mic", microphoneTextView.getText().toString());
//                    myDB.insertData("Gyroscope", gyroscopeTextView.getText().toString());
//                }
            }
        });

        showDataButton = findViewById(R.id.show_data_btn);
        showDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = myDB.getAllData().getCount();
                Toast.makeText(MainActivity.this, Integer.toString(count), Toast.LENGTH_LONG).show();
                Cursor cursor = myDB.getAllData();
                String res = "";
                while(cursor.moveToNext()){
                    String sensor = cursor.getString(0);
                    String value = cursor.getString(1);
                    res = res + sensor + " -- " + "\n" + value + "\n\n";
                }

                Intent intent = new Intent(MainActivity.this, DataActivity.class);
                intent.putExtra("Data", res);
                startActivity(intent);
            }
        });
    }

    private class RecorderValueInnerClass extends TimerTask {
        TextView soundTextView = (TextView) findViewById(R.id.microphone_textview);
        private MediaRecorder recorder;

        public RecorderValueInnerClass(MediaRecorder recorder) {
            this.recorder = recorder;
        }

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int amplitude = recorder.getMaxAmplitude();
                    soundTextView.setText("" + ((float) (20 * Math.log10((float) Math.abs(amplitude)))));
                }
            });
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }

                last_x = x;
                last_y = y;
                last_z = z;

                accelerometerTextView.setText("X: " + last_x + "\n" + "Y: " + last_y + "\n" + "Z: " + last_z);
            }
        }
        else if(mySensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyroscopeTextView.setText("X: " + sensorEvent.values[0] + "\nY: " + sensorEvent.values[1] + "\nZ: " + sensorEvent.values[2]);
        }
        else{

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor_accelerator, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensor_gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onLocationChanged(Location location) {
        gpsTextView.setText("");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        gpsTextView.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
