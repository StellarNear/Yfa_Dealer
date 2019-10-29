package stellarnear.yfa_companion.Perso;

import android.content.Context;

import java.text.DecimalFormatSymbols;

import stellarnear.yfa_companion.PostData;
import stellarnear.yfa_companion.PostDataElement;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Tools;


public class Buff {
    private float currentDuration=0; //duration are in seconds
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
        }else  if(currentDuration>=3600){
            int roundH=(int) currentDuration/3600;
            durationTxt="["+roundH+"h]";
        } else if(currentDuration>=60) {
            int roundM=(int) currentDuration/60;
            durationTxt="["+roundM+"min]";
        } else if(currentDuration>0) {
            int roundS=(int) currentDuration;
            durationTxt="["+roundS+"sec]";
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

    public void extendCast(Context mC,int casterLvl){
        this.maxDuration=calculateSeconds(casterLvl)*2; //in sec
        this.currentDuration=maxDuration;
        postData(mC);
    }

    public void normalCast(Context mC, int casterLvl) {
        this.maxDuration=calculateSeconds(casterLvl); //in sec
        this.currentDuration=maxDuration;
        postData(mC);
    }

    private void postData(Context mC) {
        new PostData(mC,new PostDataElement("Lancement du buff "+name,"Durée:"+getDurationText()));
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


    private float calculateSeconds(int casterLvl) {
        String duration = this.spellDuration;
        Tools tools = new Tools();
        float floatSeconds=0f;
        if(!duration.equalsIgnoreCase("permanente")){
            Integer result= tools.toInt(duration.replaceAll("[^0-9?!]",""));
            String duration_unit = duration.replaceAll("[0-9?!]","");
            if(duration.contains("/lvl")){
                result = result * casterLvl;
                duration_unit = duration.replaceAll("/lvl","").replaceAll("[0-9?!]","");
            }

            if(duration_unit.equalsIgnoreCase("h")){
                floatSeconds=result*3600f;
            } else if(duration_unit.equalsIgnoreCase("min")){
                floatSeconds=result*60f;
            } else if(duration_unit.equalsIgnoreCase("round")){
                floatSeconds=result*6f;
            }
        }
        return floatSeconds;
    }
}
