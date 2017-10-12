package stellarnear.yfa_dealer;

public class Spell {


    private String Name;
    private String dice_type;
    private String n_dice;
    private String dmg_type;
    private String descr;
    private int DD;
    private boolean RM;
    private String Save_type;
    private int Save_val;
    private int rank;
    public Spell(String Name,String dice_type,String n_dice,String dmg_type,String descr,int DD,boolean RM,String Save_type,int Save_val,int rank){
        this.Name=Name;
        this.dice_type=dice_type;
        this.n_dice=n_dice;
        this.dmg_type=dmg_type;
        this.descr=descr;
        this.DD=DD;
        this.RM=RM;
        this.Save_type=Save_type;
        this.Save_val=Save_val;
        this.rank=rank;
    }

    public Integer getRank(){
        return this.rank;
    }
        // methode de meta magie up les dès etc
        //methode pour recup les nom aussi

}



