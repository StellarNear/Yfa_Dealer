package stellarnear.yfa_dealer.Spells;

public class Metamagic {
    private String id;
    private String name;
    private String description;
    private int uprank;
    private boolean multicast=false;
    private boolean state;

    public Metamagic(String id,String name,String description,int uprank,boolean multicast,boolean state){
        this.id=id;
        this.name=name;
        this.description=description;
        this.uprank=uprank;
        this.multicast=multicast;
        this.state=state;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUprank() {
        return uprank;
    }

    public String getDescription() {
        return description;
    }

    public boolean canBeMultiCast(){
        return multicast;
    }

    public boolean isActive() {
        return state;
    }

    public void setState(Boolean bool){
        this.state=bool;
    }
}
