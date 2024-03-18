package com.bcit.soundshift;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.res.AssetManager;

import java.io.FileOutputStream;
import java.io.InputStream;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private ListView shiftList;
    private static final int PERMISSION_REQUEST_READ_MEDIA_AUDIO = 1;
    private int currentApiVersion = android.os.Build.VERSION.SDK_INT;


    private MediaPlayer mediaPlayer;

    private void playMusic(String filePath) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(MainActivity.this, Uri.parse(filePath));
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.i("Debugging", "Playing music now");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CheckPermission() {
        // Check if the permission is not granted
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO}, PERMISSION_REQUEST_READ_MEDIA_AUDIO);
        } else {
            // Permission is already granted, proceed with accessing the file
            Log.i("PERMISSION GRANTED", "YOU ALREADY HAD FILE PERMISSION");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_MEDIA_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Log.e("ERROR", "PERMISSION DENIED");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        makeListWork();
        setupButtonOnClickListener();


        CheckPermission();
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
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();

                String filePath = path + "/sample_music.mp3";
                playMusic(filePath);
            }
        });
    }

    private void makeListWork() {

        ArrayList<Shift> shifts = new ArrayList<>();

        DatabaseHelper sql = new DatabaseHelper(this);
        try {
            sql.createDataBase();
            sql.openDatabase();
            try {
                ArrayList<ArrayList<String>> results = sql.cursorToList(sql.executeQuery("SELECT * FROM shift"));
                for (int i = 0; i < results.size(); i++) {
                    shifts.add(new Shift(this, results.get(i).get(1), Integer.parseInt(results.get(i).get(0))));
                }
            } catch (Exception e) {
                System.out.println("An exception occurred: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("An exception occurred: " + e.getMessage());
        }

        ArrayAdapter<Shift> shiftAdapter = new ArrayAdapter<Shift>(this, android.R.layout.simple_list_item_1, shifts);
        shiftList.setAdapter(shiftAdapter);

        // Set click listener for the ListView
        shiftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the Shift object corresponding to the clicked position
                Shift clickedShift = shifts.get(position);

                // Start the next activity and pass the Shift object as an extra
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                intent.putExtra("shift", clickedShift);
                startActivity(intent);
            }
        });
    }



}