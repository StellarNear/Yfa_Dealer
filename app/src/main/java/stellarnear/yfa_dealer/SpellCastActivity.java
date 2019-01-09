package stellarnear.yfa_dealer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;
import stellarnear.yfa_dealer.Spells.SpellProfileFactory;


public class SpellCastActivity extends AppCompatActivity {

    private SpellList selected_spells;
    private Perso yfa = MainActivity.yfa;
    private Targets targets = Targets.getInstance();
    private Tools tools=new Tools();
    private Calculation calculation=new Calculation();
    private TextView round;
    private LinearLayout mainLin;

    private List<SpellProfileFactory> allProfiles = new ArrayList<>();

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.infromleft, R.anim.nothing);
            }
        });

        if (getResources().getConfiguration().orientation == 1) { //lock de l'ecran pour éviter des cast et reset étranges
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }

        selected_spells= targets.getAllSpellList();

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
            addSpellsForTarget(targets.getSpellListForTarget(tar));

        }
        refreshRound();
    }

    private void addSpellsForTarget(List<Spell> targetSpells) {
        for (final Spell spell : targetSpells) {
            SpellProfileFactory spellProfileFactory = new SpellProfileFactory(SpellCastActivity.this,getApplicationContext(),spell);
            allProfiles.add(spellProfileFactory);
            mainLin.addView(spellProfileFactory.getProfile());
            spellProfileFactory.setRefreshEventListener(new SpellProfileFactory.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    refreshRound();
                    if(selected_spells.haveBindedSpells()){
                        refreshAllProfiles();
                    }
                }
            });
        }
    }

    private void refreshAllProfiles() {
        for(SpellProfileFactory spellFacto : allProfiles){
            spellFacto.refreshProfile();
        }
    }

    private void refreshRound() {
        round.setText("["+calculation.calcRounds(getApplicationContext(),selected_spells)+" rounds]");
    }
}



