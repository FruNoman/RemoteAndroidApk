package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
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
    private final String SET_PAIRING_CONFIRMATION = "setPairingConfirmation";
    private final String GET_SCAN_MODE = "getScanMode";
    private final String GET_BONDED_DEVICES = "getBondedDevices";
    private final String GET_REMOTE_DEVICE = "getRemoteDevice";
    private final String IS_ENABLED = "isEnabled";
    private final String FACTORY_RESET = "factoryReset";
    private final String GET_BLUETOOTH_CLASS = "getBluetoothClass";
    private final String SET_SCAN_MODE = "setScanMode";
    private final String GET_DISCOVERABLE_TIMEOUT = "getDiscoverableTimeout";
    private final String SET_DISCOVERABLE_TIMEOUT = "setDiscoverableTimeout";
    private final String IS_DISCOVERING = "isDiscovering";
    private final String GET_SUPPORTED_PROFILES = "getSupportedProfiles";
    private final String GET_CONNECTION_STATE = "getConnectionState";
    private final String GET_PROFILE_CONNECTION_STATE = "getProfileConnectionState";
    private final String REMOVE_PAIRED_DEVICE = "removeBond";

    private final String GET_PAIR_STATE = "getPairState";
    private final String GET_DEVICE_NAME = "getDeviceName";
    private final String GET_DEVICE_TYPE = "getDeviceType";
    private final String GET_DEVICE_CLASS = "getDeviceClass";
    private final String SET_DEVICE_PAIRING_CONFIRM = "setDevicePairingConfirmation";
    private final String SET_DEVICE_PIN = "setDevicePin";
    private final String DEVICE_CANCEL_PAIRING = "deviceCancelPairing";
    private final String GET_MESSAGE_ACCESS_PERMISSION = "getMessageAccessPermission";
    private final String GET_SIM_ACCESS_PERMISSION = "getSimAccessPermission";
    private final String GET_PHONE_BOOK_ACCESS_PERMISSION = "getPhonebookAccessPermission";

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
                    Method method = null;
                    try {
                        method = adapter.getClass().getMethod("setScanMode", int.class, long.class);
                    }catch (NoSuchMethodException noSuchMethodException){
                        method = adapter.getClass().getMethod("setScanMode", int.class, int.class);
                    }
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, time);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth discoverable", Toast.LENGTH_SHORT).show();
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
                    setResult(SUCCESS_CODE, builder.toString(), new Bundle());
                    Toast.makeText(context, "Bluetooth get discovered devices", Toast.LENGTH_SHORT).show();
                } else if (command.contains(SET_PAIRING_CONFIRMATION)) {
                    String mac = command.split(",")[1];
                    boolean confirmation = Boolean.parseBoolean(command.split(",")[2]);
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.setPairingConfirmation(confirmation);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth set pairing confirmation " + mac + " " + confirmation, Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_SCAN_MODE)) {
                    int result = adapter.getScanMode();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get scan mode", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_BONDED_DEVICES)) {
                    Set<BluetoothDevice> deviceSet = adapter.getBondedDevices();
                    StringBuilder builder = new StringBuilder();
                    for (BluetoothDevice device : deviceSet) {
                        builder.append(device.getAddress() + ",");
                    }
                    setResult(SUCCESS_CODE, builder.toString(), new Bundle());
                    Toast.makeText(context, "Bluetooth get paired devices", Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_REMOTE_DEVICE)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    setResult(SUCCESS_CODE, String.valueOf(device.getAddress()), new Bundle());
                    Toast.makeText(context, "Bluetooth get device " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.equals(IS_ENABLED)) {
                    boolean result = adapter.isEnabled();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth is enabled?", Toast.LENGTH_SHORT).show();
                } else if (command.equals(FACTORY_RESET)) {
                    Method method = adapter.getClass().getMethod("factoryReset");
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth factory reset", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_BLUETOOTH_CLASS)) {
                    Method method = adapter.getClass().getMethod("getBluetoothClass");
                    method.setAccessible(true);
                    BluetoothClass result = (BluetoothClass) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result.hashCode()), new Bundle());
                    Toast.makeText(context, "Bluetooth get class", Toast.LENGTH_SHORT).show();
                } else if (command.contains(SET_SCAN_MODE)) {
                    int mode = Integer.parseInt(command.split(",")[1]);
                    int duration = Integer.parseInt(command.split(",")[2]);
                    Method method = null;
                    try {
                        method = adapter.getClass().getMethod("setScanMode", int.class, long.class);
                    }catch (NoSuchMethodException noSuchMethodException){
                        method = adapter.getClass().getMethod("setScanMode", int.class, int.class);
                    }
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter, mode, duration);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth set scan mode", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_DISCOVERABLE_TIMEOUT)) {
                    Method method = adapter.getClass().getMethod("getDiscoverableTimeout");
                    method.setAccessible(true);
                    int result = (int) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get discoverable timeout", Toast.LENGTH_SHORT).show();
                } else if (command.contains(SET_DISCOVERABLE_TIMEOUT)) {
                    int timeout = Integer.parseInt(command.split(",")[1]);
                    Method method = adapter.getClass().getMethod("setDiscoverableTimeout", int.class);
                    method.setAccessible(true);
                    method.invoke(adapter, timeout);
                    setResult(SUCCESS_CODE, String.valueOf(true), new Bundle());
                    Toast.makeText(context, "Bluetooth set discoverable timeout", Toast.LENGTH_SHORT).show();
                } else if (command.equals(IS_DISCOVERING)) {
                    boolean result = adapter.isDiscovering();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth factory reset", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_SUPPORTED_PROFILES)) {
                    Method method = adapter.getClass().getMethod("getSupportedProfiles");
                    method.setAccessible(true);
                    List<Integer> result = (List<Integer>) method.invoke(adapter);
                    StringBuilder builder = new StringBuilder();
                    for (Integer profile : result) {
                        builder.append(profile + ",");
                    }
                    setResult(SUCCESS_CODE, builder.toString(), new Bundle());
                    Toast.makeText(context, "Bluetooth get supported profiles", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_CONNECTION_STATE)) {
                    Method method = adapter.getClass().getMethod("getConnectionState");
                    method.setAccessible(true);
                    int result = (int) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get connection state", Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_PROFILE_CONNECTION_STATE)) {
                    int profile = Integer.parseInt(command.split(",")[1]);
                    int result = adapter.getProfileConnectionState(profile);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get profile connection state", Toast.LENGTH_SHORT).show();
                } else if (command.contains(REMOVE_PAIRED_DEVICE)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(device, (Object[]) null);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth remove paired device " + mac, Toast.LENGTH_SHORT).show();
                }

                // ------------------- BluetoothDevice methods --------------------------------

                else if (command.contains(GET_PAIR_STATE)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    int result = device.getBondState();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get pair state " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_DEVICE_NAME)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    String result = device.getName();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get device name " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_DEVICE_TYPE)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    int result = device.getType();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get device type " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_DEVICE_CLASS)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    BluetoothClass result = device.getBluetoothClass();
                    setResult(SUCCESS_CODE, String.valueOf(result.hashCode()), new Bundle());
                    Toast.makeText(context, "Bluetooth get class " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(SET_DEVICE_PAIRING_CONFIRM)) {
                    String mac = command.split(",")[1];
                    boolean confirmation = Boolean.parseBoolean(command.split(",")[2]);
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.setPairingConfirmation(confirmation);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth device set pairing confirmation " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(SET_DEVICE_PIN)) {
                    String mac = command.split(",")[1];
                    String pin = command.split(",")[2];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.setPin(pin.getBytes("UTF-8"));
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth device set pairing confirmation " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(DEVICE_CANCEL_PAIRING)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("cancelPairing");
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth cancel pairing " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_PHONE_BOOK_ACCESS_PERMISSION)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("getPhonebookAccessPermission");
                    method.setAccessible(true);
                    int result = (int) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get phone book access " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_SIM_ACCESS_PERMISSION)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("getSimAccessPermission");
                    method.setAccessible(true);
                    int result = (int) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get sim access permissions " + mac, Toast.LENGTH_SHORT).show();
                } else if (command.contains(GET_MESSAGE_ACCESS_PERMISSION)) {
                    String mac = command.split(",")[1];
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("getMessageAccessPermission");
                    method.setAccessible(true);
                    int result = (int) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                    Toast.makeText(context, "Bluetooth get message access permissions " + mac, Toast.LENGTH_SHORT).show();
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
}
