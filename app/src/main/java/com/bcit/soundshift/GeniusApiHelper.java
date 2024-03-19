package com.bcit.soundshift;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.LinkedList;

import core.GLA;
import genius.LyricsParser;
import genius.SongSearch;

public class GeniusApiHelper {

    private GLA gla;

    public GeniusApiHelper(GLA gla) {
        this.gla = gla;
    }

    public void getLyricsAsync(String title, LyricsCallback callback) {
        new FetchLyricsTask(callback).execute(title);
    }

    private static class FetchLyricsTask extends AsyncTask<String, Void, String> {
        private LyricsCallback callback;

        public FetchLyricsTask(LyricsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            LyricsParser lyricsParser = new LyricsParser(new GLA());
            long id = getSongId(new GLA(), title);
            return lyricsParser.get(Long.toString(id));
        }

        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (result != null) {
                    callback.onLyricsReceived(result);
                } else {
                    callback.onLyricsReceived("Lyrics not found");
                }
            } else {
                Log.e("GeniusApiHelper", "LyricsCallback is null");
            }
        }
    }

    public interface LyricsCallback {
        void onLyricsReceived(String lyrics);
    }

    private static long getSongId(GLA gla, String songTitle) {
        try {
            SongSearch songSearch = new SongSearch(gla, "despacito");
            LinkedList<SongSearch.Hit> hits = songSearch.getHits();
            if (!hits.isEmpty()) {
                // Assuming the first hit is the most relevant one
                Log.i("SongSearch", "Found ID: " + hits.getFirst().getId());
                return hits.getFirst().getId();
            } else {
                // If no hits found, return -1 or throw an exception
                return -1;
            }
        } catch (IOException e) {
            // Handle IOException appropriately (e.g., log or throw)
            e.printStackTrace();
            return -1;
        }
    }
}