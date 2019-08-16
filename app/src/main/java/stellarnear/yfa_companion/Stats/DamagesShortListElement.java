package stellarnear.yfa_companion.Stats;

import stellarnear.yfa_companion.Calculation;
import stellarnear.yfa_companion.Spells.Spell;

public class DamagesShortListElement {
    private String element;
    private int dmgSum;
    private int nMeta;
    private int rank;
    private int arcaneConvRank;
    private boolean mythic;

    public DamagesShortListElement(Spell spell){
        this.element=spell.getDmg_type();
        this.dmgSum=spell.getDmgResult();
        this.rank=new Calculation().currentRank(spell);
        this.nMeta=this.rank-spell.getRank();
        this.arcaneConvRank=spell.getConversion().getRank();
        this.mythic=spell.isMyth();
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

    public int getArcaneConvRank() {
        return arcaneConvRank;
    }

    public boolean isMythic() {
        return mythic;
    }

    public void addBindedSpell(Spell spell) {
        this.dmgSum+=spell.getDmgResult();
    }
}
