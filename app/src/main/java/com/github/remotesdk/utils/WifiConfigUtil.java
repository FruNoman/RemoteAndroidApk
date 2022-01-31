package com.github.remotesdk.utils;

import android.annotation.SuppressLint;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;

import java.lang.reflect.Field;

public class WifiConfigUtil {
    public static WifiConfiguration getPassWifiConfig(String networkName, String networkPass,boolean hidden) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + networkName + "\"";
        wifiConfig.preSharedKey = "\"" + networkPass + "\"";
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.hiddenSSID = hidden;
        return wifiConfig;
    }

    @SuppressLint("NewApi")
    public static WifiConfiguration getWpa3WifiConfig(String networkName, String networkPass,boolean hidden) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + networkName + "\"";
        configuration.preSharedKey = "\"" + networkPass + "\"";
        try {
            configuration.setSecurityParams(WifiConfiguration.SECURITY_TYPE_SAE);
            configuration.hiddenSSID = hidden;
        }catch (NoSuchMethodError e){
            configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.SAE);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.GCMP_256);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.GCMP_256);
            configuration.hiddenSSID= true;
            Field fieldname = null;
            try {
                fieldname = configuration.getClass().getField("requirePMF");
            } catch (NoSuchFieldException ne) {
                try {
                    fieldname = configuration.getClass().getField("requirePmf");
                } catch (NoSuchFieldException noSuchFieldException) {
                    noSuchFieldException.printStackTrace();
                }
            }
            if (fieldname != null) {
                try {
                    fieldname.set(configuration, true);
                } catch (IllegalAccessException fe) {
                    e.printStackTrace();
                }
            }
        }
        return configuration;
    }

    public static WifiConfiguration getWepWifiConfig(String networkName, String networkPass,boolean hidden) {
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
        wifiConfig.hiddenSSID = hidden;
        if (networkPass.matches("^[0-9a-fA-F]+$")) {
            wifiConfig.wepKeys[0] = networkPass;
        } else {
            wifiConfig.wepKeys[0] = "\"" + networkPass + "\"";
        }
        wifiConfig.wepTxKeyIndex = 0;
        return wifiConfig;
    }

    public static WifiConfiguration getHotspotPassConfig(String networkName, String networkPass,int appBand) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = networkName;
        conf.preSharedKey = networkPass;
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedKeyManagement.set(4);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        try {
            Field fieldname = WifiConfiguration.class.getField("apBand");
            if(fieldname != null) {
                fieldname.set(conf, appBand);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return conf;
    }

    public static WifiConfiguration getHotspotWpa3Config(String networkName, String networkPass,int appBand) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = networkName;
        conf.preSharedKey = networkPass;
        conf.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.SIM);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SAE);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.SAE);
        try {
            Field fieldname = WifiConfiguration.class.getField("apBand");
            if(fieldname != null) {
                fieldname.set(conf, appBand);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return conf;
    }

    public static WifiConfiguration getHotspotWpa2Wpa3Config(String networkName, String networkPass,int appBand) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = networkName;
        conf.preSharedKey = networkPass;
        conf.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.SIM);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SAE);
        conf.allowedKeyManagement.set(13);
        try {
            Field fieldname = WifiConfiguration.class.getField("apBand");
            if(fieldname != null) {
                fieldname.set(conf, appBand);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return conf;
    }

    public static WifiConfiguration getHotspotOpenConfig(String networkName,int appBand) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = networkName;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        conf.allowedAuthAlgorithms.clear();
        try {
            Field fieldname = WifiConfiguration.class.getField("apBand");
            if(fieldname != null) {
                fieldname.set(conf, appBand);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return conf;
    }

    public static WifiConfiguration getOpenWifiConfig(String networkName,boolean hidden) {
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
        wifiConfig.hiddenSSID = hidden;

        return wifiConfig;
    }
}
