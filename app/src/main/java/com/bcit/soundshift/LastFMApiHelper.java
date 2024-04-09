package com.bcit.soundshift;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import de.umass.lastfm.Album;
import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Track;
import de.umass.lastfm.Session;
import de.umass.lastfm.ImageSize;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class LastFMApiHelper {



    public byte[] retrieveAlbumCover(String artist, String albumName) {
        try {
            artist = artist;
            albumName = albumName;
            // Get album cover art URL
            String coverArtUrl;
            try
            {
                Album album = Album.getInfo(artist, albumName, Constants.API_KEY);
                coverArtUrl = album.getImageURL(ImageSize.LARGE);
            } catch (NumberFormatException e) {
                return null;
            }

            // Get image bytes from URL
            URL url = new URL(coverArtUrl);
            Log.i("URL", coverArtUrl);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}