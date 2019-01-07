package stellarnear.yfa_dealer.Spells;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.yfa_dealer.Tools;

public class BuildMetaList {
    private MetaList metaList;

    private Tools tools=new Tools();

    public BuildMetaList(Context mC){
        metaList=new MetaList();

        //construire la liste complete regarder xml parser
        try {
            InputStream is = mC.getAssets().open("metamagic.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("spell");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    metaList.add(new Metamagic(getValue("id",element2),
                            getValue("name",element2),
                            getValue("descr",element2),
                            tools.toInt(getValue("uprank",element2)),
                            tools.toBool(getValue("multicast",element2)),
                            false));
                }
            }

        } catch (Exception e) {e.printStackTrace();}
    }

    private String getValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = nodeList.item(0);
            return node.getNodeValue();
        } catch (Exception e){
            return "";
        }
    }

    public MetaList getMetaList() {
        return metaList;
    }
}
