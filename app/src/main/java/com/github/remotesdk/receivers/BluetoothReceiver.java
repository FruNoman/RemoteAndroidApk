package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class BluetoothReceiver extends BroadcastReceiver {
    public static final String BLUETOOTH_COMMAND = "bluetooth_remote";
    public static final String BLUETOOTH_REMOTE = "com.github.remotesdk.BLUETOOTH_REMOTE";

    private final String ENABLE = "enable";
    private final String DISABLE = "disable";
    private final String GET_STATE = "getState";
    private final String DISCOVERABLE = "discoverable";
    private final String SET_NAME = "setName";
    private final String GET_NAME = "getName";
    private final String START_DISCOVERY = "startDiscovery";
    private final String CANCEL_DISCOVERY = "cancelDiscovery";
    private final String PAIR = "pairDevice";
    private final String GET_DISCOVERED_DEVICES = "getDiscoveredDevices";


    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private Set<BluetoothDevice> discoveredDevices;


    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(device);
            }
            if (action.equals(BLUETOOTH_REMOTE)) {
                String command = intent.getStringExtra(BLUETOOTH_COMMAND);
                if (command.equals(ENABLE)) {
                    adapter.enable();
                    setResultCode(SUCCESS_CODE);
                    Toast.makeText(context, "Bluetooth enable", Toast.LENGTH_SHORT).show();
                } else if (command.equals(DISABLE)) {
                    adapter.disable();
                    setResultCode(SUCCESS_CODE);
                    Toast.makeText(context, "Bluetooth disable", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_STATE)) {
                    int state = adapter.getState();
                    setResult(SUCCESS_CODE, String.valueOf(state), new Bundle());
                    Toast.makeText(context, "Bluetooth state: " + state, Toast.LENGTH_SHORT).show();
                } else if (command.contains(DISCOVERABLE)) {
                    int time = Integer.parseInt(command.split(",")[1]);
                    try {
                        Method method = adapter.getClass().getMethod("setScanMode", int.class, long.class);
                        method.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, time);
                        setResultCode(SUCCESS_CODE);
                        Toast.makeText(context, "Bluetooth discoverable", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
                        context.startActivity(discoverableIntent);
                        setResultCode(SUCCESS_CODE);
                        Toast.makeText(context, "Bluetooth discoverable", Toast.LENGTH_SHORT).show();
                    }
                } else if (command.equals(GET_NAME)) {
                    String name = adapter.getName();
                    setResult(SUCCESS_CODE, name, new Bundle());
                    Toast.makeText(context, "Bluetooth get name: " + name, Toast.LENGTH_SHORT).show();
                } else if (command.contains(SET_NAME)) {
                    String name = command.split(",")[1];
                    boolean success = adapter.setName(name);
                    setResult(SUCCESS_CODE, String.valueOf(success), new Bundle());
                    Toast.makeText(context, "Bluetooth set name: " + name, Toast.LENGTH_SHORT).show();
                } else if (command.equals(START_DISCOVERY)) {
                    discoveredDevices = new HashSet<>();
                    boolean result = adapter.startDiscovery();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth start discovery", Toast.LENGTH_SHORT).show();
                } else if (command.equals(CANCEL_DISCOVERY)) {
                    boolean result = adapter.cancelDiscovery();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth cancel discovery", Toast.LENGTH_SHORT).show();
                } else if (command.contains(PAIR)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.createBond();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth pair device " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_DISCOVERED_DEVICES)) {
                    StringBuilder builder = new StringBuilder();
                    for (BluetoothDevice device : discoveredDevices) {
                        builder.append(device.getAddress() + ",");
                    }
                    setResultData(builder.toString());
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
