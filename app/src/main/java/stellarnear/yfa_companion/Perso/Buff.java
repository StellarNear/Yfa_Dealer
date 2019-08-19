package stellarnear.yfa_companion.Perso;

import stellarnear.yfa_companion.Spells.Spell;


public class Buff {
    private Spell spell;
    private float currentDuration=0;
    private float maxDuration=0;
    private boolean perma=false;

    public Buff(Spell spell, Boolean perma){
        this.spell=spell;
        this.perma=perma;
    }


    public boolean isActive(){
        return perma || currentDuration>0;
    }

    public boolean isPerma() {
        return perma;
    }

    public String getSpellName() {
        return spell.getName();
    }
}
