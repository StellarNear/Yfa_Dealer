package stellarnear.yfa_companion.SettingsFragments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Feat;
import stellarnear.yfa_companion.Perso.Perso;

public class PrefFeatFragment {
    private Perso yfa = MainActivity.yfa;
    private Activity mA;
    private Context mC;
    public PrefFeatFragment(Activity mA,Context mC){
        this.mA=mA;
        this.mC=mC;
    }

    public void addFeatsList(PreferenceCategory magic, PreferenceCategory def, PreferenceCategory other) {
        for (Feat feat : yfa.getAllFeats().getFeatsList()) {
            SwitchPreference switch_feat = new SwitchPreference(mC);
            switch_feat.setKey("switch_"+(feat.getId().equalsIgnoreCase("")?feat.getName():feat.getId()));
            switch_feat.setTitle(feat.getName());
            switch_feat.setSummary(feat.getDescr());
            switch_feat.setDefaultValue(feat.isActive());
            if (feat.getType().contains("feat_magic")) {
                magic.addPreference(switch_feat);
            } else if (feat.getType().contains("feat_def")) {
                def.addPreference(switch_feat);
            } else if (feat.getType().contains("feat_other")) {
                other.addPreference(switch_feat);
            }
        }
    }
}
