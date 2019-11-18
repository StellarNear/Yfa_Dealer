package stellarnear.yfa_companion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import stellarnear.yfa_companion.Rolls.Dice;
import stellarnear.yfa_companion.Spells.Spell;

public class PostDataElement {
    private String targetSheet="Yfa";
    private String date;
    private String detail ="-";
    private String typeEvent;
    private String result;

    /* lancement d'un sort */
    public PostDataElement(Spell spell){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());

        this.typeEvent="Lancement sort "+spell.getName() +" (rang:"+new Calculation().currentRank(spell)+")";

        if(spell.isFailed()||spell.contactFailed()||spell.getGlaeManager().isFailed()){
            String failPostData="-";
            if(spell.isFailed()){
                failPostData="Test de RM raté";
            } else if(spell.contactFailed()){
                failPostData="Test de contact raté";
            } else if(spell.getGlaeManager().isFailed()){
                failPostData="Glaedäyes empêche";
            }
            this.result=failPostData;
        } else {
            if(spell.getDmg_type().equalsIgnoreCase("")){
                this.result="Lancé !";
            } else {
                this.result="Dégâts : "+spell.getDmgResult();
            }
        }
    }

    /* autre posts */
    public PostDataElement(String typeEvent,int result){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent=typeEvent;
        this.result=String.valueOf(result);
    }

    public PostDataElement(String typeEvent,String resultTxt){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent=typeEvent;
        this.result=resultTxt;
    }

    public PostDataElement(String typeEvent, Dice oriDice, int result){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());

        this.typeEvent=typeEvent;
        this.result=String.valueOf(result);

        String detailTxt = String.valueOf(oriDice.getRandValue());
        if(oriDice.getMythicDice()!=null){detailTxt +=","+oriDice.getMythicDice().getRandValue();}
        this.detail =detailTxt;
    }

    public PostDataElement(String typeEvent, ArrayList<Dice> oriDices, int result){  //test contre RM peut avoir deux dès
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent=typeEvent;
        this.result=String.valueOf(result);
        String detailTxt="";
        for(Dice dice:oriDices) {
            if(!detailTxt.equalsIgnoreCase("")){detailTxt+=" || " ;}
            detailTxt += String.valueOf(dice.getRandValue());
            if (dice.getMythicDice() != null) {
                detailTxt += "," + dice.getMythicDice().getRandValue();
            }
            this.detail = detailTxt;
        }
    }

    public String getDetail() {
        return detail;
    }

    public String getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public String getTargetSheet() {
        return targetSheet;
    }

    public String getTypeEvent() {
        return typeEvent;
    }
}
