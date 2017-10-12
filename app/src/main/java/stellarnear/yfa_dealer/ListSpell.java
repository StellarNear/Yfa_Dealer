package stellarnear.yfa_dealer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ListSpell extends AppCompatActivity {

    private Context mContext;
    public List<Spell> AllSpells = new ArrayList<Spell>();
    public ListSpell(Context mC){
        mContext=mC;
    //construire la liste complete regarder xml parser

        try {
            InputStream is = mContext.getAssets().open("spells.xml");

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
                    AllSpells.add(new Spell(getValue("Name",element2),
                            getValue("dice_type",element2),
                            getValue("n_dice",element2),
                            getValue("dmg_type",element2),
                            getValue("descr",element2),
                            Integer.parseInt(getValue("DD",element2)),
                            Boolean.parseBoolean(getValue("RM",element2)),
                            getValue("Save_type",element2),
                            Integer.parseInt(getValue("Save_val",element2)),
                            Integer.parseInt(getValue("rank",element2))));
                }
            }

        } catch (Exception e) {e.printStackTrace();}

    }

    public String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public List<Spell> selectRank(int rank){
        List<Spell> sel_list = new ArrayList<Spell>();

        for(Spell spell : AllSpells){
            if (spell.getRank() == rank) {
                sel_list.add(spell);
            }
        }
        return sel_list;
    }

    //methode pour recuperaer une liste de nom basé sur un rank en bouclant sur les element de la liste Allspells
}
