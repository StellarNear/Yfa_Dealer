package stellarnear.yfa_dealer.Spells;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import stellarnear.yfa_dealer.Calculation;
import stellarnear.yfa_dealer.ContactAlertDialog;
import stellarnear.yfa_dealer.ConvertView;
import stellarnear.yfa_dealer.CustomAlertDialog;
import stellarnear.yfa_dealer.DisplayedText;
import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.ResultBuilder;
import stellarnear.yfa_dealer.SliderBuilder;
import stellarnear.yfa_dealer.TestAlertDialog;
import stellarnear.yfa_dealer.Tools;

public class SpellProfileFactory {
    private Activity mA;
    private Context mC;
    private Spell spell;
    private View profile;
    private ViewSwitcher panel;
    private boolean convDisplayed=false;
    private CustomAlertDialog metaPopup;

    private Calculation calculation=new Calculation();
    private DisplayedText displayText =new DisplayedText();
    private Perso yfa = MainActivity.yfa;
    private Tools tools=new Tools();

    private OnRefreshEventListener mListener;

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

    public void refreshProfile(){
        ((TextView)profile.findViewById(R.id.spell_name)).setText(spell.getName());
        ((TextView)profile.findViewById(R.id.spell_name)).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView)profile.findViewById(R.id.description)).setSelected(true);
            }
        }, 1500);
        ((TextView)profile.findViewById(R.id.spell_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.customToast(mC,spell.getDescr(),"center");
            }
        });

        testSpellForColorTitle();

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
                showMetaPopup();
            }
        });
        if(spell.isCast()){
            ((LinearLayout)profile.findViewById(R.id.metamagic)).setEnabled(false);
            ((TextView)profile.findViewById(R.id.text_meta)).setTextColor(Color.GRAY);
            ((ImageView)profile.findViewById(R.id.symbol_meta)).getDrawable().mutate().setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_IN);
        }

        if(!spell.getContact().equalsIgnoreCase("")){
            ((LinearLayout)profile.findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactAlertDialog contactDialog = new ContactAlertDialog(mA, mC,spell);
                    contactDialog.showAlertDialog();
                    contactDialog.setSuccessEventListener(new ContactAlertDialog.OnSuccessEventListener() {
                        @Override
                        public void onEvent() {
                            ((LinearLayout)profile.findViewById(R.id.contact)).setVisibility(View.GONE);
                            ((SeekBar) profile.findViewById(R.id.slider)).setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } else {
            ((LinearLayout)profile.findViewById(R.id.contact)).setVisibility(View.GONE);
            ((SeekBar) profile.findViewById(R.id.slider)).setVisibility(View.VISIBLE);
        }

        SliderBuilder sliderBuild =new SliderBuilder(mC,spell);
        sliderBuild.setSlider((SeekBar) profile.findViewById(R.id.slider));
        sliderBuild.setCastEventListener(new SliderBuilder.OnCastEventListener() {
            @Override
            public void onEvent() {
                if(mListener!=null){mListener.onEvent();}
                ((ImageView)profile.findViewById(R.id.button_conversion)).setVisibility(View.GONE);
                ((LinearLayout)profile.findViewById(R.id.second_panel)).removeAllViews();
                new ResultBuilder(mA,mC,spell).addResults((LinearLayout)profile.findViewById(R.id.second_panel));
                flipView();
            }
        });

        panel = ((ViewSwitcher)profile.findViewById(R.id.view_switcher));

        if(!spell.getConversion().getArcaneId().equalsIgnoreCase("") || spell.isCast()){
            ((ImageView)profile.findViewById(R.id.button_conversion)).setVisibility(View.GONE);
        } else {
            ((ImageView) profile.findViewById(R.id.button_conversion)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConvertView convertView = new ConvertView(panel, spell, mC, mA);
                    flipView();
                    convertView.setValidationEventListener(new ConvertView.OnValidationEventListener() {
                        @Override
                        public void onEvent() {
                            refreshProfile();
                            flipView();
                            if(mListener!=null){mListener.onEvent();}
                            ((ImageView) profile.findViewById(R.id.button_conversion)).setVisibility(View.GONE);
                            ((LinearLayout) profile.findViewById(R.id.second_panel)).removeAllViews();
                        }
                    });
                }
            });
        }
    }

    private void flipView() {
        if(!convDisplayed){flipNext();}else {flipPrevious();}
    }

    private void flipNext() {
        convDisplayed=true;
        panel.clearAnimation();
        ((LinearLayout)profile.findViewById(R.id.first_panel)).setClickable(false);
        ((LinearLayout)profile.findViewById(R.id.second_panel)).setClickable(true);
        Animation in = AnimationUtils.loadAnimation(mC,R.anim.infromright);
        Animation out =  AnimationUtils.loadAnimation(mC,R.anim.outtoleft);
        panel.setInAnimation(in);  panel.setOutAnimation(out);
        panel.showNext();
    }
    private void flipPrevious() {
        convDisplayed=false;
        panel.clearAnimation();
        ((LinearLayout)profile.findViewById(R.id.first_panel)).setClickable(true);
        ((LinearLayout)profile.findViewById(R.id.second_panel)).setClickable(false);
        Animation in = AnimationUtils.loadAnimation(mC,R.anim.infromleft);
        Animation out =  AnimationUtils.loadAnimation(mC,R.anim.outtoright);
        panel.setInAnimation(in);  panel.setOutAnimation(out);
        panel.showPrevious();
    }

    private void testSpellForColorTitle() {
        Drawable gd =null;
        if (spell.getDmg_type().equals("aucun")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_void);
        }else  if (spell.getDmg_type().equals("feu")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_fire);
        }else if (spell.getDmg_type().equals("foudre")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_thunder);
        }else if (spell.getDmg_type().equals("froid")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_cold);
        } else if (spell.getDmg_type().equals("acide")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_acid);
        } else {
            gd=mC.getDrawable(R.drawable.round_corner_title);
        }
        ((RelativeLayout)profile.findViewById(R.id.title_background)).setBackground(gd);
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
        if (!spell.getArea().equals("")) {
            text += "Zone\u00A0:\u00A0" + spell.getArea()+ ", ";
        }
        if (!displayText.compoTxt(mC,spell).equalsIgnoreCase("")) {
            text += "Compos\u00A0:\u00A0" + displayText.compoTxt(mC,spell) + ", ";
        }
        if (!spell.getCast_time().equals("")) {
            text += "Cast\u00A0:\u00A0" + calculation.getCastTimeTxt(spell) + ", ";
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
            CheckBox check = spell.getCheckboxeForMetaId(mA,mC,meta.getId());

            check.setTextColor(mC.getColor(R.color.dark_gray));
            ViewGroup parent = (ViewGroup) check.getParent();
            if (parent != null) {
                parent.removeView(check);
            }
            meta.setRefreshEventListener(new Metamagic.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    refreshProfile();
                    if(mListener!=null){mListener.onEvent();}
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
        metaPopup = new CustomAlertDialog(mA, mC, mainView);
        metaPopup.setPermanent(true);
        metaPopup.clickToHide(mainView.findViewById(R.id.metamagie_back));
    }

    private void showMetaPopup(){
        metaPopup.showAlert();
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}
