package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.android.dx.stock.ProxyBuilder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public static final String WIFI_COMMAND = "wifi_remote";
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


    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private WifiManager adapter;
    private ObjectMapper objectMapper = new ObjectMapper();

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
                    Toast.makeText(context, "Wifi enable", Toast.LENGTH_SHORT).show();
                } else if (command.equals(DISABLE)) {
                    boolean result = adapter.setWifiEnabled(false);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi disable", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_STATE)) {
                    int result = adapter.getWifiState();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi get state", Toast.LENGTH_SHORT).show();
                } else if (command.equals(IS_ENABLED)) {
                    boolean result = adapter.isWifiEnabled();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi is enabled", Toast.LENGTH_SHORT).show();
                } else if (command.contains(ADD_NETWORK)) {
                    String ssid = command.split(",")[1];
                    String pass = command.split(",")[2];
                    String config = command.split(",")[3];
                    WifiConfiguration wifiConfiguration = null;
                    if (config.equals("wep")) {
                        wifiConfiguration = WifiConfigUtil.getWepWifiConfig(ssid, pass);
                    } else if (config.equals("pass")) {
                        wifiConfiguration = WifiConfigUtil.getPassWifiConfig(ssid, pass);
                    } else if (config.equals("open")) {
                        wifiConfiguration = WifiConfigUtil.getOpenWifiConfig(ssid);
                    }
                    int result = adapter.addNetwork(wifiConfiguration);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi add network", Toast.LENGTH_SHORT).show();
                } else if (command.contains(ENABLE_NETWORK)) {
                    int netId = Integer.parseInt(command.split(",")[1]);
                    boolean result = adapter.enableNetwork(netId, true);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi enable network", Toast.LENGTH_SHORT).show();
                } else if (command.contains(DISABLE_NETWORK)) {
                    int netId = Integer.parseInt(command.split(",")[1]);
                    boolean result = adapter.disableNetwork(netId);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi disable network", Toast.LENGTH_SHORT).show();
                } else if (command.contains(REMOVE_NETWORK)) {
                    int netId = Integer.parseInt(command.split(",")[1]);
                    boolean result = adapter.removeNetwork(netId);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi remove network", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_CONFIGURE_NETWORKS)) {
                    List<WifiConfiguration> configuredNetworks = adapter.getConfiguredNetworks();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                    ObjectWriter writer = mapper.writer().withoutAttribute("httpProxy").withoutAttribute("pacFileUrl");
                    String result = writer.writeValueAsString(configuredNetworks);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi remove network", Toast.LENGTH_SHORT).show();
                } else if (command.equals(DISCONNECT)) {
                    boolean result = adapter.disconnect();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi disconnect", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Wifi get ap configuration", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_WIFI_AP_STATE)) {
                    Method method = adapter.getClass().getMethod("getWifiApState");
                    method.setAccessible(true);
                    int result = (int) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi get ap state", Toast.LENGTH_SHORT).show();
                } else if (command.equals(IS_WIFI_AP_ENABLED)) {
                    Method method = adapter.getClass().getMethod("isWifiApEnabled");
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi is ap enabled", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Wifi start hotspot", Toast.LENGTH_SHORT).show();
                } else if (command.equals(STOP_TETHERING)) {
                    ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    Method method = mConnectivityManager.getClass().getDeclaredMethod("stopTethering", int.class);
                    method.invoke(mConnectivityManager, ConnectivityManager.TYPE_MOBILE);
                    setResultCode(SUCCESS_CODE);
                    Toast.makeText(context, "Wifi stop hotspot", Toast.LENGTH_SHORT).show();
                } else if (command.contains(SET_WIFI_AP_CONFIGURATION)) {
                    String ssid = command.split(",")[1];
                    String pass = command.split(",")[2];
                    String config = command.split(",")[3];
                    WifiConfiguration wifiConfiguration = null;
                    if (config.equals("wep")) {
                        wifiConfiguration = WifiConfigUtil.getWepWifiConfig(ssid, pass);
                    } else if (config.equals("pass")) {
                        wifiConfiguration = WifiConfigUtil.getWpa2Config(ssid, pass);
                    } else if (config.equals("open")) {
                        wifiConfiguration = WifiConfigUtil.getOpenWifiConfig(ssid);
                    }
                    Method method = adapter.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                    boolean result = (boolean) method.invoke(adapter, wifiConfiguration);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi set hotspot configuration", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                String json = mapper.writeValueAsString(e);
                setResult(ERROR_CODE, json, new Bundle());
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
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
