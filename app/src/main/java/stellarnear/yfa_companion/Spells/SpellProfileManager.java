package stellarnear.yfa_companion.Spells;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.util.TypedValue;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Arrays;
import java.util.List;

import stellarnear.yfa_companion.ContactAlertDialog;
import stellarnear.yfa_companion.ConvertElementView;
import stellarnear.yfa_companion.ConvertView;
import stellarnear.yfa_companion.CustomAlertDialog;
import stellarnear.yfa_companion.GlaeTestAlertDialog;
import stellarnear.yfa_companion.Perso.SelfCustomLog;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.ResultBuilder;
import stellarnear.yfa_companion.SliderBuilder;
import stellarnear.yfa_companion.TestRMAlertDialog;
import stellarnear.yfa_companion.Tools;

public class SpellProfileManager extends SelfCustomLog {
    private Activity mA;
    private Context mC;
    private Spell spell;
    private View profile;
    private ViewFlipper panel;
    private Boolean resultDisplayed = false; //pour aps revenir au panneau central si le sort a ses dégats affiché
    private CustomAlertDialog metaPopup;
    private Tools tools = Tools.getTools();
    private SliderBuilder sliderBuild;
    private OnRefreshEventListener mListener;
    private String position = "info";

    public SpellProfileManager(Activity mA, Context mC, Spell spell, View profileView) {
        this.mA = mA;
        this.mC = mC;
        this.spell = spell;
        profile = profileView;
        panel = ((ViewFlipper) profile.findViewById(R.id.view_flipper));
        buildProfileMechanisms();
        panel.setDisplayedChild(1);
    }

    private void buildProfileMechanisms() {
        if (spell.isFailed() || spell.contactFailed()) {
            triggerFail("fail");
        } else if (spell.getGlaeManager().isFailed()) {
            triggerFail("glae");
        } else if (!this.position.equalsIgnoreCase("info") && !resultDisplayed) {
            movePanelTo("info");
        }

        if (spell.hasPassedRM() || !spell.hasRM()) {
            profile.findViewById(R.id.sr_test_img).setVisibility(View.GONE);
            profile.findViewById(R.id.sr_test_sepa).setVisibility(View.GONE);
        } else {
            ((ImageView) profile.findViewById(R.id.sr_test_img)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TestRMAlertDialog testAlert = new TestRMAlertDialog(mA, mC, spell);
                    testAlert.setRefreshEventListener(new TestRMAlertDialog.OnRefreshEventListener() {
                        @Override
                        public void onEvent() {
                            buildProfileMechanisms();
                            if (mListener != null) {
                                mListener.onEvent();
                            }
                        }
                    });
                    testAlert.showAlertDialog();
                }
            });
        }

        //metamagie
        ((LinearLayout) profile.findViewById(R.id.metamagic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (metaPopup == null) {
                    makeMetaPopup(spell);
                }
                showMetaPopup();
            }
        });
        if (spell.isCast()) {
            ((LinearLayout) profile.findViewById(R.id.metamagic)).setEnabled(false);
            ((TextView) profile.findViewById(R.id.text_meta)).setTextColor(Color.GRAY);
            ((ImageView) profile.findViewById(R.id.symbol_meta)).getDrawable().mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }

        //Slider
        if (sliderBuild == null) {
            sliderBuild = new SliderBuilder(mC, spell);
            sliderBuild.setSlider((SeekBar) profile.findViewById(R.id.slider));
            sliderBuild.setCastEventListener(new SliderBuilder.OnCastEventListener() {
                @Override
                public void onEvent() {
                    ((ImageView) profile.findViewById(R.id.button_change_element)).setVisibility(View.GONE);
                    ((ImageView) profile.findViewById(R.id.button_conversion)).setVisibility(View.GONE);
                    ((LinearLayout) profile.findViewById(R.id.fourth_panel)).removeAllViews();
                    new ResultBuilder(mA, mC, spell).addResults((LinearLayout) profile.findViewById(R.id.fourth_panel));
                    resultDisplayed = true;
                    movePanelTo("dmg");
                    if (mListener != null) {
                        mListener.onEvent();
                    }
                }
            });
        }

        //Glae test
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        List<String> elems = Arrays.asList("frost", "fire", "shock", "acid");
        if (spell.getGlaeManager().isTested() || !elems.contains(spell.getDmg_type()) || !settings.getBoolean("glae_switch_tier2", mC.getResources().getBoolean(R.bool.glae_switch_tier2_def))) {
            ((LinearLayout) profile.findViewById(R.id.test_glae)).setVisibility(View.GONE);
        } else {
            if (spell.getDmg_type().equalsIgnoreCase("shock")) {
                profile.findViewById(R.id.glae_fail).setVisibility(View.GONE);
                profile.findViewById(R.id.glae_boost).setVisibility(View.VISIBLE);
            } else {
                profile.findViewById(R.id.glae_fail).setVisibility(View.VISIBLE);
                profile.findViewById(R.id.glae_boost).setVisibility(View.GONE);
            }

            ((LinearLayout) profile.findViewById(R.id.test_glae)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlaeTestAlertDialog glaeDialog = new GlaeTestAlertDialog(mA, mC, spell);
                    glaeDialog.showAlertDialog();
                    glaeDialog.setEndEventListener(new GlaeTestAlertDialog.OnEndEventListener() {
                        @Override
                        public void onEvent() {
                            buildProfileMechanisms();
                            ((LinearLayout) profile.findViewById(R.id.test_glae)).setVisibility(View.GONE);
                            if (mListener != null) {
                                mListener.onEvent();
                            }
                        }
                    });
                }
            });
        }

        //sort contact
        if (spell.getContact().equalsIgnoreCase("")) {
            ((LinearLayout) profile.findViewById(R.id.contact)).setVisibility(View.GONE);
        } else {
            ((LinearLayout) profile.findViewById(R.id.contact)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactAlertDialog contactDialog = new ContactAlertDialog(mA, mC, spell);
                    contactDialog.showAlertDialog();
                    contactDialog.setRefreshEventListener(new ContactAlertDialog.OnRefreshEventListener() {
                        @Override
                        public void onEvent() {
                            buildProfileMechanisms();
                            ((LinearLayout) profile.findViewById(R.id.contact)).setVisibility(View.GONE);
                            if (mListener != null) {
                                mListener.onEvent();
                            }
                        }
                    });
                }
            });
        }

        // conversion element
        if (!elems.contains(spell.getDmg_type()) || spell.elementIsConverted() || spell.isCast()) {
            ((ImageView) profile.findViewById(R.id.button_change_element)).setVisibility(View.GONE);
        } else {
            ((ImageView) profile.findViewById(R.id.button_change_element)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConvertElementView convertElementView = new ConvertElementView(panel, spell, mC, mA);
                    if (position.equalsIgnoreCase("info")) {
                        movePanelTo("elem");
                    } else {
                        movePanelTo("info");
                    }
                    convertElementView.setValidationEventListener(new ConvertElementView.OnValidationEventListener() {
                        @Override
                        public void onEvent() {
                            movePanelTo("info");
                            buildProfileMechanisms();
                            ((ImageView) profile.findViewById(R.id.button_change_element)).setVisibility(View.GONE);
                            if (mListener != null) {
                                mListener.onEvent();
                            }
                        }
                    });
                }
            });
        }

        // conversion arcanique
        if (!spell.getConversion().getArcaneId().equalsIgnoreCase("") || spell.isCast()) {
            ((ImageView) profile.findViewById(R.id.button_conversion)).setVisibility(View.GONE);
        } else {
            ((ImageView) profile.findViewById(R.id.button_conversion)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ConvertView convertView = new ConvertView(panel, spell, mC, mA);
                        if (position.equalsIgnoreCase("info")) {
                            movePanelTo("arcane");
                        } else {
                            movePanelTo("info");
                        }
                        convertView.setValidationEventListener(new ConvertView.OnValidationEventListener() {
                            @Override
                            public void onEvent() {
                                movePanelTo("info");
                                buildProfileMechanisms();
                                ((ImageView) profile.findViewById(R.id.button_conversion)).setVisibility(View.GONE);
                                if (mListener != null) {
                                    mListener.onEvent();
                                }
                            }
                        });
                    } catch (Exception e) {
                        log.err(mC,"Erreur lors de la vue convertion", e);
                    }
                }
            });
        }
    }

    public void triggerFail(String mode) {
        ((ImageView) profile.findViewById(R.id.button_change_element)).setVisibility(View.GONE);
        ((ImageView) profile.findViewById(R.id.button_conversion)).setVisibility(View.GONE);
        ((LinearLayout) profile.findViewById(R.id.fourth_panel)).removeAllViews();
        TextView txt_view = new TextView(mC);
        txt_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String message = mode.equalsIgnoreCase("glae") ? "Glaedäyes a fait échouer le sort !" : "Le sort a raté...";
        txt_view.setText(message);
        txt_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        ((LinearLayout) profile.findViewById(R.id.fourth_panel)).addView(txt_view);
        sliderBuild.spendCast();
        resultDisplayed = true;
        movePanelTo("dmg");
    }

    private void movePanelTo(String toPosition) {
        if (!this.position.equalsIgnoreCase(toPosition)) {
            Animation in = null;
            Animation out = null;
            int indexChild = 1;
            switch (this.position) {
                case "elem":
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
                    break;
                case "info":
                    if (toPosition.equalsIgnoreCase("elem")) {
                        out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
                    } else {
                        out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
                    }
                    break;
                case "arcane":
                    out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
                    break;
            }
            switch (toPosition) {
                case "elem":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
                    indexChild = 0;
                    break;
                case "info":
                    if (this.position.equalsIgnoreCase("elem")) {
                        in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    } else {
                        in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
                    }
                    indexChild = 1;
                    break;
                case "arcane":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    indexChild = 2;
                    break;
                case "dmg":
                    in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
                    indexChild = 3;
                    break;
            }
            panel.clearAnimation();
            panel.setInAnimation(in);
            panel.setOutAnimation(out);
            panel.setDisplayedChild(indexChild);
            this.position = toPosition;
        }
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
            CheckBox check = spell.getCheckboxeForMetaId(mA, mC, meta.getId());

            check.setTextColor(mC.getColor(R.color.dark_gray));
            ViewGroup parent = (ViewGroup) check.getParent();
            if (parent != null) {
                parent.removeView(check);
            }
            meta.setRefreshEventListener(new Metamagic.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    buildProfileMechanisms();
                    if (mListener != null) {
                        mListener.onEvent();
                    }
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
                    tools.customToast(mC, meta.getDescription(), "center");
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

    private void showMetaPopup() {
        metaPopup.showAlert();
    }

    public void refreshProfileMechanisms() {
        buildProfileMechanisms();
    }

    public boolean isDone() {
        return resultDisplayed;
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}
