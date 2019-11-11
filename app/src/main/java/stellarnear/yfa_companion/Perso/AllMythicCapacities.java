package stellarnear.yfa_companion.Perso;

import android.content.Context;

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

/**
 * Created by jchatron on 26/12/2017.
 */

public class AllMythicCapacities {
    private Context mC;
    private List<MythicCapacity> allMythicCapacities = new ArrayList<>();
    private Map<String,MythicCapacity> mapIdMythiccapacity =new HashMap<>();

    public AllMythicCapacities(Context mC)
    {
        this.mC = mC;
        buildKiCapacitiesList();
    }

    private void buildKiCapacitiesList() {
        try {
            InputStream is = mC.getAssets().open("mythiccapacities.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("mythiccapacity");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    MythicCapacity MythicCapacity=new MythicCapacity(
                            readValue("name", element2),
                            readValue("descr", element2),
                            readValue("type", element2),
                            readValue("id", element2),
                            mC);
                    allMythicCapacities.add(MythicCapacity);
                    mapIdMythiccapacity.put(MythicCapacity.getId(),MythicCapacity);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MythicCapacity> getAllMythicCapacitiesList(){
        return allMythicCapacities;
    }

    public MythicCapacity getMythiccapacity(String mythiccapacitytId) {
        MythicCapacity selectedMythicCapacity;
        try {
            selectedMythicCapacity= mapIdMythiccapacity.get(mythiccapacitytId);
        } catch (Exception e){  selectedMythicCapacity=null;  }
        return selectedMythicCapacity;
    }

    public String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e){
            return "";
        }
    }
}
