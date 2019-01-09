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

    public SliderBuilder(Context mC,Spell spell){
        this.mC=mC;
        this.spell=spell;
    }

    public void setSlider(final SeekBar seek){
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 75) {
                    seekBar.setProgress(100);
                    if (spell.getRank() == 0 || yfa.getResourceValue("spell_rank_" + spell.getRank()) > 0) {
                        if (spell.getRank() != 0) {
                            yfa.castSpell(calculation.currentRank(spell));
                        }
                        if(!spell.getConversion().getArcaneId().equalsIgnoreCase("")){
                            yfa.castConvSpell(spell.getConversion().getRank());
                        }
                        mListener.onEvent();
                        Snackbar.make(seek, "Lancement du sort : " + spell.getName(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        seekBar.setProgress(1);
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

    public interface OnCastEventListener {
        void onEvent();
    }

    public void setCastEventListener(OnCastEventListener eventListener) {
        mListener = eventListener;
    }
}
