package stellarnear.yfa_companion.Perso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import stellarnear.yfa_companion.PostData;
import stellarnear.yfa_companion.PostDataElement;
import stellarnear.yfa_companion.Spells.BuildSpellList;
import stellarnear.yfa_companion.Spells.EchoList;
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
    private String tempFeat="";
    private boolean fromSpell=false;
    private boolean bloodLine=false;
    private OnAddFeatEventListener mListener;

    public Buff(Spell spell, Boolean perma){
        this.name=spell.getName();
        this.descr=spell.getDescr();
        this.id=spell.getID();
        this.spellDuration =spell.getDuration();
        this.spellRank=spell.getRank();
        this.perma=perma;
        this.fromSpell=true;
    }

    public Buff(Capacity capacity, Boolean perma){
        this.name=capacity.getName();
        this.descr=capacity.getDescr();
        this.id=capacity.getId();
        this.spellDuration =capacity.getDuration();
        this.perma=perma;
        this.bloodLine=capacity.isFromBloodLine();
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

    public void extendCast(Context mC,int casterLvl, int nCastDuration){
        this.maxDuration=calculateSeconds(casterLvl)*(1+nCastDuration); //in sec
        this.currentDuration=maxDuration;
        postData(mC);
        testForAddFeat(mC);
    }

    public void normalCast(Context mC, int casterLvl) {
        this.maxDuration=calculateSeconds(casterLvl); //in sec
        this.currentDuration=maxDuration;
        postData(mC);
        testForAddFeat(mC);
    }

    /* partie paragon soudain */

    private void testForAddFeat(final Context mC) {
        if(this.id.equalsIgnoreCase("parangon_tempfeat")){
            AlertDialog.Builder builder = new AlertDialog.Builder(mC);
            builder.setTitle("Choix du dons temporaire");
            // add a radio button list

            final ArrayList<String> featsNames=new ArrayList<>();

            final LinkedHashMap<String,String> mapFeatID=new LinkedHashMap<>();
            mapFeatID.put("Echo magique","tempfeat_magic_echo"); //on ajoute les dons temps à la main
            mapFeatID.put("Futur dons ;)","tempfeat_dummy"); //on ajoute les dons temps à la main

            for(Map.Entry<String,String> entry : mapFeatID.entrySet()){
                featsNames.add(entry.getKey());
            }

            int checkedItem = -1;
            String[] featsArray = featsNames.toArray(new String[featsNames.size()]);
            builder.setSingleChoiceItems(featsArray, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {
                    tempFeat =mapFeatID.get(featsNames.get(which));
                    BuildSpellList.resetMetas();
                    if(mListener!=null){mListener.onEvent();}
                }
            });
            builder.setPositiveButton("Ok", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public String getTempFeat() {
        return tempFeat;
    }

    public boolean isFromSpell() {
        return fromSpell;
    }

    public boolean isFromBloodLine() {
        return bloodLine;
    }

    public interface OnAddFeatEventListener {
        void onEvent();
    }

    public void setAddFeatEventListener(OnAddFeatEventListener eventListener) {
        mListener = eventListener;
    }


    /* partie communes */


    private void postData(Context mC) {
        new PostData(mC,new PostDataElement("Lancement du buff "+name,"Durée:"+getDurationText()));
    }

    public void spendTime(Context mC,int i) {
        currentDuration-=1f*i;
        if(currentDuration<=0){
            currentDuration=0;
            new PostData(mC,new PostDataElement("Expiration d'un buff",name+" a expiré"));
        }
        if(this.id.equalsIgnoreCase("parangon_tempfeat") && currentDuration==0){
            EchoList.resetEcho();
            BuildSpellList.resetMetas();
        }
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
        Tools tools = Tools.getTools();
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
