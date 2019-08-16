package stellarnear.yfa_companion.Perso;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_companion.Spells.BuildSpellList;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Spells.SpellList;

public class AllBuffs {

    private static AllBuffs instance=null;

    public ArrayList<Buff> allBuffs = null;

    public static AllBuffs getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance==null){
            instance = new AllBuffs(mC);
        }
        return instance;
    }

    public static void resetBuffsList() {
        instance = null;
    }


    private AllBuffs(Context mC){  // on construit la liste qu'une fois dans MainActivityFragmentSpell donc pas besoin de singleton
        allBuffs=new ArrayList<Buff>();
        SpellList spellList = BuildSpellList.getInstance(mC).getSpellList();

        List<String> allBuffSpellsIds= Arrays.asList("Détection de l'invisibilité","Flou");
        List<String> allBuffPermaSpellsIds= Arrays.asList("Détection de la magie","Lecture de la magie");

        for (Spell spell : spellList.asList()){
            if(allBuffSpellsIds.contains(spell.getID())){
                allBuffs.add(new Buff(spell,false));
            } else if (allBuffPermaSpellsIds.contains(spell.getID())){
                allBuffs.add(new Buff(spell,true));
            }
        }
    }

    public ArrayList<Buff> getAllBuffs() {
        return allBuffs;
    }
}
