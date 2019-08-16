package stellarnear.yfa_companion.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.List;

import stellarnear.yfa_companion.Quadrants.QuadrantManager;
import stellarnear.yfa_companion.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    View returnFragView;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView= inflater.inflate(R.layout.fragment_main, container, false);

        ImageButton fabSkill = (ImageButton) returnFragView.findViewById(R.id.button_frag_to_skill);
        setButtonActivity(fabSkill, new MainActivityFragmentSkill(), R.animator.infromleftfrag,R.animator.outfadefrag);
        Animation left = AnimationUtils.loadAnimation(getContext(),R.anim.infromleft);
        fabSkill.startAnimation(left);

        ImageButton fabCast = (ImageButton) returnFragView.findViewById(R.id.button_frag_to_spell);
        setButtonActivity(fabCast,new MainActivityFragmentSpell(),R.animator.infromrightfrag,R.animator.outfadefrag);
        Animation right = AnimationUtils.loadAnimation(getContext(),R.anim.infromright);
        fabCast.startAnimation(right);

        fadeInTooltips(getContext());

        final Context mC=getContext();
        final Activity mA=getActivity();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeIn = AnimationUtils.loadAnimation(mC,R.anim.infade);
                View mainAbiText = returnFragView.findViewById(R.id.quadrantGeneralTitle);
                mainAbiText.setVisibility(View.VISIBLE);
                mainAbiText.startAnimation(fadeIn);
                popInQuadrant(mC,mA);
            }
        }, right.getDuration());

        return returnFragView;
    }

    private void fadeInTooltips(Context mC) {
        Animation fadeIn = AnimationUtils.loadAnimation(mC,R.anim.infade);
        View tooltip1 = returnFragView.findViewById(R.id.textSkillActionTooltip);
        View tooltip2 = returnFragView.findViewById(R.id.textCastActionTooltip);

        tooltip1.setVisibility(View.VISIBLE);
        tooltip2.setVisibility(View.VISIBLE);

        tooltip1.startAnimation(fadeIn);
        tooltip2.startAnimation(fadeIn);
    }

    private void popInQuadrant(Context mC,Activity mA) {
        QuadrantManager quadrantManager = new QuadrantManager(returnFragView,mC,mA);
        Animation pop = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        int delay=(int) (0.5*pop.getDuration());
        Animation pop2 = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        Animation pop3 = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        Animation pop4 = AnimationUtils.loadAnimation(mC,R.anim.popinquadrant);
        pop2.setStartOffset(delay);
        pop3.setStartOffset(delay*2);
        pop4.setStartOffset(delay*3);
        List<LinearLayout> quadrantList = quadrantManager.quadrantAsList();
        quadrantManager.showQuadrant();
        quadrantList.get(0).startAnimation(pop);
        quadrantList.get(1).startAnimation(pop2);
        quadrantList.get(2).startAnimation(pop3);
        quadrantList.get(3).startAnimation(pop4);
    }

    private void setButtonActivity(ImageButton button, final Fragment ActivityFragment,final int animIn,final int animOut) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockOrient();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(animIn,animOut);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, ActivityFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


    private void lockOrient() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
