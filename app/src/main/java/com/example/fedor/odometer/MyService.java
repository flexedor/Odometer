package com.example.fedor.odometer;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.location.LocationListener;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.content.Context;


public class MyService extends Service {

    private final IBinder binder = new OdometerBinder();
    private LocationManager locManager;
    public static final String PERMISSION_STRING=
            Manifest.permission.ACCESS_FINE_LOCATION;
    private LocationListener listener;
    private static double distanceInMeters;
    private static Location lastLocation=null;


    public class OdometerBinder extends Binder{
        MyService getOdometer(){
           return MyService.this;
        }
    }
    @Override
    public void onCreate(){
        super.onCreate();
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation==null){
                    lastLocation=location;
                }
                distanceInMeters+=location.distanceTo(lastLocation);
                lastLocation=location;
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
        };
        locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,PERMISSION_STRING)
                ==PackageManager.PERMISSION_GRANTED){
            String provider =locManager.getBestProvider(new Criteria(),true);
            if (provider!=null){
                locManager.requestLocationUpdates(provider,1000,1,listener);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (locManager !=null && listener != null){
            if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING)==
                    PackageManager.PERMISSION_GRANTED){
                locManager.removeUpdates(listener);
            }
            locManager=null;
            listener=null;

        }

    }

    public double getDistance(){
        return this.distanceInMeters;
    }
}
