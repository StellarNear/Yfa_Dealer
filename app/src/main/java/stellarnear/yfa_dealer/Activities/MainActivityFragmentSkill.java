package stellarnear.yfa_dealer.Activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Tools;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragmentSkill extends Fragment {
    private Perso yfa= MainActivity.yfa;
    private LinearLayout linearSkillScroll;
    private View returnFragView;
    private Tools tools=new Tools();
    public MainActivityFragmentSkill() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView = inflater.inflate(R.layout.fragment_main_skill, container, false);

        linearSkillScroll = returnFragView.findViewById(R.id.skillscrollLayout);
        /*
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
                fragmentTransaction.setCustomAnimations(R.animator.infadefrag,R.animator.outtorightfrag);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
*/

        return returnFragView;
    }

    /*
    private void addAllColumns(Skill skill) {
        LinearLayout line = new LinearLayout(getContext());
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setGravity(Gravity.CENTER);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,getResources().getDimensionPixelSize(R.dimen.icon_skills_list_height)));
        line.setBackground(getResources().getDrawable(R.drawable.skill_bar_gradient));
        setNameListnerRollSkill(line,skill);

        TextView nameTxt = new TextView(getContext());
        TextView nameTitle = returnFragView.findViewById(R.id.skillNameTitle);
        nameTxt.setLayoutParams(nameTitle.getLayoutParams());
        nameTxt.setText(skill.getName());
        int imgId = getResources().getIdentifier(skill.getId(), "drawable", getContext().getPackageName());
        nameTxt.setCompoundDrawablesWithIntrinsicBounds(tools.resize(getContext(),getContext().getDrawable(imgId),(int) (getResources().getDimensionPixelSize(R.dimen.icon_skills_list_height)*0.8)),null,null,null);
        nameTxt.setPadding(getResources().getDimensionPixelSize(R.dimen.general_margin),0,0,0);
        nameTxt.setGravity(Gravity.CENTER);

        TextView totalTxt = new TextView(getContext());
        TextView totalTitle = returnFragView.findViewById(R.id.skillTotalTitle);
        totalTxt.setLayoutParams(totalTitle.getLayoutParams());
        int total = yfa.getAbilityMod(getContext(),skill.getAbilityDependence())+skill.getRank()+yfa.getSkillBonus(getContext(),skill.getId());
        totalTxt.setText(String.valueOf(total));
        totalTxt.setTypeface(null, Typeface.BOLD);
        totalTxt.setGravity(Gravity.CENTER);

        TextView abiTxt = new TextView(getContext());
        TextView abiTitle = returnFragView.findViewById(R.id.skillAbiTitle);
        abiTxt.setLayoutParams(abiTitle.getLayoutParams());
        String abScore;
        if(yfa.getAbilityMod(getContext(),skill.getAbilityDependence())>=0){
            abScore = "+"+yfa.getAbilityMod(getContext(),skill.getAbilityDependence());
        } else {
            abScore = String.valueOf(yfa.getAbilityMod(getContext(),skill.getAbilityDependence()));
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
        bonusTxt.setText(String.valueOf(yfa.getSkillBonus(getContext(),skill.getId())));
        bonusTxt.setGravity(Gravity.CENTER);

        line.addView(nameTxt);
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
                new TestAlertDialog(getActivity(),getContext(),skill,yfa.getAbilityMod(getContext(),skill.getAbilityDependence()));
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


    }*/
}
