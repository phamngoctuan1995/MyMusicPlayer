package phamngoctuan.mymusicplayer;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by phamn_000 on 4/8/2016.
 */
class JsoupAsyncTask extends AsyncTask<String, Void, Document> {

    int id;

    public JsoupAsyncTask(int i)
    {
        id = i;
    }

    @Override
    protected Document doInBackground(String... params) {
        Document doc = null;
        Document temp = null;
        try {
            doc = Jsoup.connect(params[0]).get();
            if (id < 3)
            {
                Element chart = doc.body().getElementsByAttributeValue("data-group", "_chart_song").get(id);
                Log.d("debug", chart.attr("href"));
                temp = Jsoup.connect(chart.attr("href")).get();
                Log.d("debug", temp.title());

                doc = temp;
            }
        } catch (Exception e) {
            Log.d("debug", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    protected void onPostExecute(Document document) {
        try {
            if (document == null)
                return;
            ArrayList<String> song_id = new ArrayList<String>();
            Elements songs = new Elements();
            if (id < 3) {
                Element body = document.body().getElementsByClass("table-body").first();
                songs = body.getElementsByAttributeValue("data-type", "song");
            }
            else if (id == 3)
            {
                songs = document.body().getElementsByAttributeValueMatching("data-type", "song");
                songs.remove(songs.size() - 1);
                songs.remove(songs.size() - 1);
            }
            else
            if (id == 4)
            {
                songs = document.body().getElementsByClass("tool-song-hover").get(1)
                        .getElementsByAttributeValue("data-type", "song");
            }

            for (Element song : songs) {
                String id = song.attr("data-id");
                song_id.add("http://api.mp3.zing.vn/api/mobile/song/getsonginfo?keycode=fafd463e2131914934b73310aa34a23f&requestdata={\"id\":\"<!--" + id + "-->\"}");
            }
            JsonAsyncTask json = new JsonAsyncTask(MainActivity.onlineSongs, song_id);
            json.execute();
        }
        catch (Exception e)
        {
            Log.d("debug", "Exception " + e.getMessage());
        }
        super.onPostExecute(document);
    }
}
