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

import stellarnear.yfa_companion.Errors.AllAttributeError;
import stellarnear.yfa_companion.Tools;

/**
 * Created by jchatron on 26/12/2017.
 */

public class AllCapacities extends SelfCustomLog {
    private Context mC;
    private List<Capacity> allCapacities = new ArrayList<>();
    private Map<String, Capacity> mapIdcapacity = new HashMap<>();
    private Tools tools = Tools.getTools();

    public AllCapacities(Context mC) throws AllAttributeError {
        try {
            this.mC = mC;
            buildCapacitiesList();
        } catch (Exception e) {
            throw new AllAttributeError("Error during AllCapacities creation", e);
        }

    }

    private void buildCapacitiesList() throws Exception {
        allCapacities = new ArrayList<>();
        mapIdcapacity = new HashMap<>();

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
                Capacity capacity = new Capacity(
                        readValue("name", element2),
                        readValue("shortname", element2),
                        readValue("type", element2),
                        readValue("descr", element2),
                        readValue("id", element2),
                        readValue("dailyuse", element2),
                        readValue("value", element2),
                        readValue("duration", element2),
                        tools.toBool(readValue("blood_line", element2)),
                        mC);
                allCapacities.add(capacity);
                mapIdcapacity.put(capacity.getId(), capacity);
            }
        }
        is.close();
    }

    public List<Capacity> getAllCapacitiesList() {
        return allCapacities;
    }

    public String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return ""; //certain value are not set for example shortname, duration , daily use etc
        }
    }

    public Capacity getCapacity(String id) {
        Capacity selectedCapa;
        try {
            selectedCapa = mapIdcapacity.get(id);
        } catch (Exception e) {
            selectedCapa = null;
            log.warn("Could not retrieve capacity : "+id);
        }
        return selectedCapa;
    }

    public void reset() throws Exception {
        buildCapacitiesList();
    }

    public boolean capacityIsActive(String id) {
        boolean val = false;
        try {
            val = getCapacity(id).isActive();
        } catch (Exception e) {
            log.warn("Could not check if capacity is active for id : "+id);
        }
        return val;
    }
}
