package stellarnear.yfa_dealer;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
    private Context mC;
    private Activity mA;
    private ViewSwitcher panel;
    private LinearLayout convert_slots;
    private LinearLayout convert_choices;
    private LinearLayout convert_result;
    private LinearLayout convert_confirm;
    private MetaList all_meta;
    private Metamagic currentMetaSelected;
    private Integer selected_rank;
    private List<CheckBox> list_check_rank=new ArrayList<CheckBox>();
    private List<CheckBox> list_check_choice=new ArrayList<CheckBox>();
    private List<CheckBox> meta_selected=new ArrayList<>();

    private Perso yfa = MainActivity.yfa;
    private Tools tools=new Tools();
    private Calculation calculation=new Calculation();

    private OnValidationEventListener mListener;

    public ConvertView(ViewSwitcher panel, Spell spell, Context mC,Activity mA) {
        this.spell=spell;
        this.mC=mC;
        this.mA=mA;
        this.panel=panel;

        convert_slots = panel.findViewById(R.id.convert_slots);
        convert_choices = panel.findViewById(R.id.convert_choices);
        resetChoice();

        convert_result= panel.findViewById(R.id.convert_result);
        resetResult();

        convert_confirm = panel.findViewById(R.id.convert_confirm);

        resetConfirm();

        this.all_meta=BuildMetaList.getInstance(mC).getMetaList();

        constructTierlist();
    }

    public interface OnValidationEventListener {
        void onEvent();
    }

    public void setValidationEventListener(OnValidationEventListener eventListener) {
        mListener = eventListener;
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
        currentMetaSelected=null;
        for (CheckBox check : meta_selected){
            check.setTextColor(Color.DKGRAY);
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
        for(int i=0;i<=6;i++){
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
                if (!yfa.getAllResources().checkConvertibleAvailable(selected_rank)) {
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


        CheckBox checkMax=new CheckBox(mC);
        checkMax.setText("Cap +"+2*selected_rank+" ");//checkMax.setText("Augmentation du Cap +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkMax,grid,list_check_choice);
        grid.addView(checkMax);
        list_check_choice.add(checkMax);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkMeta=new CheckBox(mC);
        checkMeta.setText("Métamagie ");//checkMeta.setText("Métamagie ");
        setListnerChoiceSelect(checkMeta,grid,list_check_choice);
        grid.addView(checkMeta);
        list_check_choice.add(checkMeta);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkNLS=new CheckBox(mC);
        checkNLS.setText("NLS +"+selected_rank+" ");//checkNLS.setText("Niveau de lanceur de sort +"+selected_rank+" ");
        setListnerChoiceSelect(checkNLS,grid,list_check_choice);
        grid.addView(checkNLS);
        list_check_choice.add(checkNLS);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkDissi=new CheckBox(mC);
        checkDissi.setText("Dissipation +"+2*selected_rank+" ");//checkDissi.setText("Test contre Dissipation +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkDissi,grid,list_check_choice);
        grid.addView(checkDissi);
        list_check_choice.add(checkDissi);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkResi=new CheckBox(mC);
        checkResi.setText("RM +"+2*selected_rank+" ");//checkResi.setText("Test contre Résistance +"+2*selected_rank+" ");
        setListnerChoiceSelect(checkResi,grid,list_check_choice);
        grid.addView(checkResi);
        list_check_choice.add(checkResi);

        addVsep(grid,4,Color.GRAY);

        CheckBox checkSauv=new CheckBox(mC);
        checkSauv.setText("JDS +"+(int) (selected_rank/2.0)+" ");//checkSauv.setText("Test contre Sauvegarde +"+(int) (selected_rank/2.0)+" ");
        setListnerChoiceSelect(checkSauv,grid,list_check_choice);
        grid.addView(checkSauv);
        list_check_choice.add(checkSauv);

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
            if (check.getText().toString().contains("Méta") && check.isChecked()){
                construct_convertview_metas();
            }

            if (check.getText().toString().contains("JDS") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));

                if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
                    result.setText("Aucun effet sur ce sort");
                } else {
                    String resistance;
                    String new_resistance;
                    int base_resistance =  calculation.saveVal(spell);
                    resistance = spell.getSave_type() + "(" + base_resistance + ")";
                    new_resistance = spell.getSave_type() + "(" + String.valueOf(base_resistance + (int) (selected_rank/2.0)) + ")";

                    result.setText("Test contre Sauvegarde : " + resistance + " > " + new_resistance);
                }

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("NLS") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                int NLS=calculation.casterLevel(spell);
                result.setText("Niveau de lanceur de sort : "+ NLS +" > "+String.valueOf(NLS +selected_rank));

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("Dissi") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
                    result.setText("Aucun effet sur ce sort");
                } else {
                    String resistance;
                    String new_resistance;
                    int base_resistance =  calculation.saveVal(spell);
                    resistance = spell.getSave_type() + "(" + base_resistance + ")";
                    new_resistance = spell.getSave_type() + "(" + String.valueOf(base_resistance+2*selected_rank) + ")";

                    result.setText("Test contre Dissipation : " + resistance + " > " + new_resistance);
                }

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("RM") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                int newNLS_resi=calculation.casterLevelSR(spell)+selected_rank*2;
                result.setText("Test contre Résistance : 1d20+"+calculation.casterLevel(spell)+" > 1d20+"+newNLS_resi);

                if (result.getParent()!=null) {
                    ((ViewGroup)result.getParent()).removeView(result);
                }
                convert_result.addView(result);

                construct_convertview_confirm();

            }

            if (check.getText().toString().contains("Cap") && check.isChecked()){
                TextView result = new TextView(mC);
                result.setTextColor(Color.parseColor("#088A29"));
                if (calculation.nDice(spell)<=0) {
                    result.setText("Ajouter " + 2 * selected_rank +" à la variable dépendante du NLS (cf. sort)");
                } else if (spell.getDice_type().equalsIgnoreCase("lvl") || spell.getN_dice_per_lvl()==0) {
                    result.setText("Aucun effet sur ce sort");
                }  else {
                    result.setText("Dégats : " + calculation.nDice(spell)+"d"+calculation.diceType(spell) + " > " + String.valueOf(calculation.nDice(spell)+2*selected_rank)+"d"+calculation.diceType(spell) );
                }

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
            int[] colorClickBox = new int[]{Color.DKGRAY, Color.parseColor("#088A29")};
            //if(!dmg_spell){colorClickBox=new int[]{Color.GRAY,Color.GRAY};checkbox.setTextColor(Color.GRAY);}

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked}, // unchecked
                            new int[]{android.R.attr.state_checked}  // checked
                    }, colorClickBox

            );

            boolean firstMeta=true;
            for(final Metamagic meta : metaListAvailable.asList()) {
                final CheckBox checkbox = new CheckBox(mC);
                CheckBox oriCheckbox =   spell.getCheckboxeForMetaId(mA,mC,meta.getId());
                if(!oriCheckbox.isEnabled()){continue;} //meta non dispo pour ce spell
                checkbox.setText(oriCheckbox.getText());
                checkbox.setButtonTintList(colorStateList);
                checkbox.setTextColor(Color.DKGRAY);
                if(!firstMeta){
                    addVsep(grid2, 4, Color.GRAY);
                }
                firstMeta=false;
                meta_selected.add(checkbox);
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkbox.setTextColor(Color.parseColor("#088A29"));
                        currentMetaSelected=meta;
                        checkbox.setEnabled(false); //evite de deselectionner
                        for (CheckBox check : meta_selected) {
                            if(check!=checkbox) {
                                check.setTextColor(Color.DKGRAY);
                                check.setChecked(false);
                                check.setEnabled(true);
                            }
                        }
                        construct_convertview_confirm();
                    }
                });

                if (checkbox.getParent()!=null) {
                    ((ViewGroup)checkbox.getParent()).removeView(checkbox);
                }
                grid2.addView(checkbox);

                ImageButton image = new ImageButton(mC);
                image.setImageDrawable(mC.getDrawable(R.drawable.ic_info_outline_black_24dp));
                LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                image.setLayoutParams(para);
                image.setForegroundGravity(Gravity.CENTER);
                image.setPadding(15, 0, 0, 0);
                image.setColorFilter(Color.GRAY);
                image.setBackgroundColor(mC.getColor(R.color.transparent));
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tools.customToast(mC,meta.getDescription(),"center");
                    }
                });
                if (image.getParent()!=null) {
                    ((ViewGroup)image.getParent()).removeView(image);
                }
                grid2.addView(image);


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
                mListener.onEvent();
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
            if (check.getText().toString().contains("JDS") && check.isChecked()){
                spell.getConversion().setArcaneId("save");
                spell.getConversion().setRank(selected_rank);
            } else if (check.getText().toString().contains("NLS") && check.isChecked()){
                spell.getConversion().setArcaneId("caster_level");
                spell.getConversion().setRank(selected_rank);
            }else if (check.getText().toString().contains("Cap") && check.isChecked()){
                spell.getConversion().setArcaneId("raise_cap");
                spell.getConversion().setRank(selected_rank);
            }else if (check.getText().toString().contains("RM") && check.isChecked()){
                spell.getConversion().setArcaneId("spell_resistance");
                spell.getConversion().setRank(selected_rank);
            }else if (check.getText().toString().contains("Dissipation") && check.isChecked()){
                spell.getConversion().setArcaneId("dispel");
                spell.getConversion().setRank(selected_rank);
            }else if (check.getText().toString().contains("Métamagie") && check.isChecked()){
                spell.getConversion().setArcaneId("metamagic_"+currentMetaSelected.getId());
                spell.getConversion().setRank(selected_rank);
                spell.getMetaList().activateFromConversion(currentMetaSelected.getId());
            }
        }
        convert_slots.removeAllViews();
        convert_choices.removeAllViews();
        convert_result.removeAllViews();
        convert_confirm.removeAllViews();
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
