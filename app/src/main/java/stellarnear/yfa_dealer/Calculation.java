package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Spells.Metamagic;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;
import stellarnear.yfa_dealer.Tools;

public class Calculation {
    private Perso yfa=MainActivity.yfa;
    private Tools tools=new Tools();

    public Calculation(){  }

    public int saveVal(Spell spell){
        int val=10;
        val+=yfa.getCharismaMod();
        val+=spell.getRank();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }
        if(spell.getConversion().getArcaneId().equalsIgnoreCase("save")){
            val+=(int) (spell.getConversion().getRank()/2.0);
        }
        if(spell.getConversion().getArcaneId().equalsIgnoreCase("dispel")){
            val+=(int) (spell.getConversion().getRank()*2.0);
        }

        return val;
    }


    public int casterLevel(Spell spell){
        int val=yfa.getCasterLevel();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }

        if(spell.getConversion().getArcaneId().equalsIgnoreCase("caster_level")){
            val+=spell.getConversion().getRank();
        }

        return val;
    }


    public int casterLevelSR(Spell spell){
        int val=yfa.getCasterLevel();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }
        if (spell.getConversion().getArcaneId().equalsIgnoreCase("caster_level")) {
            val += spell.getConversion().getRank();
        }
        if (spell.getConversion().getArcaneId().equalsIgnoreCase("spell_resistance")) {
            val += spell.getConversion().getRank() * 2;
        }

        return val;
    }

    public int nDice(Spell spell){
        int val=0;
        if(spell.getN_dice_per_lvl()==0.0){
            val = spell.getCap_dice();
        }else {
            if ((casterLevel(spell) * spell.getN_dice_per_lvl() > spell.getCap_dice()) && (spell.getCap_dice() != 0)) {
                if(spell.getConversion().getArcaneId().equalsIgnoreCase("raise_cap")){
                    val = spell.getCap_dice() + 2*spell.getConversion().getRank();
                } else {
                    val = spell.getCap_dice();
                }
            } else {
                val = (int) (casterLevel(spell) * spell.getN_dice_per_lvl());
            }
        }
        if(spell.getMetaList().metaIdIsActive("meta_enhance") && tools.toInt(spell.getDice_type())==8){ //car on passe à 2d6
            val = val * 2;
        }
        if(spell.getMetaList().metaIdIsActive("meta_extend")){
            val = (int)(val * 1.5);
        }
        return val;
    }

    public int diceType(Spell spell){
        int val = tools.toInt(spell.getDice_type());
        if(spell.getMetaList().metaIdIsActive("meta_enhance")){
            if(val==4){
                val=6;
            } else if(val==6){
                val=8;
            } else if(val==8) {
                val = 6;
            }
        }
        return val;
    }

    public int currentRank(Spell spell){
        int val=spell.getRank();
        for(Metamagic meta : spell.getMetaList().filterActive().asList()){
            if(spell.getConversion().getArcaneId().contains("metamagic")){ //une méta peut etre gratuite venant de conversion arcanique
                if(!spell.getConversion().getArcaneId().contains(meta.getId())){
                    val += meta.getUprank();
                }
            } else {
                val += meta.getUprank()*meta.getnCast();
            }
        }
        return val;
    }

    public Double range(Spell spell) {
        String range=spell.getRange();
        List<String> rangesLvl = Arrays.asList("contact", "courte", "moyenne", "longue" );

        Double distDouble =-1.0;
        int indexRange = rangesLvl.indexOf(range);
        if(spell.getMetaList().metaIdIsActive("meta_range") && rangesLvl.contains(range) && !range.equalsIgnoreCase("longue")){
            indexRange+=1;
        }
        if (indexRange>=0) {
            Integer lvl = casterLevel(spell);
            switch(rangesLvl.get(indexRange)) {
                case ("contact"):
                    distDouble=0.0;
                    break;
                case ("courte"):
                    distDouble=7.5+1.5*(lvl/2.0);
                    break;
                case ("moyenne"):
                    distDouble=30.0+3.0*lvl;
                    break;
                case ("longue"):
                    distDouble=120.0+lvl*12.0;
                    break;
            }
        }
        return distDouble;
    }

    public int calcRounds(Context mC,SpellList spellList) {
        int sum_action = 0;
        int n_convert = 0;
        int n_complexe = 0;
        int n_simple = 0;
        int n_rapide = 0;
        int n_spells = 0;
        int n_round = 0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        Boolean multicast = settings.getBoolean("multispell_switch", mC.getResources().getBoolean(R.bool.multispell_switch_def));
        if (multicast) {
            for (Spell spell : spellList.asList()) {
                n_spells += 1;
                switch (getCastTimeTxt(spell)) {
                    case "complexe":
                        n_complexe += 1;
                        break;
                    case "simple":
                        sum_action += 4;
                        n_simple += 1;
                        break;
                    case "rapide":
                        sum_action += 1;
                        n_rapide += 1;
                        break;
                }
                if (!spell.getConversion().getArcaneId().equalsIgnoreCase("")) {
                    n_convert += 1;
                }
            }
            n_round = (int) (Math.ceil(sum_action / 6.0));
            n_round += n_complexe;
            if (n_convert > n_round) {
                n_round = n_convert;
            }
            if ((int) (Math.ceil(n_rapide / 3.0)) > n_round) {
                n_round = (int) (Math.ceil(n_rapide / 3.0));
            }
            if (n_simple > n_round) {
                n_round = n_simple;
            }
            if ((int) (Math.ceil(n_spells / 3.0)) > n_round) {
                n_round = (int) (Math.ceil(n_spells / 3.0));
            }
        } else {
            for (Spell spell : spellList.asList()) {
                n_spells += 1;
                switch (getCastTimeTxt(spell)) {
                    case "complexe":
                        n_complexe += 1;
                        break;
                    case "simple":
                        sum_action += 2;
                        n_simple += 1;
                        break;
                    case "rapide":
                        sum_action += 1;
                        n_rapide += 1;
                        break;
                }
                if (!spell.getConversion().getArcaneId().equalsIgnoreCase("")) {
                    n_convert += 1;
                }
            }
            n_round = (int) (Math.ceil(sum_action / 3.0));
            n_round += n_complexe;
            if (n_convert > n_round) {
                n_round = n_convert;
            }
            if ((int) (Math.ceil(n_rapide / 2.0)) > n_round) {
                n_round = (int) (Math.ceil(n_rapide / 2.0));
            }
            if (n_simple > n_round) {
                n_round = n_simple;
            }
            if ((int) (Math.ceil(n_spells / 2.0)) > n_round) {
                n_round = (int) (Math.ceil(n_spells / 2.0));
            }
        }
        return n_round;
    }
    public String getCastTimeTxt(Spell spell) {
        String val=spell.getCast_time();
        if(val.equalsIgnoreCase("simple")&&spell.getMetaList().metaIdIsActive("meta_quicken")){
            val="rapide";
        }
        return val;
    }

}
