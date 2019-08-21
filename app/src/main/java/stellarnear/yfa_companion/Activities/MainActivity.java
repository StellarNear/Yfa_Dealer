package stellarnear.yfa_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;


public class MainActivity extends AppCompatActivity {
    public static Perso yfa;
    private boolean loading = false;
    private boolean touched = false;
    private static boolean campaignShow = false;
    private LinearLayout mainFrameFrag;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_def))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);

        if (yfa == null) {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.start_back_color));
            lockOrient();

            Thread persoCreation = new Thread(new Runnable() {
                public void run() {
                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run() {
                            yfa = new Perso(getApplicationContext());
                            loading = true;
                        }
                });
            }});

            persoCreation.start();

            final ImageView image = new ImageView(getApplicationContext());
            image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            image.setImageDrawable(getDrawable(R.drawable.background_loading)); //TODO
            image.setBackgroundColor(getColor(R.color.blue));
            setContentView(image);

            Thread loadListner = new Thread(new Runnable() {
                public void run() {
                    setLoadCompleteListner(image);
                }
            });
            loadListner.start();

        } else {
            buildMainPage();
        }
    }

    private void setLoadCompleteListner(final ImageView image) {
        Timer timerRefreshLoading = new Timer();
        timerRefreshLoading.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (loading) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageDrawable(getDrawable(R.drawable.background_loaded)); //TODO
                            image.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View arg0, MotionEvent arg1) {
                                    unlockOrient();
                                    touched = true;
                                    buildMainPage();
                                    return true;//always return true to consume event
                                }
                            });
                        }
                    });
                }
            }
        }, 333, 333);
    }

    private void buildMainPage() {
        setContentView(R.layout.activity_main);
        mainFrameFrag = findViewById(R.id.fragment_main_frame_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        final VideoView campaignVideo = (VideoView) findViewById(R.id.campaignContainer);

        Boolean switchCampaignBool = settings.getBoolean("switch_campaign_gif", getApplicationContext().getResources().getBoolean(R.bool.switch_campaign_gif_def));
        if (campaignShow && switchCampaignBool) {
            campaignVideo.setVisibility(View.VISIBLE);

            String fileName = "android.resource://"+  getPackageName() + "/raw/campaign";
            campaignVideo.setMediaController(null);
            campaignVideo.setVideoURI(Uri.parse(fileName));
            campaignVideo.start();

            campaignVideo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    campaignShow = false;
                    campaignVideo.setOnTouchListener(null);
                    campaignVideo.setVisibility(View.GONE);
                    Window window = getWindow();
                    window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
                    getSupportActionBar().show();
                    startFragment();
                    return true;//always return true to consume event
                }
            });

        } else {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
            getSupportActionBar().show();
            startFragment();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOrientStart(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (yfa != null && loading && touched) {
            yfa.refresh();
            buildMainPage();
        }
    }

    private void startFragment() {
        Fragment fragment = new MainActivityFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mainFrameFrag.getId(), fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            unlockOrient();
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                //on y est déja
                break;

            case Surface.ROTATION_90:
                Intent intent_buff = new Intent(MainActivity.this, BuffActivity.class);
                startActivity(intent_buff);
                finish();
                break;

            case Surface.ROTATION_270:
                Intent intent_help = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent_help);
                finish();
                break;
        }
    }

    private void checkOrientStart(int screenOrientation) {
        if (getRequestedOrientation() != screenOrientation) {
            setRequestedOrientation(screenOrientation);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
            }, 2000);
        }
    }

    private void lockOrient() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void unlockOrient() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

}
