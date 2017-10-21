package stellarnear.yfa_dealer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SpellCastActivity extends AppCompatActivity {

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
                onBackPressed();
            }
        });

        Intent i = getIntent();
        List<Spell> selected_spells = (List<Spell>) i.getSerializableExtra("selected_spells");   //recuperation des sorts selection dans mainActiv
        LinearLayout page2 = (LinearLayout) findViewById(R.id.linear2);

        for (final Spell spell : selected_spells) {
            spell.setSave_val(getApplicationContext()); //refresh si le charisme à bouger
            spell.meta_Materiel(getApplicationContext()); //refresh si le setting a bougé
            final TextView Spell_Title = new TextView(this);
            makeTitle(Spell_Title,spell); //fait le titre du cartouche avec le rang en petit
            setSpellTitleColor(Spell_Title,spell);
            page2.addView(Spell_Title);

            View h_sep = new View(this);
            h_sep.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,7));
            h_sep.setBackgroundColor(Color.BLACK);
            page2.addView(h_sep);


            final ViewSwitcher panel = new ViewSwitcher(this);
            Animation outtoLeft = new TranslateAnimation(  
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation de sortie vers la gauche
                    outtoLeft.setDuration(500);
                    outtoLeft.setInterpolator(new AccelerateInterpolator());

            Animation inFromRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation d'entree par la droite
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
            lol.setText("lol");
            lol.setTextColor(Color.BLACK);
            fragment2.addView(lol);

            //construction fragement 1

            final TextView descri = new TextView(this);
            descri.setText(spell.getDescr());
            descri.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            descri.setSingleLine(true);
            descri.setTextColor(Color.DKGRAY);
            descri.setHorizontalFadingEdgeEnabled(true);
            descri.postDelayed(new Runnable() {
                @Override
                public void run() {
                    descri.setSelected(true);
                }
            }, 3000);
            descri.setMarqueeRepeatLimit(-1);
            fragment1.addView(descri);

            final TextView infos = new TextView(this) ;
            infos.setSingleLine(false);
            makeInfos(infos,spell);
            fragment1.addView(infos);

            View h_sep_meta = new View(this);
            h_sep_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep_meta.setBackgroundColor(Color.GRAY);
            fragment1.addView(h_sep_meta);

            HorizontalScrollView scroll_meta= new HorizontalScrollView(this);
            scroll_meta.setHorizontalScrollBarEnabled(false);
            scroll_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            fragment1.addView(scroll_meta);

            LinearLayout grid=new LinearLayout(this);
            scroll_meta.addView(grid);

            Map<CheckBox,ImageButton> all_check_meta=construct_list_meta(spell,Spell_Title,infos);

            View v_sep_meta = new View(this);
            v_sep_meta.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep_meta.setBackgroundColor(Color.GRAY);
            grid.addView(v_sep_meta);
            for (Map.Entry<CheckBox, ImageButton> entry : all_check_meta.entrySet()){

                CheckBox checkbox = entry.getKey();
                grid.addView(checkbox);
                LinearLayout container=new LinearLayout(this);
                container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
                container.setGravity(Gravity.CENTER);
                ImageButton image = entry.getValue();
                image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                image.setForegroundGravity(Gravity.CENTER);
                image.setColorFilter(Color.GRAY);
                container.addView(image);
                grid.addView(container);

                View v_sep_meta2 = new View(this);
                v_sep_meta2.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
                v_sep_meta2.setBackgroundColor(Color.GRAY);
                grid.addView(v_sep_meta2);
            }

            View h_sep_meta2 = new View(this);
            h_sep_meta2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,4));
            h_sep_meta2.setBackgroundColor(Color.GRAY);
            fragment1.addView(h_sep_meta2);

            SeekBar cast_slide = new SeekBar(this);
            cast_slide.setMax(100);
            cast_slide.setThumb(getDrawable(R.drawable.ic_play_circle_filled_black_24dp));

            //cast_slide.setDrawingCacheBackgroundColor(Color.BLACK);

            GradientDrawable gd_background = new GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    new int[] {0xFFBDBDBD,0xFFFDEFEF});
            gd_background.setCornerRadius(0f);
            cast_slide.setBackground(gd_background);

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
            h_sep2.setBackgroundColor(Color.BLACK);
            page2.addView(h_sep2);



        }
    }

    private Map<CheckBox,ImageButton> construct_list_meta(final Spell spell,final TextView Spell_Title,final TextView infos) {
        Map<CheckBox,ImageButton> map_list_meta_check=new LinkedHashMap<>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        if (settings.getBoolean("ameliore",getResources().getBoolean(R.bool.ameliore_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort Amélioré");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Enhance_Spell(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Enhance_Spell(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });

            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Enhance_Spell_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }

        if (settings.getBoolean("rapid",getResources().getBoolean(R.bool.rapid_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Incantation rapide");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Rapid(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Rapid(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });
            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Rapid_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }
        
        //quintessence des sorts +3
        if (settings.getBoolean("quintessence",getResources().getBoolean(R.bool.quintessence_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Quintessence des sorts");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Quint(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Quint(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });
            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Quint_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }
        
        //extension d'effet +2
        if (settings.getBoolean("extension",getResources().getBoolean(R.bool.extension_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Extension d'effet");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Extend(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Extend(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });
            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Extend_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }
        
        //augementation d'intensité +1
        if (settings.getBoolean("intense",getResources().getBoolean(R.bool.intense_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Augmentation d'intensité");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Intense(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Intense(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });
            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Intense_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }
        
        //sort éloigné +1
        if (settings.getBoolean("eloigne",getResources().getBoolean(R.bool.eloigne_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort éloigné");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Far(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Far(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });
            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Far_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }
        
        if (settings.getBoolean("select",getResources().getBoolean(R.bool.select_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort séléctif");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Select_Spell(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Select_Spell(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });
            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Select_Spell_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }

        if (settings.getBoolean("silence",getResources().getBoolean(R.bool.silence_switch_def)))  {
            CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort silencieux");
            checkbox.setTextColor(Color.GRAY);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Silent(true);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);

                    } else {
                        spell.meta_Silent(false);
                        makeTitle(Spell_Title,spell);
                        makeInfos(infos,spell);
                    }
                }
            });
            ImageButton image=new ImageButton(getApplicationContext());
            image.setImageResource(R.drawable.ic_info_outline_black_24dp);
            image.setBackgroundColor(Color.TRANSPARENT);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spell.meta_Silent_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }

        return map_list_meta_check;
    }

    private void switch_page(ViewSwitcher panel) {
        panel.showNext();
    }
    
    private void makeInfos(TextView infos,Spell spell) {
        String resistance;
        if (spell.getSave_type().equals("aucun")) {
            resistance = spell.getSave_type();

        } else {
            resistance = spell.getSave_type() + "(" + spell.getSave_val() + ")";
        }
        infos.setText("Dégats : "+spell.getN_dice()+spell.getDice_typ() +", Type : "+ spell.getDmg_type()+ ", Portée : "+spell.getRange()+"\n"
                          +"Compos : "+spell.getCompo() +", Cast : "+ spell.getCast_tim()+ ", Durée : "+spell.getDuration()+"\n"
                          +"RM : "+(spell.getRM()? "oui" : "non") +", Jet de sauv : "+ resistance);
        infos.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        infos.setTextColor(Color.GRAY);
    }
    
    private void makeTitle(TextView Spell_Title,Spell spell) {
        Spell_Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Spell_Title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Spell_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        String titre_texte=spell.getName()+" (rang : "+spell.getRank()+")";
        SpannableString titre=  new SpannableString(titre_texte);
        titre.setSpan(new RelativeSizeSpan(2f), 0,spell.getName().length(), 0); // set size1
        titre.setSpan(new ForegroundColorSpan(Color.BLACK), 0,spell.getName().length(), 0);// set color1
        titre.setSpan(new ForegroundColorSpan(Color.BLACK),spell.getName().length(),titre_texte.length(), 0);// set color2
        Spell_Title.setText(titre);
    }

    public void setSpellTitleColor(TextView text,Spell spell) {
        int end=0;
        int start=0;
        if (spell.getDmg_type().equals("aucun")) {end=R.color.aucun_dark;start=R.color.aucun;}
        if (spell.getDmg_type().equals("feu")) {end=R.color.feu_dark;start=R.color.feu;}
        if (spell.getDmg_type().equals("foudre")) {end=R.color.foudre_dark;start=R.color.foudre;}
        if (spell.getDmg_type().equals("froid")) {end=R.color.froid_dark;start=R.color.froid;}
        if (spell.getDmg_type().equals("acide")) {end=R.color.acide_dark;start=R.color.acide;}

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[] {getColor(start),getColor(end)});
        gd.setCornerRadius(0f);
        text.setTextColor(Color.BLACK);
        text.setBackground(gd);
    }


}

