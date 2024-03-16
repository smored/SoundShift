import os
from tinytag import TinyTag
import sqlite3

path = r".\import_songs"  # Use raw string literal to avoid escape characters
dir_list = os.scandir(path)

conn = sqlite3.connect('soundshift.db')
cursor = conn.cursor()

get_album = "SELECT id FROM album WHERE album.name = ?"
get_sql_sequence = "SELECT seq FROM sqlite_sequence WHERE name = ?"
insert_album = "INSERT INTO album (name) VALUES (?)"
insert_artist = "INSERT INTO artist (name) VALUES (?)"
get_artist = "SELECT id FROM artist WHERE artist.name = ?"
get_song = "SELECT * FROM song where song.title = ?"
insert_song = "INSERT INTO song (title, filename, album_id, artist_id, length) VALUES (?, ?, ?, ?, ?)"

keys = ['filepath', 'title', 'artist', 'album', 'duration']


for entry in dir_list:
    try:
        if entry.is_file():
            # Get audio file properties
            tag = TinyTag.get(entry.path)
            song = {key: None for key in keys}

            song['filepath'] = entry.path
            song['title'] = tag.title
            song['artist'] = tag.artist
            song['album'] = tag.album
            song['duration'] = tag.duration

            # Print properties
            print(song)
            print()  # Print an empty line for better readability

            if(cursor.execute(get_song, (song['title'],)).fetchone() is None):
                results = cursor.execute(get_album, (song['album'],)).fetchone()

                if results is None:
                    album_id = cursor.execute(get_sql_sequence, ('album',)).fetchone()[0] + 1
                    print(album_id)
                    cursor.execute(insert_album, ((song['album']),))
                else:
                    album_id = results[0]

                results = cursor.execute(get_artist, (song['artist'],)).fetchone()

                if results is None:
                    artist_id = cursor.execute(get_sql_sequence, ('artist',)).fetchone()[0] + 1
                    print(artist_id)
                    cursor.execute(insert_artist, ((song['artist']),))
                else:
                    artist_id = results[0]
                
                cursor.execute(insert_song, (song['title'], song['filepath'], album_id, artist_id, song['duration']))

            else:
                continue

    except Exception as e:
        print(f"Error: {e} for file: {entry.path}")

conn.commit()

cursor.close()
conn.close()