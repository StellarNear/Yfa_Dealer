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
    private boolean stopToast;
    public void toastIt(final Context mC, String txt, String... modeInput) {
        // Set the toast and duration
        stopToast=false;
        String mode = modeInput.length > 0 ? modeInput[0] : "";

        final Toast mToastToShow = Toast.makeText(mC, txt, Toast.LENGTH_LONG);
        if(mode.contains("long")){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            int duration =15000;
            int toastDurationInMilliSeconds = duration;

            // Set the countdown to display the toast
            final CountDownTimer toastCountDown;
            toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, Toast.LENGTH_LONG /*Tick duration*/) {
                public void onTick(long millisUntilFinished) {
                    if(!stopToast){  mToastToShow.show();} else { mToastToShow.cancel(); }
                }
                public void onFinish() {
                    mToastToShow.cancel();
                }
            };
            mToastToShow.getView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    stopToast=true;
                    mToastToShow.cancel();
                    toastCountDown.onFinish();
                    return true;
                }
            });
            // Show the toast and starts the countdown
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);
            mToastToShow.show();
            toastCountDown.start();
        } else {
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);
            mToastToShow.show();
        }
    }

    public void toastTooltipCustomView(final Context mC, View view, String... modeInput) {
        // Set the toast and duration
        stopToast=false;
        String mode = modeInput.length > 0 ? modeInput[0] : "";

        final Toast mToastToShow = new Toast(mC);
        mToastToShow.setView(view);
        mToastToShow.setDuration(Toast.LENGTH_LONG);
        if(mode.contains("long")){
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            int duration =15000;
            int toastDurationInMilliSeconds = duration;

            // Set the countdown to display the toast
            final CountDownTimer toastCountDown;
            toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, Toast.LENGTH_LONG /*Tick duration*/) {
                public void onTick(long millisUntilFinished) {
                    if(!stopToast){  mToastToShow.show();} else { mToastToShow.cancel(); }
                }
                public void onFinish() {
                    mToastToShow.cancel();
                }
            };
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    stopToast=true;
                    mToastToShow.cancel();
                    toastCountDown.onFinish();
                    return true;
                }
            });
            // Show the toast and starts the countdown
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);
            mToastToShow.show();
            toastCountDown.start();
        } else {
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);
            mToastToShow.show();
        }
    }

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
