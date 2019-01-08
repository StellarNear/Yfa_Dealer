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
import stellarnear.yfa_dealer.Rolls.WheelDicePicker;
import stellarnear.yfa_dealer.Spells.Spell;


public class TestAlertDialog {
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogWheelPicker;
    private WheelDicePicker wheelPicker;
    private View dialogView;
    private View dialogViewWheelPicker;
    private Spell spell;
    private Calculation calculation=new Calculation();
    private int sumScore;

    private boolean robe=false;

    public TestAlertDialog(Activity mA, Context mC, Spell spell) {
        this.mA=mA;
        this.mC=mC;
        this.spell = spell;
        this.sumScore = 0;
        buildAlertDialog();
        showAlertDialog();
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        ImageView icon = dialogView.findViewById(R.id.customDialogTestIcon);
        icon.setImageDrawable(mC.getDrawable(R.drawable.ic_surrounded_shield));

        String titleTxt = "Test du niveau de lanceur de sort :\n";
        TextView title = dialogView.findViewById(R.id.customDialogTestTitle);
        title.setSingleLine(false);
        title.setText(titleTxt);


        sumScore= calculation.casterLevelSR(spell);
        Perso yfa =MainActivity.yfa;
        robe = yfa.getInventory().getAllEquipments().getEquipmentsEquiped("armor_slot").getName().equalsIgnoreCase("Robe d'archimage grise");
        if(robe){
            sumScore+=2;
        }
        String summaryTxt="Test contre RM : "+String.valueOf(sumScore);
        TextView summary = dialogView.findViewById(R.id.customDialogTestSummary);
        summary.setText(summaryTxt);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        String summaryDetail="";
        summaryDetail="Niveau lanceur de sort : "+String.valueOf(calculation.casterLevelSR(spell))+" "; //oui c'est moche il faut gerer tout les calcul au niveau du perso proprement ...

        if(robe){ summaryDetail+=", Robe d'archimage grise (+2)";}


        TextView detail = dialogView.findViewById(R.id.customDialogTestDetail);
        detail.setText(summaryDetail);

        Button diceroll = dialogView.findViewById(R.id.button_customDialog_test_diceroll);
        diceroll.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((TextView)dialogView.findViewById(R.id.customDialogTestResult)).getText().equals("")){
                    startRoll();
                } else {
                    new AlertDialog.Builder(mA)
                            .setIcon(R.drawable.ic_warning_black_24dp)
                            .setTitle("Demande de confirmation")
                            .setMessage("Es-tu sûre de vouloir te relancer ce jet ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startRoll();
                                }
                            })
                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
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
        final Dice dice = new Dice(mA,mC,20);
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
        Display display = mA.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Float factor = mC.getResources().getInteger(R.integer.percent_fullscreen_customdialog)/100f;
        alertDialog.getWindow().setLayout((int) (factor*size.x), (int)(factor*size.y));
        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams onlyButtonLL = (LinearLayout.LayoutParams) onlyButton.getLayoutParams();
        onlyButtonLL.width=ViewGroup.LayoutParams.WRAP_CONTENT;
        onlyButton.setLayoutParams(onlyButtonLL);
        onlyButton.setTextColor(mC.getColor(R.color.colorBackground));
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));
    }

    private void endSkillCalculation(final Dice dice) {
        FrameLayout resultDice= dialogView.findViewById(R.id.customDialogTestResultDice);
        resultDice.removeAllViews();
        resultDice.addView(dice.getImg());

        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogTestCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));

        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        onlyButton.setText("Ok");
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));

        resultTitle.setText("Résultat du test de niveau de lanceur de sort :");
        int sumResult=dice.getRandValue()+ calculation.casterLevelSR(spell);
        if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}
        if(robe){sumResult+=2;}

        final TextView result = dialogView.findViewById(R.id.customDialogTestResult);
        result.setText(String.valueOf(sumResult));

        dice.setMythicEventListener(new Dice.OnMythicEventListener() {
            @Override
            public void onEvent() {
                int sumResult=dice.getRandValue()+ sumScore;
                if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}
                result.setText(String.valueOf(sumResult));
            }
        });

        callToAction.setText("Fin du test de\nniveau de lanceur de sort.");
    }
}

