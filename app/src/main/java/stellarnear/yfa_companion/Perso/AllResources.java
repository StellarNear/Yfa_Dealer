package stellarnear.yfa_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.yfa_companion.Spells.SpellsRanksManager;
import stellarnear.yfa_companion.Tools;

/**
 * Created by jchatron on 02/02/2018.
 */

public class AllResources {
    private Context mC;
    private AllAbilities allAbilities;
    private AllMythicCapacities allMythicCapacities;
    private Map<String, Resource> mapIDRes = new HashMap<>();
    private List<Resource> listResources = new ArrayList<>();
    private SpellsRanksManager rankManager=null;
    private SharedPreferences settings;
    private Tools tools = new Tools();

    public AllResources(Context mC,AllAbilities allAbilities,AllMythicCapacities allMythicCapacities) {
        this.mC = mC;
        this.allAbilities=allAbilities;
        this.allMythicCapacities=allMythicCapacities;
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        buildResourcesList();
        refreshMaxs();
        loadCurrent();
    }

    private void buildResourcesList() {
        listResources = new ArrayList<>();
        try {
            InputStream is = mC.getAssets().open("resources.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("resource");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Resource res = new Resource(
                            readValue("name", element2),
                            readValue("shortname", element2),
                            tools.toBool(readValue("testable", element2)),
                            tools.toBool(readValue("hide", element2)),
                            readValue("id", element2),
                            mC);
                    listResources.add(res);
                    mapIDRes.put(res.getId(), res);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rankManager=new SpellsRanksManager(mC);
        rankManager.setRefreshEventListener(new SpellsRanksManager.OnHighTierChange() {
            @Override
            public void onEvent() {
                buildResourcesList();
            }
        });
        for (Resource res : rankManager.getSpellTiers()){
            listResources.add(res);
            mapIDRes.put(res.getId(), res);
        }
        for (Resource convRes : rankManager.getSpellConvTiers()){
            listResources.add(convRes);
            mapIDRes.put(convRes.getId(), convRes);
        }

        Resource strike = new Resource("Coup au but","Coup But",false,false,"res_true_strike",mC);
        listResources.add(strike);
        mapIDRes.put(strike.getId(), strike);

        Resource display_spell = new Resource("Rang de sorts","Sorts",false,false,"resource_display_rank",mC);
        listResources.add(display_spell);
        mapIDRes.put(display_spell.getId(), display_spell);

        Resource display_spell_conv = new Resource("Rang de sorts convertibles","Sorts Conv.",false,false,"resource_display_rank_conv",mC);
        listResources.add(display_spell_conv);
        mapIDRes.put(display_spell_conv.getId(), display_spell_conv);
    }

    public String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }


    public List<Resource> getResourcesList() {
        return listResources;
    }

    public List<Resource> getResourcesListDisplay() {
        List<Resource> list = new ArrayList<>();

        for (Resource res : listResources) {
            if (!res.isHidden()) {
                list.add(res);
            }
        }
        return list;
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
        int hpPool = readResource("resource_hp_base");
        hpPool += 3*readResource("mythic_tier"); //archimage
        hpPool += allAbilities.getAbi("ability_constitution").getMod()*allAbilities.getAbi("ability_lvl").getValue();

        getResource("resource_hp").setMax(hpPool);
        getResource("resource_regen").setMax(readResource("resource_regen"));
        getResource("resource_heroic").setMax(readResource("resource_heroic"));
        getResource("resource_mythic_points").setMax(3+2*readResource("mythic_tier"));

        rankManager.refreshMax();
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
        if(allMythicCapacities.getMythiccapacity("mythiccapacity_recover").isActive()){
            getResource("resource_hp").fullHeal();
        }
    }

    public void halfSleepReset() {
        getResource("resource_mythic_points").resetCurrent();
        if(allMythicCapacities.getMythiccapacity("mythiccapacity_recover").isActive()){
            getResource("resource_hp").fullHeal();
        }
    }

    public boolean checkSpellAvailable(Integer selected_rank) {
        return selected_rank==0 || (getResource("spell_rank_"+selected_rank)!=null && getResource("spell_rank_"+selected_rank).getCurrent()>0);
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

    public void resetRessources(){
        buildResourcesList();
    }

    public void refresh() {
        rankManager.refreshRanks();
        refreshMaxs();
    }

    public SpellsRanksManager getRankManager() {
        return rankManager;
    }
}
