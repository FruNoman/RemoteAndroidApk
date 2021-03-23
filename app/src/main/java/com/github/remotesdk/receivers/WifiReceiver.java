package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.remotesdk.utils.WifiConfigUtil;

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
                    } else {
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
                }else if (command.contains(REMOVE_NETWORK)) {
//                    List<WifiConfiguration> wifiConfigurations = adapter.getConfiguredNetworks();
//                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Wifi remove network", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            try {
                String json = objectMapper.writeValueAsString(e);
                setResult(ERROR_CODE, json, new Bundle());
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        }
    }
}
