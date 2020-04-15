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
import stellarnear.yfa_companion.RemoveDataElementAllSpellArrow;
import stellarnear.yfa_companion.Spells.EchoList;
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

    private Tools tools=Tools.getTools();
    private Context mC;
    private SharedPreferences prefs;

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
        allSkills = new AllSkills(mC);
        allAbilities = new AllAbilities(mC,inventory);
        computeCapacities(); // on a besoin de skill et abi pour les usages et valeur des capas
        allBuffs = new AllBuffs(mC,allCapacities);
        allResources = new AllResources(mC,allAbilities,allCapacities);
    }

    public void refresh() {
        allSkills.refreshAllVals();
        allAbilities.refreshAllAbilities(); //abi d'abord car resource depend de abi mais apres allskills car en depends
        computeCapacities(); //capa avant resource car certaine resource sont issue de capa apres abilities car on en a besoin
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

    // calculs

    private void computeCapacities() {
        for(Capacity cap : allCapacities.getAllCapacitiesList()){
            if(!cap.isInfinite()){
                calculDailyUsage(cap);
            }
            calculValue(cap);
        }
    }

    private void calculDailyUsage(Capacity cap) {
        if(!cap.getDailyUseString().equalsIgnoreCase("")){
            int dailyUse=0;
            if(tools.toInt(cap.getDailyUseString())==0){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
                int mainPJlvl=getAbilityScore("ability_lvl");
                switch (cap.getDailyUseString()){
                    case "ability_charisme":
                        dailyUse =getAbilityMod("ability_charisme");
                        break;
                    case "3+ability_charisme":
                        dailyUse =3+getAbilityMod("ability_charisme");
                        break;
                }
            } else {
                dailyUse=tools.toInt(cap.getDailyUseString());
            }
            cap.setDailyUse(dailyUse);
        }
    }

    private void calculValue(Capacity cap) {
        if(!cap.getValueString().equalsIgnoreCase("")) {
            int value=0;
            if (tools.toInt(cap.getValueString()) == 0) {
                int mainPJlvl = getAbilityScore("ability_lvl");
                switch (cap.getValueString()) {
                    case "(lvl-18)/2":
                        value = (int)(( mainPJlvl-18)/2);
                        break;
                    case "lvl/2":
                        value=(int) (mainPJlvl/2);
                        break;
                }
            } else {
                value = tools.toInt(cap.getValueString());
            }
            cap.setValue(value);
        }
    }

    //spells

    public void castConvSpell(Integer selected_rank) {
        allResources.castConvSpell(selected_rank);
    }
    public void castSpell(Integer selected_rank) {
        allResources.castSpell(selected_rank);
    }

    public void castSpell(Spell spell, Context contextCast) {
        if (!spell.isCast()){
            spell.cast();

            int currentRankSpell = new Calculation().currentRank(spell);
            if(currentRankSpell>0) {
                allResources.castSpell(currentRankSpell);
            }
            if(spell.getRange().equalsIgnoreCase("personnelle")){
                Buff matchingBuff = allBuffs.getBuffByID(spell.getID());
                if(matchingBuff!=null){
                    if(spell.getMetaList().metaIdIsActive("meta_duration")){
                        matchingBuff.extendCast(contextCast,getCasterLevel(),spell.getMetaList().getMetaByID("meta_duration").getnCast());
                    }
                    else {
                        matchingBuff.normalCast(contextCast,getCasterLevel());
                    }
                    if(matchingBuff.getName().equalsIgnoreCase("Parangon soudain")){
                        matchingBuff.setAddFeatEventListener(new Buff.OnAddFeatEventListener() {
                            @Override
                            public void onEvent() {
                                allBuffs.saveBuffs();
                            }
                        });
                    }
                }
            }
            new PostData(mC,new PostDataElement(spell));

            if(spell.getMetaList().metaIdIsActive("meta_echo")){
                EchoList.getInstance(mC).addEcho(spell);
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
        int val =tools.toInt(prefs.getString("base_atk",String.valueOf(mC.getResources().getInteger(R.integer.base_atk_def))));
        return val;
    }

    public int getBonusAtk() {
        int val=0;
        int epic= tools.toInt(prefs.getString("epic_atk_bonus",String.valueOf(mC.getResources().getInteger(R.integer.epic_atk_bonus_def))));
        int bonus = tools.toInt(prefs.getString("bonus_atk",String.valueOf(mC.getResources().getInteger(R.integer.bonus_atk_def))));
        int bonusTemp= tools.toInt(prefs.getString("bonus_atk_temp",String.valueOf(0)));
        val+=epic+bonus+bonusTemp;
        if(allBuffs!=null && allBuffs.getBuffByID("capacity_destiny_touch")!=null && allBuffs.getBuffByID("capacity_destiny_touch").isActive()){
            val+=allCapacities.getCapacity("capacity_destiny_touch").getValue();
        }
        return val;
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

    public Integer getSkillBonus(String skillId) {
        int bonusTemp = allSkills.getSkill(skillId).getBonus();
        bonusTemp+= inventory.getAllEquipments().getSkillBonus(skillId);

        if(inventory.getAllEquipments().testIfNameItemIsEquipped("Pierre porte bonheur")){
            bonusTemp+=1;
        }

        return bonusTemp;
    }


    public Integer getAbilityScore(String abiId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int abiScore = 0;
        if (allAbilities.getAbi(abiId) != null) {
            abiScore = allAbilities.getAbi(abiId).getValue();
            if (abiId.equalsIgnoreCase("ability_ca")) {
                abiScore=10;
                abiScore += tools.toInt(prefs.getString("bonus_temp_ca",String.valueOf(0)));
                int abiMod=0;
                if(allCapacities.capacityIsActive("capacity_revelation_prophetic_armor")){
                    abiMod = getAbilityMod("ability_charisme");
                } else {
                    abiMod = getAbilityMod("ability_dexterite");
                }
                if (inventory.getAllEquipments().hasArmorDexLimitation() && inventory.getAllEquipments().getArmorDexLimitation() < abiMod) {
                    abiScore += inventory.getAllEquipments().getArmorDexLimitation();
                } else {
                    abiScore += abiMod;
                }
                abiScore+=inventory.getAllEquipments().getArmorBonus();

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
                abiScore= getAbilityMod("ability_dexterite");
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

                    if(allBuffs.buffByIDIsActive("resistance")){
                        abiScore +=1;
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
                    if(getAllCapacities().capacityIsActive("capacity_dual_spirit")){
                        abiScore+=2;
                    }
                }
            }
            if(abiId.equalsIgnoreCase("ability_bmo")||abiId.equalsIgnoreCase("ability_dmd")){
                abiScore=getBaseAtk()+getBonusAtk()+getAbilityMod("ability_force");
                if(abiId.equalsIgnoreCase("ability_dmd")){
                    abiScore+=getAbilityMod("ability_dexterite");
                    abiScore+=10;
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
        return feat!=null && feat.isActive();
    }

    public void sleep() {
        EchoList.resetEcho();
        allResources.resetCurrent();
        commonSleep();
        new PostData(mC,new RemoveDataElementAllSpellArrow());
    }
    public void halfSleep(){
        allResources.halfSleepReset();
        commonSleep();
    }

    private void commonSleep() {
        resetTemp();
        allBuffs.spendSleepTime();
        if(allMythicCapacities!=null && allMythicCapacities.getMythiccapacity("mythiccapacity_recover").isActive()){
            allResources.getResource("resource_hp").fullHeal();
        }
        refresh();
    }

    public void reset() {
        EchoList.resetEcho();
        this.inventory.reset();
        this.allFeats.reset();
        this.allCapacities.reset();
        this.allMythicFeats.reset();
        this.allMythicCapacities.reset();
        this.allAbilities.reset();
        this.allResources.reset();
        this.allSkills.reset();
        this.stats.reset();
        this.hallOfFame.reset();
        this.allBuffs.reset();
        resetTemp();
        refresh();
        sleep();
    }

    public void loadFromSave() {
        inventory.loadFromSave();
        stats.loadFromSave();
        hallOfFame.loadFromSave();
        refresh();
    }
}
