package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        Intent i = getIntent();
        List<Spell> selected_spells = (List<Spell>) i.getSerializableExtra("selected_spells");   //recuperation des sorts selection dans mainActiv
        LinearLayout page2 = (LinearLayout) findViewById(R.id.linear2);



        for (final Spell spell : selected_spells) {
            spell.setSave_val(getApplicationContext()); //refresh si le charisme à bouger
            spell.meta_Materiel(getApplicationContext()); //refresh si le setting a bougé
            final TextView Spell_Title = new TextView(this);
            SpellPerDay spell_per_day=new SpellPerDay(getApplicationContext());
            spell_per_day.load_list_spell_per_day(getApplicationContext());
            makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext()); //fait le titre du cartouche avec le rang en petit et couleur warining si pas dispo
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
            final LinearLayout fragment2= new LinearLayout(this);
            fragment2.setOrientation(LinearLayout.HORIZONTAL);
            fragment2.setGravity(Gravity.CENTER_VERTICAL);
            fragment2.setWeightSum(7);
            fragment2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            panel.addView(fragment2);


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
                        SpellPerDay spell_per_day=new SpellPerDay(getApplicationContext());
                        spell_per_day.load_list_spell_per_day(getApplicationContext());
                        
                        if(spell_per_day.checkRank_available(spell.getRank(),getApplicationContext())){
                            spell_per_day.castSpell_rank(spell.getRank());
                            spell_per_day.save_list_spell_per_day(getApplicationContext());
                            constructFrag2(fragment2,spell);
                            switch_page(panel);
                               View view = (View) findViewById(R.id.page2);
                                Snackbar.make(view, "Lancement du sort : "+spell.getName(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        } else {
                            seekBar.setProgress(0);
                        }
                        
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

    private void constructFrag2(LinearLayout fragment2,final Spell spell) {


        if((spell.getDice_typ().contains("d3")||spell.getDice_typ().contains("d4")||spell.getDice_typ().contains("d6")||spell.getDice_typ().contains("d8"))&&(!spell.getDice_typ().contains("*d"))){
            LinearLayout Colonne1 = new LinearLayout(this);
            Colonne1.setOrientation(LinearLayout.VERTICAL);
            Colonne1.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            Colonne1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,2));
            LinearLayout Colonne2 = new LinearLayout(this);
            Colonne2.setOrientation(LinearLayout.VERTICAL);
            Colonne2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,2));
            Colonne2.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            LinearLayout Colonne3 = new LinearLayout(this);
            Colonne3.setOrientation(LinearLayout.VERTICAL);
            Colonne3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,2));
            Colonne3.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            LinearLayout Colonne4 = new LinearLayout(this);
            Colonne4.setOrientation(LinearLayout.VERTICAL);
            Colonne4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
            Colonne4.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            
            fragment2.addView(Colonne1);
            View v_sep = new View(this);
            v_sep.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep.setBackgroundColor(Color.GRAY);
            fragment2.addView(v_sep);
            fragment2.addView(Colonne2);
            View v_sep2 = new View(this);
            v_sep2.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep2.setBackgroundColor(Color.GRAY);
            fragment2.addView(v_sep2);
            fragment2.addView(Colonne3);
            View v_sep3 = new View(this);
            v_sep3.setLayoutParams(new LinearLayout.LayoutParams(4,LinearLayout.LayoutParams.MATCH_PARENT));
            v_sep3.setBackgroundColor(Color.GRAY);
            fragment2.addView(v_sep3);
            fragment2.addView(Colonne4);
            
            TextView ligne_texteC1= new TextView(this);
            ligne_texteC1.setGravity(Gravity.CENTER_HORIZONTAL |Gravity.TOP);
            ligne_texteC1.setText("Dégâts :");
            ligne_texteC1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            Colonne1.addView(ligne_texteC1);
            
            TextView ligne_texteC2= new TextView(this);
            ligne_texteC2.setGravity(Gravity.CENTER_HORIZONTAL |Gravity.TOP);
            ligne_texteC2.setText("[Plage](%) :");
            ligne_texteC2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            Colonne2.addView(ligne_texteC2);
            
            TextView ligne_texteC3= new TextView(this);
            ligne_texteC3.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            ligne_texteC3.setText("Probabilité de\ndépassement :");
            ligne_texteC3.setSingleLine(false);
            ligne_texteC3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            Colonne3.addView(ligne_texteC3);
 
            
            
            
            LinearLayout L2 = new LinearLayout(this);
            L2.setOrientation(LinearLayout.HORIZONTAL);
            fragment2.addView(L2);
            String[] all_text_dmg;
            all_text_dmg = calculDmg(spell);
            
            TextView dmg_sum_txt=new TextView(this);
            dmg_sum_txt.setText(Html.fromHtml(all_text_dmg[0]));
            dmg_sum_txt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            dmg_sum_txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
            Colonne1.addView(dmg_sum_txt);
            
           TextView dmg_range_txt=new TextView(this);
            dmg_range_txt.setText(Html.fromHtml(all_text_dmg[1]));
            dmg_range_txt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            dmg_range_txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            Colonne2.addView(dmg_range_txt);
            
            TextView dmg_range_percent_txt=new TextView(this);
            dmg_range_percent_txt.setText(Html.fromHtml(all_text_dmg[2]));
            dmg_range_percent_txt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            dmg_range_percent_txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            Colonne2.addView(dmg_range_percent_txt);
            
                 
            TextView dmg_proba_txt=new TextView(this);
            dmg_proba_txt.setText(Html.fromHtml(all_text_dmg[3]));
            dmg_proba_txt.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            dmg_proba_txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            Colonne3.addView(dmg_proba_txt);

            FloatingActionButton det_but = new FloatingActionButton(this);

            det_but.setForegroundGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            det_but.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            det_but.setImageResource(R.drawable.ic_more_horiz_black_24dp);

            det_but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    display_dmg_detail(spell);
                }});

            Colonne4.addView(det_but);

        } else if(!spell.getDmg_txt(getApplicationContext()).equals("")){
            LinearLayout enLigne = new LinearLayout(this);
            fragment2.setGravity(Gravity.CENTER_HORIZONTAL);
            enLigne.setOrientation(LinearLayout.VERTICAL);
            //enLigne.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            fragment2.addView(enLigne);
            TextView txt_view= new TextView(this);
            txt_view.setText("Dégâts :");
            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            txt_view.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
            enLigne.addView(txt_view);
   
            String color="<font color=#000000>";
            switch (spell.getDmg_type()){
                    case ("acide"):
                        color="<font color=#088A29>";
                        break;
                    case ("froid"):
                        color="<font color=#013ADF>";
                        break;
                    case ("foudre"):
                        color="<font color=#2ECCFA>";
                        break;
                    case ("feu"):
                        color="<font color=#FF4000>";
                        break;
                    case ("aucun"):
                        color="<font color=#A4A4A4>";
                        break;
            }
            TextView dmg_view= new TextView(this);
            
            dmg_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
            dmg_view.setGravity(Gravity.CENTER_HORIZONTAL);
            dmg_view.setText(Html.fromHtml(color+spell.getDmg_txt(getApplicationContext())+"</font>"));
            enLigne.addView(dmg_view);

        } else {
            TextView txt_view= new TextView(this);
            txt_view.setText("Sortilège "+spell.getName()+ " lancé !");
            txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            txt_view.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            fragment2.addView(txt_view);
        }


    }

    private Integer rand(Integer dice) {
        Random r = new Random();
        int jet = r.nextInt(dice)+1;
        return jet;
    }

    private String[] calculDmg(Spell spell) {

        Integer dmg_sum,dmg_min,dmg_max;
        dmg_sum=dmg_min=dmg_max=0;
        Integer nd3,nd4,nd6,nd8;
        nd3=nd4=nd6=nd8=0;
        String dice_txt="";
        switch (spell.getDice_typ()){
            case ("d3"):
                nd3=spell.getN_dice();
                break;
            case ("d4"):
                nd4=spell.getN_dice();
                break;
            case ("d6"):
                nd6=spell.getN_dice();
                break;
            case ("d8"):
                nd8=spell.getN_dice();
                break;
        }
        for(int i=0;i<nd3;i++){
            int jet=rand(3);
            dmg_sum+=jet;
            dice_txt+=spell.getDmg_type()+"_d3_"+jet+",";
            dmg_min+=1;
            dmg_max+=3;
        }

        for(int i=0;i<nd4;i++){
            int jet=rand(4);
            dmg_sum+=jet;
            dice_txt+=spell.getDmg_type()+"_d4_"+jet+",";
            dmg_min+=1;
            dmg_max+=4;
        }
        for(int i=0;i<nd6;i++){
            int jet=rand(6);
            dmg_sum+=jet;
            dice_txt+=spell.getDmg_type()+"_d6_"+jet+",";
            dmg_min+=1;
            dmg_max+=6;
        }
        for(int i=0;i<nd8;i++){
            int jet=rand(8);
            dmg_sum+=jet;
            dice_txt+=spell.getDmg_type()+"_d8_"+jet+",";
            dmg_min+=1;
            dmg_max+=8;
        }

        spell.setDmg_dice_roll_txt(dice_txt);

        Integer percent=0;
        Integer ecart =  dmg_max - dmg_min;
        if(!ecart.equals(0)) {
            percent = 100*(dmg_sum - dmg_min) / ecart;
        }

        Double proba=100.0-100.0*tableProba(nd3,nd4,nd6,nd8,dmg_sum);

        String text_dmg,text_range,text_dmg_percent,text_proba;
  
        String color="<font color=#000000>";
        switch (spell.getDmg_type()){
            case ("acide"):
                color="<font color=#088A29>";
                break;
            case ("froid"):
                color="<font color=#013ADF>";
                break;
            case ("foudre"):
                color="<font color=#2ECCFA>";
                break;
            case ("feu"):
                color="<font color=#FF4000>";
                break;
            case ("aucun"):
                color="<font color=#A4A4A4>";
                break;
                
        }

        text_dmg=color+dmg_sum+"</font>";
        text_range=color+"["+dmg_min+"-"+dmg_max+"]</font>";
        text_dmg_percent=color+"("+percent +"%)</font>";
        text_proba=color+String.format("%.02f", proba) +"%</font>";
        
        return new String[]{text_dmg, text_range, text_dmg_percent, text_proba};
    }

    private Map<CheckBox,ImageButton> construct_list_meta(final Spell spell,final TextView Spell_Title,final TextView infos) {
        Map<CheckBox,ImageButton> map_list_meta_check=new LinkedHashMap<>();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final SpellPerDay spell_per_day=new SpellPerDay(getApplicationContext());
        spell_per_day.load_list_spell_per_day(getApplicationContext());

        // incant rapide (+4)

        if (settings.getBoolean("rapid",getResources().getBoolean(R.bool.rapid_switch_def)))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Incantation rapide (+4)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        spell.meta_Rapid(true);
                        makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                        makeInfos(infos, spell);

                        /*  Meta quickspell pas possible pour perfection
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Incantation rapide ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Rapid(true);
                                            spell.setRank(spell.getRank()-4);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Rapid(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Rapid(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }*/

                    } else {
                        spell.meta_Rapid(false);
                        makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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

        // sort amélioré
        if ((settings.getBoolean("ameliore",getResources().getBoolean(R.bool.ameliore_switch_def)))&&(spell.getDice_typ().contains("d")))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort Amélioré (+4)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort Amélioré ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enhance_Spell(true);
                                            spell.setRank(spell.getRank()-4);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enhance_Spell(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Enhance_Spell(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }
                    } else {
                        spell.meta_Enhance_Spell(false);
                        makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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

        // sort à retardement +4

        if (settings.getBoolean("delay",getResources().getBoolean(R.bool.delay_switch_def)))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort à retardement (+4)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort à retardement ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Delay(true);
                                            spell.setRank(spell.getRank()-4);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Delay(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Delay(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }
                    } else {
                        spell.meta_Delay(false);
                       makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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
                    spell.meta_Delay_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }   
        
        //quintessence des sorts +3
        if ((settings.getBoolean("quintessence",getResources().getBoolean(R.bool.quintessence_switch_def)))&&(spell.getDice_typ().contains("d")))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Quintessence des sorts (+3)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Quintessence des sorts ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Quint(true);
                                            spell.setRank(spell.getRank()-3);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Quint(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Quint(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }

                    } else {
                        spell.meta_Quint(false);
                        makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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
        if ((settings.getBoolean("extension",getResources().getBoolean(R.bool.extension_switch_def)))&&(spell.getDice_typ().contains("d")))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Extension d'effet (+2)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Extension d'effet ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend(true);
                                            spell.setRank(spell.getRank()-2);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Extend(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }
                    } else {
                        spell.meta_Extend(false);
                       makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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
        
        //flêche naturalisée
        // si le level du sort est inférieur ou egal au niveau de sort max de wedge && que la méta est active
        if ((spell.getBaseRank() <= to_int(settings.getString("wedge_max_lvl_spell",getResources().getString(R.string.wedge_max_lvl_spell_def)))) && (settings.getBoolean("enchant_arrow",getResources().getBoolean(R.bool.enchant_arrow_switch_def))))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Flêche naturalisée (+2)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Flêche naturalisée ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enchant_arrow(true);
                                            spell.setRank(spell.getRank()-2);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Enchant_arrow(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Enchant_arrow(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }
                    } else {
                        spell.meta_Enchant_arrow(false);
                       makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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
                    spell.meta_Enchant_arrow_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }       
        
        //augementation d'intensité +1
        if (settings.getBoolean("intense",getResources().getBoolean(R.bool.intense_switch_def)))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Augmentation d'intensité (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            final Integer ori_save_val=spell.getSave_val();
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.getRank()<9) {
                            if(spell.isPerfect())
                            {
                                new AlertDialog.Builder(SpellCastActivity.this)
                                        .setTitle("Demande de confirmation")
                                        .setMessage("Veux-tu utiliser ta perfection magique sur Augmentation d'intensité ?")
                                        .setIcon(android.R.drawable.ic_menu_help)
                                        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                int rank_prev=spell.getRank();
                                                while (spell.getRank()<9) {
                                                    spell.meta_Intense(true);
                                                }
                                                spell.setRank(rank_prev);
                                                spell.setPerfect(false);
                                                checkbox.setClickable(false);
                                                makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                                makeInfos(infos, spell);
                                            }})
                                        .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                spell.meta_Intense(true);
                                                makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                                makeInfos(infos, spell);
                                            }}).show();
                            }else {
                                spell.meta_Intense(true);
                                makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                makeInfos(infos, spell);
                            }
                        } else {
                            String descr="Augmentation d'intensité ne permet pas de dépasser le rang 9.";
                            Toast toast = Toast.makeText(getApplicationContext(), descr, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,0);
                            toast.show();;
                        }

                    } else {
                            new AlertDialog.Builder(SpellCastActivity.this)
                                .setTitle("Demande de confirmation")
                                .setMessage("Veux-tu utiliser Augmentation d'intensité une fois de plus ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        checkbox.setChecked(true);
                                    }})
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        while(!spell.getSave_val().equals(ori_save_val)){
                                            spell.meta_Intense(false);
                                        }

                                        makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
                                        makeInfos(infos,spell);
                                    }}).show();

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
        
        //Extension de durée (+1)
        if ((settings.getBoolean("extension_dura",getResources().getBoolean(R.bool.extension_dura_switch_def)))&&(!spell.getDuration(getApplicationContext()).equals("instant"))&&(!spell.getDuration(getApplicationContext()).equals("permanente"))&&(!spell.getName().equals("Arrêt du temps")))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Extension de durée (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Extension de durée ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend_dura(true);
                                            spell.setRank(spell.getRank()-1);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Extend_dura(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Extend_dura(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }
                    } else {
                        spell.meta_Extend_dura(false);
                       makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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
                    spell.meta_Extend_dura_descr(getApplicationContext());
                }
            });
            map_list_meta_check.put(checkbox,image);
        }        
        
        
        //sort éloigné  +1
        String [] all_range_far_ok={"contact","courte","moyenne"};
        if ((settings.getBoolean("eloigne",getResources().getBoolean(R.bool.eloigne_switch_def)))&&(Arrays.asList(all_range_far_ok).contains(spell.getRange()))) {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort éloigné (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            final String ori_range=spell.getRange();
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                        if(spell.isPerfect())
                        {
                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort éloigné ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            int rank_prev=spell.getRank();
                                            while (!spell.getRange().equals("longue")) {
                                                spell.meta_Far(true);
                                            }
                                            spell.setRank(rank_prev);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Far(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Far(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }

                    } else {
                        new AlertDialog.Builder(SpellCastActivity.this)
                                .setTitle("Demande de confirmation")
                                .setMessage("Veux-tu utiliser Sort éloigné une fois de plus ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        checkbox.setChecked(true);
                                    }})
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        while(!ori_range.equals(spell.getRange())){
                                            spell.meta_Far(false);
                                        }
                                        makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
                                        makeInfos(infos,spell);
                                    }}).show();
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


        //sort séléctif
        if ((settings.getBoolean("select",getResources().getBoolean(R.bool.select_switch_def)))&&(spell.getN_dice()!=0))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort séléctif (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort séléctif ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Select_Spell(true);
                                            spell.setRank(spell.getRank()-1);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Select_Spell(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Select_Spell(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }
                    } else {
                        spell.meta_Select_Spell(false);
                        makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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


        //sort silencieux
        if ((settings.getBoolean("silence",getResources().getBoolean(R.bool.silence_switch_def))) && spell.getCompo().contains("V"))  {
            final CheckBox checkbox=new CheckBox(getApplicationContext());
            checkbox.setText("Sort silencieux (+1)");
            checkbox.setTextColor(Color.GRAY);
            int[] colorClickBox=new int[]{Color.GRAY,Color.GRAY};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },colorClickBox

            );
            checkbox.setButtonTintList(colorStateList);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(spell.isPerfect())
                        {

                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur Sort silencieux ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Silent(true);
                                            spell.setRank(spell.getRank()-1);
                                            spell.setPerfect(false);
                                            checkbox.setClickable(false);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            spell.meta_Silent(true);
                                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                                            makeInfos(infos, spell);
                                        }}).show();
                        }else {
                            spell.meta_Silent(true);
                            makeTitle(Spell_Title, spell, spell_per_day, getApplicationContext());
                            makeInfos(infos, spell);
                        }

                    } else {
                        spell.meta_Silent(false);
                        makeTitle(Spell_Title,spell,spell_per_day,getApplicationContext());
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
        if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
            resistance = spell.getSave_type();

        } else {
            resistance = spell.getSave_type() + "(" + spell.getSave_val() + ")";
        }
        
        String text="";
        Integer n_inf=0;
        if(!spell.getDmg_txt(getApplicationContext()).equals("")){
            text+="Dégats : "+spell.getDmg_txt(getApplicationContext())+", ";
            n_inf+=1;
        }
        if(!spell.getDmg_type().equals("")){
            text+="Type : "+ spell.getDmg_type()+", ";
            n_inf+=1;
        }
        if(!spell.getRange_txt().equals("")){
            text+="Portée : "+spell.getRange_txt()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}
        if(!spell.getCompo().equals("")){
            text+="Compos : "+spell.getCompo()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}
        
        if(!spell.getCast_tim().equals("")){
            text+="Cast : "+ spell.getCast_tim()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}
        
         if(!spell.getDuration(getApplicationContext()).equals("")){
            text+="Durée : "+spell.getDuration(getApplicationContext())+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}
        
        if(!spell.getRM().equals("")){
            text+="RM : "+spell.getRM()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}
        if(!resistance.equals("")){
            text+="Jet de sauv : "+ resistance+", ";
            n_inf+=1;
        }
        text = text.substring(0, text.length() - 2);
        infos.setText(text);
        infos.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        infos.setTextColor(Color.GRAY);
    }
    
    private void makeTitle(TextView Spell_Title,Spell spell,SpellPerDay spell_per_day,Context mC) {
        Spell_Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Spell_Title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Spell_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        String titre_texte=spell.getName()+" (rang : "+spell.getRank()+")";
        SpannableString titre=  new SpannableString(titre_texte);
        titre.setSpan(new RelativeSizeSpan(2f), 0,spell.getName().length(), 0); // set size1
        titre.setSpan(new ForegroundColorSpan(Color.BLACK), 0,spell.getName().length(), 0);// set color1
        
        if(spell_per_day.checkRank_available(spell.getRank(),getApplicationContext())){
            titre.setSpan(new ForegroundColorSpan(Color.BLACK),spell.getName().length(),titre_texte.length(), 0);// set color2
        } else {
            titre.setSpan(new ForegroundColorSpan(getColor(R.color.warning)),spell.getName().length(),titre_texte.length(), 0);// set color2
        }
        Spell_Title.setText(titre);
    }

    public void setSpellTitleColor(TextView text,Spell spell) {
        int end=R.color.aucun;
        int start=R.color.white;

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

    public Integer to_int(String key){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            value=0;
        }
        return value;
    }


    private Double tableProba(Integer nd3,Integer nd4,Integer nd6,Integer nd8,Integer sum) {

        /*
        Log.d("STATE (table)nd3", String.valueOf(nd3));
        Log.d("STATE (table)nd4", String.valueOf(nd4));
        Log.d("STATE (table)nd6", String.valueOf(nd6));
        Log.d("STATE (table)nd8", String.valueOf(nd8));
        Log.d("STATE (table)sum", String.valueOf(sum));
        */

        Integer total = nd3*3+nd4*4+nd6*6+nd8*8;
        //Log.d("STATE (table)total", String.valueOf(total));
        BigInteger[] combi_old = new BigInteger[total];          // table du nombre de combinaison pour chaque valeur somme
        BigInteger[] combi_new = new BigInteger[total];

        for (int i=1;i<=total;i++){
            combi_old[i-1]=BigInteger.ZERO;
            combi_new[i-1]=BigInteger.ZERO;
        }

        if (!nd8.equals(0)) {
            for (int i=1;i<=8;i++) {                     //on rempli la premiere itération
                combi_old[i-1]=BigInteger.ONE;
            }
            nd8-=1;
        } else if (!nd6.equals(0)) {                      //on rempli la premiere itération
            for (int i=1;i<=6;i++) {
                combi_old[i-1]=BigInteger.ONE;
            }
            nd6-=1;
        } else if (!nd4.equals(0)) {                      //on rempli la premiere itération
            for (int i=1;i<=4;i++) {
                combi_old[i-1]=BigInteger.ONE;
            }
            nd4-=1;
        } else if (!nd3.equals(0)) {                      //on rempli la premiere itération
            for (int i=1;i<=3;i++) {
                combi_old[i-1]=BigInteger.ONE;
            }
            nd3-=1;
        } else {
            return 1.0;
        }

        for (int i=1;i<=nd3;i++) {              //pour chaque nouveau dès on ajoute la somme(cf https://wizardofodds.com/gambling/dice/2/)
            //Log.d("STATE table_prob","traitement d10:"+String.valueOf(i));
            for (int j=1;j<=total;j++) {
                //Log.d("STATE table_prob","traitement ligne:"+String.valueOf(j));
                for (int k = 3; k >= 1; k--) {
                    //Log.d("STATE table_prob","traitement somme k:"+String.valueOf(k));
                    if (j-1-k>=0) {
                        //Log.d("STATE table_prob","combi_new[j-1]:"+String.valueOf(combi_new[j-1]));
                        //Log.d("STATE table_prob","combi_old[j-1-k]:"+String.valueOf(combi_old[j-1-k]));
                        combi_new[j-1]=combi_new[j-1].add(combi_old[j-1-k]);
                    }
                }
            }
            for (int l=1;l<=total;l++){
                combi_old[l-1]=combi_new[l-1];
                combi_new[l-1]=BigInteger.ZERO;
            }
        }

        for (int i=1;i<=nd4;i++) {              //pour chaque nouveau dès on ajoute la somme(cf https://wizardofodds.com/gambling/dice/2/)
            //Log.d("STATE table_prob","traitement d10:"+String.valueOf(i));
            for (int j=1;j<=total;j++) {
                //Log.d("STATE table_prob","traitement ligne:"+String.valueOf(j));
                for (int k = 4; k >= 1; k--) {
                    //Log.d("STATE table_prob","traitement somme k:"+String.valueOf(k));
                    if (j-1-k>=0) {
                        //Log.d("STATE table_prob","combi_new[j-1]:"+String.valueOf(combi_new[j-1]));
                        //Log.d("STATE table_prob","combi_old[j-1-k]:"+String.valueOf(combi_old[j-1-k]));
                        combi_new[j-1]=combi_new[j-1].add(combi_old[j-1-k]);
                    }
                }
            }
            for (int l=1;l<=total;l++){
                combi_old[l-1]=combi_new[l-1];
                combi_new[l-1]=BigInteger.ZERO;
            }
        }

        for (int i=1;i<=nd6;i++) {       //pour chaque nouveau dès on ajoute la somme(cf https://wizardofodds.com/gambling/dice/2/)
            //Log.d("STATE table_prob","traitement d6:"+String.valueOf(i));
            for (int j=1;j<=total;j++) {
                //Log.d("STATE table_prob","traitement ligne:"+String.valueOf(j));
                for (int k = 6; k >= 1; k--) {
                    //Log.d("STATE table_prob","traitement somme k:"+String.valueOf(k));
                    if (j-1-k>=0) {
                        //Log.d("STATE table_prob","combi_new[j-1]:"+String.valueOf(combi_new[j-1]));
                        //Log.d("STATE table_prob","combi_old[j-1-k]:"+String.valueOf(combi_old[j-1-k]));
                        combi_new[j-1]=combi_new[j-1].add(combi_old[j-1-k]);
                    }
                }
            }
            for (int l=1;l<=total;l++){
                combi_old[l-1]=combi_new[l-1];
                combi_new[l-1]=BigInteger.ZERO;
            }
        }

        for (int i=1;i<=nd8;i++) {                            //pour chaque nouveau dès on ajoute la somme(cf https://wizardofodds.com/gambling/dice/2/)
            //Log.d("STATE table_prob","traitement d8:"+String.valueOf(i));
            for (int j=1;j<=total;j++) {
                //Log.d("STATE table_prob","traitement ligne:"+String.valueOf(j));
                for (int k = 8; k >= 1; k--) {
                    //Log.d("STATE table_prob","traitement somme k:"+String.valueOf(k));
                    if (j-1-k>=0) {
                        //Log.d("STATE table_prob","combi_new[j-1]:"+String.valueOf(combi_new[j-1]));
                        //Log.d("STATE table_prob","combi_old[j-1-k]:"+String.valueOf(combi_old[j-1-k]));
                        combi_new[j-1]=combi_new[j-1].add(combi_old[j-1-k]);
                    }
                }
            }
            for (int l=1;l<=total;l++){
                combi_old[l-1]=combi_new[l-1];
                combi_new[l-1]=BigInteger.ZERO;
            }
        }


        /* Sortie debug de toute la table
        Log.d("STATE combi_prob_all","toutes les valeurs sum:n_comb");
        for (int i=1;i<=total;i++){
            Log.d("STATE combi_prob_all",String.valueOf(i)+":"+String.valueOf(combi_old[i-1]));
        }
        Log.d("STATE combi_sum",String.valueOf(sum));
        Log.d("STATE combi_res",String.valueOf(combi_old[sum-1]));
        */

        BigInteger sum_combi,sum_combi_tot;
        sum_combi=BigInteger.ZERO;
        sum_combi_tot=BigInteger.ZERO;
        for (int i=1;i<=total;i++){
            sum_combi_tot=sum_combi_tot.add(combi_old[i-1]);
            if (i==sum) {
                sum_combi=sum_combi_tot;
            }
        }

        BigDecimal temp_sum = new BigDecimal(sum_combi);
        BigDecimal temp_sum_tot = new BigDecimal(sum_combi_tot);
        BigDecimal result_percent;
        result_percent= temp_sum.divide(temp_sum_tot,4, RoundingMode.HALF_UP);

        //Log.d("STATE combi_res_prob",String.valueOf(result_percent));

        return result_percent.doubleValue();
    }


    private void display_dmg_detail(Spell spell){
        Intent intent = new Intent(this, DisplayDamageDetail.class);
        String text_extra=spell.getDmg_dice_roll_txt();
        //on enlève les derneir , et ; en fin de chaine
        while (text_extra.substring(text_extra.length() - 1, text_extra.length()).equals(","))
        {
            text_extra = text_extra.substring(0, text_extra.length() - 1);
        }
        //Log.d("STATE b4 display detail",text_extra);   //sorti debug
        intent.putExtra("all_dices_str",text_extra);    //transmet la variable à l'autre activité

        startActivity(intent);      //lance l'affichage des detail

    }


}



