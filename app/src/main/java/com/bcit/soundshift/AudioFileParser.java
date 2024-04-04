package com.bcit.soundshift;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;

public class AudioFileParser {

    public static ArrayList<AudioMetadata> parseAudioFiles(Context context, ArrayList<String> filePaths) {
        ArrayList<AudioMetadata> metadataList = new ArrayList<>();

        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                AudioMetadata metadata = parseAudioFile(context, Uri.fromFile(file));
                if (metadata != null) {
                    metadataList.add(metadata);
                }
            }
        }

        return metadataList;
    }

    public static AudioMetadata parseAudioFile(Context context, Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, uri);

        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);


        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (title != null || artist != null || album != null || duration != null) {
            return new AudioMetadata(title, artist, album, Long.parseLong(duration));
        } else {
            return null; // Return null if no metadata is found
        }
    }

    public static class AudioMetadata {
        private String title;
        private String artist;
        private String album;
        private long duration; // in milliseconds

        public AudioMetadata(String title, String artist, String album, long duration) {
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.duration = duration;
        }

        // Getter methods
        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public String getAlbum() {
            return album;
        }

        public long getDuration() {
            return duration;
        }
    }
}