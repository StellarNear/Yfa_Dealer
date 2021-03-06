package stellarnear.yfa_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Spells.Spell;

/**
 * Created by jchatron on 01/12/2017.
 */

public class ConvertElementView extends AppCompatActivity {

    private Spell spell;
    private Context mC;
    private Activity mA;
    private LinearLayout title;
    private LinearLayout convert_element_choices;
    private LinearLayout convert_element_result;
    private LinearLayout convert_element_confirm;
    private Tools tools=Tools.getTools();
    private OnValidationEventListener mListener;
    private Perso yfa = MainActivity.yfa;

    private List<CheckBox> listAllElements = new ArrayList<CheckBox>();
    private String selectedElement;

    public ConvertElementView(View mainView, Spell spell, Context mC, Activity mA) {
        this.spell=spell;
        this.mC=mC;
        this.mA=mA;

        title = mainView.findViewById(R.id.convert_elem_slots);
        convert_element_choices = mainView.findViewById(R.id.convert_elem_choices);
        convert_element_result = mainView.findViewById(R.id.convert_elem_result);
        resetResult();
        convert_element_confirm = mainView.findViewById(R.id.convert_elem_confirm);
        resetConfirm();

        constructTitle();
        constructElementChoice();
    }

    public interface OnValidationEventListener {
        void onEvent();
    }

    public void setValidationEventListener(OnValidationEventListener eventListener) {
        mListener = eventListener;
    }

    private void resetResult(){
        convert_element_result.removeAllViews();
        TextView result=new TextView(mC);
        result.setText("Résultat du changement");
        result.setTextSize(18);
        result.setTextColor(Color.GRAY);
        convert_element_result.addView(result);
    }

    private void resetConfirm(){
        convert_element_confirm.removeAllViews();
        TextView confirm=new TextView(mC);
        confirm.setText("Confirmation du changement");
        confirm.setTextSize(18);
        confirm.setTextColor(Color.GRAY);
        convert_element_confirm.addView(confirm);
    }

    private void constructTitle() {
        title.removeAllViews();
        TextView titleTxt=new TextView(mC);
        titleTxt.setText("Changement d'élément");
        titleTxt.setTextSize(18);
        titleTxt.setTextColor(Color.DKGRAY);
        title.addView(titleTxt);
    }

    private void constructElementChoice() {
        convert_element_choices.removeAllViews();
        List<String> listElement = Arrays.asList("frost","fire","shock","acid");
        for(String elemTxt : listElement) {

            final CheckBox elem = new CheckBox(mC);
            elem.setText(elemTxt);
            elem.setTextSize(16);
            elem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            elem.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            elem.setTextColor(Color.DKGRAY);

            listAllElements.add(elem);
            setListnerSelect(elem);
            if (spell.getDmg_type().equalsIgnoreCase(elemTxt)){
                elem.setChecked(true);
            }

            if (elem.getParent() != null) {
                ((ViewGroup) elem.getParent()).removeView(elem);
            }
            convert_element_choices.addView(elem);
        }
    }

    public void setListnerSelect(final CheckBox checkbox) {
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
                for (CheckBox check : listAllElements) {
                    check.setChecked(false);
                    check.setTextColor(Color.DKGRAY);
                }
                checkbox.setTextColor(Color.parseColor("#088A29"));
                //chose à faire sur les affichage meta dispo etc
                checkbox.setChecked(true);

                selectedElement = checkbox.getText().toString();
                triggerChoice();
            }

        });
    }
    private void triggerChoice() {
        convert_element_result.removeAllViews();
        TextView currentChoice =new TextView(mC);
        currentChoice.setTextSize(18);
        if(selectedElement.equalsIgnoreCase(spell.getDmg_type())){
            currentChoice.setText("Aucun changement");
            currentChoice.setTextColor(Color.DKGRAY);
        } else {
            currentChoice.setText(spell.getDmg_type() + " > " + selectedElement);
            currentChoice.setTextColor(Color.parseColor("#088A29"));
        }
        convert_element_result.addView(currentChoice);
        constructConvertElementview_confirm();
    }

    private void constructConvertElementview_confirm() {
        TextView confirm =new TextView(mC);
        confirm.setTextSize(18);
        convert_element_confirm.removeAllViews();
        if(selectedElement.equalsIgnoreCase(spell.getDmg_type())){
            confirm.setText("Rien à changer");
            confirm.setTextColor(Color.DKGRAY);
        } else {
            confirm.setText("Confirmer cette convertion");
            confirm.setTextColor(Color.parseColor("#088A29"));
            confirm.setCompoundDrawablesWithIntrinsicBounds(null, null, mC.getDrawable(R.drawable.ic_repeat_black_24dp), null);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(yfa.getResourceValue("resource_mythic_points")>0) {
                        new AlertDialog.Builder(mA)
                                .setTitle("Demande de confirmation")
                                .setMessage("Ce changement dépensera un point mythique (tu en as actuellement "+yfa.getResourceValue("resource_mythic_points")+")\n\nConfirmes-tu ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        applyConvertElement(); //si c'etait une méta elle est appliqué direct par la meta mais pour les autres il faut faire les cas
                                        mListener.onEvent();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    } else {
                        tools.customToast(mC, "Il ne te reste aucun point mythique", "center");
                    }
                }
            });
        }

        if (confirm.getParent()!=null) {
            ((ViewGroup)confirm.getParent()).removeView(confirm);
        }
        convert_element_confirm.addView(confirm);

    }

    private void applyConvertElement() {
        spell.setDmgType(selectedElement);
        title.removeAllViews();
        convert_element_choices.removeAllViews();
        convert_element_result.removeAllViews();
        convert_element_confirm.removeAllViews();
    }
}
