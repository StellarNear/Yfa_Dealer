package stellarnear.yfa_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Ability;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Perso.Skill;
import stellarnear.yfa_companion.Rolls.Dice;
import stellarnear.yfa_companion.Rolls.Dice20;


public class TestAlertDialog {
    Perso yfa = MainActivity.yfa;
    private Activity mA;
    private Context mC;
    private AlertDialog alertDialog;
    private Skill skill;
    private Ability abi;
    private View dialogView;
    private OnRefreshEventListener mListener;
    private String mode;
    private Integer preRandScore=0;

    public TestAlertDialog(Activity mA, Context mC, Skill skill) {
        this.mA=mA;
        this.mC=mC;
        this.skill=skill;
        this.mode="skill";
        buildAlertDialog();
        showAlertDialog();
        ((TextView)dialogView.findViewById(R.id.customDialogTitleCategory)).setText("Test de compétence :");
    }

    public TestAlertDialog(Activity mA, Context mC, Ability abi) {
        this.mA=mA;
        this.mC=mC;
        this.abi=abi;
        this.mode="abi";
        if (abi.getId().equalsIgnoreCase("ability_equipment")){
            yfa.getInventory().showEquipment(mA,mC);
        } else {
            buildAlertDialog();
            showAlertDialog();
            ((TextView)dialogView.findViewById(R.id.customDialogTitleCategory)).setText("Test de caractéristique :");
        }
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    private void buildAlertDialog() {
        LayoutInflater inflater = mA.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        if(mode.equalsIgnoreCase("skill")){
            fillWithSkill();
        } else {
            fillWithAbi();
        }
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

        Button passive = dialogView.findViewById(R.id.button_customDialog_test_passive);
        passive.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dice20 dice = new Dice20(mA,mC);
                dice.setRand(10);
                endSkillCalculation(dice);
            }
        });

        Button focus = dialogView.findViewById(R.id.button_customDialog_test_focus);
        focus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dice20 dice = new Dice20(mA,mC);
                dice.setRand(20);
                endSkillCalculation(dice);
            }
        });

        if (mode.equalsIgnoreCase("abi") && !abi.isFocusable()){
            passive.setVisibility(View.GONE);
            focus.setVisibility(View.GONE);
        }

        AlertDialog.Builder dialogBuilder  = new AlertDialog.Builder(mA, R.style.CustomDialog);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(mListener!=null){mListener.onEvent();}
            }
        });
        alertDialog = dialogBuilder.create();
    }

    private void fillWithSkill() {
        Drawable draw = mC.getDrawable(mC.getResources().getIdentifier(skill.getId(), "drawable", mC.getPackageName()));
        if(draw==null){draw=mC.getDrawable(R.drawable.mire_test);}
        ((ImageView)dialogView.findViewById(R.id.customDialogTestIcon)).setImageDrawable(draw);
        ((TextView)dialogView.findViewById(R.id.customDialogTitleName)).setText(skill.getName());
        int abScore= yfa.getAbilityMod(skill.getAbilityDependence());
        String abScoreTxt = abScore>0?"+"+abScore:String.valueOf(abScore);

        preRandScore=abScore+skill.getRank()+ yfa.getSkillBonus(skill.getId());
        boolean destinyON=yfa.getAllBuffs()!=null && yfa.getAllBuffs().getBuffByID("capacity_destiny_touch")!=null && yfa.getAllBuffs().getBuffByID("capacity_destiny_touch").isActive();
        if(destinyON){ preRandScore+=yfa.getAllCapacities().getCapacity("capacity_destiny_touch").getValue();  }
        String summaryTxt="Total : "+String.valueOf(preRandScore)+"\n"+skill.getAbilityDependence().substring(8,11).toUpperCase()+" : "+abScoreTxt+",  Rang : "+skill.getRank()+",  Bonus : "+ yfa.getSkillBonus(skill.getId());
        if(destinyON){ summaryTxt+=",  Buff : "+yfa.getAllCapacities().getCapacity("capacity_destiny_touch").getValue();}
        ((TextView)dialogView.findViewById(R.id.customDialogTestSummary)).setText(summaryTxt);
    }

    private void fillWithAbi() {
        Drawable draw = mC.getDrawable(mC.getResources().getIdentifier(abi.getId(), "drawable", mC.getPackageName()));
        if(draw==null){draw=mC.getDrawable(R.drawable.mire_test);}
        ((ImageView)dialogView.findViewById(R.id.customDialogTestIcon)).setImageDrawable(draw);
        ((TextView)dialogView.findViewById(R.id.customDialogTitleName)).setText(abi.getName());
        if (abi.getType().equalsIgnoreCase("base")){
            preRandScore=yfa.getAbilityMod(abi.getId());
        } else {
            preRandScore=yfa.getAbilityScore(abi.getId());
        }
        boolean destinyON=yfa.getAllBuffs()!=null && yfa.getAllBuffs().getBuffByID("capacity_destiny_touch")!=null && yfa.getAllBuffs().getBuffByID("capacity_destiny_touch").isActive();
        String summaryTxt="Valeur de base : "+(preRandScore>0?"+"+preRandScore:String.valueOf(preRandScore));
        if(destinyON){ preRandScore+=yfa.getAllCapacities().getCapacity("capacity_destiny_touch").getValue();
            summaryTxt+=",  Buff : "+yfa.getAllCapacities().getCapacity("capacity_destiny_touch").getValue();}
        ((TextView)dialogView.findViewById(R.id.customDialogTestSummary)).setText(summaryTxt);
    }

    private void startRoll() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        final Dice20 dice = new Dice20(mA,mC);
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
        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams onlyButtonLL = (LinearLayout.LayoutParams) onlyButton.getLayoutParams();
        onlyButtonLL.width=ViewGroup.LayoutParams.WRAP_CONTENT;
        onlyButton.setLayoutParams(onlyButtonLL);
        onlyButton.setTextColor(mC.getColor(R.color.colorBackground));
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_cancel_gradient));
    }

    private void endSkillCalculation(final Dice20 dice) {
        /* //la bague permet d'avoir un jet legendaire de montée en pusisance sur les jets de sauv
        List<String> listSave = Arrays.asList("ability_ref","ability_vig","ability_vol");
        if(abi!=null && listSave.contains(abi.getId())){
            //dice.setLegendarySurge(true);
        }*/
        FrameLayout resultDice= dialogView.findViewById(R.id.customDialogTestResultDice);
        resultDice.removeAllViews();
        resultDice.addView(dice.getImg());

        Button onlyButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        onlyButton.setText("Ok");
        onlyButton.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
        displayResult(dice);
        dice.setMythicEventListener(new Dice20.OnMythicEventListener() {
            @Override
            public void onEvent() {
                displayResult(dice);
            }
        });
    }

    private void displayResult(Dice20 dice) {
        String modePostData; int sumResultPostData;
        TextView resultTitle = dialogView.findViewById(R.id.customDialogTitleResult);
        TextView callToAction = dialogView.findViewById(R.id.customDialogTestCallToAction);
        callToAction.setTextColor(mC.getColor(R.color.secondaryTextCustomDialog));
        if (mode.equalsIgnoreCase("skill")){
            resultTitle.setText("Résultat du test de compétence :");
            int sumResult=dice.getRandValue()+preRandScore;
            if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}
            TextView result = dialogView.findViewById(R.id.customDialogTestResult);
            result.setText(String.valueOf(sumResult));
            callToAction.setText("Fin du test de compétence");

            modePostData="Test compétence "+skill.getName();sumResultPostData=sumResult;
        } else {
            resultTitle.setText("Résultat du test de caractéristique :");
            int sumResult=dice.getRandValue()+ preRandScore;
            if(dice.getMythicDice()!=null){sumResult+=dice.getMythicDice().getRandValue();}
            TextView result = dialogView.findViewById(R.id.customDialogTestResult);
            result.setText(String.valueOf(sumResult));
            callToAction.setText("Fin du test de caractéristique");
            modePostData="Test caractéristique "+abi.getName();sumResultPostData=sumResult;
        }
        new PostData(mC,new PostDataElement(modePostData,dice,sumResultPostData));
    }
}