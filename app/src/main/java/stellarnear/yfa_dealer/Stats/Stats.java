package stellarnear.yfa_dealer.Stats;

import android.content.Context;

import stellarnear.yfa_dealer.Spells.SpellList;
import stellarnear.yfa_dealer.TinyDB;

public class Stats {
    private StatsList statsList = new StatsList();
    private TinyDB tinyDB;

    public Stats(Context mC){
        tinyDB = new TinyDB(mC);
        refreshStats();
    }

    private void saveLocalStats() { //sauvegarde dans local DB
        tinyDB.putStats("localSaveStats", statsList);
    }

    public void refreshStats(){ //Initialisation ou lecture depuis DB
        StatsList listDB = tinyDB.getStats("localSaveStats");
        if (listDB.size() == 0) {
            initStats();
            saveLocalStats();
        } else {
            statsList = listDB;
        }
    }

    private void initStats(){
        this.statsList =new StatsList();
    }

    public void storeStatsFromRolls(SpellList selectedSpells) {
        Stat stat = new Stat();
        stat.feedStat(selectedSpells);
        statsList.add(stat);
        saveLocalStats();
    }

    public StatsList getStatsList() {
        return statsList;
    }

    public void resetStats() {
        this.statsList =new StatsList();
        saveLocalStats();
    }

}
