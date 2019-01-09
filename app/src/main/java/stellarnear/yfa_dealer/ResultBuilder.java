package stellarnear.yfa_dealer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Rolls.Dice;
import stellarnear.yfa_dealer.Rolls.DiceList;
import stellarnear.yfa_dealer.Rolls.ProbaFromDiceRand;
import stellarnear.yfa_dealer.Spells.Spell;

public class ResultBuilder {

    private Perso yfa=MainActivity.yfa;
    private Spell spell;
    private Context mC;
    private Activity mA;
    private Calculation calculation=new Calculation();


    public ResultBuilder(Activity mA, Context mC, Spell spell){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
    }

    public void addResults(LinearLayout layout) {
        LayoutInflater inflater = mA.getLayoutInflater();
        View resultTemplate = inflater.inflate(R.layout.spell_profile_result, null);

        DiceList diceList = new DiceList();
        List<Integer> listDiceAllowed = Arrays.asList(3,4,6,8);
        if (listDiceAllowed.contains(calculation.diceType(spell))){
            for(int i=1 ; i<= calculation.nDice(spell);i++){
                diceList.add(new Dice(mA,mC,calculation.diceType(spell)));
            }

            for (Dice dice : diceList.getList()){
                dice.rand(false);
            }

        }

        ((TextView)resultTemplate.findViewById(R.id.highscore)).setText("(record:"+String.valueOf(spell.getHighscore())+")");

        if(spell.isHighscore(diceList.getSum())){
            Tools tools = new Tools();
            tools.playVideo(mA,mC,"/raw/explosion");
            tools.customToast(mC, String.valueOf(diceList.getSum()) + " dégats !\nC'est un nouveau record !", "center");
        }

        ((TextView)resultTemplate.findViewById(R.id.damage)).setText(String.valueOf(diceList.getSum()));

        ProbaFromDiceRand probaFromDiceRand = new ProbaFromDiceRand(diceList);
        ((TextView)resultTemplate.findViewById(R.id.damage_range)).setText(String.valueOf(probaFromDiceRand.getRange()));
        ((TextView)resultTemplate.findViewById(R.id.proba)).setText(String.valueOf(probaFromDiceRand.getProba()));

        layout.addView(resultTemplate);
    }

}
