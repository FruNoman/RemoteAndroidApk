package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
    public static final String BLUETOOTH_COMMAND = "bluetooth_remote";
    public static final String BLUETOOTH_REMOTE = "com.github.remotesdk.BLUETOOTH_REMOTE";

    private final String ENABLE = "enable";
    private final String DISABLE = "disable";
    private final String GET_STATE = "getState";


    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BLUETOOTH_REMOTE)){
            String command = intent.getStringExtra(BLUETOOTH_COMMAND);
            if (command.equals(ENABLE)){
                adapter.enable();
                Toast.makeText(context, "Bluetooth adapter enable" , Toast.LENGTH_SHORT).show();
            }else if (command.equals(DISABLE)){
                adapter.disable();
                Toast.makeText(context, "Bluetooth adapter disable" , Toast.LENGTH_SHORT).show();
            }else if (command.equals(GET_STATE)){
                int state = adapter.getState();
                setResultData(String.valueOf(state));
                Toast.makeText(context, "Bluetooth adapter state: "+state , Toast.LENGTH_SHORT).show();
            }
        }
    }
}
