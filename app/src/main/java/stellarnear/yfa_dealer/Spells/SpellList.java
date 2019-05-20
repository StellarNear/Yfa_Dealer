package stellarnear.yfa_dealer.Spells;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SpellList extends AppCompatActivity {

    public List<Spell> listSpells ;
    public SpellList(){
        listSpells = new ArrayList<Spell>();    }


    public  void add(Spell spell){
        listSpells.add(spell);
    }

    public  void add(SpellList spells){
        for (Spell spell : spells.asList()){
            listSpells.add(spell);
        }
    }

    public SpellList filterByRank(int rank){
        SpellList sel_list = new SpellList();

        for(Spell spell : listSpells){
            if (spell.getRank() == rank) {
                sel_list.add(spell);
            }
        }
        return sel_list;
    }

    public SpellList filterDisplayable(){
        SpellList sel_list = new SpellList();

        for(Spell spell : listSpells){
            if (!spell.getID().contains("_sub")) {
                sel_list.add(spell);
            }
        }
        return sel_list;
    }

    public SpellList getSpellByID(String name){
        SpellList spellsAnswer = new SpellList();
        for (Spell spell : listSpells){
            if (spell.getID().equalsIgnoreCase(name)){
                spellsAnswer.add(new Spell(spell));  //pour renvoyer des copies de sort individuelles
            }
        }
        return spellsAnswer;
    }

    public int size() {
        return listSpells.size();
    }

    public List<Spell> asList() {
        return listSpells;
    }


    public boolean haveBindedSpells() {
        Boolean bool = false;
        for (Spell spell : listSpells){
            if (spell.isBinded()){
                bool=true;
            }
        }
        return bool;
    }

    public boolean isEmpty(){
        return listSpells.isEmpty();
    }

    public Iterator iterator(){
        return listSpells.iterator();
    }

    public void setSubName(int i) {
        for (Spell spell : listSpells){
            spell.setSubName(i);
        }
    }

    public void bindTo(Spell previousSpellToBind) {
        for (Spell spell : listSpells){
            spell.bindTo(previousSpellToBind);
        }
    }

    public boolean contains(Spell spell) {
        return listSpells.contains(spell);
    }

    public void remove(Spell spell) {
        listSpells.remove(spell);
    }

    public boolean hasSpellID(String name) {
        boolean isPresent = false;
        for (Spell spell : listSpells){
            if (spell.getID().equalsIgnoreCase(name)){isPresent=true;}
        }
        return isPresent;
    }

    public SpellList getUnbindedSpells() {
        SpellList spellList = new SpellList();
        for (Spell spell : listSpells){
            if(!spell.isBinded()){
                spellList.add(spell);
            } else {
                if (!spellList.contains(spell.getBindedParent())){
                    spellList.add(spell.getBindedParent());
                }
            }
        }
        return spellList;
    }
}
