package stellarnear.yfa_dealer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.util.List;

/**
 * Created by Utilisateur on 15/10/2017.
 */

public class SpellCastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell_cast);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        Intent i = getIntent();
        List<Spell> selected_spells = (List<Spell>) i.getSerializableExtra("selected_spells");
        LinearLayout page2 = (LinearLayout) findViewById(R.id.linear2);

        for (final Spell spell : selected_spells) {
            TextView Spell_Title = new TextView(this);
            Spell_Title.setText(spell.getName());
            Spell_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            Spell_Title.setTextColor(Color.BLACK);
            Spell_Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            Spell_Title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            page2.addView(Spell_Title);
            View h_sep = new View(this);
            h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,7));
            h_sep.setBackgroundColor(Color.GRAY);
            page2.addView(h_sep);


            final ViewSwitcher panel = new ViewSwitcher(this);
            Animation outtoLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
                    outtoLeft.setDuration(500);
                    outtoLeft.setInterpolator(new AccelerateInterpolator());

            Animation inFromRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            inFromRight.setDuration(500);
            inFromRight.setInterpolator(new AccelerateInterpolator());

            panel.setInAnimation(inFromRight);
            panel.setOutAnimation(outtoLeft);
            page2.addView(panel);

            LinearLayout fragment1= new LinearLayout(this);
            fragment1.setOrientation(LinearLayout.VERTICAL);
            fragment1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            panel.addView(fragment1);
            LinearLayout fragment2= new LinearLayout(this);
            fragment2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            panel.addView(fragment2);
            TextView lol = new TextView(this);
            lol.setText("LOOOL");
            lol.setTextColor(Color.BLACK);
            fragment2.addView(lol);

            final TextView descri = new TextView(this);
            descri.setText(spell.getDescr());
            descri.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            descri.setSingleLine(true);
            descri.setHorizontalFadingEdgeEnabled(true);
            descri.postDelayed(new Runnable() {
                @Override
                public void run() {
                    descri.setSelected(true);
                }
            }, 3000);
            descri.setMarqueeRepeatLimit(-1);
            fragment1.addView(descri);


            SeekBar cast_slide = new SeekBar(this);
            cast_slide.setMax(100);
            cast_slide.setThumb(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
            cast_slide.setDrawingCacheBackgroundColor(Color.GRAY);
            fragment1.addView(cast_slide);
            cast_slide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (seekBar.getProgress() > 75) {
                        seekBar.setProgress(100);
                        switch_page(panel);
                        View view = (View) findViewById(R.id.page2);
                        Snackbar.make(view, "Lancement du sort : "+spell.getName(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        seekBar.setProgress(0);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    if(progress>75){
                        seekBar.setThumb(getDrawable(R.drawable.ic_wb_sunny_black_24dp));
                        seekBar.setThumbTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                    } else {
                        seekBar.setThumb(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
                        seekBar.setThumbTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                    }

                }
            });

            //dans le switchview mettre tout les choix de meta magie descriptif du sort etc

            View h_sep2 = new View(this);
            h_sep2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,7));
            h_sep2.setBackgroundColor(Color.GRAY);
            page2.addView(h_sep2);

            /*
            cast_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
            */


        }
    }

    private void switch_page(ViewSwitcher panel) {
        panel.showNext();
    }


}

