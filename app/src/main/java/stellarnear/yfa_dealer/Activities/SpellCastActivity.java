package stellarnear.yfa_dealer.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import stellarnear.yfa_dealer.Calculation;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;
import stellarnear.yfa_dealer.Spells.SpellProfile;
import stellarnear.yfa_dealer.Targets;
import stellarnear.yfa_dealer.Tools;


public class SpellCastActivity extends AppCompatActivity {

    private SpellList selectedSpells;
    private Perso yfa = MainActivity.yfa;
    private Targets targets = Targets.getInstance();
    private Tools tools=new Tools();
    private Calculation calculation=new Calculation();
    private TextView round;
    private LinearLayout mainLin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell_cast);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);                  //back button
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivityFragmentSpell.class));
                overridePendingTransition(R.anim.infromleft, R.anim.nothing);
            }
        });

        if (getResources().getConfiguration().orientation == 1) { //lock de l'ecran pour éviter des cast et reset étranges
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        selectedSpells = targets.getAllSpellList();

        mainLin = (LinearLayout) findViewById(R.id.linear2);
        round = (TextView) findViewById(R.id.n_round);

        for(String tar : targets.getTargetList()){
            TextView textTar = new TextView(this);
            textTar.setText(tar);
            textTar.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_gps_fixed_red_24dp),null,null,null);
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
    }

    private void addSpellsForTarget(List<Spell> targetSpells) {
        for (final Spell spell : targetSpells) {
            SpellProfile spellProfile = spell.getProfile() ;
            mainLin.addView(spellProfile.getProfile(SpellCastActivity.this,getApplicationContext()));
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
        int nRound = calculation.calcRounds(getApplicationContext(), selectedSpells);
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
            tools.customToast(getApplicationContext(),"Plus de sort à lancer");
            yfa.getStats().storeStatsFromRolls(selectedSpells);
        }
    }
}



