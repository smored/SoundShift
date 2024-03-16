SELECT playlist.playlist, song.title, playlist.weight
FROM playlist
INNER JOIN playlist_songs
    on playlist.id = playlist_songs.playlist_id
INNER JOIN song
    on song.id = playlist_songs.song_id
WHERE playlist.id == 2
UNION
SELECT playlist.playlist, song.title, shift_connection.weight
FROM shift_connection
INNER JOIN playlist
    on shift_connection.playlist_2_id = playlist.id
INNER JOIN playlist_songs
    on playlist.id = playlist_songs.playlist_id
INNER JOIN song
    on song.id = playlist_songs.song_id
WHERE shift_connection.playlist_1_id = 2 AND shift_connection.shift_id = 2
UNION
SELECT playlist.playlist, song.title, shift_connection.weight
FROM shift_connection
INNER JOIN playlist
    on shift_connection.playlist_1_id = playlist.id
INNER JOIN playlist_songs
    on playlist.id = playlist_songs.playlist_id
INNER JOIN song
    on song.id = playlist_songs.song_id
WHERE shift_connection.playlist_2_id = 2 AND shift_connection.shift_id = 2