package com.example.offender_location5;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

public class MenuActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener, LocationListener {

    private static final String LOG_TAG = "MenuActivity";

    private MapView mMapView;


    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

    private static final MapPoint CUSTOM_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.537229, 127.005515);
    private static final MapPoint CUSTOM_MARKER_POINT2 = MapPoint.mapPointWithGeoCoord(37.447229, 127.015515);
    private static final String Daum_API_KEY = "0f6c455c8ff6fa7882f541e379300830";
    private MapPOIItem mCustomMarker;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        mMapView = (MapView) findViewById(R.id.map_view);
        //mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setCurrentLocationEventListener(this);
        locManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE);
        requestRuntimePermission();
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }
        mMapView.setMapViewEventListener ( this );

    }

    private void requestRuntimePermission() {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(MenuActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {



                ActivityCompat.requestPermissions(MenuActivity.this,
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
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }




    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(MenuActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MenuActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MenuActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MenuActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MenuActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MenuActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }



    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
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
            IntentFilter filter = new IntentFilter(" com.example.offender_location5.locationAlert");
            registerReceiver(receiver, filter);

            // ProximityAlert 등록을 위한 PendingIntent 객체 얻기
            Intent intent = new Intent(" com.example.offender_location5.locationAlert");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {


        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("강병훈");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8099687, 127.076489));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);

//////////////////////////////////////
        MapPOIItem marker1 = new MapPOIItem();
        marker1.setItemName("박상혁");
        marker1.setTag(0);
        marker1.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8384015, 127.085486));
        marker1.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker1.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker1);
        //////////////////////////////////////
        MapPOIItem marker2 = new MapPOIItem();
        marker2.setItemName("신태현");
        marker2.setTag(0);
        marker2.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8779344, 127.017400));
        marker2.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker2.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker2);
        //////////////////////////////////////
        MapPOIItem marker3 = new MapPOIItem();
        marker3.setItemName("윤종현");
        marker3.setTag(0);
        marker3.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7731320, 127.059349));
        marker3.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker3.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker3);
        //////////////////////////////////////
        MapPOIItem marker4 = new MapPOIItem();
        marker4.setItemName("이용배");
        marker4.setTag(0);
        marker4.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7822019, 126.992230));
        marker4.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker4.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker4);
        //////////////////////////////////////
        MapPOIItem marker5 = new MapPOIItem();
        marker5.setItemName("이용석");
        marker5.setTag(0);
        marker5.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7807873, 127.008866));
        marker5.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker5.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker5);
        //////////////////////////////////////
        MapPOIItem marker6 = new MapPOIItem();
        marker6.setItemName("임광세");
        marker6.setTag(0);
        marker6.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7466563,126.912610 ));
        marker6.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker6.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker6);
        //////////////////////////////////////
        MapPOIItem marker7 = new MapPOIItem();
        marker7.setItemName("임상윤");
        marker7.setTag(0);
        marker7.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8636366, 126.88049));
        marker7.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker7.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker7);

        ///////////////////////////////////
        MapPOIItem marker8 = new MapPOIItem();
        marker8.setItemName("전무근");
        marker8.setTag(0);
        marker8.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7664218, 127.070511)
        );
        marker8.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker8.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker8);

        //////////////////////////////////////
        MapPOIItem marker9 = new MapPOIItem();
        marker9.setItemName("조성기");
        marker9.setTag(0);
        marker9.setMapPoint(MapPoint.mapPointWithGeoCoord(36.9306703,127.039321));
        marker9.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker9.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker9);
        MapPOIItem marker10 = new MapPOIItem();
        marker10.setItemName("조영남");
        marker10.setTag(0);
        marker10.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8064267,127.077640));
        marker10.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker10.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker9);

//////////////////////////////////////
        MapPOIItem marker11 = new MapPOIItem();
        marker11.setItemName("지상현");
        marker11.setTag(0);
        marker11.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7754528, 127.008484));
        marker11.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker11.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker11);
        //////////////////////////////////////
        MapPOIItem marker12 = new MapPOIItem();
        marker12.setItemName("최병인");
        marker12.setTag(0);
        marker12.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7762009, 127.003981));
        marker12.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker12.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker12);
        //////////////////////////////////////
        MapPOIItem marker13 = new MapPOIItem();
        marker13.setItemName("최정훈");
        marker13.setTag(0);
        marker13.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7727882, 127.062183));
        marker13.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker13.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker3);
        //////////////////////////////////////
        MapPOIItem marker14 = new MapPOIItem();
        marker14.setItemName("홍무선");
        marker14.setTag(0);
        marker14.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8040862, 127.032086));
        marker14.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker14.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker14);
        //////////////////////////////////////
        MapPOIItem marker15 = new MapPOIItem();
        marker15.setItemName("황경식");
        marker15.setTag(0);
        marker15.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7901665, 126.951134));
        marker15.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker15.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker15);
        //////////////////////////////////////
        MapPOIItem marker16 = new MapPOIItem();
        marker16.setItemName("고민용");
        marker16.setTag(0);
        marker16.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8126949, 127.133132));
        marker16.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker16.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker16);
        //////////////////////////////////////
        MapPOIItem marker17 = new MapPOIItem();
        marker17.setItemName("김동현");
        marker17.setTag(0);
        marker17.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7535067, 127.127879));
        marker17.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker17.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker17);
        //////////////////////////////////////
        MapPOIItem marker18 = new MapPOIItem();
        marker18.setItemName("김상선");
        marker18.setTag(0);
        marker18.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7889610, 127.174399));
        marker18.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker18.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker18);

        //////////////////////////////////////
        MapPOIItem marker19 = new MapPOIItem();
        marker19.setItemName("김성태");
        marker19.setTag(0);
        marker19.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7892605, 127.130033));
        marker19.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker19.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker19);
        MapPOIItem marker20 = new MapPOIItem();
        marker20.setItemName("김재천");
        marker20.setTag(0);
        marker20.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7999855, 127.151531));
        marker20.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker20.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker20);

//////////////////////////////////////
        MapPOIItem marker21 = new MapPOIItem();
        marker21.setItemName("김진혁");
        marker21.setTag(0);
        marker21.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8178191, 127.157955));
        marker21.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker21.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker21);
        //////////////////////////////////////
        MapPOIItem marker22 = new MapPOIItem();
        marker22.setItemName("김철구");
        marker22.setTag(0);
        marker22.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8000308, 127.144069));
        marker22.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker22.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker22);
        //////////////////////////////////////
        MapPOIItem marker23 = new MapPOIItem();
        marker23.setItemName("김태성");
        marker23.setTag(0);
        marker23.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7735815, 127.137826));
        marker23.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker23.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker23);
        //////////////////////////////////////
        MapPOIItem marker24 = new MapPOIItem();
        marker24.setItemName(" 김한정");
        marker24.setTag(0);
        marker24.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7591279, 127.178699));
        marker24.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker24.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker24);
        //////////////////////////////////////
        MapPOIItem marker25 = new MapPOIItem();
        marker25.setItemName("노상광");
        marker25.setTag(0);
        marker25.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8131449, 127.133592));
        marker25.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker25.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker25);
        //////////////////////////////////////
        MapPOIItem marker26 = new MapPOIItem();
        marker26.setItemName("박민우");
        marker26.setTag(0);
        marker26.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8018941, 127.159850));
        marker26.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker26.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker26);
        //////////////////////////////////////
        MapPOIItem marker27 = new MapPOIItem();
        marker27.setItemName("박종호");
        marker27.setTag(0);
        marker27.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7927876, 127.163797));
        marker27.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker27.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker27);
        //////////////////////////////////////
        MapPOIItem marker28 = new MapPOIItem();
        marker28.setItemName("박훈");
        marker28.setTag(0);
        marker28.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8636366, 127.141605));
        marker28.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker28.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker28);

        //////////////////////////////////////
        MapPOIItem marker29 = new MapPOIItem();
        marker29.setItemName("서요한");
        marker29.setTag(0);
        marker29.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7723068, 127.193101));
        marker29.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker29.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker29);
        MapPOIItem marker30 = new MapPOIItem();
        marker30.setItemName("소동욱");
        marker30.setTag(0);
        marker30.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8025959, 127.146539));
        marker30.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker30.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker30);

//////////////////////////////////////
        MapPOIItem marker31 = new MapPOIItem();
        marker31.setItemName("손덕인");
        marker31.setTag(0);
        marker31.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7603887, 127.166674));
        marker31.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker31.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker31);
        //////////////////////////////////////
        MapPOIItem marker32 = new MapPOIItem();
        marker32.setItemName("심현택");
        marker32.setTag(0);
        marker32.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8084524, 127.161118));
        marker32.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker32.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker32);
        //////////////////////////////////////
        MapPOIItem marker33 = new MapPOIItem();
        marker33.setItemName("안철호");
        marker33.setTag(0);
        marker33.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8122328, 127.150502));
        marker33.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker33.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker33);
        //////////////////////////////////////
        MapPOIItem marker34 = new MapPOIItem();
        marker34.setItemName("오광훈");
        marker34.setTag(0);
        marker34.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8109835, 127.132513));
        marker34.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker34.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker34);
        //////////////////////////////////////
        MapPOIItem marker35 = new MapPOIItem();
        marker35.setItemName("오흥원");
        marker35.setTag(0);
        marker35.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7723068, 127.193101));
        marker35.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker35.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker35);
        //////////////////////////////////////
        MapPOIItem marker36 = new MapPOIItem();
        marker36.setItemName("유철태, 이상훈");
        marker36.setTag(0);
        marker36.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7723068, 127.193101));
        marker36.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker36.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker36);
        //////////////////////////////////////
        MapPOIItem marker37 = new MapPOIItem();
        marker37.setItemName("이상훈");
        marker37.setTag(0);
        marker37.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7947046, 127.144586));
        marker37.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker37.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker37);
        //////////////////////////////////////
        MapPOIItem marker38 = new MapPOIItem();
        marker38.setItemName("임도영");
        marker38.setTag(0);
        marker38.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8160308, 127.173933));
        marker38.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker38.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker38);

        //////////////////////////////////////
        MapPOIItem marker39 = new MapPOIItem();
        marker39.setItemName("장수열");
        marker39.setTag(0);
        marker39.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7698523, 127.269517));
        marker39.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker39.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker39);
        MapPOIItem marker40 = new MapPOIItem();
        marker40.setItemName("장원대");
        marker40.setTag(0);
        marker40.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8138209, 127.133470));
        marker40.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker40.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker40);

//////////////////////////////////////
        MapPOIItem marker41 = new MapPOIItem();
        marker41.setItemName("정략관");
        marker41.setTag(0);
        marker41.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7764098, 127.212669));
        marker41.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker41.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker1);
        //////////////////////////////////////
        MapPOIItem marker42 = new MapPOIItem();
        marker42.setItemName("정재우");
        marker42.setTag(0);
        marker42.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7803686, 127.136314));
        marker42.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker42.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker42);
        //////////////////////////////////////
        MapPOIItem marker43 = new MapPOIItem();
        marker43.setItemName("지재근");
        marker43.setTag(0);
        marker43.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8030373, 127.160715));
        marker43.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker43.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker3);
        //////////////////////////////////////
        MapPOIItem marker44 = new MapPOIItem();
        marker44.setItemName("김신관");
        marker44.setTag(0);
        marker44.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8330755, 127.143032));
        marker44.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker44.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker44);
        //////////////////////////////////////
        MapPOIItem marker45 = new MapPOIItem();
        marker45.setItemName("김재철");
        marker45.setTag(0);
        marker45.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8353234, 127.131500));
        marker45.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker45.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker45);
        //////////////////////////////////////
        MapPOIItem marker46 = new MapPOIItem();
        marker46.setItemName("김진겸");
        marker46.setTag(0);
        marker46.setMapPoint(MapPoint.mapPointWithGeoCoord(127.217482, 36.8666826));
        marker46.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker46.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker46);
        //////////////////////////////////////
        MapPOIItem marker47 = new MapPOIItem();
        marker47.setItemName("박세진");
        marker47.setTag(0);
        marker47.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8308459, 127.138611));
        marker47.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker47.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker7);
        //////////////////////////////////////
        MapPOIItem marker48 = new MapPOIItem();
        marker48.setItemName("소재규");
        marker48.setTag(0);
        marker48.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8271137, 127.132047));
        marker48.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker48.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker48);

        //////////////////////////////////////
        MapPOIItem marker49 = new MapPOIItem();
        marker49.setItemName("신재성");
        marker49.setTag(0);
        marker49.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7966739, 127.132499));
        marker49.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker49.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker49);
        MapPOIItem marker50 = new MapPOIItem();
        marker50.setItemName("안경호");
        marker50.setTag(0);
        marker50.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8557432, 127.185403));
        marker50.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker50.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker50);

//////////////////////////////////////
        MapPOIItem marker51 = new MapPOIItem();
        marker51.setItemName("안동현");
        marker51.setTag(0);
        marker51.setMapPoint(MapPoint.mapPointWithGeoCoord(36.7944964, 127.120978));
        marker51.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker51.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker51);
        //////////////////////////////////////
        MapPOIItem marker52 = new MapPOIItem();
        marker52.setItemName("양규상");
        marker52.setTag(0);
        marker52.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8245868, 127.143285));
        marker52.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker52.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker52);
        //////////////////////////////////////
        MapPOIItem marker53 = new MapPOIItem();
        marker53.setItemName("양준영");
        marker53.setTag(0);
        marker53.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8265533, 127.141484));
        marker53.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker53.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker53);
        //////////////////////////////////////
        MapPOIItem marker54 = new MapPOIItem();
        marker54.setItemName("유주환");
        marker54.setTag(0);
        marker54.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8546353, 127.185120));
        marker54.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker54.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker54);
        //////////////////////////////////////
        MapPOIItem marker55 = new MapPOIItem();
        marker55.setItemName("윤주완");
        marker55.setTag(0);
        marker55.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8369889, 127.140819));
        marker55.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker55.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker55);
        //////////////////////////////////////
        MapPOIItem marker56 = new MapPOIItem();
        marker56.setItemName("이규민");
        marker56.setTag(0);
        marker56.setMapPoint(MapPoint.mapPointWithGeoCoord(127.124733, 36.8027106));
        marker56.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker56.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker56);
        //////////////////////////////////////
        MapPOIItem marker57 = new MapPOIItem();
        marker57.setItemName("이원식");
        marker57.setTag(0);
        marker57.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8267005, 127.138962));
        marker57.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker57.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker57);
        //////////////////////////////////////
        MapPOIItem marker58 = new MapPOIItem();
        marker58.setItemName("이종영");
        marker58.setTag(0);
        marker58.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8891166, 127.116268));
        marker58.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker58.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker58);

        //////////////////////////////////////
        MapPOIItem marker59 = new MapPOIItem();
        marker59.setItemName("임규석");
        marker59.setTag(0);
        marker59.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8316973, 127.134767));
        marker59.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker59.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker59);
        MapPOIItem marker60 = new MapPOIItem();
        marker60.setItemName("임준식");
        marker60.setTag(0);
        marker60.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8316973, 127.134767));
        marker60.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker60.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker60);

//////////////////////////////////////
        MapPOIItem marker61 = new MapPOIItem();
        marker61.setItemName("전재현");
        marker61.setTag(0);
        marker61.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8190098, 127.134802));
        marker61.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker61.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker61);
        //////////////////////////////////////
        MapPOIItem marker62 = new MapPOIItem();
        marker62.setItemName("정범진");
        marker62.setTag(0);
        marker62.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8698210, 127.119480));
        marker62.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker62.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker62);
        //////////////////////////////////////
        MapPOIItem marker63 = new MapPOIItem();
        marker63.setItemName("조주현");
        marker63.setTag(0);
        marker63.setMapPoint(MapPoint.mapPointWithGeoCoord(127.167044, 36.8956346));
        marker63.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker63.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker63);
        //////////////////////////////////////
        MapPOIItem marker64 = new MapPOIItem();
        marker64.setItemName("최범수");
        marker64.setTag(0);
        marker64.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8210215, 127.140723));
        marker64.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker64.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker64);
        //////////////////////////////////////
        MapPOIItem marker65 = new MapPOIItem();
        marker65.setItemName("함광석");
        marker65.setTag(0);
        marker65.setMapPoint(MapPoint.mapPointWithGeoCoord(36.8055078, 127.129758));
        marker65.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker65.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker65);
        //////////////////////////////////////
        MapPOIItem marker66 = new MapPOIItem();
        marker66.setItemName("황성수");
        marker66.setTag(0);
        marker66.setMapPoint(MapPoint.mapPointWithGeoCoord(127.128163, 36.9256978));
        marker66.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker66.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker66);
        //////////////////////////////////////


    }




    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }


    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        locationText.setText("위도 : " + location.getLatitude()
                + " 경도 : " + location.getLongitude());
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
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

}

