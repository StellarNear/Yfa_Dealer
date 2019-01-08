package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Spells.Calculation;
import stellarnear.yfa_dealer.Spells.Metamagic;
import stellarnear.yfa_dealer.Spells.Spell;
import stellarnear.yfa_dealer.Spells.SpellList;


public class SpellCastActivity extends AppCompatActivity {

    private SpellList selected_spells;
    private Perso yfa = MainActivity.yfa;
    private Targets targets = Targets.getInstance();
    private Tools tools=new Tools();
    private Calculation calculation=new Calculation();
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

        LinearLayout mainLin = (LinearLayout) findViewById(R.id.linear2);

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
            addSpellsForTarget(mainLin,targets.getSpellListForTarget(tar));
        }

        calculation.calcRounds(getApplicationContext(),selected_spells);

    }

    private void addSpellsForTarget(LinearLayout mainLin,List<Spell> targetSpells) {
        for (final Spell spell : targetSpells) {
            SpellProfileFactory spellProfileFactory = new SpellProfileFactory(SpellCastActivity.this,getApplicationContext(),spell);
            mainLin.addView(spellProfileFactory.getProfile());
        }
    }
}



