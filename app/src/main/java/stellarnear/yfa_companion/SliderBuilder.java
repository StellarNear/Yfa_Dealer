package stellarnear.yfa_companion;

import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Log.CustomLog;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Spells.Spell;

public class SliderBuilder {
    private Perso yfa= MainActivity.yfa;
    private Spell spell;
    private Context mC;
    private boolean slided=false;
    private Calculation calculation=new Calculation();
    private OnCastEventListener mListener;
    private SeekBar seek;
    private Tools tools=Tools.getTools();

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
                    if(spell.isFree()) {
                        boolean hasRankAvailable=false;
                        for(int i = calculation.currentRank(spell);i <= yfa.getAllResources().getRankManager().getHighestTier();i++){
                            if( yfa.getResourceValue("spell_rank_" + i) > 0){
                                hasRankAvailable=true;
                                break;
                            }
                        }
                        if (!spell.isCast() && !hasRankAvailable) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Le rang du sort Arcane libre ne peut dépasser ton rang maximal disponible", "center");
                        } else if (!spell.isCast() &&  yfa.getResourceValue("resource_mythic_points") < 1) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Il ne te reste aucun point mythique pour lancer ce sort en Arcane libre", "center");
                        } else if (!spell.isCast() && spell.elementIsConverted() && yfa.getResourceValue("resource_mythic_points") < 2) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Il ne te reste pas assez de points mythiques pour lancer ce sort en Arcane libre avec conversion d'élément", "center");
                        } else if (!spell.isCast() && !spell.getConversion().getArcaneId().equalsIgnoreCase("") && yfa.getResourceValue("spell_conv_rank_" + spell.getConversion().getRank()) < 1) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Tu n'as pas d'emplacement de sort convertible " + calculation.currentRank(spell) + " de disponible...", "center");
                        } else {
                            startCasting();
                        }
                    } else {
                        if (!spell.isCast() &&  spell.getRank() != 0 && yfa.getResourceValue("spell_rank_" + calculation.currentRank(spell)) < 1) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Tu n'as pas d'emplacement de sort " + calculation.currentRank(spell) + " de disponible...", "center");
                        } else if (!spell.isCast() && !spell.getConversion().getArcaneId().equalsIgnoreCase("") && yfa.getResourceValue("spell_conv_rank_" + spell.getConversion().getRank()) < 1) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Tu n'as pas d'emplacement de sort convertible " + calculation.currentRank(spell) + " de disponible...", "center");
                        } else if (!spell.isCast() && spell.elementIsConverted() && yfa.getResourceValue("resource_mythic_points") < 1) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Il ne te reste aucun point mythique pour la conversion d'élément", "center");
                        } else if (!spell.isCast() && spell.isMyth() && yfa.getResourceValue("resource_mythic_points") < 1) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Il ne te reste aucun point mythique pour lancer ce sort Mythique", "center");
                        } else if (!spell.isCast() && spell.isMyth() && spell.elementIsConverted() && yfa.getResourceValue("resource_mythic_points") < 2) {
                            seekBar.setProgress(1);
                            tools.customToast(mC, "Il ne te reste pas assez de points mythiques pour lancer ce sort Mythique avec conversion d'élément", "center");
                        } else {
                            startCasting();
                        }
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
        mListener.onEvent();  //build les resultat à faire avant de spend le cast car ca PostEvent (et il faut avoir setDMg dans le spell)
        spendCast();
        Snackbar.make(seek, "Lancement du sort : " + spell.getName(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void spendCast() {
        if(!spell.isCast()) {
            slided=true;
            yfa.castSpell(spell,mC);
        } else if(!slided) {
            slided=true;
            new PostData(mC,new PostDataElement(spell)); // pour les sous sorts on peut avoir un sous sort pas lancé mais pas posté
        }
    }

    public interface OnCastEventListener {
        void onEvent();
    }

    public void setCastEventListener(OnCastEventListener eventListener) {
        mListener = eventListener;
    }
}
