package phamngoctuan.mymusicplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by phamn_000 on 4/6/2016.
 */
public class CustomArrayAdapter extends ArrayAdapter<HashMap<String, String>>{

    Context activity;
    int resources;
    ArrayList<HashMap<String, String>> listSongs;
    HashMap<Integer, View> viewCache;
    public static LayoutInflater inflater = null;

    public CustomArrayAdapter(Context context, int resource, ArrayList<HashMap<String, String>> list) {
        super(context, R.layout.list_item, list);
        this.activity = context;
        this.resources = resource;
        this.listSongs = list;
//        cacheThumbs = new HashMap<Integer, Bitmap>();
        inflater = LayoutInflater.from(context);
        viewCache = new HashMap<>();
    }

    private void createAndShowAlertDialog(final HashMap<String, String> song) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Download " + song.get("title"));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO

                    DownloadTask downloadTask = new DownloadTask(MainActivity.context);
                    File fd = new File(Environment.getExternalStorageDirectory().toString() + "/My music player");
                    if (!fd.exists())
                        fd.mkdir();

                    String params[] = {
                            song.get("path"),
                            fd.toString() + "/" + song.get("title") + ".mp3"
                    };
                    downloadTask.execute(params);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //TODO
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Exception e)
        {
            Log.d("debug", "Error dialog: " + e.getMessage());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (viewCache.containsKey(position))
        {
            return viewCache.get(position);
        }
        View rowView = inflater.inflate(R.layout.list_item, null, true);

        ImageView thumb = (ImageView) rowView.findViewById(R.id.thumb);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView artist = (TextView) rowView.findViewById(R.id.artist);
        TextView total = (TextView)rowView.findViewById(R.id.totalplay);
        HashMap<String, String> song = listSongs.get(position);
        if (song.containsKey("thumbnail")) {
            try {
                LoadBitmapAsync bm = new LoadBitmapAsync(thumb);
                bm.execute(song.get("thumbnail"));
//                thumb.setImageURI(Uri.parse(song.get("thumbnail")));
            }
            catch (Exception e)
            {
                Log.d("debug", "Load bitmap exception: " + e.getMessage());
            }
        }

        if (song.containsKey("totalplay"))
            total.setText(song.get("totalplay"));
        title.setText(song.get("title"));
        artist.setText(song.get("artist"));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.currentSong = position;
                try {
                    if (MainActivity.mp.isPlaying()) {
                        MainActivity.playSong();
                    }
                    MainActivity.initMediaPlayer();
                    MainActivity.viewPager.setCurrentItem(1);
                    MainActivity.loadSong();
                    MainActivity.playSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    HashMap<String, String> song = listSongs.get(position);
                    if (song.containsKey("totalplay")) {
                        Log.d("debug", "long click");
                        createAndShowAlertDialog(song);
                    }
                } catch (Exception e) {
                    Log.d("debug", "Error long click: " + e.getMessage());
                }
                return true;
            }
        });

        viewCache.put(position, rowView);
        return rowView;
    }
}
