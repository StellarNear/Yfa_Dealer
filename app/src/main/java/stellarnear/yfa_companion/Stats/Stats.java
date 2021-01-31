package stellarnear.yfa_companion.Stats;

import android.content.Context;

import stellarnear.yfa_companion.Perso.SelfCustomLog;
import stellarnear.yfa_companion.Spells.SpellList;
import stellarnear.yfa_companion.TinyDB;

public class Stats extends SelfCustomLog {
    private StatsList statsList = new StatsList();
    private TinyDB tinyDB;

    public Stats(Context mC){
        tinyDB = new TinyDB(mC);
        try {
            refreshStats();
        } catch (Exception e) {
            log.err("Could not refreshStats",e);
            reset();
        }
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

    public void reset() {
        this.statsList =new StatsList();
        saveLocalStats();
    }

    public void loadFromSave() {
        refreshStats();
    }
}
