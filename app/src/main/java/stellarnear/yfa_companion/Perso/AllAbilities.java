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

import stellarnear.yfa_companion.Tools;


/**
 * Created by jchatron on 05/01/2018.
 */

public class AllAbilities {

    private Map<String, Ability> mapIDAbi = new HashMap<>();
    private List<Ability> listAbilities= new ArrayList<>();
    private Context mC;
    private Tools tools=new Tools();

    public AllAbilities(Context mC) {
        this.mC = mC;
        buildAbilitiesList();
        refreshAllAbilities();
    }

    private void buildAbilitiesList() {
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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int resId = mC.getResources().getIdentifier( key.toLowerCase() + "_def", "integer", mC.getPackageName());
        return tools.toInt(settings.getString( key.toLowerCase(), String.valueOf(mC.getResources().getInteger(resId))));
    }

    public void refreshAllAbilities() {
        for (Ability abi : listAbilities) {
            //(abiKey.equals("FOR") && allStances.getCurrentStance()!=null && allStances.getCurrentStance().getId().equals("bear") pour test les stance en meme temps
            int val = 0;
            if (abi.getId().equalsIgnoreCase("ability_ca")) {
                val = 10+readAbility("ability_ca_stuff") + getAbi("ability_dexterite").getMod() + ((int) (readAbility("ability_lvl")/4.0))+ getAbi("ability_sagesse").getMod();
            } else if (abi.getId().equalsIgnoreCase("ability_bmo")) {
                val = readAbility("ability_lvl") + getAbi("ability_force").getMod();
            } else if (abi.getId().equalsIgnoreCase("ability_dmd")) {
                val = readAbility("ability_lvl") + getAbi("ability_force").getMod() + 10 + getAbi("ability_dexterite").getMod() + ((int) (readAbility("ability_lvl")/4.0))+ getAbi("ability_sagesse").getMod() ;
            } else if (abi.getId().equalsIgnoreCase("ability_init")) {
                val = getAbi("ability_dexterite").getMod();
            } else if (abi.getId().equalsIgnoreCase("ability_rm")) {
                val = readAbility("ability_lvl") + 10;
            } else if (abi.getId().equalsIgnoreCase("ability_equipment")) {
                //on laisse à 0 le nombre de piece de stuff est calculer au niveau du perso
            } else {
                val = readAbility(abi.getId());
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
}
