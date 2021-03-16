package com.github.remotesdk;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.remotesdk.receivers.BluetoothReceiver;

public class MainActivity extends AppCompatActivity {
    public String[] permissions =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_PRIVILEGED,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

    private Button secureButton;
    private BluetoothReceiver bluetoothReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 101);
            }
        }

        secureButton = findViewById(R.id.secureButton);
        secureButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                boolean canDo = Settings.System.canWrite(MainActivity.this);
                if (!canDo) {
                    Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    grantIntent.setData(Uri.parse("package:com.github.remotesdk"));
                    startActivity(grantIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Already grand write settings permissions" , Toast.LENGTH_SHORT).show();

                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothReceiver.BLUETOOTH_REMOTE);
        bluetoothReceiver = new BluetoothReceiver();
        registerReceiver(bluetoothReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
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