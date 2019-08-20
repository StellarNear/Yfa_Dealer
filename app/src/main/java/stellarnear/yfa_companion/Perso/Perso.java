package stellarnear.yfa_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_companion.Calculation;
import stellarnear.yfa_companion.HallOfFame;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Stats.Stats;
import stellarnear.yfa_companion.Tools;

/**
 * Created by jchatron on 26/12/2017.
 */

public class Perso {

    private Inventory inventory;
    private AllResources allResources;
    private Stats stats;
    private HallOfFame hallOfFame;

    private AllAbilities allAbilities;
    private AllFeats allFeats;
    private AllCapacities allCapacities;
    private AllSkills allSkills;
    private AllMythicFeats allMythicFeats;
    private AllMythicCapacities allMythicCapacities;
    private AllBuffs allBuffs;

    private Tools tools=new Tools();
    private Context mC;
    private SharedPreferences prefs;
    private Calculation calculation=new Calculation();

    public Perso(Context mC) {
        this.mC=mC;
        this.prefs= PreferenceManager.getDefaultSharedPreferences(mC);
        inventory = new Inventory(mC);
        allResources = new AllResources(mC);
        stats = new Stats(mC);
        hallOfFame=new HallOfFame(mC);
        allFeats = new AllFeats(mC);
        allCapacities = new AllCapacities(mC);
        allMythicFeats = new AllMythicFeats(mC);
        allMythicCapacities = new AllMythicCapacities(mC);
        allAbilities = new AllAbilities(mC);
        allSkills = new AllSkills(mC);
        allBuffs = new AllBuffs(mC);
    }

    public void refresh() {
        allAbilities.refreshAllAbilities();
        allFeats.refreshAllSwitch();
        allMythicFeats.refreshAllSwitch();
        allSkills.refreshAllVals();
        allResources.refreshMaxs();
    }


    public AllAbilities getAllAbilities() {
        return allAbilities;
    }

    public AllCapacities getAllCapacities() {
        return allCapacities;
    }

    public AllMythicCapacities getAllMythicCapacities() {
        return allMythicCapacities;
    }

    public AllMythicFeats getAllMythicFeats() {
        return allMythicFeats;
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

    public int getCasterLevel() {
        int val= getAbilityScore("ability_lvl");
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

    public Stats getStats() {
        return stats;
    }


    public HallOfFame getHallOfFame() {
        return hallOfFame;
    }

    public AllSkills getAllSkills() {
        return allSkills;
    }

    public Integer getSkillBonus(Context mC,String skillId) {
        int bonusTemp = allSkills.getSkill(skillId).getBonus();
        return bonusTemp;
    }


    public Integer getAbilityScore(String abiId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int abiScore = 0;
        if (allAbilities.getAbi(abiId) != null) {
            abiScore = allAbilities.getAbi(abiId).getValue();

            if (abiId.equalsIgnoreCase("ability_equipment")) {
                abiScore= inventory.getAllItemsCount();
            }

            if (abiId.equalsIgnoreCase("ability_rm")) {
                int bonusRm = tools.toInt(settings.getString("bonus_temp_rm", String.valueOf(mC.getResources().getInteger(R.integer.bonus_temp_rm_def))));
                if (bonusRm>abiScore) { abiScore = bonusRm; }
            }

            if (abiId.equalsIgnoreCase("ability_init")) {
                int currentTier = tools.toInt(settings.getString("mythic_tier", String.valueOf(mC.getResources().getInteger(R.integer.mythic_tier_def))));
                abiScore += currentTier;
            }

            if (abiId.equalsIgnoreCase("ability_ca")) {
                abiScore += tools.toInt(settings.getString("bonus_temp_ca",String.valueOf(mC.getResources().getInteger(R.integer.bonus_temp_ca_def))));

            }

            if (abiId.equalsIgnoreCase("ability_ref")||abiId.equalsIgnoreCase("ability_vig")||abiId.equalsIgnoreCase("ability_vol")) {
                abiScore += tools.toInt(settings.getString("bonus_temp_save",String.valueOf(mC.getResources().getInteger(R.integer.bonus_temp_save_def))));
                abiScore += tools.toInt(settings.getString("epic_save",String.valueOf(mC.getResources().getInteger(R.integer.epic_save_def))));
                if (abiId.equalsIgnoreCase("ability_ref")){abiScore+=getAbilityMod("ability_dexterite");}
                if (abiId.equalsIgnoreCase("ability_vig")){abiScore+=getAbilityMod("ability_constitution");}
                if (abiId.equalsIgnoreCase("ability_vol")){abiScore+=getAbilityMod("ability_sagesse");}

                if (settings.getBoolean("switch_perma_resi",mC.getResources().getBoolean(R.bool.switch_perma_resi_def))) {
                    abiScore+=1;
                }
            }
        }
        return abiScore;
    }

    public Integer getAbilityMod(String abiId) {
        int abiMod = 0;
        Ability abi = allAbilities.getAbi(abiId);
        if (abi != null && abi.getType().equalsIgnoreCase("base")) {
            int abiScore = getAbilityScore(abiId);

            float modFloat = (float) ((abiScore - 10.) / 2.0);
            if (modFloat >= 0) {
                abiMod = (int) modFloat;
            } else {
                abiMod = -1 * Math.round(Math.abs(modFloat));
            }
        }
        return abiMod;
    }

    public AllFeats getAllFeats() {
        return allFeats;
    }

    public boolean featIsActive(String featId) {
        Feat feat = allFeats.getFeat(featId);
        boolean active = feat.isActive();
        return active;
    }

    public void sleep() {
        refresh();
        allResources.sleepReset();
        allBuffs.spendSleepTime();
        resetTemp();
    }
    public void halfSleep(){
        refresh();
        allResources.halfSleepReset();
        allBuffs.spendSleepTime();
        resetTemp();
    }

    public AllBuffs getAllBuffs() {
        return allBuffs;
    }
}
