package com.bcit.soundshift;
import android.Manifest;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    private ListView shiftList;
    private Button prefsButton, playlistButton, songButton;
    private static final int PERMISSION_REQUEST_READ_MEDIA_AUDIO = 1;
    private final int currentApiVersion = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        makeListWork();

        prettyInit();

        if (currentApiVersion >= Build.VERSION_CODES.TIRAMISU) {
            CheckPermission();
        } else {
            OldCheckPermission();
            Log.e("OUT OF DATE", "Skill issue get a better phone");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Check for and request perms
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean CheckPermission() {
        // Check if the permission is not granted
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO}, PERMISSION_REQUEST_READ_MEDIA_AUDIO);
            return false;
        } else {
            // Permission is already granted, proceed with accessing the file
            Log.i("MainActivity", "YOU ALREADY HAD FILE PERMISSION");
            return true;
        }
    }

    private boolean OldCheckPermission() {
        // Check if the permission is not granted
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_MEDIA_AUDIO);
            return false;
        } else {
            // Permission is already granted, proceed with accessing the file
            Log.i("MainActivity", "YOU ALREADY HAD FILE PERMISSION");
            return true;
        }
    }

    private void findView() {
        shiftList = findViewById(R.id.shiftList);
        prefsButton = findViewById(R.id.prefs);
        songButton = findViewById(R.id.toSongScreen);
        playlistButton = findViewById(R.id.toPlaylist2);

        // temporary thing so i can test shift activity
        prefsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShift();
            }
        });

        songButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SongActivity.class);
                startActivity(intent);
            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlaylistListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void makeListWork() {

        ArrayList<Shift> shifts = new ArrayList<>();

        DatabaseHelper sql = new DatabaseHelper(this);
        try {
            //sql.createDataBase();
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

        CustomAdapter<Shift> shiftAdapter = new CustomAdapter<Shift>(this, R.layout.shift_list, shifts);
        shiftList.setAdapter(shiftAdapter);

        shiftAdapter.setTextClickListener(new CustomAdapter.OnTextClickListener() {
            @Override
            public void onTextClick(View view, int position) {
                // Retrieve the Shift object corresponding to the clicked position
                Shift clickedShift = shifts.get(position);

                // Start the next activity and pass the Shift object as an extra
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                intent.putExtra("shift", clickedShift);
                startActivity(intent);
            }
        });
        shiftAdapter.setShiftButtonClickListener(new CustomAdapter.OnShiftButtonClickListener() {
            @Override
            public void onShiftButtonClick(View view, int position) {
                // Retrieve the Shift object corresponding to the clicked position
                Shift clickedShift = shifts.get(position);

                // Start the next activity and pass the Shift object as an extra
                Intent intent = new Intent(MainActivity.this, ShiftScreenActivity.class);
                intent.putExtra("shift", clickedShift);
                startActivity(intent);
            }
        });

        shiftAdapter.setRemoveClickListener(new CustomAdapter.OnRemoveClickListener() {
            @Override
            public void onRemoveClick(View view, int position) {
                Shift clickedShift = shifts.get(position);
                showDeleteConfirmationDialog(clickedShift);
            }
        });
    }



    private void showDeleteConfirmationDialog(Shift shift) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseHelper sql = new DatabaseHelper(MainActivity.this);

                        sql.removeShift(shift.getID());
                        makeListWork();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled the delete action
                    }
                })
                .show();
    }

    private void addShift()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.shift_dialog_input, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText shiftName = promptView.findViewById(R.id.add_shiftname);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String sn = shiftName.getText().toString();

                        if(sn == null)
                        {
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT);
                        }
                        else
                        {
                            DatabaseHelper sql = new DatabaseHelper(MainActivity.this);
                            sql.addNewShift(sn);
                        }
                        makeListWork();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    public void prettyInit() {
        View thisView = findViewById(R.id.ssLogo);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(getColor(R.color.foreGrey));
        shape.setCornerRadius(20);
        thisView.setBackground(shape);
    }

}