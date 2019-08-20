package stellarnear.yfa_companion.Perso;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_companion.Spells.BuildSpellList;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Spells.SpellList;
import stellarnear.yfa_companion.TinyDB;

public class AllBuffs {

    private ArrayList<Buff> listBuffs = null;

    private TinyDB tinyDB;
    private Context mC;

    public AllBuffs(Context mC){
        this.mC=mC;
        refreshListBuffs();
    }

    private void refreshListBuffs() {
        listBuffs = new ArrayList<>();
        tinyDB = new TinyDB(mC);
        ArrayList<Buff> listDB = tinyDB.getListBuffs("localSaveListBuffs");
        if (listDB.size() == 0) {
            buildList();
            saveLocalBuffs();
        } else {
            listBuffs = listDB;
        }
    }

    private void saveLocalBuffs() {
        tinyDB.putListBuffs("localSaveListBuffs", listBuffs);
    }

    public void resetBuffsList() {
        refreshListBuffs();
    }


    private void buildList(){  // on construit la liste qu'une fois dans MainActivityFragmentSpell donc pas besoin de singleton
        SpellList spellList = BuildSpellList.getInstance(mC).getSpellList();
        /* peau de pierre, lien télépathique, renvoi des sorts, moment de prescience, liberté de mouvement

Permanence: Détection de la magie, de l'invisibilité, dons des langues, lecture de la magie, vision dans le noir, vision magique, vision des auras, flou, écholocalisation, prémonition, esprit impénétrable, bouclier, résistance, vision lucide*/
        List<String> allBuffSpellsIds= Arrays.asList("Peau de pierre","Lien télépathique","Renvoi des sorts","Moment de préscience","Liberté de mouvement","Simulacre de vie supérieur");
        List<String> allBuffPermaSpellsIds= Arrays.asList("Détection de la magie","Détection de l'invisibilité","Don des langues","Lecture de la magie","Vision dans le noir","Vision magique", "Vision des auras","Flou","Echolocalisation","Prémonition","Esprit impénétrable","Bouclier","Résistance","Vision lucide");
        //todo add permabuffonlyspell  "Vision dans le noir","Vision magique", "Vision des auras","Vision lucide"

        for (Spell spell : spellList.asList()){
            if(allBuffSpellsIds.contains(spell.getID())){
                listBuffs.add(new Buff(spell,false));
            } else if (allBuffPermaSpellsIds.contains(spell.getID())){
                listBuffs.add(new Buff(spell,true));
            }
        }
    }

    public void spendSleepTime() {
        makeTimePass(60*8);
    }

    public ArrayList<Buff> getAllPermaBuffs() {
        ArrayList permaBuffs=new ArrayList();
        for (Buff buff : listBuffs){
            if(buff.isPerma()){
                permaBuffs.add(buff);
            }
        }
        return permaBuffs;
    }
    public ArrayList<Buff> getAllTempBuffs() {
        ArrayList tempBuffs=new ArrayList();
        for (Buff buff : listBuffs){
            if(!buff.isPerma()){
                tempBuffs.add(buff);
            }
        }
        return tempBuffs;
    }

    public void makeTimePass(int i) {
        for(Buff buff: listBuffs){
            buff.spendTime(i);
        }
        saveLocalBuffs();
    }

    public void saveBuffs() {
        saveLocalBuffs();
    }

    public boolean buffIsActive(String id) {
        Buff buffAnswer=null;
        for (Buff buff : listBuffs){
            if(buff.getName().equalsIgnoreCase(id)){
                buffAnswer=buff;
                break;
            }
        }
        return buffAnswer!=null && buffAnswer.isActive();
    }
}
