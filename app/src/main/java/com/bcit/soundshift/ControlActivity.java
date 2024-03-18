package com.bcit.soundshift;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ControlActivity extends AppCompatActivity {
    private TextView lyrics;
    private ImageView coverArt;
    private Button reverseButton;
    private Button playButton;
    private Button forwardButton;
    private Shift currentShift;
    private ArrayList<Integer> whatsPlaying;
    private ShiftPlayer shiftPlayer;
    private ArrayList<ArrayList<Integer>> playedSongList;
    private int song_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);
        findView();
        setupButtonOnClickListener();
        playedSongList = new ArrayList<>();

        currentShift = (Shift) getIntent().getSerializableExtra("shift");
        currentShift.transientDatabase(this);
        currentShift.openDatabase();

        shiftPlayer = new ShiftPlayer(this);
        song_pos = 0;
        whatsPlaying = currentShift.getNextSong();
        playedSongList.add(whatsPlaying);
        PlaySong();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentShift.closeDatabase();
    }

    private void findView() {
        lyrics = findViewById(R.id.lyrics);
        coverArt = findViewById(R.id.albumArt);
        reverseButton = findViewById(R.id.reverseButton);
        playButton = findViewById(R.id.playButton);
        forwardButton = findViewById(R.id.forwardButton);
    }

    private void setupButtonOnClickListener() {
        // go back button
        reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start button redirects us to the control activity
                if(song_pos == 0)
                {
                    shiftPlayer.seekTo(0);
                }
                else
                {
                    if(shiftPlayer.getCurrentPosition() > 1000 * 10)
                    {
                        shiftPlayer.seekTo(0);
                    }
                    else
                    {
                        song_pos--;
                        whatsPlaying = playedSongList.get(song_pos);
                        PlaySong();
                    }
                }
            }
        });

        // play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shiftPlayer.shift_pausePlay();
            }
        });

        // go forwards button
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playedSongList.size() - 1 > song_pos)
                {
                    whatsPlaying = currentShift.getNextSong();
                    song_pos++;
                    playedSongList.set(song_pos, whatsPlaying);
                    PlaySong();
                }
                else
                {
                    whatsPlaying = currentShift.getNextSong();
                    playedSongList.add(whatsPlaying);
                    song_pos++;
                    PlaySong();
                }
            }
        });
    }

    private void playMusic(String filePath) throws IOException {
        shiftPlayer.shift_startMusic(this, filePath);
        Toast.makeText(this, "Attempting play " + filePath + "... Code: " + shiftPlayer.shift_getIsPlaying(), Toast.LENGTH_SHORT).show();
    }

    private void PlaySong()
    {
        ArrayList<String> whatsPlaying_str = currentShift.getSongAndPlaylistNames(whatsPlaying);
        lyrics.setText("Current Playlist: " + whatsPlaying_str.get(0) + " Current Song: " + whatsPlaying_str.get(1));

        Bitmap bitmap;
        bitmap = currentShift.getAlbumImage(whatsPlaying.get(1));
        coverArt.setImageBitmap(bitmap);

        String path = currentShift.getFilePath(whatsPlaying.get(1));

        try {
            playMusic(path);
        } catch (IOException e) {
            Log.e("ERROR", "File not found... ");
        }
    }
}
