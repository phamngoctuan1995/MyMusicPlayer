package phamngoctuan.mymusicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;

/**
 * Created by phamn_000 on 4/5/2016.
 */
class MyOnClickListener implements View.OnClickListener
{

    @Override
    public void onClick(View v) {
        boolean isPlaying;
        switch (v.getId())
        {
            case R.id.btn_shuffle:
                if (MainActivity.isShuffle)
                {
                    MainActivity.isShuffle = false;
                    MainActivity.shuffle.setImageResource(R.drawable.ic_shuffle_grey600_36dp);
                }
                else
                {
                    MainActivity.isShuffle = true;
                    MainActivity.shuffle.setImageResource(R.drawable.ic_shuffle_white_36dp);
                }
                break;
            case R.id.btn_play_mode:
                if (MainActivity.play_mode == MainActivity.PLAY_ALL)
                {
                    MainActivity.play_mode = MainActivity.PLAY_ONE;
                    MainActivity.playMode.setImageResource(R.drawable.ic_repeat_one_white_36dp);
                }
                else
                    if (MainActivity.play_mode == MainActivity.PLAY_ONE)
                    {
                        MainActivity.play_mode = MainActivity.PLAY_NORMAL;
                        MainActivity.playMode.setImageResource(R.drawable.ic_repeat_grey600_36dp);
                    }
                    else
                    {
                        MainActivity.play_mode = MainActivity.PLAY_ALL;
                        MainActivity.playMode.setImageResource(R.drawable.ic_repeat_white_36dp);
                    }
                break;
            case R.id.btn_backward:
                if (MainActivity.listSongs.size() <= 0)
                    return;
                isPlaying = MainActivity.mp.isPlaying();
                if (isPlaying)
                    try {
                        MainActivity.playSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                MainActivity.nextSong(false);
                try {
                    MainActivity.initMediaPlayer();
                    MainActivity.loadSong();
                    if (isPlaying)
                        MainActivity.playSong();
                } catch (IOException e) {
                    Log.d("debug", "exception back");
                    e.printStackTrace();
                }
                break;
            case R.id.btn_forward:
                if (MainActivity.listSongs.size() <= 0)
                    return;
                isPlaying = MainActivity.mp.isPlaying();
                if (isPlaying)
                    try {
                        MainActivity.playSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                MainActivity.nextSong(true);
                try {
                    MainActivity.initMediaPlayer();
                    MainActivity.loadSong();
                    if (isPlaying)
                        MainActivity.playSong();
                } catch (IOException e) {
                    Log.d("debug", "Excception bt next");
                    e.printStackTrace();
                }
                break;
            case R.id.btn_play:
                try {
                    MainActivity.playSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}

class UpdateSeekbar implements Runnable
{
    @Override
    public void run() {
        MediaPlayer mp = MainActivity.mp;

        long totalDuration = 0;
        long currentDuration = 0;

        if (MainActivity.listSongs.size() > 0) {
            try {
                totalDuration = mp.getDuration();
                currentDuration = mp.getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
                totalDuration = currentDuration = 0;
            }
        }

        MainActivity.setTimeText(MainActivity.current, currentDuration);
        MainActivity.setTimeText(MainActivity.total, totalDuration);
        // Updating progress bar
        MainActivity.progressBar.setMax((int) totalDuration);
        MainActivity.progressBar.setProgress((int) currentDuration);
        // Running this thread after 100 milliseconds
        MainActivity.seekHandle.postDelayed(this, 200);
    }
}

public class FragmentPlay extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        MyOnClickListener listener = new MyOnClickListener();
        MainActivity.seekHandle = new Handler(Looper.getMainLooper());
        MainActivity.seekHandle.post(new UpdateSeekbar());

        MainActivity.thumb = (CircleImage)rootView.findViewById(R.id.thumb_disk);
        MainActivity.back = (ImageView)rootView.findViewById(R.id.btn_backward);
        MainActivity.next = (ImageView)rootView.findViewById(R.id.btn_forward);
        MainActivity.play = (ImageView)rootView.findViewById(R.id.btn_play);
        MainActivity.playMode = (ImageView)rootView.findViewById(R.id.btn_play_mode);
        MainActivity.shuffle = (ImageView)rootView.findViewById(R.id.btn_shuffle);
        MainActivity.total = (TextView)rootView.findViewById(R.id.total_time);
        MainActivity.current = (TextView)rootView.findViewById(R.id.current_time);
        MainActivity.songTitle = (TextView)rootView.findViewById(R.id.song_title);
        MainActivity.songArtist = (TextView)rootView.findViewById(R.id.song_artist);

        MainActivity.progressBar = (SeekBar)rootView.findViewById(R.id.song_playing_progressbar);
        MainActivity.progressBar.setMax(0);
        MainActivity.progressBar.setProgress(0);
        MainActivity.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainActivity.setTimeText(MainActivity.current, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MainActivity.mp != null) {
                    int currentPosition = seekBar.getProgress();
                    MainActivity.mp.seekTo(currentPosition);
                }
            }
        });

        MainActivity.back.setOnClickListener(listener);
        MainActivity.next.setOnClickListener(listener);
        MainActivity.play.setOnClickListener(listener);
        MainActivity.shuffle.setOnClickListener(listener);
        MainActivity.playMode.setOnClickListener(listener);
        return rootView;
    }
}
