package stellarnear.yfa_dealer;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Spells.BuildMetaList;
import stellarnear.yfa_dealer.Spells.MetaList;
import stellarnear.yfa_dealer.Spells.Metamagic;
import stellarnear.yfa_dealer.Spells.Spell;

/**
 * Created by jchatron on 01/12/2017.
 */

public class ConvertView extends AppCompatActivity {

    private Spell spell;
    private TextView Spell_Title;
    private TextView infos;
    private ViewSwitcher panel;
    private Context mC;
    private Activity mA;
    private LinearLayout convert_slots;
    private LinearLayout convert_choices;
    private LinearLayout convert_result;
    private LinearLayout convert_confirm;
    private MetaList all_meta;
    private Integer selected_rank;
    private List<CheckBox> list_check_rank=new ArrayList<CheckBox>();
    private List<CheckBox> list_check_choice=new ArrayList<CheckBox>();
    private List<CheckBox> meta_selected=new ArrayList<>();

    private Perso yfa = MainActivity.yfa;
    private Tools tools=new Tools();
    private Calculation calculation=new Calculation();


    public ConvertView(View currentView, Spell spell, TextView Spell_Title, TextView infos, ViewSwitcher panel, Context mC, Activity mA) {
        this.spell=spell;
        this.Spell_Title=Spell_Title;
        this.infos=infos;
        this.panel=panel;
        this.mC=mC;
        this.mA=mA;

        ViewGroup viewGrp= (ViewGroup) currentView;

        final LinearLayout convert_linear = new LinearLayout(mC);
        convert_linear.setGravity(Gravity.CENTER_HORIZONTAL);
        convert_linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        convert_linear.setWeightSum(4);
        convert_linear.setOrientation(LinearLayout.VERTICAL);

        viewGrp.addView(convert_linear);

        final LinearLayout convert_slots = new LinearLayout(mC);
        convert_slots.setGravity(Gravity.CENTER);
        convert_slots.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
        convert_slots.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_slots);
        this.convert_slots=convert_slots;

        addHsep(convert_linear,4, Color.DKGRAY);

        final LinearLayout convert_choices = new LinearLayout(mC);
        convert_choices.setGravity(Gravity.CENTER);
        convert_choices.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
        convert_choices.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_choices);
        this.convert_choices=convert_choices;
        resetChoice();


        addHsep(convert_linear,4,Color.DKGRAY);

        final LinearLayout convert_result = new LinearLayout(mC);
        convert_result.setGravity(Gravity.CENTER);
        convert_result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
        convert_result.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_result);
        this.convert_result=convert_result;
        resetResult();


        addHsep(convert_linear,4,Color.DKGRAY);

        final LinearLayout convert_confirm = new LinearLayout(mC);
        convert_confirm.setGravity(Gravity.CENTER);
        convert_confirm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
        convert_confirm.setOrientation(LinearLayout.HORIZONTAL);
        convert_linear.addView(convert_confirm);
        this.convert_confirm=convert_confirm;
        resetConfirm();

        this.all_meta=BuildMetaList.getInstance(mC).getMetaList();

        constructTierlist();

    }

    private void resetChoice(){
        resetMeta();
        convert_choices.removeAllViews();
        TextView choice=new TextView(mC);
        choice.setText("Choix de conversion");
        choice.setTextSize(18);
        choice.setTextColor(Color.GRAY);
        convert_choices.addView(choice);
    }
    private void resetResult(){
        resetMeta();
        convert_result.removeAllViews();
        TextView result=new TextView(mC);
        result.setText("Résultat de la conversion");
        result.setTextSize(18);
        result.setTextColor(Color.GRAY);
        convert_result.addView(result);
    }

    private void resetMeta() {
        for (CheckBox check : meta_selected){
            check.setTextColor(Color.GRAY);
            check.setChecked(false);
        }
    }

    private void resetConfirm(){
        convert_confirm.removeAllViews();
        TextView confirm=new TextView(mC);
        confirm.setText("Confirmation de la conversion");
        confirm.setTextSize(18);
        confirm.setTextColor(Color.GRAY);
        convert_confirm.addView(confirm);
    }

    private void constructTierlist() {
        convert_slots.removeAllViews();
        list_check_rank=new ArrayList<CheckBox>();
        int max_tier=0;
        for(int i=0;i<=5;i++){
            try{
                if (yfa.getAllResources().checkConvertibleAvailable(i)) {max_tier=i;}
            }catch (Exception e){ }
        }
        if (max_tier==0) {return;}

        for(int i=1;i<=max_tier;i++) {
            if (yfa.getResourceValue("spell_conv_rank_"+i)==0) {continue;}
            final CheckBox tier = new CheckBox(mC);
            tier.setText("T" + i + " (" + yfa.getResourceValue("spell_conv_rank_"+i) + ")");
            tier.setTextSize(16);
            tier.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tier.setTextColor(Color.DKGRAY);

            setListnerTierSelect(tier);

            if (tier.getParent()!=null) {
                ((ViewGroup)tier.getParent()).removeView(tier);
            }
            convert_slots.addView(tier);
            list_check_rank.add(tier);
        }
    }

    public void setListnerTierSelect(final CheckBox checkbox) {
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
                selected_rank = tools.toInt(checkbox.getText().toString().substring(1, 2));
                if (!yfa.getAllResources().checkAnyConvertibleAvailable()) {
                    switch_page_back(panel);
                } else if (!yfa.getAllResources().checkConvertibleAvailable(selected_rank)) {
                    constructTierlist();
                } else {
                    for (CheckBox check : list_check_rank) {
                        check.setChecked(false);
                        check.setTextColor(Color.DKGRAY);
                    }
                    checkbox.setTextColor(Color.parseColor("#088A29"));
                    //chose à faire sur les affichage meta dispo etc
                    checkbox.setChecked(true);

                    construct_convertview_choices();
                }

            }

        });
    }


    private void construct_convertview_choices() {
        resetResult();resetConfirm();
        convert_choices.removeAllViews();

        list_check_choice=new ArrayList<CheckBox>();

        HorizontalScrollView scroll_meta= new HorizontalScrollView(mC);
        scroll_meta.setHorizontalScrollBarEnabled(false);
        scroll_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_choices.addView(scroll_meta);
        final LinearLayout grid=new LinearLayout(mC);
        scroll_meta.addView(grid);

        ViewGroup gridGrp= (ViewGroup)grid;
        gridGrp.removeAllViews();

        addVsep(grid,4,Color.GRAY);
        CheckBox checkMax=new CheckBox(mC);
        checkMax.setText("Augmentation du Cap +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkMax,grid,list_check_choice);
        grid.addView(checkMax);
        list_check_choice.add(checkMax);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkMeta=new CheckBox(mC);
        checkMeta.setText("Métamagie ");
        setListnerChoiceSelect(checkMeta,grid,list_check_choice);
        grid.addView(checkMeta);
        list_check_choice.add(checkMeta);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkNLS=new CheckBox(mC);
        checkNLS.setText("Niveau de lanceur de sort +"+selected_rank+" ");
        setListnerChoiceSelect(checkNLS,grid,list_check_choice);
        grid.addView(checkNLS);
        list_check_choice.add(checkNLS);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkDissi=new CheckBox(mC);
        checkDissi.setText("Test contre Dissipation +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkDissi,grid,list_check_choice);
        grid.addView(checkDissi);
        list_check_choice.add(checkDissi);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkResi=new CheckBox(mC);
        checkResi.setText("Test contre Résistance +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkResi,grid,list_check_choice);
        grid.addView(checkResi);
        list_check_choice.add(checkResi);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkSauv=new CheckBox(mC);
        checkSauv.setText("Test contre Sauvegarde +"+(int) (selected_rank/2.0)+" ");
        setListnerChoiceSelect(checkSauv,grid,list_check_choice);
        grid.addView(checkSauv);
        list_check_choice.add(checkSauv);

        addVsep(grid,4,Color.GRAY);
    }

    public void setListnerChoiceSelect(final CheckBox checkbox,final LinearLayout grid,final List<CheckBox> list_check_choice) {
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
                triggerChoice();
            }

        });
    }

    private void triggerChoice() {
        resetResult();resetConfirm();
        convert_result.removeAllViews();

        for (CheckBox check : list_check_choice)
        {
            if (check.getText().toString().contains("Métamagie") && check.isChecked()){
                construct_convertview_metas();
            }

            if (check.getText().toString().contains("Sauvegarde") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));

                if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
                    result.setText("Aucun effet sur ce sort");
                } else {
                    String resistance;
                    String new_resistance;
                    int base_resistance =  calculation.saveVal(spell,true);
                    resistance = spell.getSave_type() + "(" + base_resistance + ")";
                    int new_resi_int = calculation.saveVal(spell);
                    new_resistance = spell.getSave_type() + "(" + new_resi_int + ")";

                    result.setText("Test contre Sauvegarde : " + resistance + " > " + new_resistance);
                }

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("lanceur de sort") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                int newNLS=calculation.casterLevel(spell);
                result.setText("Niveau de lanceur de sort : "+calculation.casterLevel(spell,true)+" > "+newNLS);

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("Dissipation") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
                    result.setText("Aucun effet sur ce sort");
                } else {
                    String resistance;
                    String new_resistance;
                    int base_resistance =  calculation.saveVal(spell,true);
                    resistance = spell.getSave_type() + "(" + base_resistance + ")";
                    int new_resi_int = calculation.saveVal(spell);
                    new_resistance = spell.getSave_type() + "(" + new_resi_int + ")";

                    result.setText("Test contre Dissipation : " + resistance + " > " + new_resistance);
                }

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("Résistance") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                int newNLS_resi=calculation.casterLevelSR(spell);
                result.setText("Test contre Résistance : 1d20+"+calculation.casterLevel(spell,true)+" > 1d20+"+newNLS_resi);

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("Cap") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                /*if (spell.getDmg_txt(mC).equals("")) {
                    result.setText("Ajouter " + 2 * selected_rank +" à la variable dépendante du NLS (cf. sort)");
                } else {
                    result.setText("Dégats : " + spell.getDmg_txt(mC) + " > " + spell.getDmg_txt_addDice(mC, selected_rank));
                }*/

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

        }
    }


    private void construct_convertview_metas() {
        resetMeta();
        resetConfirm();
        convert_result.removeAllViews();

        HorizontalScrollView scroll_meta= new HorizontalScrollView(mC);
        scroll_meta.setHorizontalScrollBarEnabled(false);
        scroll_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_result.addView(scroll_meta);
        final LinearLayout grid2=new LinearLayout(mC);
        scroll_meta.addView(grid2);

        ViewGroup grid2Grp= (ViewGroup)grid2;
        grid2Grp.removeAllViews();

        int max_rank = (int) (selected_rank/2.0);

        MetaList metaListAvailable=all_meta.filterMaxRank(max_rank);

        if (max_rank==0){
            TextView no_meta= new TextView(mC);
            no_meta.setText("Aucune métamagie pour ce rang convertible");
            no_meta.setTextColor(Color.GRAY);
            grid2.addView(no_meta);

        } else {

            addVsep(grid2,4,Color.GRAY);
            int[] colorClickBox = new int[]{Color.GRAY, Color.parseColor("#088A29")};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked}, // unchecked
                            new int[]{android.R.attr.state_checked}  // checked
                    }, colorClickBox

            );

            for(final Metamagic meta : metaListAvailable.asList()) {
                final CheckBox checkbox = meta.getCheckBox(mA,mC);
                checkbox.setButtonTintList(colorStateList);

                meta_selected.add(checkbox);

                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (CheckBox check : meta_selected) {
                            check.setTextColor(Color.GRAY);
                            check.setChecked(false);
                        }
                        checkbox.setTextColor(Color.parseColor("#088A29"));
                        checkbox.setChecked(true);
                        construct_convertview_confirm();
                    }
                });

                if (checkbox.getParent()!=null) {
                    ((ViewGroup)checkbox.getParent()).removeView(checkbox);
                }
                grid2.addView(checkbox);

                ImageButton image = new ImageButton(mC);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tools.customToast(mC,meta.getDescription(),"center");
                    }
                });
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

    private void construct_convertview_confirm() {
        convert_confirm.removeAllViews();

        TextView confirm =new TextView(mC);
        confirm.setText("Confirmer cette convertion");
        confirm.setTextSize(18);
        confirm.setTextColor(Color.parseColor("#088A29"));
        confirm.setCompoundDrawablesWithIntrinsicBounds(null, null, mC.getDrawable(R.drawable.ic_repeat_black_24dp), null);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyConvert(); //si c'etait une méta elle est appliqué direct par la meta mais pour les autres il faut faire les cas
                switch_page_back(panel);
            }
        });

        if (confirm.getParent()!=null) {
            ((ViewGroup)confirm.getParent()).removeView(confirm);
        }
        convert_confirm.addView(confirm);


    }

    private void applyConvert() {
        // on se sert list_check_choice;
        // on modifie spell en fonction
        for (CheckBox check : list_check_choice)
        {
            if (check.getText().toString().contains("Sauvegarde") && check.isChecked()){
                //spell.conv_Sauv(selected_rank);
            }

            if (check.getText().toString().contains("lanceur de sort") && check.isChecked()){
                //spell.conv_NLS(selected_rank);
            }

            if (check.getText().toString().contains("Cap") && check.isChecked()){
                //spell.conv_Cap(selected_rank);
            }

            if (check.getText().toString().contains("Résistance") && check.isChecked()){
                //spell.setRMConverted(selected_rank*2);
            }
        }
        //spell.storeOri();
        refreshAllTexts(Spell_Title,spell,infos,mC);
        yfa.castConvSpell(selected_rank);
    }

    private void refreshAllTexts(final TextView Spell_Title, final Spell spell,final TextView infos, final Context mC){
        refreshInfos(infos, spell,mC);
        refreshTitle( Spell_Title,spell, mC);
        //refreshRound();
    }

    private void refreshInfos(final TextView infos, final Spell spell, final Context mC) {
        String text="";
        Integer n_inf=0;

        /*
        if(!spell.getDmg_txt(mC).equals("")){
            text+="Dégats : "+spell.getDmg_txt(mC)+", ";
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

        if(!spell.getDuration(mC).equals("")){
            text+="Durée : "+spell.getDuration(mC)+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";n_inf=0;}

        if(!spell.getRM().equals("")){
            text+="RM : "+spell.getRM()+", ";
            n_inf+=1;
        }
        if(n_inf==3){text+="\n";}

        String resistance;
        if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
            resistance = spell.getSave_type();

        } else {
            resistance = spell.getSave_type() + "(" + spell.getSave_val() + ")";
        }
        if(!resistance.equals("")){
            text+="Jet de sauv : "+ resistance+", ";
        }


        text = text.substring(0, text.length() - 2);
        if(spell.getDmg_txt(mC).equals("")){text+="\n";}
        */

        infos.setText(text);
    }


    private void refreshTitle(final TextView Spell_Title, final Spell spell, final Context mC) {
        String titre_texte=spell.getName()+" (rang : "+spell.getRank()+")";
        SpannableString titre=  new SpannableString(titre_texte);
        titre.setSpan(new RelativeSizeSpan(2f), 0,spell.getName().length(), 0); // set size1
        titre.setSpan(new ForegroundColorSpan(Color.BLACK), 0,spell.getName().length(), 0);// set color1

        if(yfa.getAllResources().checkConvertibleAvailable(spell.getRank())){
            titre.setSpan(new ForegroundColorSpan(Color.BLACK),spell.getName().length(),titre_texte.length(), 0);// set color2
        } else {
            titre.setSpan(new ForegroundColorSpan(mC.getColor(R.color.warning)),spell.getName().length(),titre_texte.length(), 0);// set color2
        }
        Spell_Title.setText(titre);
    }


    private void switch_page_back(ViewSwitcher panel) {
        setAnimPanel(panel,"retour");
        panel.showPrevious();
        setAnimPanel(panel,"aller");
    }

    private void setAnimPanel(ViewSwitcher panel, String mode) {
        if (mode.equals("aller")) {
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
        }else {
            Animation outtoRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, +1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation de sortie vers la gauche
            outtoRight.setDuration(500);
            outtoRight.setInterpolator(new AccelerateInterpolator());

            Animation inFromLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);            //animation d'entree par la droite
            inFromLeft.setDuration(500);
            inFromLeft.setInterpolator(new AccelerateInterpolator());

            panel.setInAnimation(inFromLeft);
            panel.setOutAnimation(outtoRight);
        }
    }


    private void addHsep(LinearLayout lay, int e, int Color) {

        View h_sep_meta = new View(mC);
        if (h_sep_meta.getParent()!=null) {
            ((ViewGroup)h_sep_meta.getParent()).removeView(h_sep_meta);
        }

        h_sep_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,e));
        h_sep_meta.setBackgroundColor(Color);


        lay.addView(h_sep_meta);
    }

    private void addVsep(LinearLayout lay, int e, int Color) {
        View v_sep_meta = new View(mC);
        if (v_sep_meta.getParent()!=null) {
            ((ViewGroup)v_sep_meta.getParent()).removeView(v_sep_meta);
        }

        v_sep_meta.setLayoutParams(new LinearLayout.LayoutParams(e,LinearLayout.LayoutParams.MATCH_PARENT));
        v_sep_meta.setBackgroundColor(Color);


        lay.addView(v_sep_meta);
    }

}
