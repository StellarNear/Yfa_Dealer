package stellarnear.yfa_dealer.Stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatsList {
    private List<Stat> listStats = new ArrayList<>();

    public StatsList(){
        this.listStats=new ArrayList<Stat>();
    }

    public StatsList(List<Stat> listStats){
        this.listStats=listStats;
    }

    public void add(Stat value) {
        listStats.add(value);
    }

    public int size() {
        return listStats.size();
    }

    public List<Stat> asList() {
        return listStats;
    }

    public int getNSpellHit() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListHit().size();
        }
        return tot;
    }

    public int getNSpellMiss() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListMiss().size();
        }
        return tot;
    }

    public int getNSpellHitForRank(int rankSelectedForPieChart) {
        int tot=0;
        for (Stat stat : listStats){
            for (int rank : stat.getListHit()){
                if(rank==rankSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNSpellMissForRank(int rankSelectedForPieChart) {
        int tot=0;
        for (Stat stat : listStats){
            for (int rank : stat.getListMiss()){
                if(rank==rankSelectedForPieChart){
                    tot++;
                }
            }
        }
        return tot;
    }


    public int getNCrit() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListCrit().size();
        }
        return tot;
    }

    public Stat getLastStat() {
        Stat res=null;
        if (listStats.size()>0){
            res=listStats.get(listStats.size()-1);
        }
        return res;
    }

    public boolean contains(Stat lastStat) {
        return listStats.contains(lastStat);
    }


    public int getNGlaeBoost() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListGlaeBoost().size();
        }
        return tot;
    }

    public int getNContactMiss() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListContactMiss().size();
        }
        return tot;
    }

    public int getNGlaeFail() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListGlaeFail().size();
        }
        return tot;
    }

    public int getNResist() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListResist().size();
        }
        return tot;
    }

    public int getNGlaeBoostForRank(int selectedRank) {
        int tot=0;
        for (Stat stat : listStats){
            for (int rank : stat.getListGlaeBoost()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNContactMissForRank(int selectedRank) {
        int tot=0;
        for (Stat stat : listStats){
            for (int rank : stat.getListContactMiss()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNGlaeFailForRank(int selectedRank) {
        int tot=0;
        for (Stat stat : listStats){
            for (int rank : stat.getListGlaeFail()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNResistForRank(int selectedRank) {
        int tot=0;
        for (Stat stat : listStats){
            for (int rank : stat.getListResist()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNCritForRank(int selectedRank) {
        int tot=0;
        for (Stat stat : listStats){
            for (int rank : stat.getListCrit()){
                if(rank==selectedRank){
                    tot++;
                }
            }
        }
        return tot;
    }

    public int getNSpell() {
        int tot=0;
        for (Stat stat : listStats){
            tot+=stat.getListRank().size();

        }
        return tot;
    }

    public StatsList filterByDate(String dateFormated){
        StatsList res = new StatsList();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy", Locale.FRANCE);
        for (Stat stat : listStats){
            if( formater.format(stat.getDate()).equalsIgnoreCase(dateFormated)){
                res.add(stat);
            }
        }
        return res;
    }
}
