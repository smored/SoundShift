package com.bcit.soundshift;



import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
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

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public ShiftPlayer(@NonNull Context context) {
        super((Context) context);
        this.isPlaying = false;
    }



    /**
     * Handles creating and starting the music player for a given song path
     * @param context pass in this context
     * @param songName name of the song on disk
     */
    public void shift_startMusic(Context context, String songName) throws IOException {
//        final String defType = "raw";
//        int resId = Resources.getSystem().getIdentifier(songPath, defType, context.getPackageName());
//        if (resId == 0) throw new RuntimeException("Failed to find song with path: " + songPath + ", in: " + defType);
        //create(context, resId);
        String musicFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + songName;
        setDataSource(musicFilePath);
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
