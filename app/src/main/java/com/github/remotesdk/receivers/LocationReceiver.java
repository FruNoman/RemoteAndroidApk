package com.github.remotesdk.receivers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.remotesdk.utils.LocationCallback;

import java.util.ArrayList;
import java.util.List;

public class LocationReceiver extends BroadcastReceiver {
    public static final String COMMAND = "command";
    public static final String LOCATION_REMOTE = "com.github.remotesdk.LOCATION_REMOTE";

    private LocationManager adapter;
    private LocationCallback locationCallback;
    private List<Location> locationList = new ArrayList<>();

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            locationList.add(location);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    @SuppressLint("MissingPermission")
    public LocationReceiver(LocationManager adapter) {
        this.adapter = adapter;
        this.locationCallback = new LocationCallback();
        this.adapter.registerGnssStatusCallback(locationCallback, new Handler(Looper.getMainLooper()));
    }

    private final String IS_ENABLE = "isLocationEnabled";
    private final String SET_LOCATION_STATE = "setLocationState";
    private final String GET_ALL_PROVIDERS = "getAllProviders";
    private final String GET_LAST_KNOWN_LOCATION = "getLastKnownLocation";
    private final String REQUEST_LOCATION_UPDATES = "requestLocationUpdates";
    private final String REMOVE_LOCATION_UPDATES = "removeUpdates";

    private final String GET_UPDATED_LOCATION_LIST = "getUpdatedLocations";

    private final String GET_SATELLITE_COUNT = "getSatelliteCount";
    private final String GET_CONSTELLATION_TYPE = "getConstellationType";
    private final String USED_IN_FIX = "usedInFix";

    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(LOCATION_REMOTE)) {
                String command = intent.getStringExtra(COMMAND);
                if (command.equals(IS_ENABLE)) {
                    boolean result = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        result = adapter.isLocationEnabled();
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(SET_LOCATION_STATE)) {
                    boolean state = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    int locationState = 0;
                    if (state) {
                        locationState = 3;
                    }
                    boolean result = Settings.Secure.putInt(context.getContentResolver(),
                            Settings.Secure.LOCATION_MODE, locationState);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_ALL_PROVIDERS)) {
                    StringBuilder builder = new StringBuilder();
                    for (String provider : adapter.getAllProviders()) {
                        builder.append(provider + ",");
                    }
                    setResult(SUCCESS_CODE, builder.toString(), new Bundle());
                } else if (command.equals(GET_LAST_KNOWN_LOCATION)) {
                    String provider = intent.getStringExtra("param0");
                    requestLocationUpdates(provider, context);
                    if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location location = adapter.getLastKnownLocation(provider);
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(location);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(REQUEST_LOCATION_UPDATES)) {
                    String provider = intent.getStringExtra("param0");
                    requestLocationUpdates(provider, context);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(GET_UPDATED_LOCATION_LIST)) {
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(locationList);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(GET_SATELLITE_COUNT)) {
                    int result = locationCallback.getGnssStatus().getSatelliteCount();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_CONSTELLATION_TYPE)) {
                    int satelliteIndex = intent.getIntExtra("param0", 0);
                    int result = locationCallback.getGnssStatus().getConstellationType(satelliteIndex);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(USED_IN_FIX)) {
                    int satelliteIndex = intent.getIntExtra("param0", 0);
                    boolean result = locationCallback.getGnssStatus().usedInFix(satelliteIndex);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(REMOVE_LOCATION_UPDATES)) {
                    adapter.removeUpdates(locationListener);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                }
            }


        } catch (Exception e) {
            setResult(ERROR_CODE, e.getLocalizedMessage(), new Bundle());
        }
    }


    private void requestLocationUpdates(String provider, Context context) {
        locationList = new ArrayList<>();
        try {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            adapter.requestLocationUpdates(
                    provider,
                    3000,
                    1, locationListener,
                    Looper.getMainLooper());
        } catch (Exception e) {

        }
    }
}
