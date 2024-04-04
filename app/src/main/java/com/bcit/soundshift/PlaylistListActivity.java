package com.bcit.soundshift;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlaylistListActivity extends AppCompatActivity {

    private ListView playlistsList;
    private Button addPlaylist;
    private Button removePlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_list_activity);

        findView();
        playlistList();
        setupButtonOnClickListener();

    }

    private void setupButtonOnClickListener() {
        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaylists();
            }
        });

        removePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedPlaylists();
            }
        });
    }

    private void removeSelectedPlaylists() {
        // Get the selected playlists from the adapter
        ArrayList<Playlist> selectedPlaylists = ((CustomAdapter<Playlist>) playlistsList.getAdapter()).getSelectedItems();

        // Remove the selected playlists from the database
        DatabaseHelper sql = new DatabaseHelper(PlaylistListActivity.this);
        for (Playlist playlist : selectedPlaylists) {
            sql.removePlaylist(playlist.getId()); // Assuming you have a method in your DatabaseHelper to remove a playlist by its ID
        }

        // Refresh the playlist list after removal
        playlistList();
    }
    private void addPlaylists() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.playlist_dialog_input, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText playlistname = promptView.findViewById(R.id.add_playlistname);
        final EditText playlistweight = promptView.findViewById(R.id.add_playlistweight);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String pn = playlistname.getText().toString();
                        String pw = playlistweight.getText().toString();
                        DatabaseHelper sql = new DatabaseHelper(PlaylistListActivity.this);
                        if(pn == null || pw == null)
                        {
                            Toast.makeText(PlaylistListActivity.this, "Failed", Toast.LENGTH_SHORT);
                        }
                        else
                        {
                            sql.addNewPlaylist(pn, Double.parseDouble(pw));
                        }
                        playlistList();
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

    private void playlistList() {
        ArrayList<Playlist> playlists = new ArrayList<>();

        DatabaseHelper sql = new DatabaseHelper(this);
        try {
            ArrayList<ArrayList<String>> results = sql.cursorToList(sql.executeQuery("SELECT * FROM playlist"));
            for (int i = 0; i < results.size(); i++) {
                playlists.add(new Playlist(Integer.parseInt(results.get(i).get(0)), results.get(i).get(1)));
            }
        } catch (Exception e) {
            System.out.println("An exception occurred: " + e.getMessage());
        }

        CustomAdapter<Playlist> playlistAdapter = new CustomAdapter<Playlist>(this, playlists);
        playlistsList.setAdapter(playlistAdapter);

        playlistAdapter.setTextClickListener(new CustomAdapter.OnTextClickListener() {
            @Override
            public void onTextClick(View view, int position) {
                // Handle text view click event
                // Retrieve the playlist object corresponding to the clicked position
                Playlist clickedPlaylist = playlists.get(position);

                // Handle the click event with the playlist object
                // For example, start a new activity with the playlist object
                Intent intent = new Intent(PlaylistListActivity.this, PlaylistActivity.class);
                intent.putExtra("playlist", clickedPlaylist);
                startActivity(intent);
            }
        });



    }

    private void findView() {
        playlistsList = findViewById(R.id.playlistList);
        addPlaylist = findViewById(R.id.addPlaylist);
        removePlaylist = findViewById(R.id.removePlaylist);
    }

}
