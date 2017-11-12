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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spell extends AppCompatActivity implements Serializable {


    private String  name;
    private String  descr;
    private String  dice_type;
    private String  ori_dice_type;
    private int     n_dice;
    private int     ori_n_dice;
    private String  dmg_type;
    private String  range;
    private String  cast_time;
    private String  ori_cast_time;
    private String  duration;
    private String  ori_duration;
    private String  compo;
    private Boolean[] compoBool=new Boolean[3];
    private Boolean[] ori_compoBool=new Boolean[3];
    private String rm;
    private String  save_type;
    private int     save_val;
    private int     rank;
    private int ori_rank;
    private String dmg_dice_roll_txt;
    private int caster_lvl;
    private int n_cast;
    private boolean perfect;

    public Spell(String name, String descr, String dice_type, int n_dice, String dmg_type, String range, String cast_time, String duration, String compo, String rm, String save_type, int rank,Context mC){
        this.name=name;
        this.descr=descr;
        this.dice_type=dice_type;
        this.ori_dice_type=dice_type;
        this.n_dice=n_dice;
        this.ori_n_dice=n_dice;
        this.dmg_type=dmg_type;
        this.range=range;
        this.cast_time=cast_time;
        this.ori_cast_time=cast_time;
        this.duration=duration;
        this.ori_duration=duration;
        this.compo=compo;
        setcompoBool(this.compo);
        meta_Materiel(mC);
        this.rm=rm;
        this.save_type=save_type;
        this.rank=rank;
        this.ori_rank=rank;
        setSave_val(mC);
        setCaster_lvl(mC);
        this.n_cast=0;
        setPerfect(mC);

        //tester si perfect dans meta si oui popup si il depense perfect on uprank gratos on rend le sort non perfect mais on desactive la box (plus cliquable) peut etre à faire
        // dans spellcastactivity du coup
        // si il dit non comportement normal il paye le rank


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

    //faire un get damage qui simplement concatene 4 et d6 mais en cas de truc spéciaux (comme / lvl ou jet maxé *etc il fait le calcul)
    public Integer  getN_dice(){
        return this.n_dice;
    }

    public String  getDmg_type(){
        return this.dmg_type;
    }

    public String getRange(){
        return this.range;
    }

    public String getRange_txt(){
        String rang=this.range;
        String[] ranges_lvl = { "courte", "moyenne", "longue" };

        if (Arrays.asList(ranges_lvl).contains(rang)) {
            Integer lvl = this.caster_lvl;
            Double dist_doubl =0.0;
            switch(rang) {
                case ("courte"):
                    dist_doubl=7.5+1.5*(lvl/2.0);
                    break;

                case ("moyenne"):
                    dist_doubl=30.0+3.0*lvl;
                    break;

                case ("longue"):
                    dist_doubl=120.0+lvl*12.0;
                    break;
            }

            rang= String.valueOf(dist_doubl)+"m";
        }
        return rang;
    }

    public String  getCast_tim(){
        return this.cast_time;
    }
    public String  getDuration(Context mC){
        String dura=this.duration;
        if(dura.contains("/lvl")){
            Integer lvl =  this.caster_lvl;
            //Log.d("STATE dura",dura);
            //Log.d("STATE durarepl",dura.replaceAll("[^0-9?!]",""));
            Integer factor= to_int(dura.replaceAll("[^0-9?!]",""),"Facteur numérique de durée du sort",mC);
            Integer result = factor * lvl;
            String duration_unit = dura.replaceAll("/lvl","").replaceAll("[0-9?!]","");
            dura = result+duration_unit;
        }
        return dura;
    }
    public String  getCompo(){
        String compo_out="";
        if(this.compoBool[0]){compo_out+="V,";}
        if(this.compoBool[1]){compo_out+="G,";}
        if(this.compoBool[2]){compo_out+="M,";}
        if (compo_out.endsWith(",")){compo_out=compo_out.substring(0, compo_out.length() - 1);}
        return compo_out;
    }
    public String getRM(){
        if (this.rm.equals("")) {return "";}
        return (Boolean.valueOf(this.rm)? "oui" : "non");
    }
    public String  getSave_type(){
        return this.save_type;
    }
    public Integer  getSave_val(){
        return this.save_val;
    }

    public String getDmg_txt(Context mC) {
        if (this.n_dice==0) {return "";}
        String dmg=this.n_dice+this.dice_type;

        if(this.dice_type.contains("*d")){
            Integer integer_dice = to_int(dice_type.replace("*d",""),"Type de dès",mC);
            Integer dmg_int = this.n_dice * integer_dice;
            dmg = String.valueOf(dmg_int);
            return dmg;
        }

        if(this.dice_type.contains("/lvl")){
            Integer lvl = this.caster_lvl;
            Integer dmg_int = this.n_dice * lvl;
            dmg = String.valueOf(dmg_int);
            return dmg;
        }

        return dmg;
    }

    public String getDmg_dice_roll_txt() {
        return this.dmg_dice_roll_txt;
    }

    public Integer getN_cast() {
        return this.n_cast;
    }
    
    public Integer getBaseRank() {
        return this.ori_rank;
    }

    public boolean isPerfect() {
        return this.perfect;
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

    private void setRM(String rm){
        this.rm=rm;
    }
    private void setSave_type(String save_type){
        this.save_type=save_type;
    }
    public void setRank(int rank){
        this.rank=rank;
    }

    public void setSave_val(Context mC){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        String cha_txt=prefs.getString("charisme",mC.getResources().getString(R.string.charisme_def));
        Integer charisme = to_int(cha_txt,"Modificateur de charisme",mC);
        Integer save_val_calc=charisme+this.rank+10;
        this.save_val=save_val_calc;
    }

    public void setCaster_lvl(Context mC){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        String lvl_txt=prefs.getString("nls_val",mC.getResources().getString(R.string.nls_val_def));
        Integer lvl = to_int(lvl_txt,"NLS",mC);
        String lvl_bonus_txt=prefs.getString("NLS_bonus",mC.getResources().getString(R.string.NLS_bonus_def));
        Integer lvl_bonus = to_int(lvl_bonus_txt,"NLS bonus",mC);
        Integer caster_lvl_calc=lvl+lvl_bonus;
        this.caster_lvl=caster_lvl_calc;
    }

    public void setcompoBool(String compo) { //V,G,M
        if(compo.contains("V")){
            this.compoBool[0]=true;
            this.ori_compoBool[0]=true;
        } else { this.compoBool[0]=false; this.ori_compoBool[0]=false; }
        if(compo.contains("G")){
            this.compoBool[1]=true;
            this.ori_compoBool[1]=true;
        } else {this.compoBool[1]=false; this.ori_compoBool[1]=false;}
        if(compo.contains("M")){
            this.compoBool[2]=true;
            this.ori_compoBool[2]=true;
        }else {this.compoBool[2]=false; this.ori_compoBool[2]=false;}

    }

    public void setDmg_dice_roll_txt(String dmg_dice_roll_txt) {
        this.dmg_dice_roll_txt = dmg_dice_roll_txt;
    }


    public void setN_cast(Integer ncast) {
        this.n_cast=ncast;
    }

    public void setPerfect(Context mC) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        if (this.name.equals("Désintégration")) {
            if (prefs.getBoolean("perfect_desint", mC.getResources().getBoolean(R.bool.perfect_desint_def))) {
                this.perfect=true;
            }
        }
    }

    public void setPerfect(Boolean bool) {
        this.perfect=bool;
    }

    // methode de meta magie

    public void meta_Materiel(Context mC) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        if (prefs.getBoolean("materiel_switch",mC.getResources().getBoolean(R.bool.materiel_switch_def)))  {
            if(!this.compo.contains("M+")){
                this.compoBool[2]=false;
            }
        } else {
            this.compoBool[2]=this.ori_compoBool[2];
        }
    }



    // methode de meta magie actives
    public void meta_Enhance_Spell(Boolean active) {

        if (active) {
            if(this.dice_type.contains("d4")){
                this.dice_type="d6";
            } else if(this.dice_type.contains("d6")){
                this.dice_type="d8";
            } else if(this.dice_type.contains("d8")){
                this.dice_type="d6";
                this.n_dice=this.n_dice*2;
            }
            this.rank+=4;
        } else {
            if(this.dice_type.contains("d8")){
                this.dice_type="d6";
            } else if(this.dice_type.contains("d6") && this.ori_dice_type.contains("d4")){
                this.dice_type="d4";
            } else if(this.dice_type.contains("d6") && this.ori_dice_type.contains("d8")){
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
        String descr="L’incantation du sort ne prend qu’une action rapide."+
                " Si le temps d’incantation du sort est supérieur à un round,"+
                " on ne peut pas l’accélérer avec ce don. Un sort à incantation rapide nécessite un emplacement de sort de quatre niveaux"
                +" de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
        // sort à retardement +4
    public void meta_Delay(Boolean active) {
        if (active) {
            this.rank+=4;
        } else {
            this.rank-=4;
        }
    }

    public void meta_Delay_descr(Context mC) {
        String descr="Le déclenchement du sort se produira un fois un certain temps écoulé (max 24h)."+
                " Une fois la durée fixée rien ne peut la changer (hors annulation)."+
                " Un sort retardé est visible magiquement et peut etre dissipé. Un sort à retardement nécessite un emplacement de sort de quatre niveaux"
                +" de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }

    //quintessence des sorts +3
    public void meta_Quint(boolean active) {
        String resultat=this.dice_type;
        if (active) {
            this.rank+=3;
            resultat=resultat.replace("d","*d");
        } else {
            this.rank-=3;
            resultat=resultat.replace("*d","d");
        }
        this.dice_type=resultat;
    }
    public void meta_Quint_descr(Context mC) {
        String descr="Tous les effets numériques aléatoires de la quintessence d’un sort prennent automatiquement leur valeur maximale. "+
                "Les jets de sauvegarde et les jets opposés ne sont pas affectés, pas plus que les sorts sans données numériques "+
                "et aléatoires. La quintessence d’un sort nécessite un emplacement de sort de trois niveaux de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }

    //extension d'effet+2

    public void meta_Extend(boolean active) {
        if (active) {
            this.rank+=2;
            this.n_dice += (int)(this.n_dice/2.0);
        } else {
            this.rank-=2;
            this.n_dice=ori_n_dice;
        }
    }
    public void meta_Extend_descr(Context mC) {
        String descr="Toutes les variables numériques et aléatoires d’un sort bénéficiant d'une extension d’effet augmentent de 50%."+
                "Les jets de sauvegarde et les jets opposés ne sont pas affectés, ni les sorts sans variable numérique aléatoire. Un sort à extension d’effet nécessite un emplacement de sort de deux niveaux de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
            // flêche naturalisée+2
    public void meta_Enchant_arrow(Boolean active) {
        if (active) {
            this.ori_cast_time=this.cast_time;
            this.cast_time="complexe";
            this.rank+=2;
        } else {
            this.cast_time=this.ori_cast_time;
            this.rank-=2;
        }
    }

    public void meta_Enchant_arrow_descr(Context mC) {
        String descr="Place un sort sur une flêche de l'archer-sylvestre (Wedge + Tenshi ftw). "+
            "Tout sort placé sur une flèche de cette manière est alors considéré comme "+
            "lancé. Les flèches naturalisées restent actives 24h. Utiliser ce pouvoir est "+
            "une action complexe et nécéssite un emplacement de sort de deux niveaux de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
    
    //sort selectif +1
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


    public void meta_Silent(boolean active) {

        if(active){
            this.compoBool[0] = false;
            this.rank+=1;
        } else {
            this.compoBool[0] = this.ori_compoBool[0];
            this.rank-=1;
        }
    }

    public void meta_Silent_descr(Context mC) {
        String descr="Un sort à incantation silencieuse ne nécessite pas de composante verbale."+
                " Les sorts sans composante verbale ne sont donc pas affectés. Un sort à incantation silencieuse nécessite "+
                "un emplacement de sort d’un niveau de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }


    //augmentation d'intensité pourra etre pris plusieurs fois
    public void meta_Intense(boolean active) {
        if (active) {
            this.save_val+=1;
            this.rank+=1;
            this.caster_lvl+=1;
        } else {
            this.save_val-=1;
            this.rank-=1;
            this.caster_lvl-=1;
        }
    }

    public void meta_Intense_descr(Context mC) {
        String descr="Ce don permet d’amplifier l’intensité du sort choisi en augmentant son niveau effectif"+
                " (d’un ou plusieurs niveaux, sans dépasser le 9e). Contrairement aux autres dons de métamagie, "+
                "il augmente réellement le niveau de sort modifié. Toutes les propriétés du sort qui dépendent de son niveau, nécessite un emplacement de sort égal à son nouveau niveau effectif.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }
    
    // extend durée+1
    
    public void meta_Extend_dura(boolean active) {
        if (active) {
            Pattern pattern = Pattern.compile("^([0-9]+)");  //si ca plante à test avec * à la place de +
            Matcher matcher = pattern.matcher(this.duration);
            if (matcher.find())
            {
                Integer int_dura = 2*to_int(matcher.group());
                this.duration=this.duration.replaceAll(matcher.group(1),String.valueOf(int_dura));
            }   
            this.rank+=1;
        } else {
            this.duration=this.ori_duration;
            this.rank-=1;
        }
    }

    public void meta_Extend_dura_descr(Context mC) {
        String descr="Un sort à extension de durée dure deux fois plus longtemps qu’indiqué dans sa description. Les sorts permanents, instantanés et ceux dont la durée dépend de la concentration du personnage ne sont pas concernés. Un sort à extension de durée nécessite un emplacement de sort d’un niveau de plus que son niveau réel.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }

    //sort éloigné
    public void meta_Far(boolean active) {
        String range=this.range;
        String [] all_range={"contact","courte","moyenne","longue"};
        for(int i=0;i<all_range.length;i++){
            if(all_range[i].equals(range)){
                if(active){
                    if (!all_range[i].equals("longue"))
                    {
                        this.rank += 1;
                        range = all_range[i + 1];
                    }
                } else {
                    this.rank-=1;
                    range=all_range[i-1];
                }
                this.range=range;
                break;
            }
        }
    }

    public void meta_Far_descr(Context mC) {
        String descr="Ce don permet d’augmenter la portée d'un sort (selon l’ordre « contact », « courte », « moyenne », « longue »). "
                +"Un sort éloigné occupe un emplacement de sort d’un niveau de plus que le niveau normal du sort pour chaque augmentation "+
                "de catégorie. Si le sort de base nécessite des attaques de contact au corps à corps, la version modifiée par ce don utilise des attaques de contact à distance.";
        Toast toast = Toast.makeText(mC, descr, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
        toast.show();
    }



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

    public Integer to_int(String key){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            value=0;
        }
        return value;
    }

    public Boolean to_bool(String key,String field,Context mC){
        Boolean value;
        try {
            value = Boolean.parseBoolean(key);
        } catch (Exception e){
            Toast toast = Toast.makeText(mC, "Attention la valeur : "+key+"\nDu champ : "+field+"\nEst incorrecte, valeur mise à 0.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();
            value=false;
        }
        return value;
    }



}
