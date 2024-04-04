package com.bcit.soundshift;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import de.umass.lastfm.Caller;


public class SongActivity extends AppCompatActivity {

    private Button addSong;
    private Button removeSong;
    private ListView songList;
    private TextView title;
    private static final int PICK_FILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_activity);
        findView();
        updateSongList();
        setupButtonOnClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void findView() {
        addSong = findViewById(R.id.addSong);
        removeSong = findViewById(R.id.removeSong);
        songList = findViewById(R.id.songList);
        title = findViewById(R.id.topBarText);
    }

    private void updateSongList() {
        ArrayList<Song> songs = new ArrayList<>();

        DatabaseHelper sql = new DatabaseHelper(this);
        try {
            ArrayList<ArrayList<String>> results = sql.cursorToList(sql.executeQuery("SELECT * FROM song"));
            for (int i = 0; i < results.size(); i++) {
                //Log.v("Song", results.get(i).get(1));
                try {
                    songs.add(new Song(Integer.parseInt(results.get(i).get(0)), results.get(i).get(1), results.get(i).get(2), Integer.parseInt(results.get(i).get(3)), Integer.parseInt(results.get(i).get(4)), Float.parseFloat(results.get(i).get(5))));
                    //Log.d("SongActivity", results.get(i).get(0) + " " + results.get(i).get(1) + " " + results.get(i).get(2) + " " + results.get(i).get(3) + " " + results.get(i).get(4) + " " + results.get(i).get(5));
                } catch (Exception e) {
                    System.out.println("An exception occurred: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("An exception occurred: " + e.getMessage());
        }

        CustomAdapter<Song> songAdapter = new CustomAdapter<Song>(this, songs);
        songList.setAdapter(songAdapter);

        songAdapter.setTextClickListener(new CustomAdapter.OnTextClickListener() {
            @Override
            public void onTextClick(View view, int position) {
                // Handle text view click event
                // Retrieve the playlist object corresponding to the clicked position
                Song clickedSong = songs.get(position);
                utils.displayEntryData(SongActivity.this, "song", clickedSong.getId());
            }
        });
    }

    private void setupButtonOnClickListener() {
        addSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        removeSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedSongs();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) { // Multiple files selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri selectedFileUri = data.getClipData().getItemAt(i).getUri();
                    String filePath = FileUtils.getPath(this, selectedFileUri);
                    if (filePath != null) {
                        addSong(filePath);
                    }
                }
            } else if (data.getData() != null) { // Single file selected
                Uri selectedFileUri = data.getData();
                String filePath = FileUtils.getPath(this, selectedFileUri);
                if (filePath != null) {
                    addSong(filePath);
                }
            }
        }
    }

    private void removeSelectedSongs() {
        // Get the selected playlists from the adapter
        ArrayList<Song> selectedSongs = ((CustomAdapter<Song>) songList.getAdapter()).getSelectedItems();

        // Remove the selected playlists from the database
        DatabaseHelper sql = new DatabaseHelper(SongActivity.this);
        for (Song song : selectedSongs) {
            sql.removeSong(song.getId()); // Assuming you have a method in your DatabaseHelper to remove a playlist by its ID
        }

        // Refresh the playlist list after removal
        updateSongList();
    }

    @SuppressLint("StaticFieldLeak")
    private void addSong(String filePath) {

        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        Log.i("SongActivity", filePath);

        ArrayList<AudioFileParser.AudioMetadata> metadataList = AudioFileParser.parseAudioFiles(this, filePaths);

        DatabaseHelper sql = new DatabaseHelper(this);

        for (AudioFileParser.AudioMetadata metadata : metadataList) {
            System.out.println("Title: " + metadata.getTitle());
            System.out.println("Artist: " + metadata.getArtist());
            System.out.println("Album: " + metadata.getAlbum());
            System.out.println("Duration: " + metadata.getDuration() + " milliseconds");

            int artist_id = sql.getRowIdIfExists("artist", "artist", metadata.getArtist());
            int album_id = sql.getRowIdIfExists("album", "album", metadata.getAlbum());

            if(artist_id == -1)
            {
                sql.addNewArtist(metadata.getArtist());
                artist_id = sql.getRowIdIfExists("artist", "artist", metadata.getArtist());
            }
            if(album_id == -1)
            {
                sql.addNewAlbum(metadata.getAlbum(), null);
                album_id = sql.getRowIdIfExists("album", "album", metadata.getAlbum());
                final int album_id_final = album_id;

                Caller.getInstance().setUserAgent("tst");
                LastFMApiHelper fm = new LastFMApiHelper();

                new AsyncTask<String, Void, byte[]>() {
                    @Override
                    protected byte[] doInBackground(String... params) {
                        String artist = params[0];
                        String albumName = params[1];
                        LastFMApiHelper fm = new LastFMApiHelper();
                        return fm.retrieveAlbumCover(artist, albumName);
                    }

                    @Override
                    protected void onPostExecute(byte[] albumCoverBytes) {
                        if (albumCoverBytes != null) {
                            sql.modifyAlbum(album_id_final, metadata.getAlbum(), albumCoverBytes);
                        } else {
                            Log.e("MainActivity", "Failed to retrieve album cover bytes");
                        }
                    }
                }.execute(metadata.getArtist(), metadata.getAlbum());

            }
            sql.addNewSong(metadata.getTitle(), filePath, album_id, artist_id, (double)metadata.getDuration()/1000);
        }
        updateSongList();
    }


}