package stellarnear.yfa_dealer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jchatron on 01/12/2017.
 */

public class ConvertView extends AppCompatActivity {

   View currentView;
   Spell spell;
   SpellPerDay spell_per_day;
   TextView Spell_Title;
   TextView infos;
   ViewSwitcher panel;
   Context mC;
   LinearLayout convert_slots;
   LinearLayout convert_choices;
   LinearLayout convert_result;
   LinearLayout convert_confirm;
   ListMeta all_meta;
   Integer selected_rank;


    public ConvertView(View currentView, Spell spell, SpellPerDay spell_per_day, TextView Spell_Title, TextView infos, ViewSwitcher panel, Context mC) {
        this.currentView=currentView;
        this.spell=spell;
        this.spell_per_day=spell_per_day;
        this.Spell_Title=Spell_Title;
        this.infos=infos;
        this.panel=panel;
        this.mC=mC;

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


        final ListMeta all_meta = new ListMeta(spell,Spell_Title,infos,mC);
        this.all_meta=all_meta;

        int max_tier=0;
        for(int i=0;i<=4;i++){
            try{
                if (spell_per_day.checkConvertible_available(i,mC)) {max_tier=i;}
            }catch (Exception e){ }
        }

        if (max_tier==0) {return;}

        final List<CheckBox> list_check_rank=new ArrayList<CheckBox>();

        for(int i=1;i<=max_tier;i++) {
            final CheckBox tier = new CheckBox(mC);
            tier.setText("T" + i + " (" + spell_per_day.getSpell_per_day_rank(i) + ")");
            tier.setTextSize(16);
            tier.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tier.setTextColor(Color.DKGRAY);

            setListnerTierSelect(tier,list_check_rank);

            convert_slots.addView(tier);
            list_check_rank.add(tier);

        }

    }

    private void resetChoice(){
        convert_choices.removeAllViews();
        TextView choice=new TextView(mC);
        choice.setText("Choix de conversion");
        choice.setTextSize(18);
        choice.setTextColor(Color.GRAY);
        convert_choices.addView(choice);
    }
    private void resetResult(){
        convert_result.removeAllViews();
        TextView result=new TextView(mC);
        result.setText("Résultat de la conversion");
        result.setTextSize(18);
        result.setTextColor(Color.GRAY);
        convert_result.addView(result);
    }
    private void resetConfirm(){
        convert_confirm.removeAllViews();
        TextView confirm=new TextView(mC);
        confirm.setText("Confirmation de la conversion");
        confirm.setTextSize(18);
        confirm.setTextColor(Color.GRAY);
        convert_confirm.addView(confirm);
    }



    public void setListnerTierSelect(final CheckBox checkbox,final List<CheckBox> list_check_rank) {
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
                selected_rank=0;
                for (CheckBox checkbox : list_check_rank) {
                    if (checkbox.isChecked()) {
                        selected_rank=to_int(checkbox.getText().toString().substring(1,2));
                    }
                }
                for (int i = convert_slots.getChildCount() - 1; i >= 0; i--) {
                    final CheckBox child = (CheckBox) convert_slots.getChildAt(i);
                    child.setTextColor(Color.DKGRAY);
                    child.setChecked(false);
                }
                checkbox.setTextColor(Color.parseColor("#088A29"));
                //chose à faire sur les affichage meta dispo etc
                checkbox.setChecked(true);
                construct_convertview_choices();

            }

        });
    }


    private void construct_convertview_choices() {
        resetResult();resetConfirm();
        convert_choices.removeAllViews();

        final List<CheckBox> list_check_choice=new ArrayList<CheckBox>();

        HorizontalScrollView scroll_meta= new HorizontalScrollView(mC);
        scroll_meta.setHorizontalScrollBarEnabled(false);
        scroll_meta.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        convert_choices.addView(scroll_meta);
        final LinearLayout grid=new LinearLayout(mC);
        scroll_meta.addView(grid);

        ViewGroup gridGrp= (ViewGroup)grid;
        gridGrp.removeAllViews();

        addVsep(grid,4,Color.GRAY);

        CheckBox checkMeta=new CheckBox(mC);
        checkMeta.setText("Métamagie ");
        setListnerChoiceSelect(checkMeta,grid,list_check_choice);
        grid.addView(checkMeta);
        list_check_choice.add(checkMeta);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkNLS=new CheckBox(mC);
        checkNLS.setText("NLS +"+selected_rank+" ");
        setListnerChoiceSelect(checkNLS,grid,list_check_choice);
        grid.addView(checkNLS);
        list_check_choice.add(checkNLS);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkResi=new CheckBox(mC);
        checkResi.setText("Test contre Résistance +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkResi,grid,list_check_choice);
        grid.addView(checkResi);
        list_check_choice.add(checkResi);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkSauv=new CheckBox(mC);
        checkSauv.setText("Test contre Sauvegarde +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkSauv,grid,list_check_choice);
        grid.addView(checkSauv);
        list_check_choice.add(checkSauv);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkMax=new CheckBox(mC);
        checkMax.setText("Augmentation du Cap +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkMax,grid,list_check_choice);
        grid.addView(checkMax);
        list_check_choice.add(checkMax);

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
                triggerChoice(list_check_choice);
            }

        });
    }

    private void triggerChoice(List<CheckBox>list_check_choice) {
        resetResult();resetConfirm();

        for (CheckBox check : list_check_choice)
        {
            if (check.getText().toString().contains("Métamagie") && check.isChecked()){
                construct_convertview_metas();
                construct_convertview_confirm();
            }
            if (check.getText().toString().contains("Rési") && check.isChecked()){
                construct_convertview_confirm();
            }

        }
    }


    private void construct_convertview_metas() {
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

        List<Pair_Meta_Rank> all_meta_rank_list=all_meta.getMeta_Rank(max_rank);

        if (max_rank==0){

            TextView no_meta= new TextView(mC);
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

    private void construct_convertview_confirm() {
        convert_confirm.removeAllViews();


        TextView confirm =new TextView(mC);
        confirm.setText("Confirmer cette convertion");
        confirm.setTextSize(18);
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
        Drawable img = mC.getResources().getDrawable(img_id);
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
        Drawable img = mC.getResources().getDrawable(img_id);
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
    public Integer to_int(String key){
        Integer value;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e){
            value=0;
        }
        return value;
    }


}
