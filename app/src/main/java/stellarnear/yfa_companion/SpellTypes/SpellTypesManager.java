package stellarnear.yfa_companion.SpellTypes;

import java.util.ArrayList;
import java.util.List;

import stellarnear.yfa_companion.R;

public class SpellTypesManager {
    private static SpellTypesManager instance;

    private List<Type> listTypes;

    public SpellTypesManager(){
        listTypes =new ArrayList<>();
        listTypes.add(new Type("none","aucun",R.color.aucun,R.color.aucun_dark,R.drawable.none_logo));
        listTypes.add(new Type("acid","acide",R.color.acide,R.color.acide_dark,R.drawable.acid_logo));
        listTypes.add(new Type("fire","feu",R.color.feu,R.color.feu_dark,R.drawable.fire_logo));
        listTypes.add(new Type("shock","foudre",R.color.foudre,R.color.foudre_dark,R.drawable.shock_logo));
        listTypes.add(new Type("frost","froid",R.color.froid,R.color.froid_dark,R.drawable.frost_logo));
        listTypes.add(new Type("","inoffensif",R.color.no_dmg,R.color.no_dmg_dark,R.drawable.nodmg_logo));
    }

    public static SpellTypesManager getInstance() {
        if (instance==null){instance=new SpellTypesManager();}
        return instance;
    }

    public List<Type> getElems(){
        return this.listTypes;
    }

    public List<String> getListDmgKeys() {
        List <String> list=new ArrayList<>();
        for(Type type : listTypes){
            if(type.getKey().equalsIgnoreCase("")){continue;}
            list.add(type.getKey());
        }
        return list;
    }

    public List<String> getListKeys() {
        List <String> list=new ArrayList<>();
        for(Type type : listTypes){
            list.add(type.getKey());
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

    private Type getElementByKey(String elemKey) {
        Type res=null;
        for (Type type : listTypes){
            if(type.getKey().equalsIgnoreCase(elemKey)){
                res= type;
            }
        }
        return res;
    }

    public int getDrawableId(String key) {
        return getElementByKey(key).getDrawableId();
    }
}
