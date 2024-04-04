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

    private static final String TAG = "LastFMApiHelper";
    private static final String API_KEY = "e0c830774d6ed7a898d1f4b0f59d18c7";
    private static final String SECRET = "8609e02fecda0869ea4b28a7bdbb5561";

    public byte[] retrieveAlbumCover(String artist, String albumName) {
        try {
            artist = artist;
            albumName = albumName;
            // Get album cover art URL
            String coverArtUrl;
            try
            {
                Album album = Album.getInfo(artist, albumName, API_KEY);
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

/*
    private void testFM() {
        Caller.getInstance().setUserAgent("tst");
        String artist = "Bruno Mars";
        String albumName = "Doo-Wops and hooligans";

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
                    // Do something with the album cover byte
                } else {
                    Log.e("MainActivity", "Failed to retrieve album cover bytes");
                }
            }
        }.execute(artist, albumName);
    }
 */