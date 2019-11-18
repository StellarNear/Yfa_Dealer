package stellarnear.yfa_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jchatron on 04/01/2018.
 */

public class Feat {
    private String name;
    private String type;
    private String descr;
    private String id;
    private Context mC;

    public Feat(String name, String type, String descr, String id, Context mC){
        this.name=name;
        this.type=type;
        this.descr=descr;
        this.id=id;
        this.mC=mC;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescr() {
        return descr;
    }

    public String getId() {
        return id;
    }

    public boolean isActive(){
        boolean active = false;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            active = settings.getBoolean("switch_"+this.id, true);
        } catch ( Exception e) {}
       return active;
    }
}

