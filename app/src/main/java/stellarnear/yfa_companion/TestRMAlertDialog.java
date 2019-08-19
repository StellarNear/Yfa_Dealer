package stellarnear.yfa_companion;

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

import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Rolls.Dice;
import stellarnear.yfa_companion.Spells.Spell;
import stellarnear.yfa_companion.Activities.MainActivity;

public class TestRMAlertDialog {
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private View dialogView;
    private Spell spell;
    private Calculation calculation=new Calculation();
    private int sumScore;
    private OnRefreshEventListener mListener;
    private boolean robe=false;
    private Tools tools=new Tools();

    public TestRMAlertDialog(Activity mA, Context mC, Spell spell) {
        this.mA=mA;
        this.mC=mC;
        this.spell = spell;
        this.sumScore = 0;
        buildAlertDialog();
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_rm_test_alert_dialog, null);
        ImageView icon = dialogView.findViewById(R.id.customDialogTestIcon);
        icon.setImageDrawable(mC.getDrawable(R.drawable.ic_surrounded_shield));

        String titleTxt = "Test du niveau de lanceur de sort :\n";
        TextView title = dialogView.findViewById(R.id.customDialogTestTitle);
        title.setSingleLine(false);
        title.setText(titleTxt);


        sumScore= calculation.casterLevelSR(spell);
        Perso yfa = MainActivity.yfa;
        robe = yfa.getInventory().getAllEquipments().getEquipmentsEquiped("armor_slot").getName().equalsIgnoreCase("Robe d'archimage grise");
        if(robe){
            sumScore+=2;
        }
        String summaryTxt="Test contre RM : "+String.valueOf(sumScore);
        TextView summary = dialogView.findViewById(R.id.customDialogTestSummary);
        summary.setText(summaryTxt);

        String summaryDetail="";
        summaryDetail="NLS : "+String.valueOf(calculation.casterLevelSR(spell))+" "; //oui c'est moche il faut gerer tout les calcul au niveau du perso proprement ...

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

        dialogBuilder.setPositiveButton("Succès", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tools.customToast(mC,"Quand le spell passe... !");
                spell.setRmPassed();
                if(mListener!=null){mListener.onEvent();}
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
    }

    private void startRoll() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        final Dice dice = new Dice(mA,mC,20);
        if (settings.getBoolean("switch_manual_diceroll",mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def))){
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
        Button negativButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams onlyButtonLL = (LinearLayout.LayoutParams) negativButton.getLayoutParams();
        onlyButtonLL.width=ViewGroup.LayoutParams.WRAP_CONTENT;
        negativButton.setLayoutParams(onlyButtonLL);
        negativButton.setTextColor(mC.getColor(R.color.colorBackground));
        negativButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));

        Button success = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        success.setLayoutParams(onlyButtonLL);
        success.setTextColor(mC.getColor(R.color.colorBackground));
        success.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
        success.setVisibility(View.GONE);
    }

    private void endSkillCalculation(final Dice dice) {
        FrameLayout resultDice= dialogView.findViewById(R.id.customDialogTestResultDice);
        resultDice.removeAllViews();
        resultDice.addView(dice.getImg());

        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogTestCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));

        Button successButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        successButton.setVisibility(View.VISIBLE);

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

        Button failButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        failButton.setText("Raté");
        failButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.customToast(mC,"Le sort ne passe pas la résiste magique...");
                spell.setFailed();
                if(mListener!=null){mListener.onEvent();}
                alertDialog.dismiss();
            }
        });
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}

