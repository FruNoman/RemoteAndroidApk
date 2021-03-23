package com.github.remotesdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.remotesdk.utils.WifiConfigUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        WifiManager adapter = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        adapter.setWifiEnabled(true);
        Thread.sleep(3000);
        WifiConfiguration wifiConfiguration = WifiConfigUtil.getWepWifiConfig("RNS_WEP","37DD5A0741");
        int netID = adapter.addNetwork(wifiConfiguration);
        adapter.enableNetwork(netID,true);
    }
}