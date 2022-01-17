package stellarnear.yfa_companion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.yfa_companion.Log.SelfCustomLog;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Spells.SpellList;

public class Targets extends SelfCustomLog {
    private static Targets instance = new Targets();
    private List<String> allTargets;
    private Map<String,SpellList> mapTargetListSpell;

    public static Targets getInstance() {
        return instance;
    }

    private Targets(){
        allTargets=new ArrayList<>();
        mapTargetListSpell = new HashMap<String, SpellList>();
    }

    public void addTarget(String target){
        if (!allTargets.contains(target)) {
            allTargets.add(target);
            mapTargetListSpell.put(target,new SpellList());
        }
    }

    public void addSpellToTarget(String target, Spell spell){
        try {
            for(String tar:allTargets){
                if (mapTargetListSpell.get(tar).contains(spell)){
                    mapTargetListSpell.get(tar).remove(spell);
                }
            }
            mapTargetListSpell.get(target).add(spell);
        } catch (Exception e) {
            log.err("Error during add spell to target",e);
        }
    }

    public SpellList getSpellListForTarget(String target){
        SpellList listReturn = new SpellList();
        try {
            listReturn=mapTargetListSpell.get(target);
        } catch (Exception e) {
            log.err("Error during get spell for target",e);
        }
        return listReturn;
    }

    public List<String> getTargetList(){
        return allTargets;
    }

    public void clearTargets() {
        allTargets=new ArrayList<>();
        mapTargetListSpell = new HashMap<String, SpellList>();
    }

    public SpellList getAllSpellList() {
        SpellList allSpell= new SpellList();
        for(String tar:allTargets){
            allSpell.add(mapTargetListSpell.get(tar));
        }
        return  allSpell;
    }

    public boolean anySpellAssigned() {
        boolean bool = false;
        for(String tar:allTargets){
            if(!mapTargetListSpell.get(tar).isEmpty()){
                bool=true;
                break;
            }
        }
        return bool;
    }

    public void assignAllToMain(String tar,SpellList selectedSpells) {
        if (!allTargets.contains(tar)) {
            allTargets.add(tar);
            mapTargetListSpell.put(tar,selectedSpells);
        }
    }
}
