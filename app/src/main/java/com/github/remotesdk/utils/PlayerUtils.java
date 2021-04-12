package com.github.remotesdk.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;

public class PlayerUtils {
    private MediaPlayer mediaPlayer;
    private Context context;

    public PlayerUtils(MediaPlayer mediaPlayer, Context context) {
        this.mediaPlayer = mediaPlayer;
        this.context = context;
    }

    public void playSong(String dataSource) {
        if (mediaPlayer != null) {
            try {
                Uri path = Uri.parse(dataSource);
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, path);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
            } catch (Exception e) {

            }
        }
    }

    public void seekTo(int msec) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        SystemClock.sleep(200);
                        if (!mp.isPlaying()) {
                            mp.start();
                        }
                    }
                });
                mediaPlayer.seekTo(msec);
            } catch (Exception e) {

            }
        }
    }

}
