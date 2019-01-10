package stellarnear.yfa_dealer.Spells;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.R;


public class Spell {

    private String  name;
    private String id;
    private String  descr;
    private String  dice_type;
    private Double  n_dice_per_lvl;
    private int     cap_dice;

    private String  dmg_type;
    private String  range;
    private String contact;
    private String area;
    private String  cast_time;
    private String  duration;

    private List<String> compoList=new ArrayList<>();

    private String rm;
    private String  save_type;

    private int     rank;
    private boolean perfect;
    private String perfectMetaId="";

    private int     n_sub_spell;

    private SharedPreferences settings;

    private MetaList metaList;
    private ArcaneConversion conversion=null;

    private boolean binded=false;
    private Cast cast;
    private boolean crit=false;

    //private Tools tools=new Tools();

    public Spell(Spell spell){ //copying spell
        this.id=spell.id;
        this.name=spell.name;
        this.descr=spell.descr;
        this.dice_type=spell.dice_type;
        this.n_dice_per_lvl=spell.n_dice_per_lvl;
        this.cap_dice=spell.cap_dice;
        this.dmg_type=spell.dmg_type;
        this.range=spell.range;
        this.contact=spell.contact;
        this.area=spell.area;
        this.cast_time=spell.cast_time;
        this.duration=spell.duration;
        this.compoList=spell.compoList;
        this.rm=spell.rm;
        this.save_type=spell.save_type;
        this.rank=spell.rank;
        this.perfect=spell.perfect;
        this.perfectMetaId=spell.perfectMetaId;
        this.n_sub_spell=spell.n_sub_spell;
        this.conversion=new ArcaneConversion(spell.conversion);
        this.metaList=new MetaList(spell.metaList);
        this.cast =new Cast();
        this.settings=spell.settings;
    }

    public Spell(String id, String name, String descr,Integer n_sub_spell, String dice_type, Double n_dice_per_lvl, int cap_dice, String dmg_type, String range,String contact,String area, String cast_time, String duration, String compo, String rm, String save_type, int rank,Context mC){
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
        this.contact=contact;
        this.area=area;
        this.cast_time=cast_time;
        this.duration=duration;
        calcCompo(compo);
        this.rm=rm;
        this.save_type=save_type;
        this.rank=rank;
        if (this.id.equals("Désintégration")) {
            if (settings.getBoolean("perfect_desint", mC.getResources().getBoolean(R.bool.perfect_desint_def))) {
                this.perfect=true;
            }
        }

        this.conversion=new ArcaneConversion();
        this.metaList= BuildMetaList.getInstance(mC).getMetaList();
        this.cast =new Cast();
    }

    private void calcCompo(String compo) {
        compoList.addAll(Arrays.asList(compo.split(",")));
    }


    public String getID() {
        return id;
    }

    public String getDuration() {
        return duration;
    }

    public boolean hasCompo(){
        return compoList.size()>0;
    }

    public List<String> getCompoList() {
        return compoList;
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
    public String getDice_type(){
        return this.dice_type;
    }

    public Double getN_dice_per_lvl() {
        return n_dice_per_lvl;
    }
    public int getCap_dice() {
        return this.cap_dice;
    }

    public String  getSave_type(){
        return this.save_type;
    }
    public String  getDmg_type(){
        return this.dmg_type;
    }
    public String getRange(){
        return this.range;
    }
    public String getContact(){
        return this.contact;
    }
    public String getArea(){ return this.area; }

    public String getCast_time() {
        return cast_time;
    }

    public boolean isPerfect() {
        return this.perfect;
    }

    public ArcaneConversion getConversion(){
        return this.conversion;
    }

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

    public MetaList getMetaList() {
        return metaList;
    }

    public CheckBox getCheckboxeForMetaId(final Activity mA,final Context mC, final String metaId, Boolean... fromArcanicConversion){
        Boolean fromConv = fromArcanicConversion.length > 0 ? fromArcanicConversion[0] : false;
        final CheckBox check = metaList.getMetaByID(metaId).getCheckBox(mA, mC,fromArcanicConversion);

        if(this.rank>=9 || this.rank+metaList.getMetaByID("meta_heighten").getnCast()>=9 ){
            metaList.getMetaByID("meta_heighten").getCheckBox(mA,mC).setEnabled(false);
        }

        if(this.dmg_type.equalsIgnoreCase("") && metaId.equalsIgnoreCase("meta_enhance")){
            check.setEnabled(false);
        }

        List<String> rangesDisable = Arrays.asList("contact","personnelle");
        if(rangesDisable.contains(this.range) && metaId.equalsIgnoreCase("meta_select")){
            check.setEnabled(false);
        }

        List<String> rangesAccepted = Arrays.asList("contact","courte","moyenne");
        if(!rangesAccepted.contains(this.range) && metaId.equalsIgnoreCase("meta_range")){
            check.setEnabled(false);
        }
        List<String> durationAccepted = Arrays.asList("instant","permanente");
        if(!durationAccepted.contains(this.duration) && metaId.equalsIgnoreCase("meta_duration")){
            check.setEnabled(false);
        }
        if(!this.cast_time.equalsIgnoreCase("simple") && metaId.equalsIgnoreCase("meta_quicken")){
            check.setEnabled(false);
        }
        if(this.perfect && this.rank+metaList.getMetaByID(metaId).getUprank()<=9 && !fromConv){
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean active) {
                    if(active) {

                        if (perfect) { //pour recheck sur les autre meta afficher que le sort est toujours parfait
                            new AlertDialog.Builder(mA)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur cette métamagie ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            metaList.getMetaByID(metaId).active();
                                            perfectMetaId = metaId;
                                            check.setClickable(false);
                                            perfect = false;
                                        }
                                    })
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            metaList.getMetaByID(metaId).active();
                                        }
                                    }).show();
                        } else {
                            metaList.getMetaByID(metaId).active();
                        }
                    } else {
                        metaList.getMetaByID(metaId).desactive();
                    }
                }
            });

        }

        return check;
    }

    public void setSubName(int i) {
        this.name=this.name+" "+i;
    }

    public boolean isBinded() {
        return binded;
    }

    public void bindTo(Spell previousSpellToBind) {  //pour les sub spell pour que les meta et conversion s'applique partout
        this.metaList=previousSpellToBind.metaList;
        this.conversion=previousSpellToBind.conversion;
        this.cast =previousSpellToBind.cast;
        this.binded=true;
    }

    public void cast(){
        cast.cast();
    }

    public boolean isCast(){
        return cast.isCast();
    }


    public void makeCrit() {
        this.crit=true;
    }

    public boolean isCrit(){
        return this.crit;
    }
}
