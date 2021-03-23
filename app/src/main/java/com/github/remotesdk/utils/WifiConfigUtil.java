package com.github.remotesdk.utils;

import android.net.wifi.WifiConfiguration;

public class WifiConfigUtil {
    public static WifiConfiguration getPassWifiConfig(String networkName, String networkPass) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + networkName + "\"";
        wifiConfig.preSharedKey = "\"" + networkPass + "\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        return wifiConfig;
    }

    public static WifiConfiguration getWepWifiConfig(String networkName, String networkPass) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + networkName + "\"";
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.priority = 40;
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        if (networkPass.matches("^[0-9a-fA-F]+$")) {
            wifiConfig.wepKeys[0] = networkPass;
        } else {
            wifiConfig.wepKeys[0] = "\"" + networkPass + "\"";
        }
        wifiConfig.wepTxKeyIndex = 0;
        return wifiConfig;
    }

    public static WifiConfiguration getOpenWifiConfig(String networkName) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + networkName + "\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedAuthAlgorithms.clear();
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        return wifiConfig;
    }
}
