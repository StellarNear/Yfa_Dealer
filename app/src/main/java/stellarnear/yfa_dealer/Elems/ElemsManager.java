package stellarnear.yfa_dealer.Elems;

import java.util.ArrayList;
import java.util.List;

import stellarnear.yfa_dealer.R;

public class ElemsManager {
    private static ElemsManager instance;

    private List<Elem> listElems;

    public ElemsManager(){
        listElems=new ArrayList<>();
        listElems.add(new Elem("aucun","aucun",R.color.aucun,R.color.aucun_dark,R.drawable.none_logo));
        listElems.add(new Elem("acide","acide",R.color.acide,R.color.acide_dark,R.drawable.acid_logo));
        listElems.add(new Elem("feu","feu",R.color.feu,R.color.feu_dark,R.drawable.fire_logo));
        listElems.add(new Elem("foudre","foudre",R.color.foudre,R.color.foudre_dark,R.drawable.shock_logo));
        listElems.add(new Elem("froid","froid",R.color.froid,R.color.froid_dark,R.drawable.frost_logo));
    }

    public static ElemsManager getInstance() {
        if (instance==null){instance=new ElemsManager();}
        return instance;
    }

    public List<Elem> getElems(){
        return this.listElems;
    }

    public List<String> getListKeys() {
        List <String> list=new ArrayList<>();
        for(Elem elem: listElems){
            list.add(elem.getKey());
        }
        return list;
    }

    public String getName(String elemKey) {
        return getElementByKey(elemKey).getName();
    }

    public int getColorId(String elemKey) {
        return getElementByKey(elemKey).getColorId();
    }

    public int getColorIdSombre(String elemKey){
        return getElementByKey(elemKey).getColorIdRecent();
    }

    private Elem getElementByKey(String elemKey) {
        Elem res=null;
        for (Elem elem : listElems){
            if(elem.getKey().equalsIgnoreCase(elemKey)){
                res=elem;
            }
        }
        return res;
    }

    public int getDrawableId(String key) {
        return getElementByKey(key).getDrawableId();
    }
}
