package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class BluetoothReceiver extends BroadcastReceiver {
    public static final String COMMAND = "command";
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
    private final String IS_SCAN_ALWAYS_AVAILABLE = "isScanAlwaysAvailable";
    private final String SET_SCAN_ALWAYS_AVAILABLE = "setScanAlwaysAvailable";
    private final String DISCONNECT_DEVICE_PROFILE = "DisconnectDeviceProfile";
    private final String CONNECT_DEVICE_PROFILE = "ConnectDeviceProfile";
    private final String GET_CONNECTED_PROFILES = "getConnectedProfiles";


    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    public Set<BluetoothDevice> discoveredDevices = new HashSet<>();


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
                String command = intent.getStringExtra(COMMAND);
                if (command.equals(ENABLE)) {
                    adapter.enable();
                    setResultCode(SUCCESS_CODE);
                } else if (command.equals(DISABLE)) {
                    adapter.disable();
                    setResultCode(SUCCESS_CODE);
                } else if (command.equals(GET_CONNECTED_PROFILES)) {
                    StringBuilder builder = new StringBuilder();
                    for (int x = 1; x < 22; x++) {
                        if (adapter.getProfileConnectionState(x) == BluetoothProfile.STATE_CONNECTED) {
                            builder.append(x + ",");
                        }
                    }
                    setResult(SUCCESS_CODE, builder.toString(), new Bundle());
                } else if (command.equals(CONNECT_DEVICE_PROFILE)) {
                    int currentProfile = Integer.parseInt(intent.getStringExtra("param0"));
                    String deviceMac = intent.getStringExtra("param1");
                    adapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
                        public void onServiceConnected(int profile, BluetoothProfile proxy) {
                            if (profile == currentProfile) {
                                Method method = null;
                                try {
                                    method = proxy.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
                                    method.invoke(proxy, adapter.getRemoteDevice(deviceMac));
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        public void onServiceDisconnected(int profile) {

                        }
                    }, currentProfile);
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(DISCONNECT_DEVICE_PROFILE)) {
                    int currentProfile = Integer.parseInt(intent.getStringExtra("param0"));
                    String deviceMac = intent.getStringExtra("param1");
                    adapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
                        public void onServiceConnected(int profile, BluetoothProfile proxy) {
                            if (profile == currentProfile) {
                                Method method = null;
                                try {
                                    method = proxy.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
                                    method.invoke(proxy, adapter.getRemoteDevice(deviceMac));
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        public void onServiceDisconnected(int profile) {

                        }
                    }, currentProfile);
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(GET_STATE)) {
                    int state = adapter.getState();
                    setResult(SUCCESS_CODE, String.valueOf(state), new Bundle());
                } else if (command.equals(DISCOVERABLE)) {
                    int time = Integer.parseInt(intent.getStringExtra("param0"));
                    Method method = null;
                    try {
                        method = adapter.getClass().getMethod("setScanMode", int.class, long.class);
                    } catch (NoSuchMethodException noSuchMethodException) {
                        method = adapter.getClass().getMethod("setScanMode", int.class, int.class);
                    }
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, time);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_NAME)) {
                    String name = adapter.getName();
                    setResult(SUCCESS_CODE, name, new Bundle());
                } else if (command.equals(SET_NAME)) {
                    String name = intent.getStringExtra("param0");
                    boolean success = adapter.setName(name);
                    setResult(SUCCESS_CODE, String.valueOf(success), new Bundle());
                } else if (command.equals(START_DISCOVERY)) {
                    discoveredDevices = new HashSet<>();
                    boolean result = adapter.startDiscovery();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(CANCEL_DISCOVERY)) {
                    boolean result = adapter.cancelDiscovery();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(PAIR)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.createBond();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_DISCOVERED_DEVICES)) {
                    StringBuilder builder = new StringBuilder();
                    for (BluetoothDevice device : discoveredDevices) {
                        builder.append(device.getAddress() + ",");
                    }
                    setResult(SUCCESS_CODE, builder.toString(), new Bundle());
                } else if (command.equals(SET_PAIRING_CONFIRMATION)) {
                    String mac = intent.getStringExtra("param0");
                    boolean confirmation = Boolean.parseBoolean(intent.getStringExtra("param1"));
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.setPairingConfirmation(confirmation);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_SCAN_MODE)) {
                    int result = adapter.getScanMode();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_BONDED_DEVICES)) {
                    Set<BluetoothDevice> deviceSet = adapter.getBondedDevices();
                    StringBuilder builder = new StringBuilder();
                    for (BluetoothDevice device : deviceSet) {
                        builder.append(device.getAddress() + ",");
                    }
                    setResult(SUCCESS_CODE, builder.toString(), new Bundle());
                } else if (command.equals(GET_REMOTE_DEVICE)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    setResult(SUCCESS_CODE, String.valueOf(device.getAddress()), new Bundle());
                } else if (command.equals(IS_ENABLED)) {
                    boolean result = adapter.isEnabled();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(FACTORY_RESET)) {
                    Method method = adapter.getClass().getMethod("factoryReset");
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_BLUETOOTH_CLASS)) {
                    Method method = adapter.getClass().getMethod("getBluetoothClass");
                    method.setAccessible(true);
                    BluetoothClass result = (BluetoothClass) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result.hashCode()), new Bundle());
                } else if (command.equals(SET_SCAN_MODE)) {
                    int mode = Integer.parseInt(intent.getStringExtra("param0"));
                    int duration = Integer.parseInt(intent.getStringExtra("param1"));
                    Method method = null;
                    try {
                        method = adapter.getClass().getMethod("setScanMode", int.class, long.class);
                    } catch (NoSuchMethodException noSuchMethodException) {
                        method = adapter.getClass().getMethod("setScanMode", int.class, int.class);
                    }
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(adapter, mode, duration);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_DISCOVERABLE_TIMEOUT)) {
                    Method method = adapter.getClass().getMethod("getDiscoverableTimeout");
                    method.setAccessible(true);
                    int result = (int) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(SET_DISCOVERABLE_TIMEOUT)) {
                    int timeout = Integer.parseInt(intent.getStringExtra("param0"));
                    Method method = adapter.getClass().getMethod("setDiscoverableTimeout", int.class);
                    method.setAccessible(true);
                    method.invoke(adapter, timeout);
                    setResult(SUCCESS_CODE, String.valueOf(true), new Bundle());
                } else if (command.equals(IS_DISCOVERING)) {
                    boolean result = adapter.isDiscovering();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_CONNECTION_STATE)) {
                    Method method = adapter.getClass().getMethod("getConnectionState");
                    method.setAccessible(true);
                    int result = (int) method.invoke(adapter);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_PROFILE_CONNECTION_STATE)) {
                    int profile = Integer.parseInt(intent.getStringExtra("param0"));
                    int result = adapter.getProfileConnectionState(profile);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(REMOVE_PAIRED_DEVICE)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(device, (Object[]) null);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(SET_SCAN_ALWAYS_AVAILABLE)) {
                    boolean enabled = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    int state = enabled ? 1 : 0;
                    boolean result = Settings.Global.putInt(context.getContentResolver(),
                            "bluetooth_scan_always_enabled", state);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_SCAN_ALWAYS_AVAILABLE)) {
                    boolean result = Settings.Global.getInt(context.getContentResolver(),
                            "bluetooth_scan_always_enabled", 0) != 0;
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                // ------------------- BluetoothDevice methods --------------------------------

                else if (command.equals(GET_PAIR_STATE)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    int result = device.getBondState();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_DEVICE_NAME)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    String result = device.getName();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_DEVICE_TYPE)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    int result = device.getType();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_DEVICE_CLASS)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    BluetoothClass result = device.getBluetoothClass();
                    setResult(SUCCESS_CODE, String.valueOf(result.hashCode()), new Bundle());
                } else if (command.equals(SET_DEVICE_PAIRING_CONFIRM)) {
                    String mac = intent.getStringExtra("param0");
                    boolean confirmation = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.setPairingConfirmation(confirmation);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(SET_DEVICE_PIN)) {
                    String mac = intent.getStringExtra("param0");
                    String pin = intent.getStringExtra("param1");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    boolean result = device.setPin(pin.getBytes("UTF-8"));
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(DEVICE_CANCEL_PAIRING)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("cancelPairing");
                    method.setAccessible(true);
                    boolean result = (boolean) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_PHONE_BOOK_ACCESS_PERMISSION)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("getPhonebookAccessPermission");
                    method.setAccessible(true);
                    int result = (int) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_SIM_ACCESS_PERMISSION)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("getSimAccessPermission");
                    method.setAccessible(true);
                    int result = (int) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_MESSAGE_ACCESS_PERMISSION)) {
                    String mac = intent.getStringExtra("param0");
                    BluetoothDevice device = adapter.getRemoteDevice(mac);
                    Method method = device.getClass().getMethod("getMessageAccessPermission");
                    method.setAccessible(true);
                    int result = (int) method.invoke(device);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals("ERROR_TEST")) {
                    try {
                        throw new Exception("Bluetooth failed on test error");
                    } catch (Exception e) {
                        setResult(ERROR_CODE, e.getLocalizedMessage(), new Bundle());
                    }
                }
            }
        } catch (Exception e) {
            setResult(ERROR_CODE, e.getLocalizedMessage(), new Bundle());
        }
    }
}
