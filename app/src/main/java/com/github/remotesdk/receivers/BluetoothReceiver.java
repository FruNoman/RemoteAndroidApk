package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BluetoothReceiver extends BroadcastReceiver {
    public static final String BLUETOOTH_COMMAND = "bluetooth_remote";
    public static final String BLUETOOTH_REMOTE = "com.github.remotesdk.BLUETOOTH_REMOTE";

    private final String ENABLE = "enable";
    private final String DISABLE = "disable";
    private final String GET_STATE = "getState";
    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;


    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(BLUETOOTH_REMOTE)) {
                String command = intent.getStringExtra(BLUETOOTH_COMMAND);
                if (command.equals(ENABLE)) {
                    adapter.enable();
                    setResultCode(SUCCESS_CODE);
                    Toast.makeText(context, "Bluetooth adapter enable", Toast.LENGTH_SHORT).show();
                } else if (command.equals(DISABLE)) {
                    adapter.disable();
                    setResultCode(SUCCESS_CODE);
                    Toast.makeText(context, "Bluetooth adapter disable", Toast.LENGTH_SHORT).show();
                } else if (command.equals(GET_STATE)) {
                    int state = adapter.getState();
                    setResult(SUCCESS_CODE,String.valueOf(state),new Bundle());
                    Toast.makeText(context, "Bluetooth adapter state: " + state, Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            try {
                String json = objectMapper.writeValueAsString(e);
                setResult(ERROR_CODE,json,new Bundle());
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }

        }
    }
}
