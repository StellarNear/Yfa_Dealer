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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import stellarnear.yfa_companion.Perso.AllBuffs;
import stellarnear.yfa_companion.Perso.Buff;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.ViewWeightAnimationWrapper;

/**
 * Created by jchatron on 26/12/2017.
 */

public class BuffActivity extends AppCompatActivity {
    private Perso yfa = MainActivity.yfa;
    private Context mC;
    private String currentMode="temp";
    private LinearLayout permaLin;
    private LinearLayout tempLin;
    private Activity mA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode",getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_def))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        this.mA=this;
        this.mC=getApplicationContext();
        setContentView(R.layout.buff_activity);
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

        addBuffs();
        show("tempInit");
    }



    private void show(String mode) {
        if(!currentMode.equalsIgnoreCase(mode)){
            int dura=500;
            switch (mode){
                case "perma":
                    changeTab(8,2,tempLin,dura);
                    changeTab(2,8,permaLin,dura);
                    break;
                case "temp":
                    changeTab(2,8,tempLin,dura);
                    changeTab(8,2,permaLin,dura);
                    break;
                default:
                    dura=2000;
                    changeTab(1,2,tempLin,dura);
                    changeTab(1,8,permaLin,dura);
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
        ArrayList<Buff> tempBuffs = AllBuffs.getInstance(mC).getAllTempBuffs();
        ArrayList<Buff> permaBuffs = AllBuffs.getInstance(mC).getAllPermaBuffs();
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
        for (Buff buff : listBuff){
            iElem++;
            if(iElem>nCol){
                lineHori = new LinearLayout(mC);
                lineHori.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
                lin.addView(lineHori);
                iElem=1;
            }

            lineHori.addView(logoBuff(buff,close));
        }
    }

    private LinearLayout logoBuff(Buff buff,boolean close) {
        LayoutInflater inflater = mA.getLayoutInflater();
        LinearLayout buffView = (LinearLayout) inflater.inflate(R.layout.buff_icon, null);
        ((ImageView)buffView.findViewById(R.id.buff_icon_image)).setImageDrawable(getDrawable(R.drawable.mire_test));
        if (close){
            buffView.findViewById(R.id.buff_icon_name).setVisibility(View.GONE);
        } else {
            ((TextView)buffView.findViewById(R.id.buff_icon_name)).setText(buff.getSpellName());
        }
        buffView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1));
        return buffView;
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
