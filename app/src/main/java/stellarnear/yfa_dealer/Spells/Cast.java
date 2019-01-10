package stellarnear.yfa_dealer.Spells;

public class Cast {  //objet fait pour detecter des cast entre spell binded
    private boolean cast;

    public Cast(){
        cast=false;
    }

    public void cast(){
        this.cast=true;
    }

    public boolean isCast() {
        return cast;
    }
}
