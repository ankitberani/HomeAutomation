package com.wekex.apps.homeautomation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class locationUtils implements LocationListener {
    Context context;
    LocationManager locationManager;

    public locationUtils(Context splashActivity) {
        this.context = splashActivity;
    }

    @SuppressLint({"MissingPermission"})
    public String fn_getlocation() {
        this.locationManager = (LocationManager) this.context.getSystemService("location");
        boolean isGPSEnable = this.locationManager.isProviderEnabled("gps");
        boolean isNetworkEnable = this.locationManager.isProviderEnabled("network");
        if (isGPSEnable || isNetworkEnable) {
            if (isNetworkEnable) {
                this.locationManager.requestLocationUpdates("network", 1000, 0.0f, this);
                LocationManager locationManager2 = this.locationManager;
                if (locationManager2 != null) {
                    Location location = locationManager2.getLastKnownLocation("network");
                    if (location != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(location.getLatitude());
                        sb.append("");
                        Log.e("latitude", sb.toString());
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(location.getLongitude());
                        sb2.append("");
                        Log.e("longitude", sb2.toString());
                        Constants.latitude = location.getLatitude();
                        Constants.longitude = location.getLongitude();
                    }
                }
            }
            if (isGPSEnable) {
                this.locationManager.requestLocationUpdates("gps", 1000, 0.0f, this);
                LocationManager locationManager3 = this.locationManager;
                if (locationManager3 != null) {
                    Location location2 = locationManager3.getLastKnownLocation("gps");
                    if (location2 != null) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(location2.getLatitude());
                        sb3.append("");
                        Log.e("latitude", sb3.toString());
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(location2.getLongitude());
                        sb4.append("");
                        Log.e("longitude", sb4.toString());
                        Constants.latitude = location2.getLatitude();
                        Constants.longitude = location2.getLongitude();
                    }
                }
            }
        }
        StringBuilder sb5 = new StringBuilder();
        sb5.append(String.valueOf(Constants.longitude));
        sb5.append(',');
        sb5.append(String.valueOf(Constants.latitude));
        return sb5.toString();
    }

    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double Long = location.getLongitude();
        Constants.latitude = lat;
        Constants.longitude = Long;
        this.locationManager.removeUpdates(this);
        this.locationManager = null;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}
