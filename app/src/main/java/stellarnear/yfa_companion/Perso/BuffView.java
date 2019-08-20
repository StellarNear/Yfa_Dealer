package stellarnear.yfa_companion.Perso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.CircularProgressBar;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.Tools;

public class BuffView {
    private View buffView;
    private CircularProgressBar circle;
    private Activity mA;
    private Buff buff;
    private OnCastEventListener mListenerCast;
    private OnCancelEventListener mListenerCancel;
    private boolean closed=false;
    private Tools tools=new Tools();

    public BuffView(Activity mA,Buff buff){
        this.mA=mA;
        this.buff=buff;
        LayoutInflater inflater = mA.getLayoutInflater();
        LinearLayout buffView = (LinearLayout) inflater.inflate(R.layout.buff_icon, null);
        this.buffView=buffView;
        ((ImageView)buffView.findViewById(R.id.buff_icon_image)).setImageDrawable(mA.getDrawable(R.drawable.mire_test_cercle));
        ((TextView)buffView.findViewById(R.id.buff_icon_name)).setText(buff.getName()+ buff.getDurationText());
        buffView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1));

        if(buff.isPerma()){
            buffView.findViewById(R.id.circular_progress).setVisibility(View.GONE);
            int dimPix = mA.getResources().getDimensionPixelSize(R.dimen.icon_buff_perma);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimPix, dimPix);
            ((FrameLayout)buffView.findViewById(R.id.buff_icon_frame)).setLayoutParams(layoutParams);
        } else {
            circle = buffView.findViewById(R.id.circular_progress);
            circle.setMax(100);
            refresh(false);
        }
        setListners();
    }

    private void setListners() {
        // Listeners
        ((TextView)buffView.findViewById(R.id.buff_icon_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Tools().customToast(mA,buff.getDescr(),"center");
            }
        });
    }

    public FrameLayout getMainFrame(){
        return buffView.findViewById(R.id.buff_icon_frame);
    }

    public BuffView close(){
        closed=true;
        buffView.findViewById(R.id.buff_icon_name).setVisibility(View.GONE);

        setClickListner();
        return this;
    }

    public BuffView expand(){
        closed=false;
        buffView.findViewById(R.id.buff_icon_name).setVisibility(View.VISIBLE);

        setClickListner();
        return this;
    }

    public View getView() {
        return buffView;
    }

    public void refresh(Boolean... animationInput) {
        boolean withAnim = animationInput.length > 0 ? animationInput[0] : true;  //paramet
        if(!buff.isPerma()) {
            float progress =0;
            if(buff.getMaxDuration()>0) {
                progress = 100 * (buff.getCurrentDuration() / buff.getMaxDuration());
            }
            if(withAnim) {
                circle.setProgressWithAnimation(progress);
            } else {
                circle.setProgress(progress);
            }
            int colorId = R.color.gray;
            if (progress > 80) {
                colorId = R.color.buff_max;
            } else if (progress > 60) {
                colorId = R.color.buff_ok;
            } else if (progress > 40) {
                colorId = R.color.buff_meh;
            } else if (progress > 20) {
                colorId = R.color.buff_not_ok;
            } else if (progress > 0) {
                colorId = R.color.buff_almost_gone;
            }
            circle.setColor(mA.getColor(colorId));
            ((TextView)buffView.findViewById(R.id.buff_icon_name)).setText(buff.getName()+ buff.getDurationText());
        }
    }

    public void setClickListner() {
        getMainFrame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(closed || buff.isPerma()){
                    String appendTime="\nTemps restant : "+buff.getDurationText();
                    tools.customToast(mA,buff.getName()+appendTime,"center");
                } else {
                    Perso yfa = MainActivity.yfa;
                    int currentRankAvail = yfa.getAllResources().getResource("spell_rank_" + buff.getSpellRank()).getCurrent();
                    if (currentRankAvail > 0) {
                        AlertDialog.Builder alertBuild = new AlertDialog.Builder(mA)
                                .setIcon(R.drawable.ic_spell_book)
                                .setTitle("Lancement du sort")
                                .setMessage("Veux tu lancer le sort : " + buff.getName() + "\nInfo : Il te reste " + currentRankAvail + " sort(s) du rang " + buff.getSpellRank())
                                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNeutralButton("Enlever", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(mListenerCancel!=null){mListenerCancel.onEvent();}
                                    }
                                })
                                .setPositiveButton("Lancer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(mListenerCast!=null){mListenerCast.onEvent();}
                                    }
                                });
                        alertBuild.show();
                    } else {
                        tools.customToast(mA, "Tu n'as plus de sort de rang " + buff.getSpellRank() + " de disponible...", "center");
                    }
                }
            }
        });
    }

    public interface OnCastEventListener {
        void onEvent();
    }

    public void setCastEventListener(OnCastEventListener eventListener) {
        mListenerCast = eventListener;
    }

    public interface OnCancelEventListener {
        void onEvent();
    }

    public void setCancelEventListener(OnCancelEventListener eventListener) {
        mListenerCancel = eventListener;
    }
}
