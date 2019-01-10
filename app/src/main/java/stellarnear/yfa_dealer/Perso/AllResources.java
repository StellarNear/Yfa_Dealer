package stellarnear.yfa_dealer.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.yfa_dealer.Tools;

/**
 * Created by jchatron on 02/02/2018.
 */

public class AllResources {
    private Context mC;
    private Map<String, Resource> mapIDRes = new HashMap<>();
    private List<Resource> listResources = new ArrayList<>();
    private SharedPreferences settings;
    private Tools tools = new Tools();

    public AllResources(Context mC) {
        this.mC = mC;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        buildResourcesList();
        refreshMaxs();
        loadCurrent();
    }

    private void buildResourcesList() {
        Resource resMyth = new Resource("Points mythiques","mythic_points",mC);
        listResources.add(resMyth);
        mapIDRes.put(resMyth.getId(), resMyth);

        for (int rank=1;rank<=15;rank++){
            Resource rankRes = new Resource("Sort disponible rang "+rank,"spell_rank_"+rank,mC);
            listResources.add(rankRes);
            mapIDRes.put(rankRes.getId(), rankRes);
        }
        for (int rankConv=1;rankConv<=6;rankConv++){
            Resource rankRes = new Resource("Sort convertible disponible rang "+rankConv,"spell_conv_rank_"+rankConv,mC);
            listResources.add(rankRes);
            mapIDRes.put(rankRes.getId(), rankRes);
        }

        Resource strike = new Resource("Coup au but","true_strike",mC);
        listResources.add(strike);
        mapIDRes.put(strike.getId(), strike);
    }

    public List<Resource> getResourcesList() {
        return listResources;
    }

    public Resource getResource(String resourceId) {
        Resource selectedResource;
        try {
            selectedResource = mapIDRes.get(resourceId);
        } catch (Exception e) {
            selectedResource = null;
        }
        return selectedResource;
    }
    private int readResource(String key) {
        int resId = mC.getResources().getIdentifier(key.toLowerCase() + "_def", "integer", mC.getPackageName());
        return tools.toInt(settings.getString(key.toLowerCase(), String.valueOf(mC.getResources().getInteger(resId))));
    }

    public void refreshMaxs() {
        //partie from setting
        getResource("mythic_points").setMax(3+2*readResource("mythic_tier"));
        for (int rank=1;rank<=15;rank++){
            getResource("spell_rank_"+rank).setMax(readResource("n_rank_"+rank));
        }
        for (int rankConv=1;rankConv<=6;rankConv++){
            getResource("spell_conv_rank_"+rankConv).setMax(readResource("n_rank_"+rankConv+"_conv"));
        }

        getResource("true_strike").setMax(0);
    }

    private void loadCurrent() {
        for (Resource res : listResources) {
            res.loadCurrentFromSettings();
        }
    }

    public void sleepReset() {
        for (Resource res : listResources) {
            res.resetCurrent();
        }
    }

    public boolean checkSpellAvailable(Integer selected_rank) {
        return selected_rank==0 || getResource("spell_rank_"+selected_rank).getCurrent()>0;
    }

    public boolean checkConvertibleAvailable(Integer selected_rank) {
        boolean bool=false;
        try {
            bool=getResource("spell_conv_rank_"+selected_rank).getCurrent()>0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    public boolean checkAnyConvertibleAvailable() {
        Boolean bool =false;
        for (int rankConv=1;rankConv<=6;rankConv++){
            if(getResource("spell_conv_rank_"+rankConv).getCurrent()>0){ bool=true;break;}
        }
        return bool;
    }

    public void castConvSpell(Integer selected_rank) {
        getResource("spell_conv_rank_"+selected_rank).spend(1);
        getResource("spell_rank_"+selected_rank).spend(1);
    }
    public void castSpell(Integer selected_rank) {
        getResource("spell_rank_"+selected_rank).spend(1);

        if(getResource("spell_conv_rank_"+selected_rank)!=null && (getResource("spell_conv_rank_"+selected_rank).getCurrent()>getResource("spell_rank_"+selected_rank).getCurrent())){
            getResource("spell_conv_rank_"+selected_rank).spend(1);
        }
    }

    public void halfSleepReset() {
        getResource("mythic_points").resetCurrent();
    }
}
