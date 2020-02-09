package stellarnear.yfa_companion.Spells;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Buff;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Tools;

public class BuildMetaList {

    private static BuildMetaList instance = null;
    private MetaList metaList;
    private Tools tools=new Tools();
    private Perso yfa = MainActivity.yfa;

    public static BuildMetaList getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance==null){
            instance = new BuildMetaList(mC);
        }
        return instance;
    }

    public static BuildMetaList getInstance() {  //pour eviter de relire le xml à chaque fois
        return instance;
    }

    public static void resetMetas() {
        instance = null;
    }

    private BuildMetaList(Context mC){
        metaList=new MetaList();
        //construire la liste complete regarder xml parser
        try {
            InputStream is = mC.getAssets().open("metamagic.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("metamagic");

            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    metaList.add(new Metamagic(getValue("id",element2),
                            getValue("name",element2),
                            getValue("descr",element2),
                            tools.toInt(getValue("uprank",element2)),
                            tools.toBool(getValue("multicast",element2))
                    ));
                }
            }
            removeUnavailableMetas(mC);
        } catch (Exception e) {e.printStackTrace();}


    }

    private void removeUnavailableMetas(Context mC) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        MetaList allowedList=new MetaList();
        for(Metamagic meta : metaList.asList()) {
            int defResId = mC.getResources().getIdentifier(meta.getId().toLowerCase() + "_switch_def", "bool", mC.getPackageName());
            boolean active = settings.getBoolean(meta.getId().toLowerCase() + "_switch", mC.getResources().getBoolean(defResId));
            if (active) {
                allowedList.add(meta);
            }
        }
        metaList=allowedList;
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
        MetaList metaList = new MetaList(this.metaList); //pour que chaque sort ai sa version de la métalist
        Buff parangon = yfa.getAllBuffs().getBuffByID("parangon_tempfeat");
        if(parangon!=null && parangon.isActive() && parangon.getTempFeat().equalsIgnoreCase("tempfeat_magic_echo")){
            metaList.add(0,new Metamagic("meta_echo","Écho magique","Peut le lancer le sort une seconde fois dans la même journée. Aucun effet permettant au personnage de préparer à nouveau ou de relancer un sort n'est utilisable avec Écho magique. La seconde incantation ne nécessite pas de dépenser un emplacement de sort utilisable. Un écho magique utilise un emplacement de sort de trois niveaux de plus que le niveau réel du sort.",3,true));
        }
        return metaList;
    }
}
