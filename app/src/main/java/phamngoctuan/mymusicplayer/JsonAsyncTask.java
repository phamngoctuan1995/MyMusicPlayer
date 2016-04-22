package phamngoctuan.mymusicplayer;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by phamn_000 on 4/8/2016.
 */
public class JsonAsyncTask extends AsyncTask<Void, Void, ArrayList<JSONObject>>
{
    ArrayList<HashMap<String, String>> list;
    ArrayList<String> xml_url = null;
    JsonAsyncTask(ArrayList<HashMap<String, String>> l, ArrayList<String> url)
    {
        list = l;
        xml_url = url;
    }

    @Override
    protected ArrayList<JSONObject> doInBackground(Void... params) {
        JSONObject jsonObj;
        ArrayList<JSONObject> jsonArr = new ArrayList<JSONObject>();
        for (int i = 0; i < xml_url.size(); ++i)
        {
            try {
                jsonArr.add(MyJSONReader.readJsonFromUrl(xml_url.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArr;
    }


    @Override
    protected void onPostExecute(ArrayList<JSONObject> jsonObjects) {
        super.onPostExecute(jsonObjects);
        JSONObject jsonObj;

        list.clear();
        for (int i = 0; i < jsonObjects.size(); ++i)
        {
            jsonObj = jsonObjects.get(i);
            HashMap<String, String> song = new HashMap<String, String>();

            try {
                if (jsonObj.has("title"))
                    song.put("title", jsonObj.getString("title"));
                if (jsonObj.has("artist"))
                    song.put("artist", jsonObj.getString("artist"));
                if (jsonObj.has("total_play"))
                    song.put("totalplay", jsonObj.getString("total_play"));
                if (jsonObj.has("thumbnail"))
                    song.put("thumbnail", "http://image.mp3.zdn.vn/" + jsonObj.getString("thumbnail"));
                if (jsonObj.has("source")) {
                    JSONObject jsource = jsonObj.getJSONObject("source");
                    if (jsource.has("128"))
                        song.put("path", jsource.getString("128").replaceAll("\\/", "/"));
                }
                list.add(song);
            }
            catch (Exception e)
            {
                Log.d("debug", "Json exception");
            }
        }
        MainActivity.listSongs = list;
        MainActivity.updateList();
        MainActivity.progressDialog.dismiss();
    }
}
