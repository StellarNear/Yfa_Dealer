package stellarnear.yfa_dealer.SettingsFragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import stellarnear.yfa_dealer.CustomAlertDialog;
import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.Perso.Resource;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Tools;

public class PrefSpellgemScreenFragment {
    private Activity mA;
    private Context mC;
    private CustomAlertDialog spellgemPopup;
    private Perso yfa = MainActivity.yfa;
    private Tools tools=new Tools();
    public PrefSpellgemScreenFragment(Activity mA, Context mC) {
        this.mA = mA;
        this.mC = mC;

        createSpellgemPopup();
    }

    private void createSpellgemPopup() {
        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.custom_spellgem,null);
        LinearLayout mainLin = mainView.findViewById(R.id.custom_mainlin);

        int nPerLine=0;
        LinearLayout line = new LinearLayout(mC);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
        line.setGravity(Gravity.CENTER);
        mainLin.addView(line);
        for (int rank=1;rank<=15;rank++){
            final Resource res = yfa.getAllResources().getResource("spell_rank_"+rank);

            if(res.getMax()>0) {
                if(nPerLine>3){
                    line = new LinearLayout(mC);
                    line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1));
                    mainLin.addView(line);
                    line.setGravity(Gravity.CENTER);
                    nPerLine=0;
                }
                TextView text = new TextView(mC);
                text.setText("T" + rank);
                text.setTextSize(20);
                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1));
                text.setTextColor(Color.DKGRAY);
                final int rankSelected = rank;
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new AlertDialog.Builder(mA)
                                .setTitle("Recharger ce tier de sort")
                                .setMessage("Confirmes tu recharger une utilisation du rang "+rankSelected+" ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        reload(res);
                                    }
                                })
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).show();
                    }
                });
                line.addView(text);
                nPerLine++;
            }
        }

        this.spellgemPopup = new CustomAlertDialog(mA,mC,mainView);
        this.spellgemPopup.setPermanent(true);
        this.spellgemPopup.addConfirmButton("Valider");
        this.spellgemPopup.setAcceptEventListener(new CustomAlertDialog.OnAcceptEventListener() {
            @Override
            public void onEvent() {
                Intent intent = new Intent(mA, MainActivity.class);// Switch to MainActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mA.startActivity(intent);
            }
        });
    }

    private void reload(Resource res) {
        res.earn(1);
        Resource convRes = yfa.getAllResources().getResource(res.getId().replace("spell_rank_","spell_conv_rank_"));
        if(convRes!=null && convRes.getMax()>0){
            convRes.earn(1);
        }
        tools.customToast(mC,res.getName()+" : "+res.getCurrent(),"center");
    }

    public void showSpellgem() {
        this.spellgemPopup.showAlert();
    }

}
