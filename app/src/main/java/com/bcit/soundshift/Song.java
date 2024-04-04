package com.bcit.soundshift;

import androidx.annotation.NonNull;

public class Song {

    private final int ID;
    private final String title;
    private final String filename;
    private final int album_ID;
    private final int artist_ID;
    private final float length;

    public Song(int id, String title, String filename, int albumId, int artistId, float length) {
        ID = id;
        this.title = title;
        this.filename = filename;
        album_ID = albumId;
        artist_ID = artistId;
        this.length = length;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    public int getId() {
        return ID;
    }
}
