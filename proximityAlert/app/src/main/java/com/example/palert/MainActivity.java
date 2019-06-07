package com.example.palert;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
    LocationManager locManager;
    AlertReceiver receiver;
    TextView locationText;
    PendingIntent proximityIntent;
    boolean isPermitted = false;
    boolean isLocRequested = false;
    boolean isAlertRegistered = false;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    double lat = 0;
    double lng = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationText = findViewById(R.id.location);

        requestRuntimePermission();
    }

    private void requestRuntimePermission() {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // ACCESS_FINE_LOCATION 권한이 있는 것
            isPermitted = true;
        }
        //*********************************************************************
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // read_external_storage-related task you need to do.

                    // ACCESS_FINE_LOCATION 권한을 얻음
                    isPermitted = true;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // 권한을 얻지 못 하였으므로 location 요청 작업을 수행할 수 없다
                    // 적절히 대처한다
                    isPermitted = false;

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.getLocation) {
            try {
                if(isPermitted) {
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, this);
                    isLocRequested = true;

                }
                else
                    Toast.makeText(this, "Permission이 없습니다..", Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.alert) {
            // 근접 경보를 받을 브로드캐스트 리시버 객체 생성 및 등록

            receiver = new AlertReceiver();
            IntentFilter filter = new IntentFilter("com.example.palert");
            registerReceiver(receiver, filter);

            // ProximityAlert 등록을 위한 PendingIntent 객체 얻기
            Intent intent = new Intent("com.example.palert");
            proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            try {
                // 근접 경보 등록 메소드
                // void addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent intent)

                locManager.addProximityAlert(lat, lng, 20, -1, proximityIntent);
                locManager.addProximityAlert(36.7,127.1,30,-1,proximityIntent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            isAlertRegistered = true;
        }else if(view.getId() == R.id.alert_release){
            // 자원 사용 해제
            try {
                if(isAlertRegistered) {
                    locManager.removeProximityAlert(proximityIntent);
                    unregisterReceiver(receiver);
                }
                Toast.makeText(getApplicationContext(),"근접 경보 해제 되었습니다.",Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        // 자원 사용 해제
        try {
            if(isLocRequested) {
                locManager.removeUpdates(this);
                isLocRequested = false;
            }
            if(isAlertRegistered) {
                locManager.removeProximityAlert(proximityIntent);
                unregisterReceiver(receiver);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        locationText.setText("위도 : " + location.getLatitude()
                + " 경도 : " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}