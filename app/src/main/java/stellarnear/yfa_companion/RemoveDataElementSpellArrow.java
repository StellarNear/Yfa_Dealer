package stellarnear.yfa_companion;

import stellarnear.yfa_companion.Spells.Spell;


public class RemoveDataElementSpellArrow {
    private String targetSheet= "spell_arrow";
    private String date="-";
    private String uuid ="-";
    private String typeEvent="remove_arrow_spell";
    private String caster="Yfa";
    private String result="-";

    public RemoveDataElementSpellArrow(PairSpellUuid pairSpellUuid) {
        this.uuid=pairSpellUuid.getUuid();
    }

    public String getCaster() {
        return caster;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public String getTargetSheet() {
        return targetSheet;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

    public class  PairSpellUuid{
        private Spell spell;
        private String uuid;

        private PairSpellUuid(Spell spell,String uuid){
            this.spell=spell;
            this.uuid=uuid;
        }

        public Spell getSpell() {
            return spell;
        }

        public String getUuid() {
            return uuid;
        }
    }

}

