package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Spells.Metamagic;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;

public class DisplayedText {
    private Perso yfa=MainActivity.yfa;
    private Tools tools=new Tools();
    private Calculation calculation=new Calculation();

    public DisplayedText(){  }

    public String damageTxt(Spell spell) {

        String dmg=calculation.nDice(spell)+"d"+calculation.diceType(spell);

        if(spell.getDice_type().contains("lvl")){
            Integer dmg_int = calculation.nDice(spell);
            dmg = String.valueOf(dmg_int);
        } else  if(spell.getMetaList().metaIdIsActive("meta_perfect")){
            Integer dmg_int =calculation.nDice(spell) * calculation.diceType(spell) *2;
            dmg = String.valueOf(dmg_int);
        }else  if(spell.getMetaList().metaIdIsActive("meta_max")) {
            Integer dmg_int = calculation.nDice(spell) * calculation.diceType(spell);
            dmg = String.valueOf(dmg_int);
        }
        return dmg;
    }

    public String rangeTxt(Spell spell) {
        Double rangeDouble=calculation.range(spell);
        String range ="";
        if(rangeDouble==0.0){
            range="contact";
        } else if (rangeDouble<0.0){ //si on a pas pu calculer c'est un truc fixe
            range = spell.getRange();
        } else {
            range=String.valueOf(rangeDouble)+"m";
        }
        return range;
    }

    public String compoTxt(Context mC,Spell spell) {
        List<String> valList=new ArrayList<>(spell.getCompoList());
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if(valList.contains("M") && settings.getBoolean("materiel_switch",mC.getResources().getBoolean(R.bool.materiel_switch_def)))  {
            valList.remove("M");
        }
        if(valList.contains("V")&&spell.getMetaList().metaIdIsActive("meta_silent")){
            valList.remove("V");
        }
        String txt ="";
        for (String str : valList){
            txt+=str+", ";
        }

        if(txt.length()>2) {
            txt = txt.substring(0, txt.length() - 2);
        }
        return txt;
    }

    public String durationTxt(Spell spell) {
        String dura=spell.getDuration();

        if(!dura.equalsIgnoreCase("permanente") && !dura.contains("d")){
            Integer result= tools.toInt(dura.replaceAll("[^0-9?!]",""));
            String duration_unit = dura.replaceAll("[0-9?!]","");
            if(dura.contains("/lvl")){
                Integer lvl =  calculation.casterLevel(spell);
                result = result * lvl;
                duration_unit = dura.replaceAll("/lvl","").replaceAll("[0-9?!]","");
            }
            if(spell.getMetaList().metaIdIsActive("meta_duration")){ result=result*2; }
            dura = result+duration_unit;
        }
        return dura;
    }


}
