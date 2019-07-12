package stellarnear.yfa_dealer.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.yfa_dealer.Calculation;
import stellarnear.yfa_dealer.Elems.ElemsManager;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;

public class DamagesShortListElement {
    private String element;
    private int dmgSum;
    private int nMeta;
    private int rank;

    public DamagesShortListElement(Spell spell){
        this.element=spell.getDmg_type();
        this.dmgSum=spell.getDmgResult();
        this.rank=new Calculation().currentRank(spell);
        this.nMeta=this.rank-spell.getRank();
    }

    public String getElement() {
        return element;
    }

    public int getDmgSum() {
        return dmgSum;
    }

    public int getRank() {
        return rank;
    }

    public int getnMeta() {
        return nMeta;
    }
}
