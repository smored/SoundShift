package com.bcit.soundshift;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Shift implements Serializable {
    private final String name;
    private final int ID;
    private int currentPlaylist_id = -1;
    private int currentSong_id = -1;
    private transient DatabaseHelper sql;

    Shift(Context context, String name, int ID)
    {
        this.name = name;
        this.ID = ID;
        sql = new DatabaseHelper(context);
        AvailableSongs = new ArrayList<>();
    }

    public void transientDatabase(Context context)
    {
        sql = new DatabaseHelper(context);
    }

    private class song
    {
        public String title;
        public int playlist_id;
        public int song_id;
        public float weight;
        public song(String t, int p, int s, float w)
        {
            title = t;
            playlist_id = p;
            song_id = s;
            weight = w;
        }
    };

    ArrayList<song> AvailableSongs;

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }


    private boolean updateSongList()
    {
        ArrayList<String> params = new ArrayList<>();
        params.add(Integer.toString(ID));
        params.add(Integer.toString(currentPlaylist_id));
        params.add(Integer.toString(currentSong_id));

        ArrayList<ArrayList<String>> result = sql.cursorToList(sql.executeQuery(sql.replaceNamedParams(sql.SongListQuery, params)));

        if(AvailableSongs.size() != 0)
        {
            AvailableSongs.clear();
        }
        for(int i  = 0; i < result.size(); i++)
        {
            AvailableSongs.add(new song(result.get(i).get(0), Integer.parseInt(result.get(i).get(1)), Integer.parseInt(result.get(i).get(2)), Float.parseFloat(result.get(i).get(3))));
        }
        return true;
    }
    public ArrayList<Integer> getNextSong()
    {
        if (currentPlaylist_id == -1)
        {
            ArrayList<String> params = new ArrayList<>();
            params.add(Integer.toString(ID));

            ArrayList<ArrayList<String>> result = sql.cursorToList(sql.executeQuery(sql.replaceNamedParams(sql.AllPlaylistsQuery, params)));

            ArrayList<Integer> playlists = new ArrayList<>();
            ArrayList<Float> weight = new ArrayList<>();

            for(int i = 0; i < result.size(); i++)
            {
                playlists.add(Integer.parseInt(result.get(i).get(0)));
                weight.add(1f);
            }
            ArrayList<Integer> starting_playlist = weightedShuffle(playlists, weight, 1);

            currentPlaylist_id = starting_playlist.get(0);
        }

        updateSongList();

        ArrayList<Integer> songs = new ArrayList<>();
        ArrayList<Float> weight = new ArrayList<>();

        for(int i = 0; i < AvailableSongs.size(); i++)
        {
            songs.add(AvailableSongs.get(i).song_id);
            weight.add(AvailableSongs.get(i).weight);
        }
        ArrayList<Integer> next_song = weightedShuffle(songs, weight, 1);

        for(int i = 0; i < AvailableSongs.size(); i++)
        {
            if (next_song.get(0) == AvailableSongs.get(i).song_id)
            {
                currentPlaylist_id = AvailableSongs.get(i).playlist_id;
                currentSong_id = AvailableSongs.get(i).song_id;
                return new ArrayList<>(Arrays.asList(AvailableSongs.get(i).playlist_id, AvailableSongs.get(i).song_id));
            }
        }
        return new ArrayList<>(Arrays.asList(-1, -1));
    }

    public ArrayList<String> getSongAndPlaylistNames(ArrayList<Integer> id)
    {
        ArrayList<String> stringList = new ArrayList<>();
        for (Integer num : id) {
            stringList.add(Integer.toString(num));
        }
        return sql.cursorToSingleList(sql.executeQuery(sql.replaceNamedParams(sql.PlaylistSongArtistNames, stringList)));
    }

    private void setCurrentPlaylist_id(int id)
    {
        currentPlaylist_id = id;
    }

    private static ArrayList<Integer> weightedShuffle(ArrayList<Integer> songs, ArrayList<Float> weights, int numItems) {
        ArrayList<Integer> selectedItems = new ArrayList<>();
        float totalWeight = 0;

        // Calculate the total weight of all songs
        for (Float weight : weights) {
            totalWeight += weight;
        }

        // Perform weighted shuffling
        Random random = new Random();
        while (selectedItems.size() < numItems) {
            double randNum = random.nextDouble() * totalWeight;
            double cumulativeWeight = 0;

            for (int i = 0; i < songs.size(); i++) {
                cumulativeWeight += weights.get(i);
                if (cumulativeWeight >= randNum) {
                    selectedItems.add(songs.get(i));
                    break;
                }
            }
        }

        return selectedItems;
    }

    public Bitmap getAlbumImage(int song_id)
    {
        ArrayList<String> song_string = new ArrayList<>();
        song_string.add(Integer.toString(song_id));
        Cursor cursor = sql.executeQuery("SELECT album.cover_art " +
                "FROM album " +
                "INNER JOIN song " +
                "on album.id = song.album_id " +
                "WHERE song.id = ?", song_string);

        byte[] blobData;
        if (cursor != null && cursor.moveToFirst()) {
           blobData  = cursor.getBlob(0);
           if (blobData == null) return null;
           return BitmapFactory.decodeByteArray(blobData, 0, blobData.length);
        }
        return null;
    }

    public String getFilePath(int song_id)
    {
        ArrayList<String> song_string = new ArrayList<>();
        song_string.add(Integer.toString(song_id));
        ArrayList<ArrayList<String>> out = sql.cursorToList(sql.executeQuery("SELECT song.filename " +
                "FROM song " +
                "WHERE song.id = " + song_string.get(0)));
        return out.get(0).get(0);
    }

    public void setSong(ArrayList<Integer> playlist_and_song)
    {
        currentPlaylist_id = playlist_and_song.get(0);
        currentSong_id = playlist_and_song.get(1);
    }
}
