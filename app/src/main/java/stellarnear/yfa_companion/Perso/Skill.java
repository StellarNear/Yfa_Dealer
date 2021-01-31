package stellarnear.yfa_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.yfa_companion.Tools;

/**
 * Created by jchatron on 10/01/2018.
 */

public class Skill extends SelfCustomLog {
    private String name;
    private String abilityDependence;
    private String descr;
    private String id;
    private int rank;
    private int bonus;
    private Context mC;
    private Tools tools = Tools.getTools();

    public Skill(String name, String abilityDependence, String descr, String id, Context mC) {
        try {
            this.name = name;
            this.abilityDependence = abilityDependence;
            this.descr = descr;
            this.id = id;
            this.mC = mC;
            refreshVals();
        } catch (Exception e) {
            log.err("Error on Skill init : " + this.name, e);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAbilityDependence() {
        return this.abilityDependence;
    }

    public void refreshVals() {
        refreshRank();
        refreshBonus();
    }

    private void refreshRank() {
        int valTemp = 0;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            int valDefId = mC.getResources().getIdentifier(this.id + "_rankDEF", "integer", mC.getPackageName());
            int valDef = mC.getResources().getInteger(valDefId);
            valTemp = tools.toInt(settings.getString(this.id + "_rank", String.valueOf(valDef)));
        } catch (Exception e) {
            log.warn("Could not find val def for " + this.id);
        }
        this.rank = valTemp;
    }

    public int getRank() {
        return this.rank;
    }

    private void refreshBonus() {
        int bonusTemp = 0;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            int bonusDefId = mC.getResources().getIdentifier(this.id + "_bonusDEF", "integer", mC.getPackageName());
            int bonusDef = mC.getResources().getInteger(bonusDefId);
            bonusTemp = tools.toInt(settings.getString(this.id + "_bonus", String.valueOf(bonusDef)));
        } catch (Exception e) {
            log.warn("Could not find bonus def for " + this.id);
        }
        this.bonus = bonusTemp;
    }

    public int getBonus() {
        return this.bonus;
    }
}