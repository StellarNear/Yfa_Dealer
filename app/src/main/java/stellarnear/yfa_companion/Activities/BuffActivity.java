package stellarnear.yfa_companion.Activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

import stellarnear.yfa_companion.Perso.Buff;
import stellarnear.yfa_companion.Perso.BuffManager;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.ViewWeightAnimationWrapper;

/**
 * Created by jchatron on 26/12/2017.
 */

public class BuffActivity extends AppCompatActivity {
    private Context mC;
    private String currentMode="temp";
    private LinearLayout permaLin;
    private LinearLayout tempLin;
    private ArrayList<BuffManager> listTempManagers=new ArrayList<>();
    private Perso yfa=MainActivity.yfa;
    private Activity mA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode",getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_def))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        this.mA=BuffActivity.this;
        this.mC=getApplicationContext();
        setContentView(R.layout.buff_activity);
        String title=getString(R.string.buff_activity);
        SpannableString titleSpan = new SpannableString(title);
        titleSpan.setSpan(new ForegroundColorSpan(getColor(R.color.textColorPrimary)),0,title.length(),0);
        titleSpan.setSpan(new RelativeSizeSpan(1.5f)  ,0,getString(R.string.buff_activity).length(),0);
        Toolbar mActionBarToolbarhelp = (Toolbar) findViewById(R.id.toolbarBuff);
        setSupportActionBar(mActionBarToolbarhelp);
        getSupportActionBar().setTitle(titleSpan);
        tempLin=findViewById(R.id.buff_temp);
        permaLin=findViewById(R.id.buff_perma);

        findViewById(R.id.temp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show("temp");
                addBuffs();
            }
        });

        findViewById(R.id.perma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show("perma");
                addBuffs();
            }
        });


        setTimeButtons();

        addBuffs();
        show("tempInit");
    }

    private void setTimeButtons() {
        findViewById(R.id.buttonMin30).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimeToBuffs(30);
            }
        });
        findViewById(R.id.buttonH1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimeToBuffs(60);
            }
        });
        findViewById(R.id.buttonH3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimeToBuffs(60*3);
            }
        });
        findViewById(R.id.buttonH5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTimeToBuffs(60*5);
            }
        });

    }

    private void addTimeToBuffs(int i) {
        yfa.getAllBuffs().makeTimePass(i);
        for(BuffManager buffManager:listTempManagers){
            buffManager.refreshView();
        }
    }


    private void show(String mode) {
        if(!currentMode.equalsIgnoreCase(mode)){
            int dura=500;
            switch (mode){
                case "perma":
                    changeTab(8,2,tempLin,dura);
                    changeTab(8,2,(LinearLayout)findViewById(R.id.buff_temp_tab_icon),dura);
                    changeTab(2,8,permaLin,dura);
                    changeTab(2,8,(LinearLayout)findViewById(R.id.buff_perma_tab_icon),dura);
                    break;
                case "temp":
                    changeTab(2,8,tempLin,dura);
                    changeTab(2,8,(LinearLayout)findViewById(R.id.buff_temp_tab_icon),dura);
                    changeTab(8,2,permaLin,dura);
                    changeTab(8,2,(LinearLayout)findViewById(R.id.buff_perma_tab_icon),dura);
                    break;
                default:
                    dura=2000;
                    changeTab(1,8,tempLin,dura);
                    changeTab(1,8,(LinearLayout)findViewById(R.id.buff_temp_tab_icon),dura);
                    changeTab(1,2,permaLin,dura);
                    changeTab(1,2,(LinearLayout)findViewById(R.id.buff_perma_tab_icon),dura);
                    mode="temp";
                    break;
            }
            this.currentMode=mode;
        }
    }

    private void changeTab(int weightFrom, int weightTo, LinearLayout lin, int dura) {
        ViewWeightAnimationWrapper animationWrapper = new ViewWeightAnimationWrapper(lin);
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                animationWrapper,
                "weight",
                weightFrom,
                weightTo);
        anim.setDuration(dura);
        anim.start();
    }


    private void addBuffs() {
        tempLin.removeAllViews();
        permaLin.removeAllViews();
        ArrayList<Buff> tempBuffs = yfa.getAllBuffs().getAllTempBuffs();
        ArrayList<Buff> permaBuffs = yfa.getAllBuffs().getAllPermaBuffs();
        if(currentMode.equalsIgnoreCase("temp")){
            populate(permaLin,permaBuffs,true);
            populate(tempLin,tempBuffs,false);
        } else {
            populate(permaLin,permaBuffs,false);
            populate(tempLin,tempBuffs,true);
        }
    }

    private void populate(LinearLayout lin,ArrayList<Buff> listBuff , Boolean close) {
        int nCol=3;
        if(close){
            nCol=2;
        }
        LinearLayout lineHori = new LinearLayout(mC);
        lineHori.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
        lin.addView(lineHori);
        int iElem=0;
        listTempManagers=new ArrayList<>();
        for (Buff buff : listBuff){
            BuffManager buffManager= new BuffManager(mA,buff);
            listTempManagers.add(buffManager);
            iElem++;
            if(iElem>nCol){
                lineHori = new LinearLayout(mC);
                lineHori.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
                lin.addView(lineHori);
                iElem=1;
            }

            View ico;
            if(close){
                ico=buffManager.getbuffView().close().getView();
            } else {
                ico=buffManager.getbuffView().expand().getView();
            }
            if (ico.getParent() != null) {
                ((ViewGroup) ico.getParent()).removeView(ico);
            }
            lineHori.addView(ico);
        }
    }





    /*

    Activity stuff

     */


    @Override
    protected void onResume(){
        super.onResume();
        checkOrientStart(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation()==Surface.ROTATION_0) {
            Intent intent = new Intent(mA, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
        if (display.getRotation()==Surface.ROTATION_180) {
            Intent intent = new Intent(mA, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }

    private void checkOrientStart(int screenOrientation) {
        if (getRequestedOrientation()!=screenOrientation) {
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
}
