package stellarnear.yfa_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import stellarnear.yfa_companion.Calculation;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Spells.SpellList;
import stellarnear.yfa_companion.Spells.SpellProfile;
import stellarnear.yfa_companion.Targets;
import stellarnear.yfa_companion.Tools;


public class MainActivityFragmentSpellCast extends Fragment {

    private SpellList selectedSpells;
    private Perso yfa = MainActivity.yfa;
    private Targets targets = Targets.getInstance();
    private Tools tools=new Tools();
    private Calculation calculation=new Calculation();
    private TextView round;
    private LinearLayout mainLin;
    private View returnFragView;

    public MainActivityFragmentSpellCast() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }
        super.onCreate(savedInstanceState);

        returnFragView= inflater.inflate(R.layout.spell_cast, container, false);
        selectedSpells = targets.getAllSpellList();

        mainLin = (LinearLayout) returnFragView.findViewById(R.id.linear2);
        round = (TextView) returnFragView.findViewById(R.id.n_round);

        ((FrameLayout) returnFragView.findViewById(R.id.back_spell_from_spell_cast)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToSpell();
            }
        });


        for(String tar : targets.getTargetList()){
            TextView textTar = new TextView(getContext());
            textTar.setText(tar);
            textTar.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.drawable.ic_gps_fixed_red_24dp),null,null,null);
            textTar.setCompoundDrawablePadding(10);
            textTar.setTextSize(20);
            textTar.setTextColor(Color.DKGRAY);
            textTar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            textTar.setLayoutParams(para);

            mainLin.addView(textTar);
            addSpellsForTarget(targets.getSpellListForTarget(tar).asList());

        }



        refreshRound();
        return returnFragView;
    }

    private void addSpellsForTarget(List<Spell> targetSpells) {
        for (final Spell spell : targetSpells) {
            SpellProfile spellProfile = spell.getProfile() ;
            View spellProfileView=spellProfile.getProfile(getActivity(),getContext());
            if(spellProfileView.getParent()!=null){((ViewGroup)spellProfileView.getParent()).removeView(spellProfileView);}
            mainLin.addView(spellProfileView);
            spellProfile.setRefreshEventListener(new SpellProfile.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    refreshRound();
                    if(selectedSpells.haveBindedSpells()){
                        refreshAllProfiles();
                    }
                    testAllEndRound();
                }
            });
        }
    }



    private void refreshAllProfiles() {
        for(Spell spell : selectedSpells.asList()){
            spell.refreshProfile();
        }
    }

    private void refreshRound() {
        int nRound = calculation.calcRounds(getContext(), selectedSpells);
        round.setText("["+nRound+" round"+(nRound>1 ? "s":"")+"]");
    }

    private void testAllEndRound() {
        boolean end = true;
        for (Spell spell : selectedSpells.asList()){
            if(!spell.getProfile().isDone()){
                end=false;
            }
        }
        if(end){
            tools.customToast(getContext(),"Plus de sort à lancer");
            yfa.getStats().storeStatsFromRolls(selectedSpells);
        }
    }

    public void backToSpell() {
        Fragment fragment = new MainActivityFragmentSpell();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtorightfrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}



