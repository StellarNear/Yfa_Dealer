package stellarnear.yfa_companion;

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

import stellarnear.yfa_companion.Log.SelfCustomLog;

/**
 * Created by jchatron on 16/02/2018.
 */

public class AllGeneralHelpInfos extends SelfCustomLog {
    List<GeneralHelpInfo> listGeneralHelpInfos;

    public AllGeneralHelpInfos(Context mC) throws Exception {
        listGeneralHelpInfos = new ArrayList<>();

        InputStream is = mC.getAssets().open("help.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        Element element = doc.getDocumentElement();
        element.normalize();

        NodeList nList = doc.getElementsByTagName("help");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element2 = (Element) node;
                GeneralHelpInfo help = new GeneralHelpInfo(
                        readValue("name", element2),
                        readValue("descr", element2),
                        readValue("id", element2));
                listGeneralHelpInfos.add(help);
            }
        }
        is.close();
    }

    private String readValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e) {
            log.warn("Could not retrieve value for : "+tag);
            return "";
        }
    }

    public List<GeneralHelpInfo> getListGeneralHelpInfos() {
        return listGeneralHelpInfos;
    }
}
