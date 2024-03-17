package com.bcit.soundshift;
import android.content.Context;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Random;

public class Shift {
    private final String name;
    private final int ID;
    private int currentPlaylist_id = -1;
    private DatabaseHelper sql;

    Shift(Context context, String name, int ID)
    {
        this.name = name;
        this.ID = ID;
        sql = new DatabaseHelper(context);
    }

    public void openDatabase()
    {
        sql.openDatabase();
    }
    public void closeDatabase()
    {
        sql.close();
    }
    private class song
    {
        public String title;
        public int playlist_id;
        public int song_id;
        public int weight;
        public song(String t, int p, int s, int w)
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

        ArrayList<ArrayList<String>> result = sql.cursorToList(sql.executeQuery(sql.replaceNamedParams(sql.SongListQuery, params)));

        AvailableSongs.clear();
        for(int i  = 0; i < result.size(); i++)
        {
            AvailableSongs.add(new song(result.get(i).get(0), Integer.parseInt(result.get(i).get(1)), Integer.parseInt(result.get(i).get(2)), Integer.parseInt(result.get(i).get(3))));
        }
        return true;
    }
    public int getNextSong()
    {
        if (currentPlaylist_id == -1)
        {
            ArrayList<String> params = new ArrayList<>();
            params.add(Integer.toString(ID));

            ArrayList<ArrayList<String>> result = sql.cursorToList(sql.executeQuery(sql.replaceNamedParams(sql.AllPlaylistsQuery, params)));

            ArrayList<Integer> playlists = new ArrayList<>();
            ArrayList<Integer> weight = new ArrayList<>();

            for(int i = 0; i < result.size(); i++)
            {
                playlists.add(Integer.parseInt(result.get(i).get(0)));
                weight.add(0);
            }
            ArrayList<Integer> starting_playlist = weightedShuffle(playlists, weight, 1);

            currentPlaylist_id = starting_playlist.get(0);
        }

        updateSongList();

        ArrayList<Integer> songs = new ArrayList<>();
        ArrayList<Integer> weight = new ArrayList<>();

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
            }
        }

        return next_song.get(0);
    }

    private void setCurrentPlaylist_id(int id)
    {
        currentPlaylist_id = id;
    }

    private static ArrayList<Integer> weightedShuffle(ArrayList<Integer> songs, ArrayList<Integer> weights, int numItems) {
        ArrayList<Integer> selectedItems = new ArrayList<>();
        int totalWeight = 0;

        // Calculate the total weight of all songs
        for (int weight : weights) {
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
}
