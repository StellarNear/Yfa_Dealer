package stellarnear.yfa_companion.Spells;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Perso.Resource;
import stellarnear.yfa_companion.Perso.SelfCustomLog;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Tools;

public class SpellsRanksManager extends SelfCustomLog {
    private Tools tools= Tools.getTools();
    private SharedPreferences settings;
    private int highestSpellRank=0;
    private int highestSpellConvank=0;
    private ArrayList<Resource> spellTiers;
    private ArrayList<Resource> spellConvTiers;
    private Perso yfa= MainActivity.yfa;
    private OnHighTierChange mListner;
    private Context mC;
    public SpellsRanksManager(Context mC) throws Exception {
        this.mC=mC;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        refreshRanks();
    }

    public void refreshRanks() throws Exception {
        int newHighTier=tools.toInt(settings.getString("highest_tier_spell",String.valueOf(mC.getResources().getInteger(R.integer.highest_tier_spell_def))));
        int newHighConvTier=tools.toInt(settings.getString("highest_tier_spell_conv",String.valueOf(mC.getResources().getInteger(R.integer.highest_tier_spell_conv_def))));
        if(highestSpellRank!=newHighTier || highestSpellConvank!=newHighConvTier){
            highestSpellRank = newHighTier;
            highestSpellConvank = newHighConvTier;
            refreshAllTiers();
            if(mListner!=null){mListner.onEvent();}
        }
    }

    private void refreshAllTiers() {
        spellTiers=new ArrayList<>();
        spellConvTiers=new ArrayList<>();
        for(int rank=1;rank<=highestSpellRank;rank++){
            Resource rankRes = new Resource("Sort disponible rang "+rank,"Sort "+rank,true,true,"spell_rank_"+rank,mC);
            int val = readResourceMax( rankRes.getId());
            rankRes.setMax(val);
            rankRes.setCurrent(readResourceCurrent(rankRes.getId()));
            rankRes.setFromSpell();
            spellTiers.add(rankRes);
        }
        for(int rankConv=1;rankConv<=highestSpellConvank;rankConv++){
            Resource rankConvRes = new Resource("Sort convertible disponible rang "+rankConv,"Sort Conv "+rankConv,true,true,"spell_conv_rank_"+rankConv,mC);
            int valConv = readResourceMax( rankConvRes.getId());
            rankConvRes.setMax(valConv);
            rankConvRes.setCurrent(readResourceCurrent(rankConvRes.getId()));
            rankConvRes.setFromSpell();
            spellConvTiers.add(rankConvRes);
        }
    }

    private int readResourceCurrent(String id) {
        int val=0;
        try {
            val=settings.getInt(id + "_current",  0);
        } catch (Exception e) {
            log.warn("Could not find resource current for "+id);
        }
        return val;
    }

    private int readResourceMax(String key) {
        int val=0;
        int defId=0;
        try {
            defId = mC.getResources().getIdentifier(key.toLowerCase() + "_def", "integer", mC.getPackageName());
        } catch (Exception e) {
            log.warn("Could not find def value for "+key);
        }
        try {  //deux trycatch different car on peut ne pas avoir de valeur def defini mais une valeur manuelle dans les settings
            if(defId!=0){
                val=tools.toInt(settings.getString(key.toLowerCase(), String.valueOf(mC.getResources().getInteger(defId))));
            } else {
                val=tools.toInt(settings.getString(key.toLowerCase(), "0"));
            }
        } catch (Resources.NotFoundException e) {
            log.warn("Could not read resource max for "+key);
        }
        return val;
    }

    public ArrayList<Resource> getSpellTiers() {
        return spellTiers;
    }

    public ArrayList<Resource> getSpellConvTiers() {
        return spellConvTiers;
    }

    public void refreshMax() {
        for(Resource res : spellTiers){
            int val = readResourceMax( res.getId());
            res.setMax(val);
        }
        for(Resource resConv : spellConvTiers){
            int val = readResourceMax( resConv.getId());
            resConv.setMax(val);
        }
    }

    public int getHighestTier() {
        return highestSpellRank;
    }

    public String getPercentAvail() {
        int allRankCurrent=0;
        int allRankMax=0;
        for (Resource res : spellTiers){
            allRankCurrent+=res.getCurrent();
            allRankMax+=res.getMax();
        }
        return Math.round(100f*allRankCurrent/allRankMax)+"%";
    }

    public String getPercentAvailConv() {
        int allRankCurrent=0;
        int allRankMax=0;
        for (Resource res : spellConvTiers){
            allRankCurrent+=res.getCurrent();
            allRankMax+=res.getMax();
        }
        return Math.round(100f*allRankCurrent/allRankMax)+"%";
    }

    public interface OnHighTierChange {
        void onEvent() throws Exception;
    }

    public void setRefreshEventListener(OnHighTierChange eventListener) {
        mListner = eventListener;
    }
}
