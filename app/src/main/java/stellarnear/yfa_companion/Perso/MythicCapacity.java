package stellarnear.yfa_companion.Perso;

/**
 * Created by jchatron on 26/12/2017.
 */

public class MythicCapacity {
    private String name;
    private String descr;
    private String type;
    private String id;
    public MythicCapacity(String name, String descr,String type, String id)
    {
        this.name=name;
        this.descr=descr;
        this.type=type;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public String getDescr() {
        return descr;
    }

    public String getType() {
        return type;
    }

    public String getId(){return id;}
}
