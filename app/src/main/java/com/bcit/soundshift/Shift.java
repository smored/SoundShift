package com.bcit.soundshift;


import androidx.annotation.NonNull;

public class Shift {
    private String name;
    private int ID;
    private Playlist[] playlistArray;

    public Shift(String name, int ID, Playlist[] playlistArray) {
        this.name = name;
        this.ID = ID;
        this.playlistArray = playlistArray;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public Playlist[] getPlaylistArray() {
        return playlistArray;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
