package stellarnear.yfa_dealer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Rolls.Dice;
import stellarnear.yfa_dealer.Spells.Spell;


public class GlaeTestAlertDialog {
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private View dialogView;
    private Spell spell;
    private Dice dice;

    private Boolean boost=false;
    private Boolean fail=false;
    private Perso yfa = MainActivity.yfa;

    private OnEndEventListener mListener;

    private Tools tools = new Tools();

    public GlaeTestAlertDialog(Activity mA, Context mC, Spell spell) {
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        buildAlertDialog();
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        ImageView icon = dialogView.findViewById(R.id.customDialogTestIcon);

        if(spell.getDmg_type().equalsIgnoreCase("foudre")){
            this.boost=true; this.fail=false;
            icon.setImageDrawable(mC.getDrawable(R.drawable.ic_crowned_explosion));
        } else {
            this.fail=true; this.boost=false;
            icon.setImageDrawable(mC.getDrawable(R.drawable.ic_thunder_skull));
        }

        String titleTxt = "Test de Glaedäyes :\n";
        final TextView title = dialogView.findViewById(R.id.customDialogTestTitle);
        title.setSingleLine(false);
        title.setText(titleTxt);


        String summaryTxt="Test de ";
        if(spell.getDmg_type().equalsIgnoreCase("foudre")){
            summaryTxt+=" surpuissance de la foudre";
        } else {
            summaryTxt+=" réprobation de l'élément "+spell.getDmg_type();
        }
        TextView summary = dialogView.findViewById(R.id.customDialogTestSummary);
        summary.setText(summaryTxt);

        TextView detail = dialogView.findViewById(R.id.customDialogTestDetail);
        detail.setVisibility(View.GONE);

        Button diceroll = dialogView.findViewById(R.id.button_customDialog_test_diceroll);
        diceroll.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextView)dialogView.findViewById(R.id.customDialogTestResult)).getText().equals("")){
                    startRoll();
                }
            }
        });

        AlertDialog.Builder dialogBuilder  = new AlertDialog.Builder(mA, R.style.CustomDialog);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked cancel button
            }
        });
        alertDialog = dialogBuilder.create();
    }

    private void startRoll() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        yfa.getAllResources().getResource("true_strike").spend(1);
        dice = new Dice(mA,mC,20);
        if (settings.getBoolean("switch_manual_diceroll",mC.getResources().getBoolean(R.bool.switch_manual_diceroll_DEF))){
            dice.rand(true);
            dice.setRefreshEventListener(new Dice.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    endSkillCalculation(dice);
                }
            });
        } else {
            dice.rand(false);
            endSkillCalculation(dice);
        }
    }

    public void showAlertDialog(){
        alertDialog.show();

        Button failButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams onlyButtonLL = (LinearLayout.LayoutParams) failButton.getLayoutParams();
        onlyButtonLL.width=ViewGroup.LayoutParams.WRAP_CONTENT;
        onlyButtonLL.setMargins(10,0,10,0);
        failButton.setLayoutParams(onlyButtonLL);
        failButton.setTextColor(mC.getColor(R.color.colorBackground));
        failButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));

        Display display = mA.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Float factor = mC.getResources().getInteger(R.integer.percent_fullscreen_customdialog)/100f;
        alertDialog.getWindow().setLayout((int) (factor*size.x), (int)(factor*size.y));
    }

    private void endSkillCalculation(final Dice dice) {
        FrameLayout resultDice= dialogView.findViewById(R.id.customDialogTestResultDice);
        resultDice.removeAllViews();
        resultDice.addView(dice.getImg());
        dice.delt();

        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogTestCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));

        resultTitle.setText("Résultat du test :");

        String resultMessage="Le sort est normal";
        if(dice.getRandValue()==1 && this.fail){resultMessage="Le sort rate...";}
        if(dice.getRandValue()==20 && this.boost){resultMessage="Le sort est boosté !";}

        final TextView result = dialogView.findViewById(R.id.customDialogTestResult);
        result.setText(String.valueOf(resultMessage));
        result.setTextSize(35);

        callToAction.setText("Fin du test.");

        Button failButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        failButton.setText("Ok");
        failButton.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dice.getRandValue()==1 && fail){
                    spell.makeGlaeFail();
                } else if (dice.getRandValue() == 20 && boost) {
                    spell.makeGlaeBoost();
                    tools.customToast(mC, "Que la puissance de Glaedäyes retentisse !","center");
                }
                spell.getGlaeManager().setTested();
                if (mListener != null) {
                    mListener.onEvent();
                }
                alertDialog.dismiss();
            }
        });
    }

    public interface OnEndEventListener {
        void onEvent();
    }

    public void setEndEventListener(OnEndEventListener eventListener) {
        mListener = eventListener;
    }

}

