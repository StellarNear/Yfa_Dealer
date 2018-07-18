package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jchatron on 16/02/2018.
 */

public class Tools {


    public Integer toInt(String key) {
        Integer value = 0;
        try {   value = Integer.parseInt(key);  } catch (Exception e) {     }
        return value;
    }

    public Long toLong(String key) {
        Long value = 0L;
        try {   value = Long.parseLong(key);  } catch (Exception e) {     }
        return value;
    }

    public Boolean toBool(String key) {
        Boolean value = false;
        try {
            value = Boolean.valueOf(key);
        } catch (Exception e) {
        }
        return value;
    }

    public Drawable resize(Context mC, Drawable image, int pixelSizeIcon) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixelSizeIcon, pixelSizeIcon, false);
        return new BitmapDrawable(mC.getResources(), bitmapResized);
    }

    public Drawable resize(Context mC, int imageId, int pixel_size_icon) {
        Drawable image = mC.getDrawable(imageId);
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixel_size_icon, pixel_size_icon, false);
        return new BitmapDrawable(mC.getResources(), bitmapResized);
    }
}
