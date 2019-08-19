package stellarnear.yfa_companion.SettingsFragments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Capacity;
import stellarnear.yfa_companion.Perso.Perso;

public class PrefCapaFragment {
    private Perso yfa = MainActivity.yfa;
    private Activity mA;
    private Context mC;

    public PrefCapaFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;
    }

    public void addCapaList(PreferenceCategory sorc) {
        for (Capacity capacity : yfa.getAllCapacities().getAllCapacitiesList()) {
            SwitchPreference switch_feat = new SwitchPreference(mC);
            switch_feat.setKey("switch_" + capacity.getId());
            switch_feat.setTitle(capacity.getName());
            switch_feat.setSummary(capacity.getDescr());
            switch_feat.setDefaultValue(true);
            sorc.addPreference(switch_feat);
        }
    }
}
