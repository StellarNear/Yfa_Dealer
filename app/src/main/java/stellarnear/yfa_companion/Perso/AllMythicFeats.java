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
import stellarnear.yfa_companion.Log.SelfCustomLog;

/**
 * Created by jchatron on 26/12/2017.
 */
public class AllMythicFeats extends SelfCustomLog {
    private Context mC;
    private List<MythicFeat> allMythicFeatsList = new ArrayList<>();
    private Map<String, MythicFeat> mapIdMythicFeat = new HashMap<>();

    public AllMythicFeats(Context mC) throws AllAttributeError {
        try {
            this.mC = mC;
            buildFeatsList();
        } catch (Exception e) {
            throw new AllAttributeError("Error during AllMythicFeats creation", e);
        }
    }

    private void buildFeatsList() throws Exception {
        allMythicFeatsList = new ArrayList<>();
        mapIdMythicFeat = new HashMap<>();
        InputStream is = mC.getAssets().open("mythicfeats.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        Element element = doc.getDocumentElement();
        element.normalize();

        NodeList nList = doc.getElementsByTagName("feat");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element2 = (Element) node;
                MythicFeat feat = new MythicFeat(
                        readValue("name", element2),
                        readValue("type", element2),
                        readValue("descr", element2),
                        readValue("id", element2),
                        mC);
                allMythicFeatsList.add(feat);
                mapIdMythicFeat.put(feat.getId(), feat);
            }
        }
        is.close();
    }

    public List<MythicFeat> getMythicFeatsList() {
        return allMythicFeatsList;
    }

    public MythicFeat getMythicFeat(String featId) {
        MythicFeat selectedFeat;
        try {
            selectedFeat = mapIdMythicFeat.get(featId);
        } catch (Exception e) {
            selectedFeat = null;
            log.warn("Could not retrieve "+featId);
        }
        return selectedFeat;
    }

    public String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            return ""; //some feat don't have ID (only utility feat for example)
        }
    }

    public void reset() throws Exception {
        buildFeatsList();
    }
}
