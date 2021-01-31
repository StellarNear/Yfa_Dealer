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
            switch_feat.setKey("switch_" + (capacity.getId().equalsIgnoreCase("")?capacity.getName():capacity.getId()));
            switch_feat.setTitle(capacity.getName());
            String descr="";
            if(capacity.getDailyUse()!=0){
                descr+=capacity.getDailyUse()+"/j ";
            }
            if (capacity.getValue()!=0){
                descr+="Valeur : "+capacity.getValue();
            }
            if(!descr.equalsIgnoreCase("")){
                descr+="\n";
            }
            descr+=capacity.getDescr();
            switch_feat.setSummary(descr);
            switch_feat.setDefaultValue(true);
            sorc.addPreference(switch_feat);
        }
    }
}
