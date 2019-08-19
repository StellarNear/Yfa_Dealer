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

    public ArrayList<Buff> tempBuffs = null;
    public ArrayList<Buff> permaBuffs = null;

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
        tempBuffs=new ArrayList<Buff>();
        permaBuffs =new ArrayList<Buff>();
        SpellList spellList = BuildSpellList.getInstance(mC).getSpellList();
        /* peau de pierre, lien télépathique, renvoi des sorts, moment de prescience, liberté de mouvement

Permanence: Détection de la magie, de l'invisibilité, dons des langues, lecture de la magie, vision dans le noir, vision magique, vision des auras, flou, écholocalisation, prémonition, esprit impénétrable, bouclier, résistance, vision lucide*/
        List<String> allBuffSpellsIds= Arrays.asList("Peau de pierre","Lien télépathique","Renvoi des sorts","Moment de préscience","Liberté de mouvement","Simulacre de vie supérieur");
        List<String> allBuffPermaSpellsIds= Arrays.asList("Détection de la magie","Détection de l'invisibilité","Don des langues","Lecture de la magie","Vision dans le noir","Vision magique", "Vision des auras","Flou","Echolocalisation","Prémonition","Esprit impénétrable","Bouclier","Résistance","Vision lucide");
        //todo add permabuffonlyspell  "Vision dans le noir","Vision magique", "Vision des auras","Vision lucide"

        for (Spell spell : spellList.asList()){
            if(allBuffSpellsIds.contains(spell.getID())){
                tempBuffs.add(new Buff(spell,false));
            } else if (allBuffPermaSpellsIds.contains(spell.getID())){
                permaBuffs.add(new Buff(spell,true));
            }
        }
    }


    public ArrayList<Buff> getAllPermaBuffs() {
        return permaBuffs;
    }
    public ArrayList<Buff> getAllTempBuffs() {
        return tempBuffs;
    }
}
