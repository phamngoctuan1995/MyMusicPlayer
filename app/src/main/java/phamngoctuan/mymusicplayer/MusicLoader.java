package phamngoctuan.mymusicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by phamn_000 on 4/5/2016.
 */
public class MusicLoader {

    private Context mainActivity;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    final String MEDIA_PATH = MediaStore.Audio.Media.getContentUri("internal").toString();
    // Constructor
    public MusicLoader(Context ma) {
        mainActivity = ma;
    }

    /**
     * Function to read all mp3 files from sdcard and store the details in
     * ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor;
        ContentResolver cr = mainActivity.getContentResolver();
        String[] projection = {
//				MediaStore.Audio.Media._ID,
//				MediaStore.Audio.Media.ARTIST,
//				MediaStore.Audio.Media.TITLE,
//				MediaStore.Audio.Media.DATA,
//				MediaStore.Audio.Media.DISPLAY_NAME,
//				MediaStore.Audio.Media.DURATION
                "*"
        };

        cursor = cr.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while(cursor.moveToNext()) {
            HashMap<String, String> song = new HashMap<String, String>();
            song.put("title", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            song.put("path", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            song.put("artist", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            song.put("album_id", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            songsList.add(song);
        }
        // return songs list array
        return songsList;
    }

    /**
     * The class is used to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
