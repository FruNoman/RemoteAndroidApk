package com.github.remotesdk.receivers;

import static android.os.Looper.getMainLooper;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiP2PReceiver extends BroadcastReceiver {
    public static final String WIFI_P2P_COMMAND = "wifi_p2p_command";
    public static final String WIFI_P2P_REMOTE = "com.github.remotesdk.WIFI_P2P_REMOTE";

    private final String INITIALIZE = "initialize";
    private final String DISCOVER_PEERS = "discoverPeers";
    private final String STOP_DISCOVER_PEERS = "stopDiscoverPeers";
    private final String REQUEST_PEERS = "requestPeers";
    private final String CONNECT = "connectToPeer";
    private final String REMOVE_P2P_CONNECTION = "removeP2PConnection";
    private final String REMOVE_PERSISTENT_GROUP = "deletePersistentGroup";
    private final String REQUEST_GROUP_INFO = "requestGroupInfo";
    private final String GET_GROUP_INFO = "getGroupInfo";
    private final String REQUEST_DISCOVERY_STATE = "requestDiscoveryState";
    private final String GET_DISCOVERY_STATE = "getDiscoveryState";
    private final String REQUEST_P2P_STATE = "requestP2PState";
    private final String GET_P2P_STATE = "getP2PState";

    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private WifiP2pManager adapter;
    private Context appContext;
    private WifiP2pManager.Channel channel;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private List<WifiP2pGroup> groups = new ArrayList<WifiP2pGroup>();
    private int discoveryState = 1;
    private int p2pState = 1;

    private Object monitor = new Object();


    public WifiP2PReceiver(WifiP2pManager adapter, Context context) {
        this.adapter = adapter;
        this.appContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(WIFI_P2P_REMOTE)) {
                String command = intent.getStringExtra(WIFI_P2P_COMMAND);
                if (command.equals(INITIALIZE)) {
                    channel = adapter.initialize(appContext.getApplicationContext(), getMainLooper(), null);
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.equals(DISCOVER_PEERS)) {
                    if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    adapter.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int reason) {
                        }
                    });
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.equals(REQUEST_PEERS)) {
                    adapter.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                        @Override
                        public void onPeersAvailable(WifiP2pDeviceList peerList) {
                            peers = new ArrayList<>(peerList.getDeviceList());
                        }
                    });
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(peers);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());

                } else if (command.equals(STOP_DISCOVER_PEERS)) {
                    adapter.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.equals(REQUEST_GROUP_INFO)) {
                    groups = new ArrayList<>();
                    adapter.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                        @Override
                        public void onGroupInfoAvailable(WifiP2pGroup group) {
                            groups.add(group);
                        }
                    });
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.equals(GET_GROUP_INFO)) {
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(groups);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(REQUEST_DISCOVERY_STATE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        adapter.requestDiscoveryState(channel, new WifiP2pManager.DiscoveryStateListener() {
                            @Override
                            public void onDiscoveryStateAvailable(int state) {
                                discoveryState = state;
                            }
                        });
                    }
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.equals(GET_DISCOVERY_STATE)) {
                    setResult(SUCCESS_CODE, String.valueOf(discoveryState), new Bundle());
                } else if (command.equals(GET_P2P_STATE)) {
                    setResult(SUCCESS_CODE, String.valueOf(p2pState), new Bundle());
                } else if (command.equals(REQUEST_P2P_STATE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        adapter.requestP2pState(channel, new WifiP2pManager.P2pStateListener() {
                            @Override
                            public void onP2pStateAvailable(int state) {
                                p2pState = state;
                            }
                        });
                    }
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.contains(CONNECT)) {
                    String address = command.split(",")[1];
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = address;
                    config.wps.setup = WpsInfo.PBC;
                    adapter.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.equals(REMOVE_P2P_CONNECTION)) {
                    adapter.removeGroup(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                } else if (command.contains(REMOVE_PERSISTENT_GROUP)) {
                    int groupId = Integer.parseInt(command.split(",")[1]);
                    try {
                        Method method = adapter.getClass().getMethod("deletePersistentGroup", channel.getClass(), int.class, WifiP2pManager.ActionListener.class);
                        method.invoke(adapter, channel, groupId, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    } catch (NoSuchMethodException e) {
                    }
                    setResult(SUCCESS_CODE, String.valueOf(""), new Bundle());
                }
            }
        } catch (Exception e) {
            setResult(ERROR_CODE, "error", new Bundle());
        }
    }

}
