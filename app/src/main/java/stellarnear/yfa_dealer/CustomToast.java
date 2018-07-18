package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    private boolean stopToast=false;
    int duration =15000;
    int toastDurationInMilliSeconds = duration;
    private CountDownTimer toastCountDown=null;
    private Toast mToastToShow;
    private String mode;


    public CustomToast(final Context mC, String txt, String... modeInput) {
        // Set the toast and duration
        mode = modeInput.length > 0 ? modeInput[0] : "";
        mToastToShow = Toast.makeText(mC, txt, Toast.LENGTH_LONG);
        if(mode.contains("long")){
            // Set the countdown to display the toast
            toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, Toast.LENGTH_LONG /*Tick duration*/) {
                public void onTick(long millisUntilFinished) {
                    if(!stopToast){  mToastToShow.show();} else { mToastToShow.cancel(); }
                }
                public void onFinish() {
                    mToastToShow.cancel();
                }
            };

            // Show the toast and starts the countdown
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);
        } else {
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);
        }
    }


    public CustomToast(final Context mC, View view, String... modeInput) {
        // Set the toast and duration
        mode = modeInput.length > 0 ? modeInput[0] : "";
        mToastToShow = new Toast(mC);
        mToastToShow.setView(view);
        mToastToShow.setDuration(Toast.LENGTH_LONG);
        if(mode.contains("long")){
            // Set the countdown to display the toast
            toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, Toast.LENGTH_LONG /*Tick duration*/) {
                public void onTick(long millisUntilFinished) {
                    if(!stopToast){  mToastToShow.show();}
                }
                public void onFinish() {
                    mToastToShow.cancel();
                }
            };

            // Show the toast and starts the countdown
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);

        } else {
            if(mode.contains("center")){
                TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
            }
            mToastToShow.setGravity(Gravity.CENTER,0,0);

        }
    }

    public void showToast(){
        mToastToShow.show();
        if(mode.contains("long")){
            toastCountDown.start();
        }
    }

    public void hideToast(){
        mToastToShow.cancel();
        stopToast=true;
        if(mode.contains("long")){
            toastCountDown.cancel();
            toastCountDown.onFinish();
        }
        mToastToShow.cancel();
    }

    public void clickToHide(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideToast();
            }
        });
    }
}
