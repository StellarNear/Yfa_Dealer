package stellarnear.yfa_dealer.Stats;

import java.util.ArrayList;
import java.util.List;

import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;

public class DamagesShortList {
    private List<DamagesShortListElement> listElements;

    public DamagesShortList(SpellList spells){
        buildList(spells);
    }

    public DamagesShortList(){
        listElements=new ArrayList<>();
    }


    private void add(DamagesShortListElement element) {
        listElements.add(element);
    }
    private void buildList(SpellList spells) {
        listElements=new ArrayList<>();
        for (Spell spell:spells.asList()){
            listElements.add(new DamagesShortListElement(spell));
        }
    }

    public int getDmgMoy(){
        int result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getDmgSum();
        }
        return listElements.size()>0?(int)(result/listElements.size()):0;
    }

    public DamagesShortList filterByElem(String elem){
        DamagesShortList result=new DamagesShortList();
        for(DamagesShortListElement element : listElements){
            if(element.getElement().equalsIgnoreCase(elem)){
                result.add(element);
            }
        }
        return result;
    }

    public DamagesShortList filterByRank(int rank){
        DamagesShortList result=new DamagesShortList();
        for(DamagesShortListElement element : listElements){
            if(element.getRank()==rank){
                result.add(element);
            }
        }
        return result;
    }


    public DamagesShortList filterByNMeta(int iMeta) {
        DamagesShortList result=new DamagesShortList();
        for(DamagesShortListElement element : listElements){
            if(element.getnMeta()==iMeta){
                result.add(element);
            }
        }
        return result;
    }
}
