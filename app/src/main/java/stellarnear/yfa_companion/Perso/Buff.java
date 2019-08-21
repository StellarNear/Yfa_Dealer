package stellarnear.yfa_companion.Perso;

import java.text.DecimalFormatSymbols;

import stellarnear.yfa_companion.Spells.Spell;


public class Buff {
    private float currentDuration=0; //in minute
    private float maxDuration=0;
    private String spellDuration;
    private String name;
    private String id;
    private String descr;
    private int spellRank;
    private boolean perma;

    public Buff(Spell spell, Boolean perma){
        this.name=spell.getName();
        this.descr=spell.getDescr();
        this.id=spell.getID();
        this.spellDuration =spell.getDuration();
        this.spellRank=spell.getRank();
        this.perma=perma;
    }

    public String getDurationText(){
        String durationTxt ="";
        if(perma) {
            durationTxt= DecimalFormatSymbols.getInstance().getInfinity();
        }else  if(currentDuration>=60){
            int roundH=(int) currentDuration/60;
            durationTxt="["+roundH+"h]";
        } else if(currentDuration>0) {
            durationTxt="["+(int)currentDuration+"min]";
        }
        return durationTxt;
}

    public String getSpellDuration() {
        return spellDuration;
    }

    public boolean isActive(){
        return perma || currentDuration>0;
    }

    public boolean isPerma() {
        return perma;
    }

    public String getName() {
        return name;
    }

    public float getMaxDuration() {
        return maxDuration;
    }

    public float getCurrentDuration() {
        return currentDuration;
    }

    public void cancel() {
        this.currentDuration=0;
    }

    public void normalCast(float minutesDura) {
        this.maxDuration=minutesDura;
        this.currentDuration=maxDuration;
    }

    public void spendTime(int i) {
        currentDuration-=1f*i;
        if(currentDuration<0){currentDuration=0;}
    }

    public int getSpellRank() {
        return spellRank;
    }

    public String getDescr() {
        return descr;
    }

    public String getId() {
        return this.id;
    }
}
