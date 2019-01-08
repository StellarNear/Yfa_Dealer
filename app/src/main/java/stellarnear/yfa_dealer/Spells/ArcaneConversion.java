package stellarnear.yfa_dealer.Spells;

public class ArcaneConversion {
    private String arcaneId;
    private int rank;
    public ArcaneConversion(String arcaneId,int rank){
        this.arcaneId=arcaneId;
        this.rank=rank;
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

}
