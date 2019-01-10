package stellarnear.yfa_dealer.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Tools;

/**
 * Created by jchatron on 26/12/2017.
 */

public class Perso {

    private Inventory inventory;
    private AllResources allResources;
    private Tools tools=new Tools();
    private Context mC;
    private SharedPreferences prefs;

    public Perso(Context mC) {
        inventory = new Inventory(mC);
        allResources = new AllResources(mC);
        this.mC=mC;
        this.prefs= PreferenceManager.getDefaultSharedPreferences(mC);
    }

    public void refresh() {
        allResources.refreshMaxs();
    }

    public AllResources getAllResources() {
        return this.allResources;
    }

    public Integer getResourceValue(String resId){
        Resource res = allResources.getResource(resId);
        Integer value=0;
        if(res!=null){
            value=res.getCurrent();
        }
        return value;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void castConvSpell(Integer selected_rank) {
        allResources.castConvSpell(selected_rank);
    }
    public void castSpell(Integer selected_rank) {
        allResources.castSpell(selected_rank);
    }

    public int getCharismaMod() {
        return  tools.toInt(prefs.getString("charisme",String.valueOf(mC.getResources().getInteger(R.integer.charisme_def))));
    }

    public int getStrMod() {
        return tools.toInt(prefs.getString("force",String.valueOf(mC.getResources().getInteger(R.integer.force_def))));
    }

    public int getDexMod() {
        return tools.toInt(prefs.getString("dexterite",String.valueOf(mC.getResources().getInteger(R.integer.dexterite_def))));
    }

    public int getCasterLevel() {
        int val=0;
        val+= tools.toInt(prefs.getString("nls_val",String.valueOf(mC.getResources().getInteger(R.integer.nls_val_def))));
        if (prefs.getBoolean("karma_switch",false)){
            val+=4;
        }
        if (prefs.getBoolean("ioun_stone",true)){
            val+=1;
        }
        val+=tools.toInt(prefs.getString("NLS_bonus",String.valueOf(0)));
        return val;
    }


}
