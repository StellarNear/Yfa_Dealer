package stellarnear.yfa_companion.SettingsFragments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.MythicCapacity;
import stellarnear.yfa_companion.Perso.Perso;

public class PrefMythicCapaFragment {
    private Perso yfa = MainActivity.yfa;
    private Activity mA;
    private Context mC;

    public PrefMythicCapaFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void addMythicCapaList(PreferenceCategory common, PreferenceCategory protect ,PreferenceCategory all) {
           for (MythicCapacity capacity : yfa.getAllMythicCapacities().getAllMythicCapacitiesList()) {
            SwitchPreference switch_feat = new SwitchPreference(mC);
            switch_feat.setKey("switch_"+capacity.getId());
            switch_feat.setTitle(capacity.getName());
            switch_feat.setSummary(capacity.getDescr());
            switch_feat.setDefaultValue(true);
            if (capacity.getType().contains("Commun")) {
                common.addPreference(switch_feat);
            } else if (capacity.getType().equals("Voie du Protecteur")) {
                protect.addPreference(switch_feat);
            } else if (capacity.getType().contains("Voie Universelle")) {
                all.addPreference(switch_feat);
            }
        }
    }
}
