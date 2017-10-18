package stellarnear.yfa_dealer;

import java.io.Serializable;

public class Spell implements Serializable {


    private String  name;
    private String  descr;
    private String  dice_type;
    private int     n_dice;
    private String  dmg_type;
    private String  range;
    private String  cast_time;
    private String  duration;
    private String  compo;
    private int     dd;
    private boolean rm;
    private String  save_type;
    private int     save_val;
    private int     rank;
    public Spell(String name,String descr,String dice_type,int n_dice,String dmg_type,String range,String cast_time,String duration,String compo,int dd,boolean rm,String save_type,int save_val,int rank){
        this.name=name;
        this.descr=descr;
        this.dice_type=dice_type;
        this.n_dice=n_dice;
        this.dmg_type=dmg_type;
        this.range=range;
        this.cast_time=cast_time;
        this.duration=duration;
        this.compo=compo;
        this.dd=dd;
        this.rm=rm;
        this.save_type=save_type;
        this.save_val=save_val;
        this.rank=rank;
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
    public Integer  getN_dice(){
        return this.n_dice;
    }
    public String  getDmg_type(){
        return this.dmg_type;
    }
    public String  getRange(){
        return this.range;
    }
    public String  getCast_tim(){
        return this.cast_time;
    }
    public String  getDuration(){
        return this.duration;
    }
    public String  getCompo(){
        return this.compo;
    }
    public Integer     getDD(){
        return this.dd;
    }
    public boolean getRM(){
        return this.rm;
    }
    public String  getSave_type(){
        return this.save_type;
    }
    public Integer     getSave_val(){
        return this.save_val;
    }


    private void setName(String name){
        this.name=name;
    }
    private void setDescr(String descr){
        this.descr=descr;
    }
    private void setDice_type(String dice_type){
        this.dice_type=dice_type;
    }
    private void setN_dice(int n_dice){
        this.n_dice=n_dice;
    }
    private void setDmg_type(String dmg_type){
        this.dmg_type=dmg_type;
    }
    private void setRange(String range){
        this.range=range;
    }
    private void setCast_time(String cast_time){
        this.cast_time=cast_time;
    }
    private void setDuration(String duration){
        this.duration=duration;
    }
    private void setCompo(String compo){
        this.compo=compo;
    }
    private void setDD(int dd){
        this.dd=dd;
    }
    private void setRM(boolean rm){
        this.rm=rm;
    }
    private void setSave_type(String save_type){
        this.save_type=save_type;
    }
    private void setSave_val(int save_val){
        this.save_val=save_val;
    }
    private void setRank(int rank){
        this.rank=rank;
    }

    // methode de meta magie
    public void meta_Enhance_Spell(Boolean active) {
        if (active) {
            this.n_dice+=1;
            this.rank+=4;
        } else {
            this.n_dice-=1;
            this.rank-=4;
        }
    }
    
    public void meta_Enhance_Spell_descr() {
        String descr="Les dés de dégâts pour les sorts que vous lancez augmentent d'un pas 
            (c.a.d., d6>d8, d8>2d6, etc.). Un sort amélioré utilise un emplacement de sorts quatre fois plus haut 
            que le niveau réel du sort. N'a aucun effet sur les sorts qui n'infligent pas de dégâts.";
        Toast toast = Toast.makeText(getApplicationContext(), descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
    

}
