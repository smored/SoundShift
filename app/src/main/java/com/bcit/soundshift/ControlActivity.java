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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);
        findView();
        setupButtonOnClickListener();
        currentShift = (Shift) getIntent().getSerializableExtra("shift");
        currentShift.transientDatabase(this);
        shiftPlayer = new ShiftPlayer(this);
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
                lyrics.setText("WOAH!!! did you really like the song that much??!?!?");
            }
        });

        // play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start button redirects us to the control activity
                lyrics.setText("WOAH!!! did you just PLAY??!?!?");
            }
        });

        // go forwards button
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentShift.openDatabase();
                whatsPlaying = currentShift.getNextSong();
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

                currentShift.closeDatabase();


            }
        });
    }

    private void playMusic(String filePath) throws IOException {
        shiftPlayer.shift_startMusic(this, filePath);
        Toast.makeText(this, "Attempting play " + filePath + "... Code: " + shiftPlayer.shift_getIsPlaying(), Toast.LENGTH_SHORT).show();
    }



}
