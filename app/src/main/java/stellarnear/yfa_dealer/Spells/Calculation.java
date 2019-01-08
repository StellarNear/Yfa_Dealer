package stellarnear.yfa_dealer.Spells;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Tools;

public class Calculation {
    private Perso yfa=MainActivity.yfa;
    private Tools tools=new Tools();

    public Calculation(){  }

    public int saveVal(Spell spell, boolean... withoutConversion){
        Boolean noConv = withoutConversion.length > 0 ? withoutConversion[0] : false;
        int val=10;
        val+=yfa.getCharismaMod();
        val+=spell.getRank();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }

        if(!noConv){
            if(spell.getConversion().getArcaneId().equalsIgnoreCase("save")){
                val+=(int) (spell.getConversion().getRank()/2.0);
            }
            if(spell.getConversion().getArcaneId().equalsIgnoreCase("dispel")){
                val+=(int) (spell.getConversion().getRank()*2.0);
            }
        }

        return val;
    }


    public int casterLevel(Spell spell,boolean... withoutConversion){
        Boolean noConv = withoutConversion.length > 0 ? withoutConversion[0] : false;
        int val=yfa.getCasterLevel();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }
        if(!noConv){
            if(spell.getConversion().getArcaneId().equalsIgnoreCase("caster_level")){
                val+=spell.getConversion().getRank();
            }
        }
        return val;
    }


    public int casterLevelSR(Spell spell,boolean... withoutConversion){
        Boolean noConv = withoutConversion.length > 0 ? withoutConversion[0] : false;
        int val=yfa.getCasterLevel();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }
        if(!noConv) {
            if (spell.getConversion().getArcaneId().equalsIgnoreCase("caster_level")) {
                val += spell.getConversion().getRank();
            }
            if (spell.getConversion().getArcaneId().equalsIgnoreCase("spell_resistance")) {
                val += spell.getConversion().getRank() * 2;
            }
        }

        return val;
    }

    public int nDice(Spell spell){
        int val=0;
        if(spell.getN_dice_per_lvl()==0.0){
            val = spell.getCap_dice();
        }else {
            if ((casterLevel(spell) * spell.getN_dice_per_lvl() > spell.getCap_dice()) && (spell.getCap_dice() != 0)) {
                val = spell.getCap_dice();
            } else {
                val = (int) (casterLevel(spell) * spell.getN_dice_per_lvl());
            }
        }
        return val;
    }

    public String damageTxt(Spell spell) {
        int nDice=nDice(spell);
        String dmg="";
        if(nDice!=0){
            dmg=nDice+"d"+spell.getDice_type();
        }

        if(spell.getDice_type().contains("lvl")){
            Integer dmg_int = nDice;
            dmg = String.valueOf(dmg_int);
        } else  if(spell.getMetaList().metaIdIsActive("meta_max")){
            Integer integer_dice = tools.toInt(spell.getDice_type());
            Integer dmg_int =nDice * integer_dice;
            dmg = String.valueOf(dmg_int);
        }else  if(spell.getMetaList().metaIdIsActive("meta_perfect")){
            Integer integer_dice = tools.toInt(spell.getDice_type());
            Integer dmg_int =nDice * integer_dice*2;
            dmg = String.valueOf(dmg_int);
        }
        return dmg;
    }

    public String rangeTxt(Spell spell) {
        String range=spell.getRange();
        List<String> rangesLvl = Arrays.asList("contact", "courte", "moyenne", "longue" );

        int indexRange = rangesLvl.indexOf(range);
        if(spell.getMetaList().metaIdIsActive("meta_range") && rangesLvl.contains(range) && !range.equalsIgnoreCase("longue")){
            indexRange+=1;
        }
        if (indexRange>=0) {
            Integer lvl = casterLevel(spell);
            Double dist_doubl =0.0;
            switch(rangesLvl.get(indexRange)) {
                case ("courte"):
                    dist_doubl=7.5+1.5*(lvl/2.0);
                    break;
                case ("moyenne"):
                    dist_doubl=30.0+3.0*lvl;
                    break;
                case ("longue"):
                    dist_doubl=120.0+lvl*12.0;
                    break;
            }
            range= String.valueOf(dist_doubl)+"m";
        }
        return range;
    }

    public String compoTxt(Spell spell) {
        String val="";
        if(spell.getCompoBool()[0]){val+="V,";}
        if(spell.getCompoBool()[1]){val+="G,";}
        if(spell.getCompoBool()[2]){val+="M,";}
        if (val.endsWith(",")){val=val.substring(0, val.length() - 1);}
        return val;
    }

    public String durationTxt(Spell spell) {
        String dura=spell.getDuration();
        Integer result= tools.toInt(dura.replaceAll("[^0-9?!]",""));
        String duration_unit = dura.replaceAll("[0-9?!]","");
        if(dura.contains("/lvl")){
            Integer lvl =  casterLevel(spell);
            result = result * lvl;
            duration_unit = dura.replaceAll("/lvl","").replaceAll("[0-9?!]","");
        }
        if(spell.getMetaList().metaIdIsActive("meta_duration")){ result=result*2; }
        dura = result+duration_unit;
        return dura;
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
                switch (spell.getCast_time()) {
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
                switch (spell.getCast_time()) {
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

}
