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
    private OnCastExtendEventListener mListenerCastExtend;
    private boolean closed=false;
    private Tools tools=new Tools();
    private Perso yfa = MainActivity.yfa;

    public BuffView(Activity mA,Buff buff){
        this.mA=mA;
        this.buff=buff;
        LayoutInflater inflater = mA.getLayoutInflater();
        LinearLayout buffView = (LinearLayout) inflater.inflate(R.layout.buff_icon, null);
        this.buffView=buffView;

        ((TextView)buffView.findViewById(R.id.buff_icon_name)).setText(buff.getName());
        buffView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1));

        setImage();
        if(buff.isPerma()){
            int dimPix = mA.getResources().getDimensionPixelSize(R.dimen.icon_buff_perma);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimPix, dimPix);
            ((FrameLayout)buffView.findViewById(R.id.buff_icon_frame)).setLayoutParams(layoutParams);
            ((ImageView) buffView.findViewById(R.id.buff_icon_image_big)).setVisibility(View.GONE);
            circle = buffView.findViewById(R.id.circular_progress);
            circle.setMax(100);
            circle.setProgress(100);
            circle.setColor(mA.getColor(R.color.buff_time_perma));
            circle.setStrokeWidth(5);
            ((FrameLayout)buffView.findViewById(R.id.buff_icon_frame)).removeView(circle);
            ((FrameLayout)buffView.findViewById(R.id.buff_icon_frame)).addView(circle);//pour faire passer le cercle sur l'icone
        } else {
            ((ImageView) buffView.findViewById(R.id.buff_icon_image_small)).setVisibility(View.GONE);
            circle = buffView.findViewById(R.id.circular_progress);
            circle.setMax(100);
            refresh(false);
        }
        setNameClick();
    }

    private void setImage() {
        int imgInt = 0;
        try {
            imgInt = mA.getResources().getIdentifier(buff.getId(), "drawable", mA.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();

        }
        if(imgInt==0){imgInt =R.drawable.mire_test_cercle;}
        if(buff.isPerma()) {
            ((ImageView) buffView.findViewById(R.id.buff_icon_image_small)).setImageDrawable(mA.getDrawable(imgInt));
        } else {
            if (buff.isActive()) {
                ((ImageView) buffView.findViewById(R.id.buff_icon_image_big)).setImageDrawable(mA.getDrawable(imgInt));
            } else {
                ((ImageView) buffView.findViewById(R.id.buff_icon_image_big)).setImageDrawable(tools.convertToGrayscale(mA.getDrawable(imgInt).mutate()));
            }
        }
    }

    private void setNameClick() {
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
        setImage();
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
                    String appendTime="";
                    if(buff.isActive()){appendTime="\nTemps restant "+buff.getDurationText();} else { appendTime="\nAmélioration inactive"; }
                    tools.customToast(mA,buff.getName()+appendTime,"center");
                } else if (buff.isFromSpell()){
                    popupSpellCastNormal();
                } else {
                    popupCapaCast();
                }
            }
        });
        getMainFrame().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!closed && !buff.isPerma() ) {
                    if(buff.isFromSpell()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mA);
                        builder.setTitle("Nombre d'utilisations de l'extension de durée");
                        // add a radio button list
                        int checkedItem = -1;
                        String[] numbers = {"1", "2", "3", "4", "5"};
                        builder.setSingleChoiceItems(numbers, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                popupMultiDuraExtend(which + 1);
                                dialog.cancel();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else if(buff.isFromBloodLine()) {
                        popupCapaCastExtend();
                    }
                }
                return true;
            }
        });
    }

    private void popupSpellCastNormal() {
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

    private void popupCapaCast() {
        int currentUsageAvail = yfa.getAllResources().getResource(buff.getId().replace("capacity","resource")).getCurrent();
        if (currentUsageAvail > 0) {
            AlertDialog.Builder alertBuild = new AlertDialog.Builder(mA)
                    .setIcon(R.drawable.ic_spell_book)
                    .setTitle("Lancement de la capacité")
                    .setMessage("Veux tu lancer la capacité : " + buff.getName() + "\nInfo : Il te reste " + currentUsageAvail + " utilisation.")
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
            tools.customToast(mA, "Tu n'as plus d'utilisation de la capacité " + buff.getName() + "...", "center");
        }
    }

    private void popupCapaCastExtend() {
        int currentUsageAvail = yfa.getAllResources().getResource(buff.getId().replace("capacity","resource")).getCurrent();
        int surgeEpicBlood =  yfa.getAllResources().getResource("capacity_epic_bloodline".replace("capacity","resource")).getCurrent();
        if (currentUsageAvail > 0 && surgeEpicBlood>0) {
            final int nCast=yfa.getAllCapacities().getCapacity("capacity_epic_bloodline").getValue();
            AlertDialog.Builder alertBuild = new AlertDialog.Builder(mA)
                    .setIcon(R.drawable.ic_spell_book)
                    .setTitle("Lancement de la capacité (Ext*"+nCast+")")
                    .setMessage("Veux tu surcharger la capacité : " + buff.getName() +
                            "\nInfo : Il te reste " + currentUsageAvail + " utilisation de la capacité."+
                            "\nEt " + surgeEpicBlood + " utilisation de lignage épique.")
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setNeutralButton("Enlever", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListenerCancel != null) {
                                mListenerCancel.onEvent();
                            }
                        }
                    })
                    .setPositiveButton("Lancer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListenerCastExtend != null) {
                                mListenerCastExtend.onEvent(nCast);
                            }
                        }
                    });
            alertBuild.show();
        } else {
            if(currentUsageAvail<=0) {
                tools.customToast(mA, "Tu n'as plus d'utilisation de la capacité " + buff.getName() + "...", "center");
            } else {
                tools.customToast(mA, "Tu n'as plus d'utilisation de lignage épique ...", "center");
            }
        }
    }

    private void popupMultiDuraExtend(final int nCast) {
        int currentRankAvail = yfa.getResourceValue("spell_rank_" + (int) (buff.getSpellRank()+nCast));
        if (currentRankAvail > 0) {
            AlertDialog.Builder alertBuild = new AlertDialog.Builder(mA)
                    .setIcon(R.drawable.ic_spell_book)
                    .setTitle("Lancement du sort (Ext*"+nCast+")")
                    .setMessage("Veux tu lancer le sort étendu en durée : " + buff.getName() + "\nInfo : Il te reste " + currentRankAvail + " sort(s) du rang " +(int) (buff.getSpellRank()+nCast))
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setNeutralButton("Enlever", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListenerCancel != null) {
                                mListenerCancel.onEvent();
                            }
                        }
                    })
                    .setPositiveButton("Lancer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListenerCastExtend != null) {
                                mListenerCastExtend.onEvent(nCast);
                            }
                        }
                    });
            alertBuild.show();
        } else {
            tools.customToast(mA, "Tu n'as plus de sort de rang " + (int) (buff.getSpellRank()+1) + " de disponible...", "center");
        }
    }

    public interface OnCastEventListener {
        void onEvent();
    }

    public void setCastEventListener(OnCastEventListener eventListener) {
        mListenerCast = eventListener;
    }

    public interface OnCastExtendEventListener {
        void onEvent(int nCast);
    }

    public void setCastExtendEventListener(OnCastExtendEventListener eventListener) {
        mListenerCastExtend = eventListener;
    }

    public interface OnCancelEventListener {
        void onEvent();
    }

    public void setCancelEventListener(OnCancelEventListener eventListener) {
        mListenerCancel = eventListener;
    }
}
