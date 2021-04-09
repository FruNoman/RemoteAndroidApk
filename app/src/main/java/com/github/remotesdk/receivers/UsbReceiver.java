package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsbReceiver extends BroadcastReceiver {
    public static final String USB_COMMAND = "usb_remote";
    public static final String USB_REMOTE = "com.github.remotesdk.USB_REMOTE";

    private final String GET_DEVICE_LIST = "getDeviceList";

    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private UsbManager usbManager;

    public UsbReceiver(UsbManager usbManager) {
        this.usbManager = usbManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            String action = intent.getAction();
            if (action.equals(USB_REMOTE)) {
                String command = intent.getStringExtra(USB_COMMAND);
                if (command.equals(GET_DEVICE_LIST)) {
                    List<UsbDevice> usbDevices = new ArrayList<>(usbManager.getDeviceList().values());
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(usbDevices);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }
            }
        }catch (Exception e){
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
