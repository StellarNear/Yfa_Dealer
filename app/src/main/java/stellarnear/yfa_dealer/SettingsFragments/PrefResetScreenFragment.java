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

import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Tools;

public class PrefResetScreenFragment {
    private Perso yfa=MainActivity.yfa;
    private Activity mA;
    private Context mC;

    public PrefResetScreenFragment(Activity mA, Context mC) {
        this.mA=mA;
        this.mC=mC;
    }

    public void addResetScreen() {
        View window = mA.findViewById(android.R.id.content);
        window.setBackgroundResource(R.drawable.reset_background);
        new AlertDialog.Builder(mC)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Remise à zéro des paramètres")
                .setMessage("Es-tu sûre de vouloir réinitialiser ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearSettings();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mA, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        mA.startActivity(intent);
                    }
                })
                .show();
    }

    private void clearSettings() {
        int time = 1500; // in milliseconds
        final Tools tools = new Tools();
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                yfa.resetTemp();
                yfa.refresh();
                yfa.getAllResources().sleepReset();
                yfa.getInventory().resetInventory();
                yfa.getStats().resetStats();
                tools.customToast(mC, "Remise à zero des paramètres de l'application", "center");
                Intent intent = new Intent(mA, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mA.startActivity(intent);
            }
        }, time);
    }


}
