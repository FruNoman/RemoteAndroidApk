package com.github.remotesdk;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chibde.visualizer.LineVisualizer;
import com.chibde.visualizer.SquareBarVisualizer;
import com.github.remotesdk.receivers.AudioReceiver;
import com.github.remotesdk.receivers.BluetoothReceiver;
import com.github.remotesdk.receivers.DeviceAdminSample;
import com.github.remotesdk.receivers.EnvironmentReceiver;
import com.github.remotesdk.receivers.LocationReceiver;
import com.github.remotesdk.receivers.MediaSessionReceiver;
import com.github.remotesdk.receivers.PlayerReceiver;
import com.github.remotesdk.receivers.TelephonyReceiver;
import com.github.remotesdk.receivers.UsbReceiver;
import com.github.remotesdk.receivers.WifiP2PReceiver;

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
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY
            };

    private Button secureButton;
    private Button adminButton;
    private Button doNotDisturbButton;
    private SurfaceView surfaceView;
    private LineVisualizer lineVisualizer;
    private TextView trackName;
    private TextView trackArtist;

    private BluetoothReceiver bluetoothReceiver;
    private WifiReceiver wifiReceiver;
    private TelephonyReceiver telephonyReceiver;
    private EnvironmentReceiver environmentReceiver;
    private UsbReceiver usbReceiver;
    private PlayerReceiver playerReceiver;
    private LocationReceiver locationReceiver;
    private AudioReceiver audioReceiver;
    private MediaSessionReceiver mediaSessionReceiver;
    private WifiP2PReceiver wifiP2PReceiver;

    private DevicePolicyManager devicePolicyManager;
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;
    private StorageManager storageManager;
    private UsbManager usbManager;
    private InputManager inputManager;
    private MediaPlayer player;
    private LocationManager locationManager;
    private AudioManager audioManager;
    private MediaSessionManager mediaSessionManager;
    private WifiP2pManager wifiP2pManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 101);
            }
        }

        lineVisualizer = findViewById(R.id.visualizer);
        trackName = findViewById(R.id.trackName);
        trackArtist = findViewById(R.id.trackArtist);

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        storageManager = (StorageManager) getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
        usbManager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
        inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        player = new MediaPlayer();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);

        secureButton = findViewById(R.id.secureButton);
        secureButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                grantIntent.setData(Uri.parse("package:com.vf_test_automation_framework"));
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

        doNotDisturbButton = findViewById(R.id.disturbButton);
        doNotDisturbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent disturbIntent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS");
                disturbIntent.setData(Uri.parse("package:com.vf_test_automation_framework"));
                startActivity(disturbIntent);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothReceiver.BLUETOOTH_REMOTE);
//        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(WifiReceiver.WIFI_REMOTE);
        intentFilter.addAction(EnvironmentReceiver.ENVIRONMENT_REMOTE);
        intentFilter.addAction(UsbReceiver.USB_REMOTE);
        intentFilter.addAction(PlayerReceiver.PLAYER_REMOTE);
        intentFilter.addAction(LocationReceiver.LOCATION_REMOTE);

        intentFilter.addAction(TelephonyReceiver.TELEPHONY_REMOTE);
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.addAction("android.bluetooth.mapmce.profile.action.MESSAGE_RECEIVED");
        intentFilter.addAction(AudioReceiver.AUDIO_REMOTE);
        intentFilter.addAction(MediaSessionReceiver.MEDIA_SESSION_REMOTE);
        intentFilter.addAction(WifiP2PReceiver.WIFI_P2P_REMOTE);


        bluetoothReceiver = new BluetoothReceiver();
        wifiReceiver = new WifiReceiver(wifiManager);
        telephonyReceiver = new TelephonyReceiver(telephonyManager);
        environmentReceiver = new EnvironmentReceiver(storageManager);
        usbReceiver = new UsbReceiver(usbManager, inputManager);
        playerReceiver = new PlayerReceiver(player, getApplicationContext(), this);
        locationReceiver = new LocationReceiver(locationManager);
        audioReceiver = new AudioReceiver(audioManager);
        mediaSessionReceiver = new MediaSessionReceiver(mediaSessionManager, getApplicationContext());
        wifiP2PReceiver = new WifiP2PReceiver(wifiP2pManager,getApplicationContext());

        registerReceiver(bluetoothReceiver, intentFilter);
        registerReceiver(wifiReceiver, intentFilter);
        registerReceiver(telephonyReceiver, intentFilter);
        registerReceiver(environmentReceiver, intentFilter);
        registerReceiver(usbReceiver, intentFilter);
        registerReceiver(playerReceiver, intentFilter);
        registerReceiver(locationReceiver, intentFilter);
        registerReceiver(audioReceiver, intentFilter);
        registerReceiver(mediaSessionReceiver, intentFilter);
        registerReceiver(wifiP2PReceiver, intentFilter);

    }

    public void setVideoDisplay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adminButton.setVisibility(View.GONE);
                secureButton.setVisibility(View.GONE);
                doNotDisturbButton.setVisibility(View.GONE);
                trackName.setVisibility(View.GONE);
                trackArtist.setVisibility(View.GONE);
                lineVisualizer.setVisibility(View.GONE);
//                lineVisualizer.release();
                surfaceView.setVisibility(View.VISIBLE);
            }
        });
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.setDisplay(surfaceView.getHolder());
            }
        });
    }

    public void setDefaultDisplay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adminButton.setVisibility(View.VISIBLE);
                secureButton.setVisibility(View.VISIBLE);
                doNotDisturbButton.setVisibility(View.VISIBLE);
                lineVisualizer.setVisibility(View.GONE);
                trackName.setVisibility(View.GONE);
                trackArtist.setVisibility(View.GONE);

//                lineVisualizer.release();
                surfaceView.setVisibility(View.GONE);
            }
        });
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.setDisplay(null);
            }
        });
    }

    public void setTrackInfo(String title, String artist) {
        trackName.setText(title);
        if (artist != null) {
            trackArtist.setText(artist);
        } else {
            trackArtist.setText("");
        }
    }


    public void setLineVisualisation(int session) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adminButton.setVisibility(View.GONE);
                secureButton.setVisibility(View.GONE);
                doNotDisturbButton.setVisibility(View.GONE);
                surfaceView.setVisibility(View.GONE);
                lineVisualizer.setVisibility(View.VISIBLE);
                trackName.setVisibility(View.VISIBLE);
                trackArtist.setVisibility(View.VISIBLE);

                lineVisualizer.setPlayer(session);
            }
        });
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.setDisplay(null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(telephonyReceiver);
        unregisterReceiver(environmentReceiver);
        unregisterReceiver(usbReceiver);
        unregisterReceiver(playerReceiver);
        unregisterReceiver(locationReceiver);
        unregisterReceiver(audioReceiver);
        unregisterReceiver(mediaSessionReceiver);
        unregisterReceiver(wifiP2PReceiver);
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.reset();
            player.release();
            player = null;
        }
        setDefaultDisplay();
    }

}