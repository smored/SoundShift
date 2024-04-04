package com.bcit.soundshift;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PlaylistActivity extends AppCompatActivity {

    private Playlist playlist;
    private Button addSong;
    private Button removeSong;
    private ListView songList;
    private TextView playlistTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);
        playlist = (Playlist) getIntent().getSerializableExtra("playlist");

        findView();
        updateSongList();
        setupButtonOnClickListener();

        playlistTitle.setText(playlist.toString());

    }

    private void findView() {
        addSong = findViewById(R.id.addSong);
        removeSong = findViewById(R.id.removeSong);
        songList = findViewById(R.id.songList);
        playlistTitle = findViewById(R.id.playlistName);
    }

    private void updateSongList() {
        ArrayList<Song> songs = new ArrayList<>();

        Log.d("DEBUG", "Entered updateSongList");
        DatabaseHelper sql = new DatabaseHelper(this);
        try {
            ArrayList<ArrayList<String>> results = sql.cursorToList(sql.executeQuery("SELECT song.id, song.title, song.filename, song.album_id, song.artist_id, song.length FROM playlist_songs INNER JOIN song ON song.id = playlist_songs.song_id WHERE playlist_songs.playlist_id = ?", Integer.toString(playlist.getId())));
            for (int i = 0; i < results.size(); i++) {
                //Log.v("Song", results.get(i).get(1));
                try {
                    songs.add(new Song(Integer.parseInt(results.get(i).get(0)), results.get(i).get(1), results.get(i).get(2), Integer.parseInt(results.get(i).get(3)), Integer.parseInt(results.get(i).get(4)), Float.parseFloat(results.get(i).get(5))));
                } catch (Exception e) {
                    System.out.println("An exception occurred: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("An exception occurred: " + e.getMessage());
        }

        CustomAdapter<Song> songAdapter = new CustomAdapter<Song>(this, songs);
        songList.setAdapter(songAdapter);
    }

    private void setupButtonOnClickListener() {
        addSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSongSelectionDialog();
            }
        });

        removeSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedSongs();
            }
        });
        playlistTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.displayEntryData(PlaylistActivity.this, "playlist", playlist.getId());
            }
        });

    }




    private void removeSelectedSongs() {
        // Get the selected playlists from the adapter
        ArrayList<Song> selectedSongs = ((CustomAdapter<Song>) songList.getAdapter()).getSelectedItems();

        // Remove the selected playlists from the database
        DatabaseHelper sql = new DatabaseHelper(PlaylistActivity.this);
        for (Song song : selectedSongs) {
            sql.removeSongFromPlaylist(playlist.getId(), song.getId());
        }

        // Refresh the playlist list after removal
        updateSongList();
    }
    public void showSongSelectionDialog() {

        SearchView songSearch;
        ArrayAdapter<Song> songAdapter;
        ArrayList<Song> songListArray; 

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_songs, null);
        dialogBuilder.setView(dialogView);

        ListView songListView = dialogView.findViewById(R.id.songListView);
        songSearch = dialogView.findViewById(R.id.songSearchView);




        ArrayList<Song> songs = new ArrayList<>();
        // Populate song list
        DatabaseHelper sql = new DatabaseHelper(this);
        try {
            ArrayList<ArrayList<String>> results = sql.cursorToList(sql.executeQuery("SELECT * FROM song"));
            for (int i = 0; i < results.size(); i++) {
                //Log.v("Song", results.get(i).get(1));
                try {
                    songs.add(new Song(Integer.parseInt(results.get(i).get(0)), results.get(i).get(1), results.get(i).get(2), Integer.parseInt(results.get(i).get(3)), Integer.parseInt(results.get(i).get(4)), Float.parseFloat(results.get(i).get(5))));
                } catch (Exception e) {
                    System.out.println("An exception occurred: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("An exception occurred: " + e.getMessage());
        }
        songListArray = songs;
        // Create an adapter to display the songs in the ListView
        songAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, songListArray);
        songListView.setAdapter(songAdapter);
        songListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        songSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                songAdapter.getFilter().filter(query);
                return true;
            }
        });

        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve selected songs
                SparseBooleanArray checkedItems = songListView.getCheckedItemPositions();
                for (int i = 0; i < checkedItems.size(); i++) {
                    int position = checkedItems.keyAt(i);
                    if (checkedItems.get(position)) {
                        Song selectedSong = songAdapter.getItem(position);
                        sql.addNewPlaylistSong(playlist.getId(), selectedSong.getId());
                    }
                }
                sql.writeToDownloads();
                updateSongList();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

}
