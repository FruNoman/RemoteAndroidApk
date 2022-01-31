package com.github.remotesdk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.github.remotesdk.MainActivity;
import com.github.remotesdk.utils.PlayerUtils;


public class PlayerReceiver extends BroadcastReceiver {
    public static final String PLAYER_COMMAND = "player_command";
    public static final String PLAYER_REMOTE = "com.vf_test_automation_framework.PLAYER_REMOTE";
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
    private final String DISPLAY_VIDEO = "displayPlayerView";
    private final String PLAY_FOLDER = "playFolder";
    private final String NEXT_SONG = "nextSong";
    private final String PREV_SONG = "prevSong";
    private final String PAUSE_SONG = "pauseSong";
    private final String REV_SONG = "revSong";

    private final String GET_CURRENT_PLAYING_FILE = "getCurrentPlayingFile";
    private final String GET_MEDIA_METADATA = "getMediaMetadata";


    private MediaPlayer mediaPlayer;
    private PlayerUtils playerUtils;
    private MainActivity activity;


    public PlayerReceiver(MediaPlayer mediaPlayer, Context context, MainActivity activity) {
        this.mediaPlayer = mediaPlayer;
        this.playerUtils = new PlayerUtils(mediaPlayer, context, activity);
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(PLAYER_REMOTE)) {
                String command = intent.getStringExtra(PLAYER_COMMAND);
                if (command.contains(PLAY_SONG)) {
                    String dataSource = command.split(",")[1];
                    playerUtils.setMediaFile(dataSource);
                    playerUtils.playSong();
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.contains(PLAY_FOLDER)) {
                    String dataSource = command.split(",")[1];
                    playerUtils.setMediaFolder(dataSource);
                    playerUtils.playSong();
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(STOP_SONG)) {
                    playerUtils.stopSong();
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(PAUSE_SONG)) {
                    playerUtils.pause();
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(REV_SONG)) {
                    playerUtils.rev();
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(NEXT_SONG)) {
                    playerUtils.nextSong();
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(PREV_SONG)) {
                    playerUtils.prevSong();
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
                } else if (command.equals(GET_CURRENT_PLAYING_FILE)) {
                    String result = playerUtils.getCurrentPlayingFile();
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.contains(GET_MEDIA_METADATA)) {
                    String dataSource = command.split(",")[1];
                    int metaData = Integer.parseInt(command.split(",")[2]);
                    String result = playerUtils.getMediaMetadata(dataSource, metaData);
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.contains(DISPLAY_VIDEO)) {
                    String display = command.split(",")[1];
                    View decorView = activity.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_IMMERSIVE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
                    if (display.equals("video")) {
                        activity.setVideoDisplay();
                    } else if (display.equals("equalizer")) {
                        activity.setLineVisualisation(mediaPlayer.getAudioSessionId());
                    } else {
                        activity.setDefaultDisplay();
                    }
                    setResult(SUCCESS_CODE, "", new Bundle());
                }

            }
        } catch (Exception e) {
            setResult(ERROR_CODE, "error", new Bundle());
            e.printStackTrace();
        }
    }
}
