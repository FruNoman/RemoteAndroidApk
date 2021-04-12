package com.github.remotesdk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.remotesdk.utils.PlayerUtils;

public class PlayerReceiver extends BroadcastReceiver {
    public static final String PLAYER_COMMAND = "player_command";
    public static final String PLAYER_REMOTE = "com.github.remotesdk.PLAYER_REMOTE";

    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private final String PLAY_SONG = "playSong";
    private final String STOP_SONG = "stopSong";
    private final String SEEK_TO = "seekToSong";
    private final String GET_DURATION = "getSongDuration";
    private final String GET_CURRENT_POSITION = "getSongCurrentPosition";
    private final String IS_SONG_PLAYING = "isSongPlaying";
    private final String IS_SONG_LOOPING = "isSongLooping";
    private final String SET_LOOPING = "setSongLooping";


    private MediaPlayer mediaPlayer;
    private Context context;
    private PlayerUtils playerUtils;

    public PlayerReceiver(MediaPlayer mediaPlayer, Context context) {
        this.mediaPlayer = mediaPlayer;
        this.playerUtils = new PlayerUtils(mediaPlayer, context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(PLAYER_REMOTE)) {
                String command = intent.getStringExtra(PLAYER_COMMAND);
                if (command.contains(PLAY_SONG)) {
                    String dataSource = command.split(",")[1];
                    playerUtils.playSong(dataSource);
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(STOP_SONG)) {
                    playerUtils.stopSong();
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.contains(SEEK_TO)) {
                    int time = Integer.parseInt(command.split(",")[1]);
                    playerUtils.seekTo(time);
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(GET_DURATION)) {
                    int result = mediaPlayer.getDuration();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_CURRENT_POSITION)) {
                    int result = mediaPlayer.getCurrentPosition();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(IS_SONG_PLAYING)) {
                    boolean result = mediaPlayer.isPlaying();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(SET_LOOPING)) {
                    boolean state = Boolean.parseBoolean(command.split(",")[1]);
                    mediaPlayer.setLooping(state);
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(IS_SONG_LOOPING)) {
                    boolean result = mediaPlayer.isLooping();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }
            }
        } catch (Exception e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                String json = mapper.writeValueAsString(e);
                setResult(ERROR_CODE, json, new Bundle());
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        }
    }
}
