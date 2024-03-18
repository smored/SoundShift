package com.bcit.soundshift;



import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;

public class ShiftPlayer extends MediaPlayer {
    // TODO: Play Pause button, Find media on disk. Play song with media player. Get track length and progress. Play multiple files? Load next file while song is playing so dont have to wait
    // Important for later: use mediaPlayer in a service so it can run as a background activity
    private boolean isPlaying;
    private int songLength;
    private int songProgress;

    public ShiftPlayer(@NonNull Context context) {
        this.isPlaying = false;
    }


    /**
     * Handles creating and starting the music player for a given song path
     * @param context pass in this context
     * @param filePath path of the song on disk
     */
    public void shift_startMusic(Context context, String filePath) throws IOException {
        if (this.isPlaying()) {
            Log.w("ShiftPlayer", "Trying to play but already initialized");
            return;
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + filePath;
        Log.i("ShiftPlayer", "Attempting to play file with path: " + path);
        setDataSource(context, Uri.parse(path));
        prepare();
        start();
        isPlaying = true;
    }

    /**
     * Pauses if playing and Plays if paused
     */
    public void shift_pausePlay () {
        if (isPlaying) {
            pause();
            isPlaying = false;
        } else {
            start();
            isPlaying = true;
        }
    }

    /**
     * Overloaded function that allows to set instead of toggle play state
     * @param forcePlay true = Force Play, false = Force Pause
     */
    public void shift_pausePlay (boolean forcePlay) {
        if (forcePlay) {
            start();
        } else {
            pause();
        }
        isPlaying = forcePlay;
    }

    /**
     * Stops music and releases media player; must call shift_startMusic() to start a new track
     */
    public void shift_stopMusic() {
        stop();
        release();
        isPlaying = false;
    }

    /**
     * returns if the media player is currently playing something
     * @return isPlaying bool
     */
    public boolean shift_getIsPlaying() {
        return isPlaying;
    }

}
