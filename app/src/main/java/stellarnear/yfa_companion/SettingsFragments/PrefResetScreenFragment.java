package stellarnear.yfa_companion.SettingsFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Tools;
public class PrefResetScreenFragment extends Preference {
    private Context mC;
    private Perso yfa=MainActivity.yfa;
    private View mainView;

    public PrefResetScreenFragment(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public PrefResetScreenFragment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }
    public PrefResetScreenFragment(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent)
    {
        super.onCreateView(parent);
        this.mC=getContext();

        mainView = new View(getContext());
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(parent.getWidth(), parent.getHeight());  //pour full screen
        mainView.setLayoutParams(params);
        addResetScreen();
        return mainView;
    }

    public void addResetScreen() {
        mainView.setBackgroundResource(R.drawable.reset_background);
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
                Intent intent = new Intent(mC,  MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mC.startActivity(intent);
            }
        }, time);
    }


}
