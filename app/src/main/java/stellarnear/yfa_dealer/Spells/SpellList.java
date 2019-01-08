package stellarnear.yfa_dealer.Spells;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class SpellList extends AppCompatActivity {

    public List<Spell> listSpells ;
    public SpellList(){
        listSpells = new ArrayList<Spell>();    }


    public  void add(Spell spell){
        listSpells.add(spell);
    }

    public void addAll(List<Spell> spells) {
        listSpells.addAll(spells);
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

    public Spell getSpellByID(String name){
        Spell spellAnswer = null;
        for (Spell spell : listSpells){
            if (spell.getID().equalsIgnoreCase(name)){
                spellAnswer=spell;
            }
        }
        return spellAnswer;
    }

    public int size() {
        return listSpells.size();
    }

    public List<Spell> asList() {
        return listSpells;
    }


}
