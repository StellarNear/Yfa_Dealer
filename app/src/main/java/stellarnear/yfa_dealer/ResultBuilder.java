package stellarnear.yfa_dealer;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Rolls.Dice;
import stellarnear.yfa_dealer.Rolls.DiceList;
import stellarnear.yfa_dealer.Rolls.DisplayRolls;
import stellarnear.yfa_dealer.Rolls.ProbaFromDiceRand;
import stellarnear.yfa_dealer.Spells.Spell;

public class ResultBuilder {

    private Perso yfa=MainActivity.yfa;
    private Spell spell;
    private int spellColorId;
    private Context mC;
    private Activity mA;
    private Calculation calculation=new Calculation();
    private DisplayedText displayedText=new DisplayedText();
    private Tools tools=new Tools();


    public ResultBuilder(Activity mA, Context mC, Spell spell){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        setSpellColor();
    }

    public void addResults(LinearLayout layout) {
        LayoutInflater inflater = mA.getLayoutInflater();
        LinearLayout resultTemplate = (LinearLayout) inflater.inflate(R.layout.spell_profile_result, null);

        if (spell.getDmg_type().equalsIgnoreCase("")) {
            resultTemplate.removeAllViews();
            TextView txt_view = new TextView(mC);
            txt_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            txt_view.setText("Sortilège " + spell.getName() + " lancé !");
            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            resultTemplate.addView(txt_view);
        } else if (spell.getMetaList().metaIdIsActive("meta_perfect") || spell.getMetaList().metaIdIsActive("meta_max") || !displayedText.damageTxt(spell).contains("d") ) {
            resultTemplate.removeAllViews();
            TextView txt_view = new TextView(mC);
            txt_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            if(spell.isCrit()){
                Drawable explo = mC.getDrawable(R.drawable.ic_grade_black_24dp);
                explo.mutate().setColorFilter(spellColorId,PorterDuff.Mode.SRC_IN);
                txt_view.setCompoundDrawablesWithIntrinsicBounds(explo,null,explo,null);
                int sumDmg = 2*tools.toInt(displayedText.damageTxt(spell));
                checkHighScore(sumDmg);
                txt_view.setText(sumDmg+" dégâts !");
            }else{
                int sumDmg = tools.toInt(displayedText.damageTxt(spell));
                checkHighScore(sumDmg);
                txt_view.setText(sumDmg+" dégâts !");
            }
            resultTemplate.addView(txt_view);
            txt_view.setTextColor(spellColorId);
        } else {
            final DiceList diceList = new DiceList();
            List<Integer> listDiceAllowed = Arrays.asList(3,4,6,8,10);
            if (listDiceAllowed.contains(calculation.diceType(spell))){
                for(int i=1 ; i<= calculation.nDice(spell);i++){
                    diceList.add(new Dice(mA,mC,calculation.diceType(spell),spell.getDmg_type()));
                }
                for (Dice dice : diceList.getList()){
                    dice.rand(false);
                }

            }

            ((TextView)resultTemplate.findViewById(R.id.highscore)).setText("(record:"+String.valueOf(spell.getHighscore())+")");
            checkHighScore(diceList.getSum());

            if(spell.isCrit()){
                Drawable explo = mC.getDrawable(R.drawable.ic_grade_black_24dp);
                explo.setColorFilter(spellColorId,PorterDuff.Mode.SRC_IN);
                ((TextView)resultTemplate.findViewById(R.id.damage)).setCompoundDrawablesWithIntrinsicBounds(explo,null,explo,null);
                ((TextView)resultTemplate.findViewById(R.id.damage)).setText(String.valueOf(2*diceList.getSum()));
            }else{
                ((TextView)resultTemplate.findViewById(R.id.damage)).setText(String.valueOf(diceList.getSum()));
            }
            ((TextView)resultTemplate.findViewById(R.id.damage)).setTextColor(spellColorId);

            ProbaFromDiceRand probaFromDiceRand = new ProbaFromDiceRand(diceList);
            ((TextView)resultTemplate.findViewById(R.id.damage_range)).setText(String.valueOf(probaFromDiceRand.getRange()));
            ((TextView)resultTemplate.findViewById(R.id.damage_range)).setTextColor(spellColorId);
            ((TextView)resultTemplate.findViewById(R.id.proba)).setText(String.valueOf(probaFromDiceRand.getProba()));
            ((TextView)resultTemplate.findViewById(R.id.proba)).setTextColor(spellColorId);
            ((FloatingActionButton)resultTemplate.findViewById(R.id.fab_detail)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisplayRolls displayRolls=new DisplayRolls(mA,mC,diceList);
                    displayRolls.showPopup();
                }
            });
        }
        layout.addView(resultTemplate);
    }

    private void checkHighScore(int sumDmg) {
        if(spell.isHighscore(sumDmg)){
            Tools tools = new Tools();
            tools.playVideo(mA,mC,"/raw/explosion");
            tools.customToast(mC, String.valueOf(sumDmg) + " dégats !\nC'est un nouveau record !", "center");
        }
    }

    private void setSpellColor() {
        switch (spell.getDmg_type()){
            case "froid":
                spellColorId =mC.getColor(R.color.froid_dark);
                break;

            case "feu":
                spellColorId =mC.getColor(R.color.feu_dark);
                break;

            case "foudre":
                spellColorId =mC.getColor(R.color.foudre_dark);
                break;

            case "acide":
                spellColorId =mC.getColor(R.color.acide_dark);
                break;

            default:
                spellColorId=mC.getColor(R.color.aucun_dark);
                break;
        }


    }

}
