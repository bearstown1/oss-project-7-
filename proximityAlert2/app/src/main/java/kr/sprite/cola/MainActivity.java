package kr.sprite.cola;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {



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

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // ACCESS_FINE_LOCATION 권한을 얻음
                    isPermitted = true;

                } else {


                    // 권한을 얻지 못 하였으므로 location 요청 작업을 수행할 수 없다
                    // 적절히 대처한다
                    isPermitted = false;

                }
                return;
            }


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
            // 알림을 받을 브로드캐스트 리시버 객체 생성 및 등록
            Toast.makeText(this,"알림이 설정되었습니다",Toast.LENGTH_LONG).show();
            receiver = new AlertReceiver();
            IntentFilter filter = new IntentFilter(" kr.sprite.cola.locationAlert");
            registerReceiver(receiver, filter);

            // ProximityAlert 등록을 위한 PendingIntent 객체 얻기
            Intent intent = new Intent(" kr.sprite.cola.locationAlert");
            proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            try {
                // 알림 등록 메소드
                // void addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent intent)
                //성범죄자 주거지역 샘플들

                locManager.addProximityAlert(lat, lng, 20, -1, proximityIntent);
                locManager.addProximityAlert(36.8099687,127.076489,100,-1,proximityIntent);
                locManager.addProximityAlert(36.8384015,127.085486,100,-1,proximityIntent);
                locManager.addProximityAlert(36.8779344,127.017400,100,-1,proximityIntent);
                locManager.addProximityAlert(36.7731320,127.059349,100,-1,proximityIntent);
                locManager.addProximityAlert(36.7822019,126.992230,100,-1,proximityIntent);
                locManager.addProximityAlert(36.7807873,126.912610,100,-1,proximityIntent);
                locManager.addProximityAlert(36.7466563,127.008866,100,-1,proximityIntent);
                locManager.addProximityAlert(36.8636366,126.880491,100,-1,proximityIntent);
                locManager.addProximityAlert(36.7664218,127.070511,100,-1,proximityIntent);
                locManager.addProximityAlert(36.9306703,127.039321,100,-1,proximityIntent);
                locManager.addProximityAlert(36.8064267,127.077640,100,-1,proximityIntent);
                locManager.addProximityAlert(36.7754528,127.008484,100,-1,proximityIntent);


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
                Toast.makeText(getApplicationContext(),"알림이 해제 되었습니다.",Toast.LENGTH_SHORT).show();
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