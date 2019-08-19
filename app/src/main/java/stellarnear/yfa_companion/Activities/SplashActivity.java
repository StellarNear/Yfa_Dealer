package stellarnear.yfa_companion.Activities;

/**
 * Created by jchatron on 20/02/2018.
 */


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import stellarnear.yfa_companion.R;


/**
 * Created by jchatron on 26/12/2017.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashvideo_full_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        final VideoView oppening = (VideoView) findViewById(R.id.campaignContainer);
        String fileName = "android.resource://"+  getPackageName() + "/raw/yfa_openning";
        oppening.setMediaController(null);
        oppening.setVideoURI(Uri.parse(fileName));

        oppening.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                oppening.stopPlayback();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        oppening.setZOrderOnTop(true);
        oppening.start();
    }
}