package stellarnear.yfa_companion.SettingsFragments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.text.InputType;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.EditTextPreference;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Perso.Resource;
import stellarnear.yfa_companion.Log.SelfCustomLog;
import stellarnear.yfa_companion.Spells.SpellsRanksManager;
import stellarnear.yfa_companion.Tools;

public class PrefSpellRankFragment extends SelfCustomLog {
    private Perso yfa = MainActivity.yfa;
    private Activity mA;
    private Context mC;
    private SpellsRanksManager rankManager;
    private PreferenceCategory spell;
    private PreferenceCategory spellConv;
    private Tools tools=Tools.getTools();
    public PrefSpellRankFragment(Activity mA, Context mC){
        this.mA=mA;
        this.mC=mC;
        this.rankManager=yfa.getAllResources().getRankManager();
    }


    public void addSpellRanks(PreferenceCategory spell, PreferenceCategory spellConv) throws Exception {
        this.spell=spell;
        this.spellConv=spellConv;
        refreshList();
    }

    private void refreshList() throws Exception {
        rankManager.refreshRanks();
        spell.removeAll();
        spellConv.removeAll();
        for (Resource res : rankManager.getSpellTiers()) {
            EditTextPreference text = new EditTextPreference(mC, InputType.TYPE_CLASS_NUMBER);
            text.setKey(res.getId());
            text.setTitle(res.getShortname());
            text.setSummary(res.getName()+" : %s");
            text.setDefaultValue(String.valueOf(readDef(res.getId())));
            spell.addPreference(text);
        }

        for (Resource resConv: rankManager.getSpellConvTiers()) {
            EditTextPreference text = new EditTextPreference(mC,InputType.TYPE_CLASS_NUMBER);
            text.setKey(resConv.getId());
            text.setTitle(resConv.getShortname());
            text.setSummary(resConv.getName()+" : %s");
            text.setDefaultValue(String.valueOf(readDef(resConv.getId())));
            spellConv.addPreference(text);
        }
    }

    private int readDef(String key) {
        int val=0;
        try {
            int defId = mC.getResources().getIdentifier(key.toLowerCase() + "_def", "integer", mC.getPackageName());
            val=tools.toInt(String.valueOf(mC.getResources().getInteger(defId)));
        } catch (Exception e) {
            log.warn("Could not find resource def for "+key);
        }
        return val;
    }

    public void refresh() throws Exception {
        if(spell!=null){refreshList();}
    }
}
