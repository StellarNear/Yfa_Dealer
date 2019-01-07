package stellarnear.yfa_dealer.Spells;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jchatron on 28/11/2017.
 */

public class MetaList extends AppCompatActivity {
    private List<Metamagic> listMeta = new ArrayList<>();

    public MetaList(){
        listMeta = new ArrayList<>();
    }

    public void add(Metamagic metamagic){
        listMeta.add(metamagic);
    }

    public Metamagic getMetaByID(String id){
        Metamagic metaAnswer = null;
        for (Metamagic meta : listMeta){
            if (meta.getId().equalsIgnoreCase(id)){
                metaAnswer=meta;
            }
        }
        return metaAnswer;
    }

    public List<Metamagic> asList(){
        return listMeta;
    }
}
