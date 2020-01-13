package stellarnear.yfa_companion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import stellarnear.yfa_companion.Rolls.Dice;
import stellarnear.yfa_companion.Spells.Metamagic;
import stellarnear.yfa_companion.Spells.Spell;


public class PostDataElement {
    private String targetSheet= "Yfa";
    private String date="-";
    private String detail ="-";
    private String typeEvent="-";
    private String result="-";

    private Spell arrowSpell=null; //pour les fleches avec sort



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


    public PostDataElement(String typeEvent,String detail,String resultTxt){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent=typeEvent;
        this.detail=detail;
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

    /* lancement d'un sort */
    public PostDataElement(Spell spell){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());

        this.typeEvent="Lancement sort "+spell.getName() +" (rang de base:"+spell.getRank()+")";

        if(spell.getMetaList().hasAnyMetaActive()){
            this.detail="Métamagies:";
            int nRank=0;
            for(Metamagic meta : spell.getMetaList().getAllActivesMetas().asList()){
                this.detail+=meta.getName()+"(+"+meta.getUprank()*meta.getnCast()+")";
                nRank+=meta.getnCast()*meta.getUprank();
            }
            this.typeEvent+=" (+"+nRank+" rangs métamagies)";
            if(spell.getMetaList().metaIdIsActive("meta_arrow")){
                this.arrowSpell=spell;
            }
        }
        if(spell.isFailed()||spell.contactFailed()){
            String failPostData="-";
            if(spell.isFailed()){
                failPostData="Test de RM raté";
            } else if(spell.contactFailed()){
                failPostData="Test de contact raté";
            }
            this.result=failPostData;
        } else {
            if(spell.getDmg_type().equalsIgnoreCase("")){
                this.result="Lancé !";
            } else {
                if(spell.getDmg_type().equalsIgnoreCase("heal")){
                    this.result = "Soins : " + spell.getDmgResult();
                } else {
                    this.result = "Dégâts : " + spell.getDmgResult();
                }
            }
        }
    }


    public PostDataElement(RemoveDataElementSpellArrow.PairSpellUuid pairSpellUuid) { //constructeur où on appelle pas à nouveau le store arrow
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        Spell spell=pairSpellUuid.getSpell();

        this.typeEvent="Lancement sort "+spell.getName() +" (rang de base:"+spell.getRank()+")";

        if(spell.getMetaList().hasAnyMetaActive()){
            this.detail="Métamagies:";
            int nRank=0;
            for(Metamagic meta : spell.getMetaList().getAllActivesMetas().asList()){
                this.detail+=meta.getName()+"(+"+meta.getUprank()*meta.getnCast()+")";
                nRank+=meta.getnCast()*meta.getUprank();
            }
            this.typeEvent+=" (+"+nRank+" rangs métamagies)";
        }
        if(spell.isFailed()||spell.contactFailed()){
            String failPostData="-";
            if(spell.isFailed()){
                failPostData="Test de RM raté";
            } else if(spell.contactFailed()){
                failPostData="Test de contact raté";
            }
            this.result=failPostData;
        } else {
            if(spell.getDmg_type().equalsIgnoreCase("")){
                this.result="Lancé !";
            } else {
                if(spell.getDmg_type().equalsIgnoreCase("heal")){
                    this.result = "Soins : " + spell.getDmgResult();
                } else {
                    this.result = "Dégâts : " + spell.getDmgResult();
                }
            }
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

    public Spell getArrowSpell() {
        return arrowSpell;
    }
}
