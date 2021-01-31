package stellarnear.yfa_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
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

import androidx.appcompat.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Spells.BuildSpellList;
import stellarnear.yfa_companion.Spells.EchoList;


public class MainActivity extends CustomActivity {
    public static Perso yfa;
    private boolean loading = false;
    private boolean changescreen = false;
    private boolean touched = false;
    private static boolean campaignShow = true;
    private LinearLayout mainFrameFrag;

    @Override
    protected void doActivity() {
        if (yfa == null) {
            Window window = getWindow();
            window.setStatusBarColor(getColor(R.color.start_back_color));
            lockOrient();

            Thread persoCreation = new Thread(new Runnable() {
                public void run() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                yfa = new Perso(getApplicationContext());
                                BuildSpellList.getInstance(getApplicationContext()); //s'assure qu'une instance est présente
                                EchoList.getInstance(getApplicationContext()); //s'assure qu'une instance est présente
                                loading = true;
                            } catch (Exception e) {
                                log.fatal(MainActivity.this,"Error during Perso creation",e);
                            }
                        }
                    });
                }
            });

            persoCreation.start();

            ImageView image = new ImageView(getApplicationContext());
            image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setImageDrawable(getDrawable(R.drawable.splash_image));
            image.setBackgroundColor(getColor(R.color.blue));
            setContentView(image);

            Thread loadListner = new Thread(new Runnable() {
                public void run() {
                    setLoadCompleteListner();
                }
            });
            loadListner.start();
        }
    }

    private void setLoadCompleteListner() {
        Timer timerRefreshLoading = new Timer();
        changescreen = false;
        timerRefreshLoading.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (loading && !changescreen) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changescreen = true;
                            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                            View videoLayout = inflater.inflate(R.layout.full_screen_video_player, null);
                            setContentView(videoLayout);

                            VideoView openning = videoLayout.findViewById(R.id.fullscreen_video);
                            openning.setBackground(getDrawable(R.drawable.splash_image));
                            String fileName = "android.resource://" + getPackageName() + "/raw/yfa_openning";
                            openning.setMediaController(null);
                            openning.setVideoURI(Uri.parse(fileName));
                            openning.setZOrderOnTop(true);
                            openning.start();

                            openning.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View arg0, MotionEvent arg1) {
                                    if (!touched) {
                                        unlockOrient();
                                        touched = true;
                                        buildMainPage();
                                    }
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

            String fileName = "android.resource://" + getPackageName() + "/raw/" + getString(R.string.current_campaign) + "_campaign";
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
    protected void onResumeActivity() throws Exception {
        checkOrientStart(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (yfa != null) {
            yfa.refresh();
            buildMainPage();
        }
    }

    private void startFragment() {
        Fragment fragment = new MainActivityFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mainFrameFrag.getId(), fragment, "frag_main");
        fragmentTransaction.commit();
    }


    @Override
    protected void onBackPressedActivity() {
        MainActivityFragmentSpellCast fragSpellCast = (MainActivityFragmentSpellCast) getFragmentManager().findFragmentByTag("frag_spell_cast");
        if (fragSpellCast != null && fragSpellCast.isVisible()) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
            fragSpellCast.backToSpell();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroyActivity() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
    }

    @Override
    public boolean onOptionsItemSelectedActivity(MenuItem item) {
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
    public void onConfigurationChangedActivity() {
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
