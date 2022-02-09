package com.github.remotesdk.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MicrophoneInfo;
import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;

public class AudioReceiver extends BroadcastReceiver {
    public static final String COMMAND = "command";
    public static final String AUDIO_REMOTE = "com.github.remotesdk.AUDIO_REMOTE";

    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private final String START_BLUETOOTH_SCO = "startBluetoothSco";
    private final String STOP_BLUETOOTH_SCO = "stopBluetoothSco";
    private final String IS_BLUETOOTH_SCO_ON = "isBluetoothScoOn";
    private final String SET_BLUETOOTH_A2DP_ON = "setBluetoothA2dpOn";
    private final String SET_SPEAKER_PHONE_ON = "setSpeakerphoneOn";
    private final String SET_MICROPHONE_MUTE = "setMicrophoneMute";
    private final String IS_SPEAKER_PHONE_ON = "isSpeakerphoneOn";
    private final String IS_BLUETOOTH_SCO_AVAILABLE_OFF_CALL = "isBluetoothScoAvailableOffCall";
    private final String IS_MICROPHONE_MUTE = "isMicrophoneMute";
    private final String IS_MUSIC_ACTIVE = "isMusicActive";
    private final String IS_STREAM_MUTE = "isStreamMute";
    private final String IS_BLUETOOTH_A2DP_ON = "isBluetoothA2dpOn";
    private final String IS_WIRED_HEADSET_ON = "isWiredHeadsetOn";
    private final String GET_MODE = "getMode";
    private final String GET_RINGER_MODE = "getRingerMode";
    private final String GET_PROPERTY = "getProperty";
    private final String GET_PARAMETERS = "getParameters";
    private final String GET_MICROPHONES = "getMicrophones";
    private final String SET_WIRED_HEADSET_ON = "setWiredHeadsetOn";
    private final String SET_BLUETOOTH_SCO_ON = "setBluetoothScoOn";
    private final String SET_MODE = "setMode";
    private final String SET_RINGER_MODE = "setRingerMode";
    private final String SET_STREAM_VOLUME = "setStreamVolume";
    private final String GET_AUDIO_DEVICES = "getAudioDevicesInfo";
    private final String GET_STREAM_VOLUME = "getAudioStreamVolume";
    private final String GET_STREAM_MAX_VOLUME = "getAudioStreamMaxVolume";
    private final String ADJUST_VOLUME = "adjustVolume";

    private final String IS_VOLUME_FIXED = "isVolumeFixed";

    private AudioManager adapter;

    public AudioReceiver(AudioManager audioManager) {
        this.adapter = audioManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(AUDIO_REMOTE)) {
                String command = intent.getStringExtra(COMMAND);
                if (command.equals(START_BLUETOOTH_SCO)) {
                    adapter.startBluetoothSco();
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(STOP_BLUETOOTH_SCO)) {
                    adapter.stopBluetoothSco();
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(IS_BLUETOOTH_SCO_ON)) {
                    boolean result = adapter.isBluetoothScoOn();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(SET_BLUETOOTH_A2DP_ON)) {
                    boolean state = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    adapter.setBluetoothA2dpOn(state);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(SET_SPEAKER_PHONE_ON)) {
                    boolean state = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    adapter.setSpeakerphoneOn(state);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(SET_MICROPHONE_MUTE)) {
                    boolean state = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    adapter.setMicrophoneMute(state);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(IS_SPEAKER_PHONE_ON)) {
                    boolean result = adapter.isSpeakerphoneOn();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_BLUETOOTH_SCO_AVAILABLE_OFF_CALL)) {
                    boolean result = adapter.isBluetoothScoAvailableOffCall();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_MICROPHONE_MUTE)) {
                    boolean result = adapter.isMicrophoneMute();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_MUSIC_ACTIVE)) {
                    boolean result = adapter.isMusicActive();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_STREAM_MUTE)) {
                    int mode = Integer.parseInt(intent.getStringExtra("param0"));
                    boolean result = adapter.isStreamMute(mode);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_BLUETOOTH_A2DP_ON)) {
                    boolean result = adapter.isBluetoothA2dpOn();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_WIRED_HEADSET_ON)) {
                    boolean result = adapter.isWiredHeadsetOn();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_MODE)) {
                    int result = adapter.getMode();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_RINGER_MODE)) {
                    int result = adapter.getRingerMode();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_PROPERTY)) {
                    String property = intent.getStringExtra("param0");
                    String result = adapter.getProperty(property);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(GET_PARAMETERS)) {
                    String parameters = intent.getStringExtra("param0");
                    String result = adapter.getParameters(parameters);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(GET_MICROPHONES)) {
                    @SuppressLint({"NewApi", "LocalSuppress"})
                    List<MicrophoneInfo> microphones = adapter.getMicrophones();
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(microphones);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(SET_WIRED_HEADSET_ON)) {
                    boolean state = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    adapter.setWiredHeadsetOn(state);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(SET_BLUETOOTH_SCO_ON)) {
                    boolean state = Boolean.parseBoolean(intent.getStringExtra("param0"));
                    adapter.setBluetoothScoOn(state);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(SET_MODE)) {
                    int mode = Integer.parseInt(intent.getStringExtra("param0"));
                    adapter.setMode(mode);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(SET_RINGER_MODE)) {
                    int mode = Integer.parseInt(intent.getStringExtra("param0"));
                    adapter.setRingerMode(mode);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(SET_STREAM_VOLUME)) {
                    int mode = Integer.parseInt(intent.getStringExtra("param0"));
                    int index = Integer.parseInt(intent.getStringExtra("param1"));
                    int flag = Integer.parseInt(intent.getStringExtra("param2"));
                    adapter.setStreamVolume(mode, index, flag);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(GET_AUDIO_DEVICES)) {
                    int flag = Integer.parseInt(intent.getStringExtra("param0"));
                    AudioDeviceInfo[] deviceInfos = new AudioDeviceInfo[]{};
                    deviceInfos = adapter.getDevices(flag);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                    String result = mapper.writeValueAsString(deviceInfos);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(GET_STREAM_VOLUME)) {
                    int mode = Integer.parseInt(intent.getStringExtra("param0"));
                    int result = adapter.getStreamVolume(mode);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_STREAM_MAX_VOLUME)) {
                    int mode = Integer.parseInt(intent.getStringExtra("param0"));
                    int result = adapter.getStreamMaxVolume(mode);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(ADJUST_VOLUME)) {
                    int direction = Integer.parseInt(intent.getStringExtra("param0"));
                    int flag = Integer.parseInt(intent.getStringExtra("param1"));
                    adapter.adjustVolume(direction, flag);
                    setResult(SUCCESS_CODE, "success", new Bundle());
                } else if (command.equals(IS_VOLUME_FIXED)) {
                    boolean result = adapter.isVolumeFixed();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

            }
        } catch (Exception e) {
            setResult(ERROR_CODE, e.getLocalizedMessage(), new Bundle());
        }
    }
}
