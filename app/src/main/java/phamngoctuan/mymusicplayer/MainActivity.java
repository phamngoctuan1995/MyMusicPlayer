package phamngoctuan.mymusicplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static Context context;
    public static ProgressDialog progressDialog;
    public static String default_search_url = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q=";
    public static ArrayList<HashMap<String, String>> listSongs;
    public static ArrayList<HashMap<String, String>> onlineSongs;
    public static ArrayList<HashMap<String, String>> offlineSongs;
    public static ViewPager viewPager;
    public static ListView list;

    public static MediaPlayer mp;
    public static int currentSong = -1;
    public static boolean isShuffle = false;
    public static int PLAY_ALL = 0;
    public static int PLAY_ONE = 1;
    public static int PLAY_NORMAL = 2;
    public static int play_mode = PLAY_ALL;
    public static Handler seekHandle;

    public static CircleImage thumb;
    public static SeekBar progressBar;
    public static TextView songTitle;
    public static TextView songArtist;
    public static ImageView back;
    public static ImageView next;
    public static ImageView play;
    public static ImageView playMode;
    public static ImageView shuffle;
    public static TextView current;
    public static TextView total;

    public static void initMediaPlayer()
    {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if ((MainActivity.play_mode != MainActivity.PLAY_NORMAL
                        || MainActivity.currentSong != MainActivity.listSongs.size() - 1)) {
                    if (MainActivity.currentSong == -1) {
                        MainActivity.currentSong = 0;
                        try {
                            loadSong();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    nextSong(true);
                    try {
                        MainActivity.thumb.Stop();
                        MainActivity.initMediaPlayer();
                        MainActivity.loadSong();
                        MainActivity.playSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        MainActivity.thumb.Stop();
                        MainActivity.initMediaPlayer();
                        MainActivity.currentSong = 0;
                        MainActivity.loadSong();
                        MainActivity.playSong();
                        MainActivity.playSong();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void initListView()
    {
        MusicLoader loader = new MusicLoader(this);
        offlineSongs = loader.getPlayList();
        listSongs = offlineSongs;
        onlineSongs = new ArrayList<HashMap<String, String>>();
    }
    private void inittabView()
    {
        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), 2);

        viewPager.setAdapter(adapter);
    }
    private void getMusicOnline(String url, int type)
    {
        final String[] mess = {"Bảng xếp hạng  nhạc Việt", "Bảng xếp hạng  nhạc Anh", "Bảng xếp hạng  nhạc Hàn", "Tìm kiếm online", "Nhạc mới"};
        if (!isNetworkAvailable())
        {
            Log.d("debug", "No internet");
            Toast.makeText(this, "No internet access", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = progressDialog.show(this, "Đang tải...", mess[type], false);
        JsoupAsyncTask task = new JsoupAsyncTask(type);
        task.execute(url);
        return;
    }

    public static void updateList()
    {
        viewPager.setCurrentItem(0);
        CustomArrayAdapter adapter = new CustomArrayAdapter(context, R.layout.list_item, listSongs);
        list.setAdapter(adapter);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initListView();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        inittabView();
        initMediaPlayer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        SearchView searchView;
        final MenuItem search_item = menu.findItem(R.id.action_search);
        searchView = (SearchView) search_item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = default_search_url + query;
                url.replaceAll(" ", "+");
                getMusicOnline(url, 3);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nhac_moi) {
            getMusicOnline("http://mp3.zing.vn/", 4);
        } else if (id == R.id.xephang_viet) {
            getMusicOnline("http://mp3.zing.vn/", 0);
        } else if (id == R.id.xephang_anh) {
            getMusicOnline("http://mp3.zing.vn/", 1);
        } else if (id == R.id.xephang_han) {
            getMusicOnline("http://mp3.zing.vn/", 2);

        } else if (id == R.id.list_songs_off)
        {
            listSongs = offlineSongs;
            updateList();
        }
        else if (id == R.id.playlist_off) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void playSong() throws IOException {
        if (mp != null && listSongs.size() > 0)
            try {
                if (mp.isPlaying()) // Pause
                {
                    mp.pause();
                    MainActivity.thumb.Stop();
                    MainActivity.play.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                } else {
                    mp.start();
                    MainActivity.thumb.Start();
                    MainActivity.play.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                }
            }
            catch (Exception e)
            {
                loadSong();
            }
    }

    public static void loadSong() throws IOException {
        if (listSongs.size() <= 0)
            return;

        if (mp == null) {
            currentSong = -1;
            initMediaPlayer();
            currentSong = 0;
        }

        HashMap<String, String> song = listSongs.get(currentSong);
        try {
            mp.setDataSource(song.get("path"));
            mp.prepare();
            songTitle.setText(song.get("title"));
            songArtist.setText(song.get("artist"));
            if (song.containsKey("thumbnail")) {
                try {
                    LoadBitmapAsync bm = new LoadBitmapAsync(thumb);
                    bm.execute(song.get("thumbnail"));
                }
                catch (Exception e)
                {
                    Log.d("debug", "Load bitmap exception: " + e.getMessage());
                }
            }
            else
                thumb.setImageResource(R.drawable.disk);
        }
        catch (Exception e) {
        }
    }

    public static void nextSong(boolean isNext) {
        if (listSongs.size() <= 0)
            return;
        if (MainActivity.isShuffle) {
            Random rand = new Random(System.currentTimeMillis());
            MainActivity.currentSong = rand.nextInt(MainActivity.listSongs.size());
        }
        else if (play_mode != PLAY_ONE) {
                if (isNext)
                    MainActivity.currentSong = (MainActivity.currentSong + 1) % MainActivity.listSongs.size();
                else {
                    if (MainActivity.currentSong == 0)
                        MainActivity.currentSong = MainActivity.listSongs.size() - 1;
                    else
                        MainActivity.currentSong -= 1;
                }
        }
    }

    public static void setTimeText(TextView text, long duration)
    {
        long minute, second, hour;
        hour = TimeUnit.MILLISECONDS.toHours((long) duration);
        minute = TimeUnit.MILLISECONDS.toMinutes((long) duration);
        second = TimeUnit.MILLISECONDS.toSeconds((long) duration);
        if (hour <= 0)
            text.setText(String.format("%02d:%02d", minute, second - minute * 60));
        else
            text.setText(String.format("%d:%02d:%02d", hour, minute - hour * 60, second - minute * 60));
    }
}
