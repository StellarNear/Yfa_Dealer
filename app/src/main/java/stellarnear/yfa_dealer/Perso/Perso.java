package stellarnear.yfa_dealer.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.Calculation;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Tools;

/**
 * Created by jchatron on 26/12/2017.
 */

public class Perso {

    private Inventory inventory;
    private AllResources allResources;
    private Tools tools=new Tools();
    private Context mC;
    private SharedPreferences prefs;
    private Calculation calculation=new Calculation();

    public Perso(Context mC) {
        inventory = new Inventory(mC);
        allResources = new AllResources(mC);
        this.mC=mC;
        this.prefs= PreferenceManager.getDefaultSharedPreferences(mC);
    }

    public void refresh() {
        allResources.refreshMaxs();
    }

    public AllResources getAllResources() {
        return this.allResources;
    }

    public Integer getResourceValue(String resId){
        Resource res = allResources.getResource(resId);
        Integer value=0;
        if(res!=null){
            value=res.getCurrent();
        }
        return value;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void castConvSpell(Integer selected_rank) {
        allResources.castConvSpell(selected_rank);
    }
    public void castSpell(Integer selected_rank) {
        allResources.castSpell(selected_rank);
    }
    public void castSpell(Spell spell) {
        if (!spell.isCast()){
            spell.cast();
            allResources.castSpell(calculation.currentRank(spell));
            if (spell.getID().equalsIgnoreCase("true_strike")) {
                getAllResources().getResource("true_strike").earn(1);
            }
        }
    }


    public int getCharismaMod() {
        return  tools.toInt(prefs.getString("charisme",String.valueOf(mC.getResources().getInteger(R.integer.charisme_def))));
    }

    public int getStrMod() {
        return tools.toInt(prefs.getString("force",String.valueOf(mC.getResources().getInteger(R.integer.force_def))));
    }

    public int getDexMod() {
        return tools.toInt(prefs.getString("dexterite",String.valueOf(mC.getResources().getInteger(R.integer.dexterite_def))));
    }

    public int getCasterLevel() {
        int val=0;
        val+= tools.toInt(prefs.getString("nls_val",String.valueOf(mC.getResources().getInteger(R.integer.nls_val_def))));
        if (prefs.getBoolean("karma_switch",false)){
            val+=4;
        }
        if (prefs.getBoolean("ioun_stone",true)){
            val+=1;
        }
        val+=tools.toInt(prefs.getString("NLS_bonus",String.valueOf(0)));
        return val;
    }


    public int getBaseAtk() {
        return tools.toInt(prefs.getString("base_atk",String.valueOf(mC.getResources().getInteger(R.integer.base_atk_def))));
    }

    public int getBonusAtk() {
        int epic= tools.toInt(prefs.getString("epic_atk_bonus",String.valueOf(mC.getResources().getInteger(R.integer.epic_atk_bonus_def))));
        int bonus = tools.toInt(prefs.getString("bonus_atk",String.valueOf(mC.getResources().getInteger(R.integer.bonus_atk_def))));
        int bonusTemp= tools.toInt(prefs.getString("bonus_atk_temp",String.valueOf(0)));
        return epic+bonus+bonusTemp;
    }

    public void resetTemp() {
        List<String> allTempList = Arrays.asList("NLS_bonus","bonus_atk_temp");
        for (String temp : allTempList) {
            prefs.edit().putString(temp, "0").apply();
        }
        prefs.edit().putBoolean("karma_switch", false).apply();
    }
}
