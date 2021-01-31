package stellarnear.yfa_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.Perso.Skill;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.TestAlertDialog;
import stellarnear.yfa_companion.Tools;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragmentSkill extends CustomFragment {
    private Perso yfa= MainActivity.yfa;
    private LinearLayout linearSkillScroll;
    private View returnFragView;
    private Tools tools=Tools.getTools();

    @Override
    public View returnFragView() {
        returnFragView = inflater.inflate(R.layout.fragment_main_skill, container, false);

        linearSkillScroll = returnFragView.findViewById(R.id.skillscrollLayout);

        for (Skill skill : yfa.getAllSkills().getSkillsList()) {
            addAllColumns(skill);
        }

        ImageButton buttonMain = (ImageButton) returnFragView.findViewById(R.id.button_frag_skill_to_main);

        animate(buttonMain);

        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlockOrient();
                Fragment fragment = new MainActivityFragment();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtoleftfrag);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return returnFragView;
    }


    private void addAllColumns(Skill skill) {
        LinearLayout line = new LinearLayout(getContext());
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setGravity(Gravity.CENTER);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,getResources().getDimensionPixelSize(R.dimen.icon_skills_list_height)));
        line.setBackground(getResources().getDrawable(R.drawable.skill_bar_gradient));
        setNameListnerRollSkill(line, skill);

        LinearLayout iconAndName = new LinearLayout(getContext());
        iconAndName.setOrientation(LinearLayout.HORIZONTAL);
        iconAndName.setGravity(Gravity.CENTER_VERTICAL);
        TextView nameTitle = returnFragView.findViewById(R.id.skillNameTitle);
        iconAndName.setLayoutParams(nameTitle.getLayoutParams());

        ImageView icon = new ImageView(getContext());
        int imgId = R.drawable.mire_test;
        try {
            imgId = getResources().getIdentifier(skill.getId(), "drawable", getContext().getPackageName());
        } catch (Exception e) {
            log.warn("No image for : "+skill.getId());
        }
        icon.setImageDrawable(getContext().getDrawable(imgId));
        tools.resize(icon,(int) (getResources().getDimensionPixelSize(R.dimen.icon_skills_list_height) * 0.8));
        LinearLayout.LayoutParams para = (LinearLayout.LayoutParams) icon.getLayoutParams();
        para.setMarginStart(getResources().getDimensionPixelSize(R.dimen.general_margin));
        iconAndName.addView(icon);

        TextView nameTxt = new TextView(getContext());
        nameTxt.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1));
        nameTxt.setText(skill.getName());
        nameTxt.setGravity(Gravity.CENTER);
        iconAndName.addView(nameTxt);

        TextView totalTxt = new TextView(getContext());
        TextView totalTitle = returnFragView.findViewById(R.id.skillTotalTitle);
        totalTxt.setLayoutParams(totalTitle.getLayoutParams());
        int total = yfa.getAbilityMod(skill.getAbilityDependence())+skill.getRank()+yfa.getSkillBonus(skill.getId());
        totalTxt.setText(String.valueOf(total));
        totalTxt.setTypeface(null, Typeface.BOLD);
        totalTxt.setGravity(Gravity.CENTER);

        TextView abiTxt = new TextView(getContext());
        TextView abiTitle = returnFragView.findViewById(R.id.skillAbiTitle);
        abiTxt.setLayoutParams(abiTitle.getLayoutParams());
        String abScore;
        if(yfa.getAbilityMod(skill.getAbilityDependence())>=0){
            abScore = "+"+yfa.getAbilityMod(skill.getAbilityDependence());
        } else {
            abScore = String.valueOf(yfa.getAbilityMod(skill.getAbilityDependence()));
        }
        abiTxt.setText(skill.getAbilityDependence().substring(8,11) + " : " +abScore );  //la clef de l'id etant ability_x
        abiTxt.setGravity(Gravity.CENTER);

        TextView rankTxt = new TextView(getContext());
        TextView rankTitle = returnFragView.findViewById(R.id.skillRankTitle);
        rankTxt.setLayoutParams(rankTitle.getLayoutParams());
        rankTxt.setText(String.valueOf(skill.getRank()));
        rankTxt.setGravity(Gravity.CENTER);

        TextView bonusTxt = new TextView(getContext());
        TextView bonusTitle = returnFragView.findViewById(R.id.skillBonusTitle);
        bonusTxt.setLayoutParams(bonusTitle.getLayoutParams());
        bonusTxt.setText(String.valueOf(yfa.getSkillBonus(skill.getId())));
        bonusTxt.setGravity(Gravity.CENTER);

        line.addView(iconAndName);
        line.addView(totalTxt);
        line.addView(abiTxt);
        line.addView(rankTxt);
        line.addView(bonusTxt);

        linearSkillScroll.addView(line);
    }

    private void setNameListnerRollSkill(LinearLayout line,final Skill skill) {
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TestAlertDialog(getActivity(),getContext(),skill);
            }
        });
    }

    private void unlockOrient() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    private void animate(final ImageButton buttonMain) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation anim = new ScaleAnimation(1f,1.25f,1f,1.25f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setRepeatCount(1);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(666);

                buttonMain.startAnimation(anim);
            }
        }, getResources().getInteger(R.integer.translationFragDuration));


    }
}
