package com.bcit.soundshift;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private ListView shiftList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        makeListWork();
        setupButtonOnClickListener();
    }

    private void findView() {
        startButton = findViewById(R.id.startButton);
        shiftList = findViewById(R.id.shiftList);
    }

    private void setupButtonOnClickListener() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start button redirects us to the control activity
                Intent controlActivityIntent = new Intent(MainActivity.this, ControlActivity.class);
                startActivity(controlActivityIntent);
            }
        });
    }

    private void makeListWork() {
        // hardcoding for testing
        Playlist[] playlists = {new Playlist()};
        Shift[] shifts = {
                new Shift("Chill", 1, playlists),
                new Shift("Rock", 2, playlists),
                new Shift("Metal", 3, playlists)
        };

        ArrayAdapter<Shift> shiftAdapter = new ArrayAdapter<Shift>(this, android.R.layout.simple_list_item_1, shifts);

        shiftList.setAdapter(shiftAdapter);
    }

}