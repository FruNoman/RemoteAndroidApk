package com.github.remotesdk.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Formatter;

import androidx.core.app.ActivityCompat;

import com.android.dx.stock.ProxyBuilder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.remotesdk.utils.TetheringCallback;
import com.github.remotesdk.utils.WifiConfigUtil;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    public static final String WIFI_COMMAND = "wifi_command";
    public static final String WIFI_REMOTE = "com.github.remotesdk.WIFI_REMOTE";

    private final String ENABLE = "enable";
    private final String DISABLE = "disable";
    private final String GET_STATE = "getState";
    private final String IS_ENABLED = "isEnabled";
    private final String ADD_NETWORK = "addNetwork";
    private final String ENABLE_NETWORK = "enableNetwork";
    private final String DISABLE_NETWORK = "disableNetwork";
    private final String REMOVE_NETWORK = "removeNetwork";
    private final String GET_CONFIGURE_NETWORKS = "getConfiguredNetworks";
    private final String DISCONNECT = "disconnect";
    private final String GET_WIFI_AP_CONFIGURATION = "getWifiApConfiguration";
    private final String GET_WIFI_AP_STATE = "getWifiApState";
    private final String IS_WIFI_AP_ENABLED = "isWifiApEnabled";
    private final String START_TETHERING = "startTethering";
    private final String STOP_TETHERING = "stopTethering";
    private final String SET_WIFI_AP_CONFIGURATION = "setWifiApConfiguration";
    private final String RECONNECT = "reconnect";
    private final String REASSOCIATE = "reassociate";
    private final String START_SCAN = "startScan";
    private final String GET_SCAN_RESULT = "getScanResults";
    private final String IS_CONNECTED = "isWifiNetworkConnected";
    private final String IS_SCAN_ALWAYS_AVAILABLE = "isScanAlwaysAvailable";
    private final String SET_SCAN_ALWAYS_AVAILABLE = "setScanAlwaysAvailable";
    private final String GET_IP_ADDRESS = "getIpAddress";


    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private WifiManager adapter;

    public WifiReceiver(WifiManager adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(WIFI_REMOTE)) {
                String command = intent.getStringExtra(WIFI_COMMAND);
                if (command.equals(ENABLE)) {
                    boolean result = adapter.setWifiEnabled(true);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(DISABLE)) {
                    boolean result = adapter.setWifiEnabled(false);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_STATE)) {
                    int result = adapter.getWifiState();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_ENABLED)) {
                    boolean result = adapter.isWifiEnabled();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(ADD_NETWORK)) {
                    String ssid = command.split(",")[1];
                    String pass = command.split(",")[2];
                    String config = command.split(",")[3];
                    String hidden = command.split(",")[4];
                    boolean hidd = false;
                    if (hidden != null) {
                        if (hidden.toLowerCase().contains("true")) {
                            hidd = true;
                        }
                    }
                    WifiConfiguration wifiConfiguration = new WifiConfiguration();
                    if (config.equals("wep")) {
                        wifiConfiguration = WifiConfigUtil.getWepWifiConfig(ssid, pass,hidd);
                    } else if (config.equals("pass")) {
                        wifiConfiguration = WifiConfigUtil.getPassWifiConfig(ssid, pass,hidd);
                    } else if (config.equals("open")) {
                        wifiConfiguration = WifiConfigUtil.getOpenWifiConfig(ssid,hidd);
                    } else if (config.equals("wpa3")) {
                        wifiConfiguration = WifiConfigUtil.getWpa3WifiConfig(ssid, pass,hidd);
                    }

                    int result = adapter.addNetwork(wifiConfiguration);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(ENABLE_NETWORK)) {
                    int netId = Integer.parseInt(command.split(",")[1]);
                    boolean result = adapter.enableNetwork(netId, true);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(DISABLE_NETWORK)) {
                    int netId = Integer.parseInt(command.split(",")[1]);
                    boolean result = adapter.disableNetwork(netId);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(REMOVE_NETWORK)) {
                    int netId = Integer.parseInt(command.split(",")[1]);
                    boolean result = adapter.removeNetwork(netId);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_CONFIGURE_NETWORKS)) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    List<WifiConfiguration> configuredNetworks = adapter.getConfiguredNetworks();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    ObjectWriter writer = mapper.writer().withoutAttribute("httpProxy").withoutAttribute("pacFileUrl");
                    String result = writer.writeValueAsString(configuredNetworks);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(DISCONNECT)) {
                    boolean result = adapter.disconnect();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_WIFI_AP_CONFIGURATION)) {
                    Method method = adapter.getClass().getMethod("getWifiApConfiguration");
                    method.setAccessible(true);
                    WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(adapter);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    ObjectWriter writer = mapper.writer().withoutAttribute("httpProxy").withoutAttribute("pacFileUrl");
                    String result = writer.writeValueAsString(wifiConfiguration);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(GET_WIFI_AP_STATE)) {
                    Method method = adapter.getClass().getMethod("getWifiApState");
                    method.setAccessible(true);
                    int result = (int) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_WIFI_AP_ENABLED)) {
                    Method method = adapter.getClass().getMethod("isWifiApEnabled");
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(START_TETHERING)) {
                    boolean result = false;
                    TetheringCallback callback = new TetheringCallback() {
                        @Override
                        public void onTetheringStarted() {

                        }

                        @Override
                        public void onTetheringFailed() {

                        }
                    };
                    File outputDir = context.getCacheDir();
                    Object proxy = null;
                    try {
                        proxy = ProxyBuilder.forClass(OnStartTetheringCallbackClass())
                                .dexCache(outputDir).handler(new InvocationHandler() {
                                    @Override
                                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                        switch (method.getName()) {
                                            case "onTetheringStarted":
                                                callback.onTetheringStarted();
                                                break;
                                            case "onTetheringFailed":
                                                callback.onTetheringFailed();
                                                break;
                                            default:
                                                ProxyBuilder.callSuper(proxy, method, args);
                                        }
                                        return null;
                                    }

                                }).build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Method method = null;
                    try {
                        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        method = connectivityManager.getClass().getDeclaredMethod("startTethering", int.class, boolean.class, OnStartTetheringCallbackClass(), Handler.class);
                        if (method != null) {
                            method.invoke(connectivityManager, 0, false, proxy, null);
                            result = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(STOP_TETHERING)) {
                    ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    Method method = mConnectivityManager.getClass().getDeclaredMethod("stopTethering", int.class);
                    method.invoke(mConnectivityManager, ConnectivityManager.TYPE_MOBILE);
                    setResultCode(SUCCESS_CODE);
                } else if (command.contains(SET_WIFI_AP_CONFIGURATION)) {
                    String ssid = command.split(",")[1];
                    String pass = command.split(",")[2];
                    String config = command.split(",")[3];
                    int appBand = Integer.parseInt(command.split(",")[4]);
                    WifiConfiguration wifiConfiguration = null;
                    if (config.equals("pass")) {
                        wifiConfiguration = WifiConfigUtil.getHotspotPassConfig(ssid, pass, appBand);
                    } else if (config.equals("open")) {
                        wifiConfiguration = WifiConfigUtil.getHotspotOpenConfig(ssid, appBand);
                    } else if (config.equals("wpa3")) {
                        wifiConfiguration = WifiConfigUtil.getHotspotWpa3Config(ssid, pass, appBand);
                    } else if (config.equals("wpa2wpa3")) {
                        wifiConfiguration = WifiConfigUtil.getHotspotWpa2Wpa3Config(ssid, pass, appBand);
                    }
                    Method method = adapter.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                    boolean result = (boolean) method.invoke(adapter, wifiConfiguration);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_CONNECTED)) {
                    WifiInfo wifiInfo = adapter.getConnectionInfo();
                    boolean result = false;
                    if (wifiInfo != null) {
                        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                            result = true;
                        }
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_SCAN_ALWAYS_AVAILABLE)) {
                    boolean result = Settings.Global.getInt(context.getContentResolver(),
                            "wifi_scan_always_enabled", 0) != 0;
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(SET_SCAN_ALWAYS_AVAILABLE)) {
                    boolean enabled = Boolean.parseBoolean(command.split(",")[1]);
                    int state = enabled ? 1 : 0;
                    boolean result = Settings.Global.putInt(context.getContentResolver(),
                            "wifi_scan_always_enabled", state);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(RECONNECT)) {
                    boolean result = adapter.reconnect();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(REASSOCIATE)) {
                    boolean result = adapter.reassociate();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(START_SCAN)) {
                    boolean state = adapter.startScan();
                    setResult(SUCCESS_CODE, String.valueOf(state), new Bundle());
                } else if (command.equals(GET_SCAN_RESULT)) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<ScanResult> results = adapter.getScanResults();
                    String json = mapper.writeValueAsString(results);
                    setResult(SUCCESS_CODE, String.valueOf(json), new Bundle());
                } else if (command.equals(GET_IP_ADDRESS)) {
                    WifiInfo wifiInfo = adapter.getConnectionInfo();
                    String result = "";
                    if (wifiInfo != null) {
                        int ip = wifiInfo.getIpAddress();
                        result = Formatter.formatIpAddress(ip);
                    }
                    setResult(SUCCESS_CODE, result, new Bundle());
                }
            }
        } catch (Exception e) {
            setResult(ERROR_CODE, "error", new Bundle());
        }
    }

    private Class OnStartTetheringCallbackClass() {
        try {
            return Class.forName("android.net.ConnectivityManager$OnStartTetheringCallback");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
