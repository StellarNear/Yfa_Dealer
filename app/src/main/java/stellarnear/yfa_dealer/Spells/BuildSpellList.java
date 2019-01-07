package stellarnear.yfa_dealer.Spells;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.yfa_dealer.Tools;


public class BuildSpellList extends AppCompatActivity {

    public SpellList allSpells = new SpellList();
    private Tools tools=new Tools();

    public BuildSpellList(Context mC, String mode){

    //construire la liste complete regarder xml parser

        try {
            InputStream is = mC.getAssets().open("spells"+mode+".xml");

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
                    allSpells.add(new Spell(getValue("id",element2),
                                            getValue("name",element2),
                                            getValue("descr",element2),
                                            tools.toInt(getValue("n_sub_spell",element2)),
                                            getValue("dice_type",element2),
                                            tools.toDouble(getValue("n_dice_per_lvl",element2)),
                                            tools.toInt(getValue("cap_dice",element2)),
                                            getValue("dmg_type",element2),
                                            getValue("range",element2),
                                            getValue("cast_time",element2),
                                            getValue("duration",element2),
                                            getValue("compo",element2),
                                            getValue("rm",element2),
                                            getValue("save_type",element2),
                                            tools.toInt(getValue("rank",element2)),
                                            mC));
                }
            }

        } catch (Exception e) {e.printStackTrace();}

    }

    public SpellList getSpellList(){
        return allSpells;
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

    //methode pour recuperaer une liste de nom basé sur un rank en bouclant sur les element de la liste Allspells
}
