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
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
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

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("n_rank_1", prefs.getString("n_rank_1_start", String.valueOf(mC.getResources().getInteger(R.integer.n_rank_1_def))));
                editor.putString("n_rank_2", prefs.getString("n_rank_2_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_2_def)))));
                editor.putString("n_rank_3", prefs.getString("n_rank_3_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_3_def)))));
                editor.putString("n_rank_4", prefs.getString("n_rank_4_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_4_def)))));
                editor.putString("n_rank_5", prefs.getString("n_rank_5_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_5_def)))));
                editor.putString("n_rank_6", prefs.getString("n_rank_6_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_6_def)))));
                editor.putString("n_rank_7", prefs.getString("n_rank_7_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_7_def)))));
                editor.putString("n_rank_8", prefs.getString("n_rank_8_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_8_def)))));
                editor.putString("n_rank_9", prefs.getString("n_rank_9_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_9_def)))));
                editor.putString("n_rank_10", prefs.getString("n_rank_10_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_10_def)))));
                editor.putString("n_rank_11", prefs.getString("n_rank_11_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_11_def)))));
                editor.putString("n_rank_12", prefs.getString("n_rank_12_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_12_def)))));
                editor.putString("n_rank_13", prefs.getString("n_rank_13_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_13_def)))));
                editor.putString("n_rank_14", prefs.getString("n_rank_14_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_14_def)))));
                editor.putString("n_rank_15", prefs.getString("n_rank_15_start", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_15_def)))));

                editor.putString("n_rank_1_conv", prefs.getString("n_rank_1_start_conv", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_1_conv_def)))));
                editor.putString("n_rank_2_conv", prefs.getString("n_rank_2_start_conv", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_2_conv_def)))));
                editor.putString("n_rank_3_conv", prefs.getString("n_rank_3_start_conv", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_3_conv_def)))));
                editor.putString("n_rank_4_conv", prefs.getString("n_rank_4_start_conv", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_4_conv_def)))));
                editor.putString("n_rank_5_conv", prefs.getString("n_rank_5_start_conv", String.valueOf(mC.getResources().getInteger((R.integer.n_rank_5_conv_def)))));
                editor.apply();

                yfa.getAllResources().sleepReset();

                resetTemp();
                tools.customToast(mC, "Une nouvelle journée pleine de sortilèges t'attends.", "center");
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
