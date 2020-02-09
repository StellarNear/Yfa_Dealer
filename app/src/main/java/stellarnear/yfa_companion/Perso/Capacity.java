package stellarnear.yfa_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jchatron on 26/12/2017.
 */

public class Capacity {
    private String name;
    private String shortname;
    private String type;
    private String descr;
    private String id;
    private Context mC;
    private String dailyUseString;
    private int dailyUse=0;
    private boolean infinite=false;
    private boolean bloodLine=false;
    private String valueString;
    private String duration;
    private int value=0;



    public Capacity(String name,String shortname, String type, String descr, String id,String dailyUse,String valueString,String duration,Boolean bloodLine,Context mC){
        this.name=name;
        this.shortname=shortname;
        this.type=type;
        this.descr=descr;
        this.valueString=valueString;
        this.id=id;
        this.mC=mC;
        this.dailyUseString = dailyUse;
        if(dailyUse.equalsIgnoreCase("infinite")){
            this.infinite=true;
        }
        this.duration=duration;
        this.bloodLine=bloodLine;
    }

    public void setDailyUse(int dailyUse) {
        this.dailyUse = dailyUse;
    }

    public String getDailyUseString() {
        return dailyUseString;
    }

    public String getValueString() {
        return valueString;
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
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

    public boolean isInfinite() {
        return infinite;
    }

    public int getDailyUse() {
        return dailyUse;
    }

    public boolean isActive(){
        boolean active = false;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            active = settings.getBoolean("switch_"+this.id, true);
        } catch ( Exception e) {}
        return active;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        this.value=val;
    }

    public String getDuration() {
        return this.duration;
    }

    public boolean isFromBloodLine() {
        return bloodLine;
    }
}
