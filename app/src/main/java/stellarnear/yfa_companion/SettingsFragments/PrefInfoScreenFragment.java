package stellarnear.yfa_companion.SettingsFragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import stellarnear.yfa_companion.BuildConfig;
import stellarnear.yfa_companion.CustomAlertDialog;
import stellarnear.yfa_companion.R;

public class PrefInfoScreenFragment {
    private Activity mA;
    private Context mC;
    private CustomAlertDialog infoPopup;

    public PrefInfoScreenFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;

        createInfoPopup();
    }

    private void createInfoPopup() {
        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.custom_info_patchnote,null);
        LinearLayout mainLin = mainView.findViewById(R.id.custom_info_patchnote);

        TextView version = new TextView(mC);
        version.setText("Version actuelle : " + BuildConfig.VERSION_NAME);
        version.setTextSize(22);
        mainLin.addView(version);
        TextView time = new TextView(mC);
        time.setText("Temps de travail nécessaire : " + mC.getString(R.string.time_spend));
        mainLin.addView(time);

        final TextView texte_infos = new TextView(mC);
        texte_infos.setSingleLine(false);
        texte_infos.setTextColor(Color.DKGRAY);
        texte_infos.setText(mC.getString(R.string.basic_infos));
        mainLin.addView(texte_infos);

        final TextView titlePatch = new TextView(mC);
        titlePatch.setTextSize(20);
        titlePatch.setTextColor(Color.DKGRAY);
        titlePatch.setText("Liste des changements de version :");
        mainLin.addView(titlePatch);

        ScrollView scroll_info = new ScrollView(mC);
        mainLin.addView(scroll_info);
        final TextView textePatch = new TextView(mC);
        textePatch.setSingleLine(false);
        textePatch.setTextColor(Color.DKGRAY);
        textePatch.setText(mC.getString(R.string.patch_list));
        scroll_info.addView(textePatch);

        this.infoPopup = new CustomAlertDialog(mA,mC,mainView);
        this.infoPopup.setPermanent(true);
        this.infoPopup.clickToHide(mainView.findViewById(R.id.custom_info_patchnote_title));

    }

    public void showInfo() {
        this.infoPopup.showAlert();
    }

}
