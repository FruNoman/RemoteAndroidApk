package com.github.remotesdk.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import com.github.remotesdk.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerUtils {
    private MediaPlayer mediaPlayer;
    private Context context;
    private MediaSessionCompat mediaSessionCompat;
    private List<String> playList = new ArrayList<>();
    private int nowPlaying = 0;
    private MainActivity activity;


    public PlayerUtils(MediaPlayer mediaPlayer, Context context, MainActivity activity) {
        this.mediaPlayer = mediaPlayer;
        this.context = context;
        this.mediaSessionCompat = new MediaSessionCompat(context, "MEDIA");
        this.activity = activity;
    }

    public void setMediaFile(String file) {
        this.nowPlaying = 0;
        playList = new ArrayList<>();
        playList.add(file);
    }

    public void setMediaFolder(String file) {
        this.nowPlaying = 0;
        playList = new ArrayList<>();
        File[] files = new File(file).listFiles();
        for (File track : files) {
            playList.add(track.getAbsolutePath());
        }
    }

    public String getCurrentPlayingFile() {
        return playList.get(nowPlaying);
    }

    public String getMediaMetadata(String dataSource, int mediaMetadataCompat) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(dataSource);
        if (mediaMetadataCompat == MediaMetadataRetriever.METADATA_KEY_TITLE) {
            String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (title == null) {
                title = new File(dataSource).getName();
            }
            return title;
        } else {
            return metaRetriever.extractMetadata(mediaMetadataCompat);
        }
    }

    public void playSong() {
        if (mediaPlayer != null) {
            try {
                String dataSource = playList.get(nowPlaying);
                Uri path = Uri.parse(dataSource);

                String title = "";
                String artist = "";
                long duration = 0;
                try {
                    MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                    metaRetriever.setDataSource(dataSource);
                    title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    if (title == null) {
                        title = new File(dataSource).getName();
                    }
                    artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    duration = Long.parseLong(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                } catch (Exception e) {
                    title = new File(dataSource).getName();
                }
                mediaSessionCompat.setCallback(mediaSessionCompatCallBack);
                mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
                PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                        .setActions(
                                PlaybackStateCompat.ACTION_PLAY |
                                        PlaybackStateCompat.ACTION_PAUSE |
                                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                        PlaybackStateCompat.ACTION_PLAY_PAUSE)
                        .setState(PlaybackStateCompat.STATE_PLAYING,
                                0, 1);
                ;

                mediaSessionCompat.setPlaybackState(mStateBuilder.build());
                mediaSessionCompat.setActive(true);
                mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        .build());
                activity.setTrackInfo(title, artist);
                mediaSessionCompat.setPlaybackState(mStateBuilder.build());

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
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        nextSong();
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public void nextSong() {
        nowPlaying = nowPlaying + 1;
        if (nowPlaying == playList.size()) {
            nowPlaying = 0;
        }
        playSong();
    }

    public void pause() {
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED,
                        mediaPlayer.getCurrentPosition(), 1);
        mediaSessionCompat.setPlaybackState(mStateBuilder.build());
        mediaPlayer.pause();
    }

    public void rev() {
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING,
                        mediaPlayer.getCurrentPosition(), 1);
        mediaSessionCompat.setPlaybackState(mStateBuilder.build());
        mediaPlayer.start();
    }

    public void prevSong() {
        nowPlaying = nowPlaying - 1;
        if (nowPlaying <= 0) {
            nowPlaying = playList.size() - 1;
        }
        playSong();
    }


    public void stopSong() {
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED,
                        mediaPlayer.getCurrentPosition(), 1);
        mediaSessionCompat.setPlaybackState(mStateBuilder.build());
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
                PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING,
                                msec, 1);
                ;

                mediaSessionCompat.setPlaybackState(mStateBuilder.build());
            } catch (Exception e) {

            }
        }
    }


    private MediaSessionCompat.Callback mediaSessionCompatCallBack = new MediaSessionCompat.Callback() {
        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            String intentAction = mediaButtonEvent.getAction();

            if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (event != null) {
                    int action = event.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                                if (!mediaPlayer.isPlaying()) {
                                    rev();
                                }
//                                Toast.makeText(context, "Play Button is pressed!", Toast.LENGTH_SHORT).show();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                nextSong();
//                                Toast.makeText(context, "Next Button is pressed!", Toast.LENGTH_SHORT).show();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                                if (mediaPlayer.isPlaying()) {
                                    pause();
                                }
//                                Toast.makeText(context, "Pause Button is pressed!", Toast.LENGTH_SHORT).show();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                prevSong();
//                                Toast.makeText(context, "Previous Button is pressed!", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;

                    }
                }
            }
            return super.onMediaButtonEvent(mediaButtonEvent);
        }
    };

}
