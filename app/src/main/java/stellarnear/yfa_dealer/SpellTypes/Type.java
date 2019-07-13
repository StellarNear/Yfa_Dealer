package stellarnear.yfa_dealer.SpellTypes;


public class Type {

    private int colorId;
    private int colorIdRecent;
    private String name;
    private String key;
    private int drawableId;

    public Type(String key, String name, int colorId, int colorIdRecent, int drawableId){
        this.key=key;
        this.name=name;
        this.colorId=colorId;
        this.colorIdRecent=colorIdRecent;
        this.drawableId=drawableId;
    }

    public int getColorId() {
        return colorId;
    }

    public int getColorIdRecent() {
        return colorIdRecent;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getDrawableId() {
        return this.drawableId;
    }
}

