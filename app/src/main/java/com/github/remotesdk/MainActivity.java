package com.github.remotesdk;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.remotesdk.receivers.BluetoothReceiver;
import com.github.remotesdk.receivers.DeviceAdminSample;
import com.github.remotesdk.receivers.EnvironmentReceiver;
import com.github.remotesdk.receivers.TelephonyReceiver;
import com.github.remotesdk.receivers.WifiReceiver;

public class MainActivity extends AppCompatActivity {
    public String[] permissions =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_PRIVILEGED,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,

                    Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
            };

    private Button secureButton;
    private Button adminButton;

    private BluetoothReceiver bluetoothReceiver;
    private WifiReceiver wifiReceiver;
    private TelephonyReceiver telephonyReceiver;
    private EnvironmentReceiver environmentReceiver;

    private DevicePolicyManager devicePolicyManager;
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 101);
            }
        }

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        secureButton = findViewById(R.id.secureButton);
        secureButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                grantIntent.setData(Uri.parse("package:com.github.remotesdk"));
                startActivity(grantIntent);
            }
        });

        adminButton = findViewById(R.id.deviceAdmin);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName componentName = new ComponentName(getApplicationContext(), DeviceAdminSample.class);
                boolean result = devicePolicyManager.isAdminActive(componentName);
                if (!result) {
                    Intent adminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                    startActivity(adminIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Remove admin permission", Toast.LENGTH_SHORT).show();
                    devicePolicyManager.removeActiveAdmin(componentName);

                }

            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothReceiver.BLUETOOTH_REMOTE);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(WifiReceiver.WIFI_REMOTE);
        intentFilter.addAction(EnvironmentReceiver.ENVIRONMENT_REMOTE);

        intentFilter.addAction(TelephonyReceiver.TELEPHONY_REMOTE);
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);


        bluetoothReceiver = new BluetoothReceiver();
        wifiReceiver = new WifiReceiver(wifiManager);
        telephonyReceiver = new TelephonyReceiver(telephonyManager);
        environmentReceiver = new EnvironmentReceiver();

        registerReceiver(bluetoothReceiver, intentFilter);
        registerReceiver(wifiReceiver, intentFilter);
        registerReceiver(telephonyReceiver, intentFilter);
        registerReceiver(environmentReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(telephonyReceiver);
        unregisterReceiver(environmentReceiver);
        super.onDestroy();

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 101: {
//                for (String permission:permissions){
//                    if (ContextCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
//                        Toast.makeText(getApplicationContext(), "Remote SDK require permission "+permission, Toast.LENGTH_SHORT).show();
//                        ActivityCompat.requestPermissions(this, permissions, 101);
//                    }
//                }
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
}