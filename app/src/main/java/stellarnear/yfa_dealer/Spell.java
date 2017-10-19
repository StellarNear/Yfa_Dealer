package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.Serializable;

public class Spell extends AppCompatActivity implements Serializable {


    private String  name;
    private String  descr;
    private String  dice_type;
    private String  ori_dice_type;
    private int     n_dice;
    private String  dmg_type;
    private String  range;
    private String  cast_time;
    private String  ori_cast_time;
    private String  duration;
    private String  compo;
    private boolean rm;
    private String  save_type;
    private int     save_val;
    private int     rank;
    public Spell(String name, String descr, String dice_type, int n_dice, String dmg_type, String range, String cast_time, String duration, String compo, boolean rm, String save_type, int save_val, int rank,Context mC){
        this.name=name;
        this.descr=descr;
        this.dice_type=dice_type;
        this.ori_dice_type=dice_type;
        this.n_dice=n_dice;
        this.dmg_type=dmg_type;
        this.range=range;
        this.cast_time=cast_time;
        this.ori_cast_time=cast_time;
        this.duration=duration;
        this.compo=compo;
        meta_Materiel(mC);
        this.rm=rm;
        this.save_type=save_type;
        this.rank=rank;
        setSave_val(mC);
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
    public boolean getRM(){
        return this.rm;
    }
    public String  getSave_type(){
        return this.save_type;
    }
    public Integer  getSave_val(){
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

    private void setRM(boolean rm){
        this.rm=rm;
    }
    private void setSave_type(String save_type){
        this.save_type=save_type;
    }
    public void setSave_val(Context mC){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        String cha_txt=prefs.getString("charisme",mC.getResources().getString(R.string.charisme_def));
        Integer charisme = to_int(cha_txt,"Modificateur de charisme",mC);
        Integer save_val_calc=charisme+this.rank+10;
        this.save_val=save_val_calc;
    }
    private void setRank(int rank){
        this.rank=rank;
    }

 
    
    // methode de meta magie
    public void meta_Enhance_Spell(Boolean active) {
        
        if (active) {
            if(this.dice_type.equals("d4")){
                this.dice_type="d6";
                this.ori_dice_type="d4";
            } else if(this.dice_type.equals("d6")){
                this.dice_type="d8";
            } else if(this.dice_type.equals("d8")){
                this.dice_type="d6";
                this.n_dice=this.n_dice*2;
                this.ori_dice_type="d8";
            }
            this.rank+=4;
        } else {
           if(this.dice_type.equals("d8")){
                this.dice_type="d6";
            } else if(this.dice_type.equals("d6") && this.ori_dice_type.equals("d4")){
                this.dice_type="d4";
            } else if(this.dice_type.equals("d6") && this.ori_dice_type.equals("d8")){
                this.n_dice=this.n_dice/2;
                this.dice_type="d8"; 
            }
            this.rank-=4;
        }
    }
    
    public void meta_Enhance_Spell_descr(Context mC) {
        String descr="Les dés de dégâts pour les sorts que vous lancez augmentent d'un pas " +
                "(c.a.d., d6>d8, d8>2d6, etc.). Un sort amélioré utilise un emplacement de sorts quatre fois plus haut " +
                "que le niveau réel du sort. N'a aucun effet sur les sorts qui n'infligent pas de dégâts.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
        public void meta_Rapid(Boolean active) {
        if (active) {
            this.ori_cast_time=this.cast_time;
            this.cast_time="rapide";
            this.rank+=4;
        } else {
            this.cast_time=this.ori_cast_time;
            this.rank-=4;
        }
    }
    
    public void meta_Rapid_descr(Context mC) {
        String descr=" L’incantation du sort ne prend qu’une action rapide."+
            " Si le temps d’incantation du sort est supérieur à un round,"+
            " on ne peut pas l’accélérer avec ce don. Un sort à incantation rapide nécessite un emplacement de sort de quatre niveaux"
            +" de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
    

    public void meta_Select_Spell(boolean active) {
        if (active) {
            this.rank+=1;
        } else {
            this.rank-=1;
        }
    }
    public void meta_Select_Spell_descr(Context mC) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        String cha_txt=prefs.getString("charisme",mC.getResources().getString(R.string.charisme_def));
        String descr="Lorsque le personnage lance un sort de zone sélectif, il peut choisir "+ cha_txt +" cibles situées dans la zone."+
            " Les cibles choisies échappent aux effets du sort. Un sort sélectif occupe un emplacement de sort d’un niveau de plus"+
            " que le niveau normal du sort.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    

    public void meta_Silent(boolean active) {//à refaire mais pour test la
        String resultat=this.compo;
        if (active) {
            resultat=resultat.replace("V","-");
            this.rank+=1;
        } else {
            resultat=resultat.replace("-","V");
            this.rank-=1;
        }
        this.compo=resultat;
    }
    
    public void meta_Silent_descr(Context mC) {
        String descr="Un sort à incantation silencieuse ne nécessite pas de composante verbale."+
            " Les sorts sans composante verbale ne sont donc pas affectés. Un sort à incantation silencieuse nécessite "+
            "un emplacement de sort d’un niveau de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
    public void meta_Materiel(Context mC) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        String resultat=this.compo;
        if (prefs.getBoolean("materiel_switch",mC.getResources().getBoolean(R.bool.materiel_switch_def)))  {
            resultat=resultat.replace("M","-");
        } else {
            resultat=resultat.replace("-","M");
        }
        this.compo=resultat;
    }
    
   //augmentation d'intensité
   //quintessence des sorts
   //incant rapid
   //extension d'effet
   
   //sort éloigné
    
    //perfection magique vraiment à part uniquement sur desintégration permet de mettre une metamagie du choix gratos

    public Integer to_int(String key,String field,Context mC){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            Toast toast = Toast.makeText(mC, "Attention la valeur : "+key+"\nDu champ : "+field+"\nEst incorrecte, valeur mise à 0.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            value=0;
        }
        return value;
    }



}
