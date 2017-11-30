package stellarnear.yfa_dealer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
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
            final SpellPerDay spell_per_day=new SpellPerDay(getApplicationContext());
            spell_per_day.load_list_spell_per_day(getApplicationContext());

            setSpellTitleColor(Spell_Title,spell);
            page2.addView(Spell_Title);

            addHsep(page2,7,Color.BLACK);


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

            makeTitle(Spell_Title,infos, spell, spell_per_day, panel ,getApplicationContext()); //fait le titre du cartouche avec le rang en petit et couleur warining si pas dispo

            addHsep(fragment1,4,Color.GRAY);

            HorizontalScrollView scroll_meta= new HorizontalScrollView(this);
            scroll_meta.setHorizontalScrollBarEnabled(false);
            scroll_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            fragment1.addView(scroll_meta);

            LinearLayout grid=new LinearLayout(this);
            scroll_meta.addView(grid);

            ListMeta all_meta = new ListMeta(spell,Spell_Title,infos,SpellCastActivity.this);
            List<Pair_Meta_Rank> all_meta_list=all_meta.getAllMeta();

            addVsep(grid,4,Color.GRAY);

            for (int iter=0;iter<all_meta_list.size();iter++){

                CheckBox checkbox = all_meta_list.get(iter).getMeta().getCheckbox();

                checkbox.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        makeTitle(Spell_Title,infos, spell, spell_per_day, panel, getApplicationContext());
                        makeInfos(infos, spell);
                    }
                });     // a test à remplacer par le changetextlistener

                grid.addView(checkbox);

                ImageButton image = all_meta_list.get(iter).getMeta().getImgageButton();
                image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
                image.setForegroundGravity(Gravity.CENTER);
                image.setColorFilter(Color.GRAY);
                grid.addView(image);

                addVsep(grid,4,Color.GRAY);
            }

            addHsep(fragment1,4,Color.GRAY);

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

            addHsep(page2,7,Color.BLACK);

        }
    }

    private void addHsep(LinearLayout lay, int e, int Color) {
        View h_sep_meta = new View(this);
        h_sep_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,e));
        h_sep_meta.setBackgroundColor(Color);
        lay.addView(h_sep_meta);
    }

    private void addVsep(LinearLayout lay, int e, int Color) {
        View v_sep_meta = new View(this);
        v_sep_meta.setLayoutParams(new LinearLayout.LayoutParams(e,LinearLayout.LayoutParams.MATCH_PARENT));
        v_sep_meta.setBackgroundColor(Color);
        lay.addView(v_sep_meta);
    }

    private void constructFrag2(LinearLayout fragment2,final Spell spell) {
        fragment2.removeAllViews();

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


    private void switch_page(ViewSwitcher panel) {
        panel.showNext();
    }

    private void switch_page_back(ViewSwitcher panel) {
        panel.showPrevious();
    }



    private void makeInfos(final TextView infos,final Spell spell) {
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
    

    private void makeTitle(final TextView Spell_Title, final TextView infos, final Spell spell, final SpellPerDay spell_per_day, final ViewSwitcher panel,final Context mC) {
        Spell_Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Spell_Title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Spell_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
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

        if (spell_per_day.checkAnyconvertible_available(mC) && !spell.isConverted()){
            Spell_Title.setCompoundDrawablesWithIntrinsicBounds(null, null, changeColor(R.drawable.ic_repeat_black_24dp,Color.parseColor("#088A29")), null);
            Spell_Title.setCompoundDrawablePadding(-Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 25,getResources().getDisplayMetrics())));
            Spell_Title.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= Spell_Title.getRight() - Math.round(TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 25,getResources().getDisplayMetrics()))) {
                            // your action for drawable click event
                            new AlertDialog.Builder(SpellCastActivity.this)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Tu veux utiliser un slot convertible ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Snackbar.make(Spell_Title, "OUAIS ON CONVERTI!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            spell.setConverted(true);
                                            Spell_Title.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                            Spell_Title.setOnTouchListener(null);
                                            switch_page(panel);
                                            construct_convertview(panel.getCurrentView(),spell,spell_per_day,Spell_Title,infos,panel,mC);
                                        }})
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }}).show();
                            return true;
                        }
                    }
                    return true;
                }
            });
        }

    }


    private void construct_convertview(final View currentView,final  Spell spell,final  SpellPerDay spell_per_day,final TextView Spell_Title,final TextView infos,final ViewSwitcher panel,final  Context mC) {
        ViewGroup viewGrp= (ViewGroup) currentView;

        final LinearLayout convert_linear = new LinearLayout(this);
        convert_linear.setGravity(Gravity.CENTER_HORIZONTAL);
        convert_linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        convert_linear.setOrientation(LinearLayout.VERTICAL);

        viewGrp.addView(convert_linear);

        final LinearLayout convert_slots = new LinearLayout(this);
        convert_slots.setGravity(Gravity.CENTER_HORIZONTAL);
        convert_slots.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_slots.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_slots);

        addHsep(convert_linear,4,Color.DKGRAY);

        final LinearLayout convert_choices = new LinearLayout(this);
        convert_choices.setGravity(Gravity.CENTER_HORIZONTAL);
        convert_choices.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_choices.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_choices);

        addHsep(convert_linear,4,Color.DKGRAY);

        final LinearLayout convert_result = new LinearLayout(this);
        convert_result.setGravity(Gravity.CENTER_HORIZONTAL);
        convert_result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_result.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_result);

        addHsep(convert_linear,4,Color.DKGRAY);
        
        final LinearLayout convert_confirm = new LinearLayout(this);
        convert_confirm.setGravity(Gravity.CENTER_HORIZONTAL);
        convert_confirm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_confirm.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_confirm);




        int max_tier=0;
        for(int i=0;i<=4;i++){
            try{
                if (spell_per_day.checkConvertible_available(i,mC)) {max_tier=i;}
            }catch (Exception e){ }
        }

        if (max_tier==0) {return;}

        final List<CheckBox> list_check_rank=new ArrayList<CheckBox>();

        final ListMeta all_meta = new ListMeta(spell,Spell_Title,infos,SpellCastActivity.this);

        for(int i=1;i<=max_tier;i++) {
            final CheckBox tier = new CheckBox(this);
            tier.setText("T" + i + " (" + spell_per_day.getSpell_per_day_rank(i) + ")");
            tier.setTextSize(16);
            tier.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tier.setTextColor(Color.DKGRAY);

            setListnerTierSelect(tier,convert_choices,convert_slots,all_meta,list_check_rank,convert_result,convert_confirm,panel);

            convert_slots.addView(tier);
            list_check_rank.add(tier);

        }

    }

    public void setListnerTierSelect(final CheckBox checkbox, final LinearLayout convert_choice, final LinearLayout convert_slots, final ListMeta all_meta,final List<CheckBox> list_check_rank,final LinearLayout convert_result, final LinearLayout convert_confirm,final  ViewSwitcher panel) {
        int[] colorClickBox = new int[]{Color.DKGRAY, Color.parseColor("#088A29")};
        //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                }, colorClickBox

        );
        checkbox.setButtonTintList(colorStateList);

        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = convert_slots.getChildCount() - 1; i >= 0; i--) {
                    final CheckBox child = (CheckBox) convert_slots.getChildAt(i);
                    child.setTextColor(Color.DKGRAY);
                    child.setChecked(false);
                }
                checkbox.setTextColor(Color.parseColor("#088A29"));
                //chose à faire sur les affichage meta dispo etc
                checkbox.setChecked(true);
                construct_convertview_choices(convert_choice, all_meta, list_check_rank,convert_result,convert_confirm,panel);

            }

        });
    }


    private void construct_convertview_choices(LinearLayout convert_choice, ListMeta all_meta, List<CheckBox> list_check_rank,LinearLayout convert_result,LinearLayout convert_confirm,final  ViewSwitcher panel) {
        convert_choice.removeAllViews();
        convert_result.removeAllViews();
        convert_confirm.removeAllViews();
        Integer selected_rank=0;
        for (CheckBox checkbox : list_check_rank) {
            if (checkbox.isChecked()) {
                selected_rank=to_int(checkbox.getText().toString().substring(1,2));
            }
        }

        final List<CheckBox> list_check_choice=new ArrayList<CheckBox>();

        HorizontalScrollView scroll_meta= new HorizontalScrollView(this);
        scroll_meta.setHorizontalScrollBarEnabled(false);
        scroll_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_choice.addView(scroll_meta);
        final LinearLayout grid=new LinearLayout(this);
        scroll_meta.addView(grid);

        ViewGroup gridGrp= (ViewGroup)grid;
        gridGrp.removeAllViews();

        addVsep(grid,4,Color.GRAY);

        CheckBox checkMeta=new CheckBox(this);
        checkMeta.setText("Métamagie ");
        setListnerChoiceSelect(checkMeta,grid,all_meta,selected_rank,list_check_choice,convert_result,convert_confirm,panel);
        grid.addView(checkMeta);
        list_check_choice.add(checkMeta);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkNLS=new CheckBox(this);
        checkNLS.setText("NLS +"+selected_rank+" ");
        setListnerChoiceSelect(checkNLS,grid,all_meta,selected_rank,list_check_choice,convert_result,convert_confirm,panel);
        grid.addView(checkNLS);
        list_check_choice.add(checkNLS);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkResi=new CheckBox(this);
        checkResi.setText("Test contre Résistance +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkResi,grid,all_meta,selected_rank,list_check_choice,convert_result,convert_confirm,panel);
        grid.addView(checkResi);
        list_check_choice.add(checkResi);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkSauv=new CheckBox(this);
        checkSauv.setText("Test contre Sauvegarde +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkSauv,grid,all_meta,selected_rank,list_check_choice,convert_result,convert_confirm,panel);
        grid.addView(checkSauv);
        list_check_choice.add(checkSauv);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkMax=new CheckBox(this);
        checkMax.setText("Augmentation du Cap +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkMax,grid,all_meta,selected_rank,list_check_choice,convert_result,convert_confirm,panel);
        grid.addView(checkMax);
        list_check_choice.add(checkMax);

        addVsep(grid,4,Color.GRAY);



    }

    public void setListnerChoiceSelect(final CheckBox checkbox, final LinearLayout grid,final ListMeta all_meta,final int selected_rank,final List<CheckBox> list_check_choice,final LinearLayout convert_result,final LinearLayout convert_confirm,final  ViewSwitcher panel) {
        int[] colorClickBox = new int[]{Color.DKGRAY, Color.parseColor("#088A29")};
        //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}  // checked
                }, colorClickBox

        );
        checkbox.setButtonTintList(colorStateList);

        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = grid.getChildCount() - 1; i >= 0; i--) {
                    if (grid.getChildAt(i) instanceof CheckBox) {
                        final CheckBox child = (CheckBox) grid.getChildAt(i);
                        child.setTextColor(Color.DKGRAY);
                        child.setChecked(false);
                    }
                }
                checkbox.setTextColor(Color.parseColor("#088A29"));
                //chose à faire sur les affichage meta dispo etc
                checkbox.setChecked(true);
                triggerChoice(convert_result,all_meta,selected_rank,list_check_choice,convert_confirm,panel);
            }

        });
    }

    private void triggerChoice(LinearLayout convert_result,ListMeta all_meta, int selected_rank, List<CheckBox> list_check_choice,final LinearLayout convert_confirm,final  ViewSwitcher panel) {
    convert_result.removeAllViews();

        for (CheckBox check : list_check_choice)
        {
            if (check.getText().toString().contains("Métamagie") && check.isChecked()){
                construct_convertview_metas(convert_result, all_meta,selected_rank);
                construct_convertview_confirm(convert_confirm,panel);
            }
            if (check.getText().toString().contains("Rési") && check.isChecked()){
                construct_convertview_confirm(convert_confirm,panel);
            }

        }
    }


    private void construct_convertview_metas(LinearLayout layout, ListMeta all_meta, int selected_rank) {
        layout.removeAllViews();
        HorizontalScrollView scroll_meta= new HorizontalScrollView(this);
        scroll_meta.setHorizontalScrollBarEnabled(false);
        scroll_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(scroll_meta);
        final LinearLayout grid2=new LinearLayout(this);
        scroll_meta.addView(grid2);

        ViewGroup grid2Grp= (ViewGroup)grid2;
        grid2Grp.removeAllViews();

        int max_rank = (int) (selected_rank/2.0);

        List<Pair_Meta_Rank> all_meta_rank_list=all_meta.getMeta_Rank(max_rank);

        if (max_rank==0){

            TextView no_meta= new TextView(this);
            no_meta.setText("Aucune métamagie pour ce rang convertible");
            no_meta.setTextColor(Color.GRAY);
            grid2.addView(no_meta);

        } else {

            addVsep(grid2,4,Color.GRAY);

            for (int iter = 0; iter < all_meta_rank_list.size(); iter++) {

                CheckBox checkbox = all_meta_rank_list.get(iter).getMeta().getCheckbox();

                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dostuff
                    }
                });

                if (checkbox.getParent()!=null) {
                    ((ViewGroup)checkbox.getParent()).removeView(checkbox);
                }
                grid2.addView(checkbox);

                ImageButton image = all_meta_rank_list.get(iter).getMeta().getImgageButton();
                image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                image.setForegroundGravity(Gravity.TOP);
                image.setColorFilter(Color.GRAY);
                if (image.getParent()!=null) {
                    ((ViewGroup)image.getParent()).removeView(image);
                }
                grid2.addView(image);

                addVsep(grid2, 4, Color.GRAY);
            }
        }
    }

    private void construct_convertview_confirm(LinearLayout convert_confirm,final ViewSwitcher panel) {
        TextView confirm =new TextView(this);
        confirm.setText("Confirmer cette convertion");
        confirm.setTextSize(14);
        confirm.setTextColor(Color.parseColor("#088A29"));
        confirm.setCompoundDrawablesWithIntrinsicBounds(null, null, changeColor(R.drawable.ic_repeat_black_24dp,Color.parseColor("#088A29")), null);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_page_back(panel);
            }
        });


        if (confirm.getParent()!=null) {
            ((ViewGroup)confirm.getParent()).removeView(confirm);
        }
        convert_confirm.addView(confirm);


    }


    private Drawable changeColor(int img_id, String color) {
        Drawable img = getResources().getDrawable(img_id);
        int iColor = Color.parseColor(color);

        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        img.setColorFilter(colorFilter);
        return img;
    }

    private Drawable changeColor(int img_id, int color) {
        Drawable img = getResources().getDrawable(img_id);
        int iColor = color;

        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        img.setColorFilter(colorFilter);
        return img;
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



