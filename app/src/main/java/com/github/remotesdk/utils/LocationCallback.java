package com.github.remotesdk.utils;

import android.location.GnssStatus;
import android.os.Build;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.N)
public class LocationCallback extends GnssStatus.Callback {
    private GnssStatus gnssStatus;
    private Object monitor = new Object();

    public synchronized GnssStatus getGnssStatus() {
        synchronized (monitor) {
            return gnssStatus;
        }
    }

    public synchronized void setGnssStatus(GnssStatus gnssStatus) {
        synchronized (monitor) {
            this.gnssStatus = gnssStatus;
        }
    }

    public LocationCallback() {
        super();
    }

    @Override
    public void onStarted() {
        super.onStarted();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }

    @Override
    public void onFirstFix(int ttffMillis) {
        super.onFirstFix(ttffMillis);
    }

    @Override
    public void onSatelliteStatusChanged(GnssStatus status) {
        super.onSatelliteStatusChanged(status);
        setGnssStatus(status);
    }
}
