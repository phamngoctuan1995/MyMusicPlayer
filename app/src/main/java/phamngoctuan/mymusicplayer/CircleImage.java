package phamngoctuan.mymusicplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by phamn_000 on 4/4/2016.
 */
public class CircleImage extends ImageView {

    public CircleImage(Context context) {
        super(context);
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int angle = 0;
    private int radius;
    private Shader shader = null;
    private Handler handle = new Handler();
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            angle = (angle + 2) % 360;
            invalidate();
            handle.postDelayed(update, 40);
        }
    };


    public void Start()
    {
        handle.post(update);
    }

    public void Stop()
    {
        handle.removeCallbacks(update);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getHeight() > getWidth())
            radius = getWidth() / 2;
        else
            radius = getHeight() / 2;

        Bitmap original = Bitmap.createBitmap(
                    getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas originalCanvas = new Canvas(original);
        super.onDraw(originalCanvas);    // ImageView
            // Lợi dụng hệ thống vẽ ảnh source vào bitmap original

        shader = new BitmapShader(original,
                    Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        Matrix matrix = new Matrix();
        matrix.setRotate(angle, getWidth() / 2, getHeight() / 2);
        shader.setLocalMatrix(matrix);
        Paint paint = new Paint();
        paint.setShader(shader);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
    }

}

