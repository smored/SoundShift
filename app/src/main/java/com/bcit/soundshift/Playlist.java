package com.bcit.soundshift;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Playlist implements Serializable {

    private final int id;
    private final String name;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
