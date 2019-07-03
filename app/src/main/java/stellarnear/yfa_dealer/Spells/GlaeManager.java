package stellarnear.yfa_dealer.Spells;

public class GlaeManager { //Glaedäyes
    private Boolean glaeBoost=false;
    private Boolean tested=false;
    private Boolean fail=false;

    public GlaeManager(){
    }

    public GlaeManager(GlaeManager gM){
        this.glaeBoost=gM.glaeBoost;
    }

    public void setBoosted(){
        this.glaeBoost=true;
    }

    public boolean isBoosted(){
        return this.glaeBoost;
    }


    public Boolean isTested() {
        return tested;
    }

    public void setTested() {
        this.tested = true;
    }

    public void setFailed(){
        this.fail=true;
    }

    public boolean isFailed() {
        return this.fail;
    }
}
