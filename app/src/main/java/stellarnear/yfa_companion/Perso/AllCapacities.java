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

public class AllCapacities {
    private Context mC;
    private List<Capacity> allCapacities = new ArrayList<>();
    private Map<String,Capacity> mapIdcapacity =new HashMap<>();

    public AllCapacities(Context mC)
    {
        this.mC = mC;
        buildKiCapacitiesList();
    }

    private void buildKiCapacitiesList() {
        try {
            InputStream is = mC.getAssets().open("capacities.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("capacity");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    Capacity Capacity=new Capacity(
                            readValue("name", element2),
                            readValue("descr", element2),
                            readValue("type", element2),
                            readValue("id", element2));
                    allCapacities.add(Capacity);
                    mapIdcapacity.put(Capacity.getId(),Capacity);
                }
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Capacity> getAllCapacitiesList(){
        return allCapacities;
    }

    public Capacity getcapacity(String capacitytId) {
        Capacity selectedCapacity;
        try {
            selectedCapacity= mapIdcapacity.get(capacitytId);
        } catch (Exception e){  selectedCapacity=null;  }
        return selectedCapacity;
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
