package stellarnear.yfa_dealer.Spells;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.yfa_dealer.Calculation;
import stellarnear.yfa_dealer.CustomAlertDialog;
import stellarnear.yfa_dealer.DisplayedText;
import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.TestAlertDialog;
import stellarnear.yfa_dealer.Tools;

public class SpellProfileFactory {
    private Activity mA;
    private Context mC;
    private Spell spell;
    private View profile;

    private Calculation calculation=new Calculation();
    private DisplayedText displayText =new DisplayedText();
    private Perso yfa = MainActivity.yfa;
    private Tools tools=new Tools();

    public SpellProfileFactory(Activity mA, Context mC, Spell spell){
        this.mA=mA;
        this.mC=mC;
        this.spell=spell;
        LayoutInflater inflater = mA.getLayoutInflater();
        profile = inflater.inflate(R.layout.spell_profile, null);
    }
    public View getProfile() {
        refreshProfile();
        return profile;
    }

    private void refreshProfile(){

        ((TextView)profile.findViewById(R.id.spell_name)).setText(spell.getName());

        ((TextView)profile.findViewById(R.id.current_rank)).setText("(rang : "+calculation.currentRank(spell)+")");
        if(!yfa.getAllResources().checkSpellAvailable(calculation.currentRank(spell))){
            ((TextView)profile.findViewById(R.id.current_rank)).setTextColor(Color.RED);
        }else {
            ((TextView)profile.findViewById(R.id.current_rank)).setTextColor(Color.BLACK);
        }

        ((TextView)profile.findViewById(R.id.description)).setText(spell.getDescr());
        ((TextView)profile.findViewById(R.id.description)).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView)profile.findViewById(R.id.description)).setSelected(true);
            }
        }, 1500);

        ((TextView)profile.findViewById(R.id.infos)).setText(printInfo());

        ((ImageView)profile.findViewById(R.id.sr_test_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TestAlertDialog(mA, mC, spell);
            }
        });

        ((LinearLayout)profile.findViewById(R.id.metamagic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMetaPopup(spell);
            }
        });
    }

    private String printInfo() {
        String text = "";
        if (calculation.nDice(spell)>0) {
            text += "Dégats\u00A0:\u00A0" + displayText.damageTxt(spell) + ", ";
        }
        if (!spell.getDmg_type().equals("")) {
            text += "Type\u00A0:\u00A0" + spell.getDmg_type() + ", ";
        }
        if (!spell.getRange().equals("")) {
            text += "Portée\u00A0:\u00A0" + displayText.rangeTxt(spell)+ ", ";
        }
        if (!displayText.compoTxt(mC,spell).equalsIgnoreCase("")) {
            text += "Compos\u00A0:\u00A0" + displayText.compoTxt(mC,spell) + ", ";
        }
        if (!spell.getCast_time().equals("")) {
            text += "Cast\u00A0:\u00A0" + spell.getCast_time() + ", ";
        }
        if (!spell.getDuration().equals("") && !spell.getDuration().equalsIgnoreCase("instant")) {
            text += "Durée\u00A0:\u00A0" + displayText.durationTxt(spell) + ", ";
        }
        if (!spell.hasRM()||spell.hasRM()) {
            text += "RM\u00A0:\u00A0" + (spell.hasRM() ? "oui":"non") + ", ";
        }
        String resistance;
        if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
            resistance = spell.getSave_type();
        } else {
            resistance = spell.getSave_type() + "(" + calculation.saveVal(spell) + ")";
        }
        if (!resistance.equals("")) {
            text += "Sauv\u00A0:\u00A0" + resistance + ", ";
        }
        text = text.substring(0, text.length() - 2);
        return text;
    }

    private void makeMetaPopup(Spell spell) {
        LayoutInflater inflate = mA.getLayoutInflater();

        final View mainView = inflate.inflate(R.layout.metamagie_dialog, null);

        LinearLayout mainLin = mainView.findViewById(R.id.metamagie_main_linear);
        mainLin.removeAllViews();

        for (final Metamagic meta : spell.getMetaList().asList()) {
            LinearLayout metaLin = new LinearLayout(mC);
            metaLin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            metaLin.setGravity(Gravity.CENTER);
            CheckBox check = meta.getCheckBox(mA,mC);

            check.setTextColor(mC.getColor(R.color.dark_gray));
            ViewGroup parent = (ViewGroup) check.getParent();
            if (parent != null) {
                parent.removeView(check);
            }
            meta.setRefreshEventListener(new Metamagic.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    refreshProfile();
                }
            });
            metaLin.addView(check);

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

            ViewGroup parentImg = (ViewGroup) image.getParent();
            if (parentImg != null) {
                parentImg.removeView(image);
            }
            metaLin.addView(image);
            mainLin.addView(metaLin);
        }
        final CustomAlertDialog metaPopup = new CustomAlertDialog(mA, mC, mainView);
        metaPopup.setPermanent(true);
        metaPopup.clickToHide(mainView.findViewById(R.id.metamagie_back));
        metaPopup.showAlert();
    }
}
