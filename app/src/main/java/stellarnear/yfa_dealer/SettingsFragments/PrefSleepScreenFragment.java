package stellarnear.yfa_dealer.SettingsFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Tools;

public class PrefSleepScreenFragment {
    private Perso yfa=MainActivity.yfa;
    private Activity mA;
    private Context mC;
    private Tools tools=new Tools();

    public PrefSleepScreenFragment(Activity mA, Context mC) {
        this.mA=mA;
        this.mC=mC;
    }

    public void addSleepScreen() {
        View window = mA.findViewById(android.R.id.content);
        window.setBackgroundResource(R.drawable.sleep_background);
        new AlertDialog.Builder(mC)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Repos")
                .setMessage("Es-tu sûre de vouloir te reposer maintenant ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sleep();
                    }
                })
                .setNegativeButton("Sans sorts", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        halfSleep();
                    }
                })
                .setNeutralButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    private void sleep() {
        final Tools tools = new Tools();
        tools.customToast(mC, "Fais de beaux rêves !", "center");
        int time = 2000; // in milliseconds
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                yfa.getAllResources().sleepReset();
                resetTemp();
                tools.customToast(mC, "Une nouvelle journée pleine de sortilèges t'attends.", "center");
                Intent intent = new Intent(mA, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mA.startActivity(intent);
            }
        }, time);
    }

    private void halfSleep() {
        final Tools tools = new Tools();
        tools.customToast(mC, "Fais de beaux rêves !", "center");
        int time = 2000; // in milliseconds
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                yfa.getAllResources().halfSleepReset();
                resetTemp();
                tools.customToast(mC, "Une sans sortilèges t'attends...", "center");
                Intent intent = new Intent(mA, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mA.startActivity(intent);
            }
        }, time);
    }

    private void resetTemp() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        List<String> allTempList = Arrays.asList("NLS_bonus");
        for (String temp : allTempList) {
            prefs.edit().putString(temp, "0").apply();
        }
        prefs.edit().putBoolean("karma_switch", false).apply();
    }

}
