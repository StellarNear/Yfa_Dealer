package stellarnear.yfa_dealer.Spells;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Tools;


public class Spell {

    private String  name;
    private String id;
    private String  descr;
    private String  dice_type;
    private Double  n_dice_per_lvl;
    private int     cap_dice;

    private String  dmg_type;
    private String  range;
    private String  cast_time;
    private String  duration;

    private Boolean[] compoBool=new Boolean[3]; //V,G,M

    private String rm;
    private String  save_type;

    private int     rank;
    private boolean perfect;
    private boolean converted;

    private int     n_sub_spell;

    private SharedPreferences settings;

    private MetaList metaList;

    //private Tools tools=new Tools();

    public Spell(Context mC,Spell spell){ //copying spell
        if(spell.id.equalsIgnoreCase("")){
            this.id=spell.name;
        } else {
            this.id=spell.id;
        }
        this.name=spell.name;
        this.descr=spell.descr;
        this.dice_type=spell.dice_type;
        this.n_dice_per_lvl=spell.n_dice_per_lvl;
        this.cap_dice=spell.cap_dice;
        this.dmg_type=spell.dmg_type;
        this.range=spell.range;
        this.cast_time=spell.cast_time;
        this.duration=spell.duration;
        this.compoBool=spell.compoBool;
        this.rm=spell.rm;
        this.save_type=spell.save_type;
        this.rank=spell.rank;

        if (this.id.equals("Désintégration")) {
            if (settings.getBoolean("perfect_desint", mC.getResources().getBoolean(R.bool.perfect_desint_def))) {
                this.perfect=true;
            }
        }

        this.converted=false;

        this.n_sub_spell=spell.n_sub_spell;

        this.metaList=spell.metaList;
    }

    public Spell(String id, String name, String descr,Integer n_sub_spell, String dice_type, Double n_dice_per_lvl, int cap_dice, String dmg_type, String range, String cast_time, String duration, String compo, String rm, String save_type, int rank,Context mC){
        settings = PreferenceManager.getDefaultSharedPreferences(mC);

        if(id.equalsIgnoreCase("")){
            this.id=name;
        } else {
            this.id=id;
        }
        this.name=name;
        this.descr=descr;
        this.n_sub_spell=n_sub_spell;
        this.dice_type=dice_type;
        this.n_dice_per_lvl=n_dice_per_lvl;
        this.cap_dice=cap_dice;
        this.dmg_type=dmg_type;
        this.range=range;
        this.cast_time=cast_time;
        this.duration=duration;
        calcCompo(compo);
        this.rm=rm;
        this.save_type=save_type;
        this.rank=rank;

        //this.convertion=null ; //TODO : un objet conversion ?  genre apres on peut faire if spell.getConvBytID("resistanc").isActive()

        this.metaList=new BuildMetaList(mC).getMetaList();
    }

    private void calcCompo(String compo) {
        if(compo.contains("V")){
            compoBool[0]=true;
        }
        if(compo.contains("G")){
            compoBool[1]=true;
        }
        if(compo.contains("M")){
            compoBool[2]=true;
        }
    }


    public String getID() {
        return id;
    }

    public Integer getRank(){
        return this.rank;
    }
    public String  getName(){
        return this.name;
    }
    public String  getDescr(){
        return this.descr;
    }
    public String  getDice_typ(){
        return this.dice_type;
    }
    public String  getSave_type(){
        return this.save_type;
    }
    public Integer  getSave_val(){
        return 0;
    } //TODO gerer ailleurs probablement
    public String  getDmg_type(){
        return this.dmg_type;
    }
    public String getRange(){
        return this.range;
    }


    public boolean isPerfect() {
        return this.perfect;
    }

    public boolean isConverted() {        return this.converted;    }

    public int getNSubSpell() {
        return this.n_sub_spell;
    }

    public boolean isHighscore(int val){
        boolean returnVal=false;
        int highscore=settings.getInt(this.id+"_highscore",0);
        if(val>highscore){
            returnVal=true;
            settings.edit().putInt(this.id+"_highscore",val).apply();
        }
        return returnVal;
    }

    public int getHighscore(){
        int highscore=settings.getInt(this.id+"_highscore",0);
        return highscore;
    }

    public boolean hasRM() {
        return Boolean.valueOf(this.rm);
    }


    public void setSubName(int i) {
        this.name=this.name+" "+i;
    }
}
