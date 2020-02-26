package stellarnear.yfa_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.yfa_companion.Tools;


/**
 * Created by jchatron on 05/01/2018.
 */

public class AllAbilities {

    private Map<String, Ability> mapIDAbi = new HashMap<>();
    private List<Ability> listAbilities= new ArrayList<>();
    private Context mC;
    private Tools tools=Tools.getTools();
    private Inventory inventory;

    public AllAbilities(Context mC,Inventory inventory) {
        this.mC = mC;
        this.inventory=inventory;
        buildAbilitiesList();
        refreshAllAbilities();
    }

    private void buildAbilitiesList() {
        mapIDAbi = new HashMap<>();
        listAbilities= new ArrayList<>();
        try {
            InputStream is = mC.getAssets().open("abilities.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("ability");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Ability abi = new Ability(
                            readValue("name", element2),
                            readValue("shortname", element2),
                            readValue("type", element2),
                            readValue("descr", element2),
                            tools.toBool(readValue("testable", element2)),
                            tools.toBool(readValue("focusable", element2)),
                            readValue("id", element2),
                            mC);
                    listAbilities.add(abi);
                    mapIDAbi.put(abi.getId(), abi);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }

    private int readAbility(String key) {
        int val=0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int resId = mC.getResources().getIdentifier( key.toLowerCase() + "_def", "integer", mC.getPackageName());
        try {
            val=tools.toInt(settings.getString( key.toLowerCase(), String.valueOf(mC.getResources().getInteger(resId))));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public void refreshAllAbilities() {
        for (Ability abi : listAbilities) {
            int val = 0;
            List<String> allBasicAbi = Arrays.asList("ability_force","ability_dexterite","ability_constitution","ability_sagesse","ability_intelligence","ability_charisme");
            if(allBasicAbi.contains(abi.getId())) {
                val = readAbility(abi.getId()+"_base"); //on prend que la valeur de base + augement perma le reste est faut au niveau du perso avec le stuff
                val += readAbility(abi.getId()+"_augment");
                if((abi.getId().equalsIgnoreCase("ability_constitution") || abi.getId().equalsIgnoreCase("ability_charisme"))&& inventory.getAllEquipments().testIfNameItemIsEquipped("Glaedäyes Ah-Yaedeyes")){
                 val+=2;
                }
            }  else {
                val = readAbility(abi.getId());
            }

            if(abi.getId().equalsIgnoreCase("ability_rm")){
                int valRMInvent = inventory.getAllEquipments().getAbiBonus(abi.getId());
                if(valRMInvent>val){
                    val=valRMInvent;
                }
            } else {
                val += inventory.getAllEquipments().getAbiBonus(abi.getId());
            }
            abi.setValue(val);
        }
    }

    public List<Ability> getAbilitiesList(String... type){
        String typeSelected = type.length > 0 ? type[0] : "";  //parametre optionnel type
        List<Ability> list= new ArrayList<>();
        if (typeSelected.equalsIgnoreCase("base")
                ||typeSelected.equalsIgnoreCase("general")
                ||typeSelected.equalsIgnoreCase("def")
                ||typeSelected.equalsIgnoreCase("advanced")){
            for(Ability abi : listAbilities){
                if(abi.getType().equalsIgnoreCase(typeSelected)){
                    list.add(abi);
                }
            }
        } else {
            list=listAbilities;
        }
        return list;
    }

    public Ability getAbi(String abiId){
        Ability selecteAbi;
        try {
            selecteAbi=mapIDAbi.get(abiId.toLowerCase());
        } catch (Exception e){  selecteAbi=null;  }
        return selecteAbi;
    }

    public void reset() {
        buildAbilitiesList();
        refreshAllAbilities();
    }
}
