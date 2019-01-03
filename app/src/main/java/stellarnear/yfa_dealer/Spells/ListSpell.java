package stellarnear.yfa_dealer.Spells;

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
    public List<Spell> allSpells = new ArrayList<Spell>();
    public ListSpell(Context mC,String mode){
        mContext=mC;
    //construire la liste complete regarder xml parser

        try {
            InputStream is = mContext.getAssets().open("spells"+mode+".xml");

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
                    allSpells.add(new Spell(getValue("ID",element2),
                                            getValue("name",element2),
                                            getValue("descr",element2),
                                            getValue("dice_type",element2),
                                            to_double(getValue("n_dice_per_lvl",element2)),
                                            to_int(getValue("cap_dice",element2)),
                                            getValue("dmg_type",element2),
                                            getValue("range",element2),
                                            getValue("cast_time",element2),
                                            getValue("duration",element2),
                                            getValue("compo",element2),
                                            getValue("rm",element2),
                                            getValue("save_type",element2),
                                            to_int(getValue("rank",element2)),
                                            mC));
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

    public List<Spell> selectRank(int rank){
        List<Spell> sel_list = new ArrayList<Spell>();

        for(Spell spell : allSpells){
            if (spell.getRank() == rank) {
                sel_list.add(spell);
            }
        }
        return sel_list;
    }

    public Spell getSpellByID(String name){
        Spell spellAnswer = null;
        for (Spell spell : allSpells){
            if (spell.getID().equalsIgnoreCase(name)){
                spellAnswer=spell;
            }
        }
        return spellAnswer;
    }

    public Integer to_int(String key){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            value=0;
        }
        return value;
    }
    public Double to_double(String key){
        Double value;
        try {
            value = Double.parseDouble(key);
        } catch (Exception e){
            value=0.0;
        }
        return value;
    }

    public Boolean to_bool(String key){
        Boolean value;
        try {
            value = Boolean.parseBoolean(key);
        } catch (Exception e){
            value=false;
        }
        return value;
    }
    //methode pour recuperaer une liste de nom basé sur un rank en bouclant sur les element de la liste Allspells
}