package stellarnear.yfa_companion.Perso;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import stellarnear.yfa_companion.Spells.BuildSpellList;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Spells.SpellList;
import stellarnear.yfa_companion.TinyDB;
import stellarnear.yfa_companion.Tools;

public class AllBuffs {

    private ArrayList<Buff> listBuffs = null;

    private TinyDB tinyDB;
    private Context mC;
    private Tools tools=new Tools();

    public AllBuffs(Context mC){
        this.mC=mC;
        refreshListBuffs();
    }

    private void refreshListBuffs() {
        listBuffs = new ArrayList<>();
        tinyDB = new TinyDB(mC);
        ArrayList<Buff> listDB = tinyDB.getListBuffs("localSaveListBuffs");
        if (listDB.size() == 0) {
            buildList();
            saveLocalBuffs();
        } else {
            listBuffs = listDB;
        }
    }

    private void saveLocalBuffs() {
        tinyDB.putListBuffs("localSaveListBuffs", listBuffs);
    }

    public void resetBuffsList() {
        refreshListBuffs();
    }


    private void buildList(){  // on construit la liste qu'une fois dans MainActivityFragmentSpell donc pas besoin de singleton
        SpellList allSpells = new SpellList();
        allSpells.add(BuildSpellList.getInstance(mC).getSpellList());
        allSpells.add(getAllBuffSpells());
        List<String> allBuffSpellsNames= Arrays.asList("Coup au but","Peau de pierre","Lien télépathique","Renvoi des sorts","Moment de préscience","Liberté de mouvement","Simulacre de vie supérieur");
        List<String> allBuffPermaSpellsNames= Arrays.asList("Détection de la magie","Détection de l'invisibilité","Don des langues","Lecture de la magie","Vision dans le noir","Vision magique", "Vision des auras","Flou","Echolocalisation","Prémonition","Esprit impénétrable","Bouclier","Résistance","Vision lucide");
        for (Spell spell : allSpells.asList()){
            if(allBuffSpellsNames.contains(spell.getName())){
                listBuffs.add(new Buff(spell,false));
            } else if (allBuffPermaSpellsNames.contains(spell.getName())){
                listBuffs.add(new Buff(spell,true));
            }
        }
    }

    public SpellList getAllBuffSpells() {
        SpellList buffSpells=new SpellList();
        try {
            InputStream is = mC.getAssets().open("buff_spells.xml");

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
                    buffSpells.add(new Spell(getValue("id",element2),
                            getValue("mythic",element2),
                            getValue("normalSpellId",element2),
                            getValue("name",element2),
                            getValue("descr",element2),
                            tools.toInt(getValue("n_sub_spell",element2)),
                            getValue("dice_type",element2),
                            tools.toDouble(getValue("n_dice_per_lvl",element2)),
                            tools.toInt(getValue("cap_dice",element2)),
                            getValue("dmg_type",element2),
                            getValue("range",element2),
                            getValue("contact",element2),
                            getValue("area",element2),
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
        return buffSpells;
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

    public void spendSleepTime() {
        makeTimePass(60*60*8);
    }

    public ArrayList<Buff> getAllPermaBuffs() {
        ArrayList permaBuffs=new ArrayList();
        for (Buff buff : listBuffs){
            if(buff.isPerma()){
                permaBuffs.add(buff);
            }
        }
        return permaBuffs;
    }
    public ArrayList<Buff> getAllTempBuffs() {
        ArrayList tempBuffs=new ArrayList();
        for (Buff buff : listBuffs){
            if(!buff.isPerma()){
                tempBuffs.add(buff);
            }
        }
        return tempBuffs;
    }

    public void makeTimePass(int i) {
        for(Buff buff: listBuffs){
            buff.spendTime(i);
        }
        saveLocalBuffs();
    }

    public void saveBuffs() {
        saveLocalBuffs();
    }

    public boolean buffByIDIsActive(String id) {
        Buff buffAnswer=null;
        for (Buff buff : listBuffs){
            if(buff.getId().equalsIgnoreCase(id)){
                buffAnswer=buff;
                break;
            }
        }
        return buffAnswer!=null && buffAnswer.isActive();
    }

    public Buff getBuffByID(String id) {
        Buff buffAnswer=null;
        for (Buff buff : listBuffs){
            if(buff.getId().equalsIgnoreCase(id)){
                buffAnswer=buff;
                break;
            }
        }
        return buffAnswer;
    }
}
