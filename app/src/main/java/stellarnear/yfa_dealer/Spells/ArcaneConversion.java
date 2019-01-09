package stellarnear.yfa_dealer.Spells;

public class ArcaneConversion {
    private String arcaneId;
    private int rank;
    public ArcaneConversion(ArcaneConversion ac){
        this.arcaneId=ac.arcaneId;
        this.rank=ac.rank;
    }
    public ArcaneConversion(){
        this.arcaneId="";
        this.rank=0;
    }

    public int getRank() {
        return rank;
    }

    public String getArcaneId() {
        return arcaneId;
    }

    public void setArcaneId(String s) {
        this.arcaneId=s;
    }
    public void setRank(int i){
        this.rank=i;
    }
}
