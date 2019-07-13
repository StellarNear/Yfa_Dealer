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
        float result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getDmgSum();
        }
        return listElements.size()>0?Math.round(result/listElements.size()):0;
    }

    public int getDmgSum() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getDmgSum();
        }
        return result;
    }

    public int getMinDmg() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            if(result==0 && element.getDmgSum()!=0){result=element.getDmgSum();}
            if(result>element.getDmgSum()){result=element.getDmgSum();}
        }
        return result;
    }

    public int getMaxDmg() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            if(result<element.getDmgSum()){result=element.getDmgSum();}
        }
        return result;
    }

    public int getRankMoy() {
        float result=0;
        for(DamagesShortListElement element : listElements){
            result+=element.getRank();
        }
        return listElements.size()>0?Math.round(result/listElements.size()):0;
    }

    public int getNDamageSpell() {
        int result=0;
        for(DamagesShortListElement element : listElements){
            if(element.getDmgSum()>0) {
                result++;
            }
        }
        return result;
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
