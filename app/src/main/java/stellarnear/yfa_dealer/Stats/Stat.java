package stellarnear.yfa_dealer.Stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import stellarnear.yfa_dealer.Calculation;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;

public class Stat {
    private DamagesShortList damagesShortList;
    private List<Integer> rankList =new ArrayList<>();
    private List<Integer> rankMetaList =new ArrayList<>();
    private List<Integer> rankHitList =new ArrayList<>();
    private List<Integer> rankAllMiss =new ArrayList<>();
    private List<Integer> rankContactMiss =new ArrayList<>();
    private List<Integer> rankCrit =new ArrayList<>();
    private List<Integer> rankGlaeBoost =new ArrayList<>();
    private List<Integer> rankGlaeFail =new ArrayList<>();
    private List<Integer> rankResist =new ArrayList<>();
    private Date date=null;
    private UUID uuid;

    public Stat(){  }

    public void feedStat(SpellList spells){
        damagesShortList =new DamagesShortList(spells);
        Calculation calculation =new Calculation();
        for (Spell spell :spells.asList()){
            int currentRank = calculation.currentRank(spell);
            rankList.add(currentRank);
            rankMetaList.add(currentRank-spell.getRank());
            if(spell.contactFailed()){
                rankAllMiss.add(currentRank);
                rankContactMiss.add(currentRank);
            }
            if(spell.isCrit()){
                rankCrit.add(currentRank);
            }
            if(spell.getGlaeManager().isBoosted()){
                rankGlaeBoost.add(currentRank);
            }
            if(spell.getGlaeManager().isFailed()){
                rankAllMiss.add(currentRank);
                rankGlaeFail.add(currentRank);
            }
            if(spell.isFailed()){
                rankAllMiss.add(currentRank);
                rankResist.add(currentRank);
            }
            if(!spell.isFailed() && !spell.getGlaeManager().isFailed() && !spell.contactFailed()){
                rankHitList.add(currentRank);
            }
        }
        this.date=new Date();
        this.uuid=UUID.randomUUID();
    }

    public int getRankMoyDmg(int rank) {
        return damagesShortList.filterByRank(rank).getDmgMoy();
    }

    public int getRankElemMoyDmg(int rank, String elem) {
       return damagesShortList.filterByRank(rank).filterByElem(elem).getDmgMoy();
    }


    public int getMetaElemMoyDmg(int iMeta, String elem) {
        return damagesShortList.filterByNMeta(iMeta).filterByElem(elem).getDmgMoy();
    }

    public int getMetaMoyDmg(int iMeta) {
        return damagesShortList.filterByNMeta(iMeta).getDmgMoy();
    }

    public int getElemMoyDmg(String elem) {
        return damagesShortList.filterByElem(elem).getDmgMoy();
    }

    public int getElemRankMoy(String elem) {
        return damagesShortList.filterByElem(elem).getRankMoy();
    }

    public int getRankMoy() {
        return damagesShortList.getRankMoy();
    }

    public int getMoyDmg() {
        return damagesShortList.getDmgMoy();
    }

    public int getSumDmgElem(String elem) {
        return damagesShortList.filterByElem(elem).getDmgSum();
    }

    public int getSumDmg() {
        return damagesShortList.getDmgSum();
    }


    public int getMinDmg() {
        return damagesShortList.getMinDmg();
    }

    public int getMaxDmg() {
        return damagesShortList.getMaxDmg();
    }

    public int getMinDmgElem(String elem) {
        return damagesShortList.filterByElem(elem).getMinDmg();
    }

    public int getMaxDmgElem(String elem) {
        return damagesShortList.filterByElem(elem).getMaxDmg();
    }

    public Date getDate() {
        return date;
    }

    public int getNDamageSpell() {
        return damagesShortList.getNDamageSpell();
    }


    @Override
    public boolean equals(Object obj) {
        boolean returnValue=false;
        if (obj != null) {
            if(Stat.class.isAssignableFrom(obj.getClass())){
                Stat obStat=(Stat)obj;
                if((obStat.uuid!=null && this.uuid!=null) && (this.uuid.equals(obStat.uuid))){
                    returnValue=true;
                }
            }
        }
        return returnValue;
    }

    @Override
    public int hashCode() {
        return this.uuid != null ? this.uuid.hashCode() : 0;
    }

    public List<Integer> getListMiss() {
        return rankAllMiss;
    }

    public List<Integer> getListHit() {
        return rankHitList;
    }

    public List<Integer> getListCrit() {
        return rankCrit;
    }

    public  List<Integer> getListGlaeBoost() {
        return rankGlaeBoost;
    }

    public  List<Integer> getListContactMiss() {
        return rankContactMiss;
    }

    public  List<Integer> getListGlaeFail() {
        return rankGlaeFail;
    }

    public  List<Integer> getListResist() {
        return rankResist;
    }

    public List<Integer> getListRank() {
        return rankList;
    }

    public List<Integer> getListMetaUprank() {
        return rankMetaList;
    }

}
