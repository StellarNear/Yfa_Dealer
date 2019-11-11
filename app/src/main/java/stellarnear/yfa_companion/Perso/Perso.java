package stellarnear.yfa_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_companion.Calculation;
import stellarnear.yfa_companion.HallOfFame;
import stellarnear.yfa_companion.PostData;
import stellarnear.yfa_companion.PostDataElement;
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
        stats = new Stats(mC);
        hallOfFame=new HallOfFame(mC);
        allFeats = new AllFeats(mC);
        allCapacities = new AllCapacities(mC);
        allMythicFeats = new AllMythicFeats(mC);
        allMythicCapacities = new AllMythicCapacities(mC);
        allAbilities = new AllAbilities(mC);
        allResources = new AllResources(mC,allAbilities,allMythicCapacities);
        allSkills = new AllSkills(mC);
        allBuffs = new AllBuffs(mC);
    }

    public void refresh() {
        allAbilities.refreshAllAbilities();
        allFeats.refreshAllSwitch();
        allMythicFeats.refreshAllSwitch();
        allSkills.refreshAllVals();
        allResources.refresh();
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

    public AllBuffs getAllBuffs() {
        return allBuffs;
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
            if(spell.getRange().equalsIgnoreCase("personnelle")){
                Buff matchingBuff = allBuffs.getBuffByID(spell.getID());
                if(matchingBuff!=null){
                    if(spell.getMetaList().metaIdIsActive("meta_duration")){ matchingBuff.extendCast(mC,getCasterLevel());} else { matchingBuff.normalCast(mC,getCasterLevel());}
                }
            }
            new PostData(mC,new PostDataElement(spell));
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
        int val =tools.toInt(prefs.getString("base_atk",String.valueOf(mC.getResources().getInteger(R.integer.base_atk_def))));
        return val;
    }

    public int getBonusAtk() {
        int epic= tools.toInt(prefs.getString("epic_atk_bonus",String.valueOf(mC.getResources().getInteger(R.integer.epic_atk_bonus_def))));
        int bonus = tools.toInt(prefs.getString("bonus_atk",String.valueOf(mC.getResources().getInteger(R.integer.bonus_atk_def))));
        int bonusTemp= tools.toInt(prefs.getString("bonus_atk_temp",String.valueOf(0)));
        return epic+bonus+bonusTemp;
    }

    public void resetTemp() {
        List<String> allTempList = Arrays.asList("NLS_bonus","bonus_atk_temp","bonus_temp_ca","bonus_temp_save","bonus_temp_rm");
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

        if(inventory.getAllEquipments().testIfNameItemIsEquipped("Pierre porte bonheur")){
            bonusTemp+=1;
        }

        if(skillId.equalsIgnoreCase("skill_estimate")) {
            Equipment gant = inventory.getAllEquipments().getEquipmentsEquiped("hand_slot");
            if (gant != null && gant.getName().equalsIgnoreCase("Gant d'estimation")) {
                bonusTemp += 3;
            }
        }

        if(skillId.equalsIgnoreCase("skill_stealth")) {
            Equipment boot = inventory.getAllEquipments().getEquipmentsEquiped("equipment_feet");
            if (boot != null && boot.getName().equalsIgnoreCase("Bottes elfiques")) {
                bonusTemp += 5;
            }
        }

        return bonusTemp;
    }


    public Integer getAbilityScore(String abiId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int abiScore = 0;
        if (allAbilities.getAbi(abiId) != null) {
            abiScore = allAbilities.getAbi(abiId).getValue();
            if (abiId.equalsIgnoreCase("ability_ca")) {
                abiScore += tools.toInt(settings.getString("bonus_temp_ca",String.valueOf(0)));
                if(allBuffs.buffByIDIsActive("shield")){
                    abiScore +=4;
                }
                if(allBuffs.buffByIDIsActive("premonition")){
                    abiScore +=2;
                }
            }

            if (abiId.equalsIgnoreCase("ability_equipment")) {
                abiScore= inventory.getAllItemsCount();
            }

            if (abiId.equalsIgnoreCase("ability_rm")) {
                int bonusRm = tools.toInt(settings.getString("bonus_temp_rm", String.valueOf(0)));
                if (bonusRm>abiScore) { abiScore = bonusRm; }
            }

            if (abiId.equalsIgnoreCase("ability_init")) {
                if(getAllMythicCapacities().getMythiccapacity("mythiccapacity_init").isActive()) {
                    int currentTier = tools.toInt(settings.getString("mythic_tier", String.valueOf(mC.getResources().getInteger(R.integer.mythic_tier_def))));
                    if(getAllMythicFeats().getMythicFeat("mythicfeat_parangon").isActive()){
                        currentTier+=2;
                    }
                    abiScore += currentTier;
                }
            }

            if (abiId.equalsIgnoreCase("ability_ref")||abiId.equalsIgnoreCase("ability_vig")||abiId.equalsIgnoreCase("ability_vol")) {
                abiScore += tools.toInt(settings.getString("bonus_temp_save",String.valueOf(0)));
                abiScore += tools.toInt(settings.getString("epic_save",String.valueOf(mC.getResources().getInteger(R.integer.epic_save_def))));

                if (settings.getBoolean("ioun_stone_luck",true)) {
                    abiScore+=1;
                }
                Equipment torse= inventory.getAllEquipments().getEquipmentsEquiped("armor_slot") ;
                if (torse!=null && torse.getName().equalsIgnoreCase("Robe d'archimage grise")) {
                    abiScore+=4;
                } else {
                    if(allBuffs.buffByIDIsActive("resistance")){
                        abiScore +=1;
                    }
                }

                if (abiId.equalsIgnoreCase("ability_ref")){
                    abiScore+=getAbilityMod("ability_dexterite");
                    if(allBuffs.buffByIDIsActive("premonition")){
                        abiScore +=2;
                    }
                    if(featIsActive("feat_inhuman_reflexes")){
                        abiScore+=2;
                    }
                }
                if (abiId.equalsIgnoreCase("ability_vig")){
                    abiScore+=getAbilityMod("ability_constitution");

                }
                if (abiId.equalsIgnoreCase("ability_vol")){
                    abiScore+=getAbilityMod("ability_sagesse");
                    if(allCapacities.capacityExist("capacity_dual_spirit")){
                        abiScore+=2;
                    }
                }
            }
            if(abiId.equalsIgnoreCase("ability_bmo")||abiId.equalsIgnoreCase("ability_dmd")){
                abiScore+=getBaseAtk()+getBonusAtk();
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

    public void reset() {
        resetTemp();
        refresh();
        allResources.sleepReset();
        inventory.resetInventory();
        allBuffs.resetBuffsList();
    }
}
