package com.github.remotesdk.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;

import com.github.remotesdk.MainActivity;

public class MediaSessionReceiver extends BroadcastReceiver {
    public static final String MEDIA_SESSION_COMMAND = "media_session_command";
    public static final String MEDIA_SESSION_REMOTE = "com.github.remotesdk.MEDIA_SESSION_REMOTE";

    private MediaSessionManager adapter;
    private ComponentName componentName;

    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private final String TRANSPORT_CONTROL_PLAY = "transportControlPlay";
    private final String TRANSPORT_CONTROL_PAUSE = "transportControlPause";
    private final String TRANSPORT_CONTROL_NEXT = "transportControlNext";
    private final String TRANSPORT_CONTROL_PREV = "transportControlPrev";
    private final String GET_PLAYBACK_STATE_CURRENT_POSITION = "getPlaybackStateCurrentPosition";
    private final String GET_META_DATA = "getMetaData";
    private final String TRANSPORT_CONTROL_IS_PLAYING = "transportControlIsPlaying";



    public MediaSessionReceiver(MediaSessionManager adapter,Context context) {
        this.adapter= adapter;
        this.componentName =new ComponentName(context, MainActivity.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(MEDIA_SESSION_REMOTE)) {
                String command = intent.getStringExtra(MEDIA_SESSION_COMMAND);
                if (command.equals(TRANSPORT_CONTROL_PLAY)) {
                    adapter.getActiveSessions(componentName).get(0).getTransportControls().play();
                    setResult(SUCCESS_CODE, "success", new Bundle());
                }else if (command.equals(TRANSPORT_CONTROL_PAUSE)) {
                    adapter.getActiveSessions(componentName).get(0).getTransportControls().pause();
                    setResult(SUCCESS_CODE, "success", new Bundle());
                }
                else if (command.equals(TRANSPORT_CONTROL_NEXT)) {
                    adapter.getActiveSessions(componentName).get(0).getTransportControls().skipToNext();
                    setResult(SUCCESS_CODE, "success", new Bundle());
                }
                else if (command.equals(TRANSPORT_CONTROL_PREV)) {
                    adapter.getActiveSessions(componentName).get(0).getTransportControls().skipToPrevious();
                    setResult(SUCCESS_CODE, "success", new Bundle());
                }
                else if (command.equals(GET_PLAYBACK_STATE_CURRENT_POSITION)) {
                    long result = adapter.getActiveSessions(componentName).get(0).getPlaybackState().getPosition();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.equals(TRANSPORT_CONTROL_IS_PLAYING)) {
                    boolean result = (adapter.getActiveSessions(componentName).get(0).getPlaybackState().getState() == PlaybackState.STATE_PLAYING);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }
                else if (command.contains(GET_META_DATA)) {
                    String type = command.split(",")[1];
                    String metaData = command.split(",")[2];
                    MediaMetadata data = adapter.getActiveSessions(componentName).get(0).getMetadata();
                    String result = "";
                    if (type.equals("string")){
                        result = data.getString(metaData);
                    }else if (type.equals("long")){
                        result = String.valueOf(data.getLong(metaData));
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }
            }
        } catch (Exception e) {
            setResult(ERROR_CODE, "error", new Bundle());
        }
    }
}
