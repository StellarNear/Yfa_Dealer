package stellarnear.yfa_companion.Perso;

import android.app.Activity;
import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.yfa_companion.Errors.AllAttributeError;
import stellarnear.yfa_companion.TinyDB;
import stellarnear.yfa_companion.Tools;


/**
 * Created by jchatron on 05/01/2018.
 */

public class AllEquipments extends SelfCustomLog {
    private List<Equipment> listEquipments = new ArrayList<>();
    private Context mC;
    private Activity mA;
    private Boolean editable;
    private Tools tools = Tools.getTools();
    private TinyDB tinyDB;
    private DisplayEquipmentManager displayEquipmentManager = null;


    public AllEquipments(Context mC) throws Exception {
        try {
            this.mC = mC;
            refreshEquipment();
        } catch (Exception e) {
            reset();
            throw new AllAttributeError("Error during AllEquipments creation", e);
        }
    }

    private void refreshEquipment() throws Exception {
        tinyDB = new TinyDB(mC);
        List<Equipment> listDB = tinyDB.getListEquipments("localSaveListEquipments");  //on save avec le pjID pour avoir une database differente pour halda
        if (listDB.size() == 0) {
            buildList();
            saveLocalAllEquipments();
        } else {
            listEquipments = listDB;
        }
    }

    private void saveLocalAllEquipments() {
        tinyDB.putListEquipments("localSaveListEquipments", listEquipments); //on save avec le pjID pour avoir une database differente pour halda
    }

    private void buildList() throws Exception {
        listEquipments = new ArrayList<>();
        displayEquipmentManager = null;
        InputStream is = mC.getAssets().open("equipment.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);
        Element element = doc.getDocumentElement();
        element.normalize();
        NodeList nList = doc.getElementsByTagName("equipment");
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element2 = (Element) node;
                Equipment equi = new Equipment(
                        readValue("name", element2),
                        readValue("descr", element2),
                        readValue("value", element2),
                        readValue("imgId", element2),
                        readValue("slotId", element2),
                        tools.toBool(readValue("equipped", element2)),
                        tools.toInt(readValue("armor", element2)));
                if (equi.getSlotId().equalsIgnoreCase("armor_slot")) {
                    setupCapDex(equi, element2);
                }  // pour les pieces d'armure
                equi.setAbilityUp(element2);
                equi.setSkillUp(element2);
                listEquipments.add(equi);
            }
        }
        is.close();
    }

    private void setupCapDex(Equipment equipment, Element element) {
        String valMaxDexModTxt = "";
        try {
            NodeList nodeList = element.getElementsByTagName("maxDexMod").item(0).getChildNodes();
            Node node = nodeList.item(0);
            valMaxDexModTxt = node.getNodeValue();
        } catch (Exception e) {
            valMaxDexModTxt = "";
            log.warn("Could not retrieve maxDexMod for " + equipment.getName());
        }

        if (!valMaxDexModTxt.equalsIgnoreCase("")) {
            equipment.setMaxDexMod(tools.toInt(valMaxDexModTxt));
        }
    }

    private String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return ""; //some equipment don't have all type of value imgID etc
        }
    }

    public DisplayEquipmentManager getDisplayManager() {
        if (displayEquipmentManager == null) {
            displayEquipmentManager = new DisplayEquipmentManager(mA, mC, listEquipments);
            displayEquipmentManager.setSaveEventListener(new DisplayEquipmentManager.OnSaveEventListener() {
                @Override
                public void onEvent() {
                    saveLocalAllEquipments();
                }
            });
        }
        return displayEquipmentManager;
    }

    public boolean testIfNameItemIsEquipped(String nameItem) {
        boolean val = false;
        for (Equipment equi : getListAllEquipmentsEquiped()) {
            if (equi.getName().equalsIgnoreCase(nameItem)) {
                val = true;
            }
        }
        return val;
    }

    public int getAllEquipmentsSize() {
        return listEquipments.size();
    }

    public List<Equipment> getListAllEquipmentsEquiped() {
        List<Equipment> list = new ArrayList<>();
        for (Equipment equipment : listEquipments) {
            if (equipment.isEquiped()) {
                list.add(equipment);
            }
        }
        return list;
    }

    public Equipment getEquipmentsEquiped(String slot) {
        Equipment equiFind = null;
        for (Equipment equipment : listEquipments) {
            if (equipment.isEquiped() && equipment.getSlotId().equalsIgnoreCase(slot)) {
                equiFind = equipment;
            }
        }
        return equiFind;
    }

    public boolean hasArmorDexLimitation() {
        Equipment armor = getEquipmentsEquiped("armor_slot");
        boolean val = false;
        try { //les vieux objet n'ont pas le limited max def donc ca fera un test sur null
            if (armor != null && armor.isLimitedMaxDex()) {
                val = true;
            }
        } catch (Exception e) {
            log.warn("Could not test armorDexLimit for " + armor.getName());
        }
        return val;
    }

    public int getArmorDexLimitation() {
        Equipment armor = getEquipmentsEquiped("armor_slot");
        int val = 999; // pas limité
        if (armor != null && armor.isLimitedMaxDex()) {
            val = armor.getMaxDexMod();
        }
        return val;
    }

    public void reset() throws Exception {
        buildList();
        saveLocalAllEquipments();
    }

    public void loadFromSave() throws Exception {
        refreshEquipment();
    }

    public int getArmorBonus() {
        int val = 0;
        for (Equipment equi : getListAllEquipmentsEquiped()) {
            val += equi.getArmor();
        }
        return val;
    }

    public int getSkillBonus(String skillId) {
        int val = 0;
        for (Equipment equipment : getListAllEquipmentsEquiped()) {
            try {
                val += equipment.getMapSkillUp().get(skillId);
            } catch (Exception e) {
                //no bonus for this skill
            }
        }
        return val;
    }

    public int getAbiBonus(String abiId) {
        int val = 0;
        for (Equipment equipment : getListAllEquipmentsEquiped()) {
            try {
                int valBonusEqui = equipment.getMapAbilityUp().get(abiId);
                if (val < valBonusEqui) {  //bonus d'altération max uniquement pas de cumul
                    val = valBonusEqui;
                }
            } catch (Exception e) {
                //no bonus for this ability
            }
        }
        return val;
    }
}

