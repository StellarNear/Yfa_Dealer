package stellarnear.yfa_dealer.Spells;

public class DmgType {
    private String dmgType;
    private Boolean converted=false;

    public DmgType(String dmgType){
        this.dmgType=dmgType;
    }

    public String getDmgType() {
        return this.dmgType;
    }

    public void setDmgType(String newElement) {
        this.dmgType=newElement;
    }

    public void setConverted() {
        this.converted = true;
    }

    public boolean isConverted(){
        return this.converted;
    }
}
