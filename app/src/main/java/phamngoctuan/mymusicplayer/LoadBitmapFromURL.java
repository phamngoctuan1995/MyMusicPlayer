package phamngoctuan.mymusicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by phamn_000 on 4/8/2016.
 */

class LoadBitmapAsync extends AsyncTask<String, Void, Bitmap>
{
    ImageView view;
    boolean isPlay;

    LoadBitmapAsync(ImageView v, boolean play)
    {
        view  = v;
        isPlay = play;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        URL url = null;
        Bitmap bitMap = null;
        try {
            url = new URL(params[0]);
            InputStream is = url.openConnection().getInputStream();
            bitMap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitMap;
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
        if (bmp != null) {
            view.setImageBitmap(bmp);
            if (isPlay)
            {
                LinearLayout ll = (LinearLayout)view.getParent();
                ll.setBackground(new BitmapDrawable(BlurBuilder.blur(MainActivity.context, bmp)));
            }
        }

        super.onPostExecute(bmp);
    }
}