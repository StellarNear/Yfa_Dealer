package stellarnear.yfa_dealer;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.SeekBar;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Spells.Spell;

public class SliderBuilder {
    private Perso yfa=MainActivity.yfa;
    private Spell spell;
    private Context mC;
    private Calculation calculation=new Calculation();
    private OnCastEventListener mListener;
    private SeekBar seek;
    private Tools tools=new Tools();

    public SliderBuilder(Context mC,Spell spell){
        this.mC=mC;
        this.spell=spell;
    }

    public void setSlider(final SeekBar seek){
        this.seek=seek;
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 75) {
                    seekBar.setProgress(100);
                    if (spell.getRank() != 0 && yfa.getResourceValue("spell_rank_" + calculation.currentRank(spell)) <1) {
                        seekBar.setProgress(1);
                        tools.customToast(mC,"Tu n'as pas d'emplacement de sort "+calculation.currentRank(spell)+" de disponible...","center");
                    } else if (!spell.isCast() && !spell.getConversion().getArcaneId().equalsIgnoreCase("") && yfa.getResourceValue("spell_conv_rank_" + spell.getConversion().getRank())<1) {
                        seekBar.setProgress(1);
                        tools.customToast(mC, "Tu n'as pas d'emplacement de sort convertible " + calculation.currentRank(spell) + " de disponible...","center");
                    } else if (!spell.isCast() && spell.elementIsConverted() && yfa.getResourceValue("mythic_points") < 1) {
                        seekBar.setProgress(1);
                        tools.customToast(mC, "Il ne te reste aucun point mythique pour la conversion d'élément","center");
                    } else if(!spell.isCast() && spell.isMyth() && yfa.getResourceValue("mythic_points") <1){
                        seekBar.setProgress(1);
                        tools.customToast(mC, "Il ne te reste aucun point mythique pour lancer ce sort Mythique","center");
                    } else if(!spell.isCast() && spell.isMyth() && spell.elementIsConverted() && yfa.getResourceValue("mythic_points") <2){
                        seekBar.setProgress(1);
                        tools.customToast(mC, "Il ne te reste pas assez de points mythiques pour lancer ce sort Mythique avec conversion d'élément","center");
                    } else {
                        startCasting();
                    }
                } else {
                    seekBar.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress > 75) {
                    seekBar.setThumb(mC.getDrawable(R.drawable.thumb_select));
                } else {
                    seekBar.setThumb(mC.getDrawable(R.drawable.thumb_unselect));
                }
            }
        });
    }

    private void startCasting() {
        spendCast();
        mListener.onEvent();
        Snackbar.make(seek, "Lancement du sort : " + spell.getName(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void spendCast() {
        if(!spell.isCast()) {
            if (spell.getRank() != 0) {
                yfa.castSpell(spell);
            }
            if (!spell.getConversion().getArcaneId().equalsIgnoreCase("")) {
                yfa.castConvSpell(spell.getConversion().getRank());
            }
            if (spell.isMyth()) {
                yfa.getAllResources().getResource("mythic_points").spend(1);
                tools.customToast(mC, "Sort Mythique\nIl te reste " + yfa.getResourceValue("mythic_points") + " point(s) mythique(s)", "center");
            }
            if (spell.elementIsConverted()) {
                yfa.getAllResources().getResource("mythic_points").spend(1);
                tools.customToast(mC, "Conversion d'élément\nIl te reste " + yfa.getResourceValue("mythic_points") + " point(s) mythique(s)", "center");
            }
        }
    }

    public interface OnCastEventListener {
        void onEvent();
    }

    public void setCastEventListener(OnCastEventListener eventListener) {
        mListener = eventListener;
    }
}
