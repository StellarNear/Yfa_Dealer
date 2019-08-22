package stellarnear.yfa_companion.SettingsFragments.DisplayStatsScreenFragment;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.SpellTypes.SpellTypesManager;
import stellarnear.yfa_companion.Stats.DamagesShortList;
import stellarnear.yfa_companion.Stats.DamagesShortListElement;
import stellarnear.yfa_companion.Tools;

public class DSSFDmgInfoManager {
    private Perso yfa = MainActivity.yfa;

    private Context mC;
    private View mainView;
    private SpellTypesManager elems;
    private Map<String, CheckBox> mapElemCheckbox;
    private DamagesShortList selectedDamageShortList =new DamagesShortList();
    private String selectedBracket;
    private boolean allStats;
    private int infoTxtSize = 12;

    private Tools tools=new Tools();

    public DSSFDmgInfoManager(View mainView, Map<String, CheckBox> mapElemCheckbox, Context mC) {
        this.mainView = mainView;
        this.elems= SpellTypesManager.getInstance();
        this.mapElemCheckbox=mapElemCheckbox;
        this.mC = mC;

        addInfos(null);
    }

    public void setSubSelectionBracket(String s) {
        this.selectedBracket=s;
    }

    public void addInfos(DamagesShortList selectedDamageShortList){
        if(selectedDamageShortList==null){
            this.selectedDamageShortList = yfa.getStats().getStatsList().getDamageShortList();
            this.allStats=true;
        } else { this.selectedDamageShortList =selectedDamageShortList; this.allStats=false;}
        if(this.selectedDamageShortList.size()>0){addInfos();}
    }

    private void addInfos(){
        final LinearLayout mainLin = mainView.findViewById(R.id.chart_dmg_info_text);
        mainLin.removeAllViews();

        LinearLayout bloc1 = setBlock();
        mainLin.addView(bloc1);
        LinearLayout bloc2 = setBlock();
        mainLin.addView(bloc2);

        addInfosSelection(bloc1);
        if(allStats){addInfosRecent(bloc2);}else{addInfosDetails(bloc2);}
    }

    private void addInfosSelection(LinearLayout bloc1) {
        TextView t = new TextView(mC);
        String label="Tout";
        if(!allStats){label=selectedBracket;}
        t.setText(label);
        t.setGravity(Gravity.CENTER);
        t.setTextSize(18);
        t.setTextColor(Color.BLACK);
        bloc1.addView(t);

        LinearLayout lineMin = createLine();
        bloc1.addView(lineMin);
        LinearLayout lineMoy = createLine();
        bloc1.addView(lineMoy);
        LinearLayout lineMax = createLine();
        bloc1.addView(lineMax);

        TextView titleMin = createTextElement("min");
        lineMin.addView(titleMin);
        TextView titleMoy = createTextElement("moy");
        lineMoy.addView(titleMoy);
        TextView titleMax = createTextElement("max");
        lineMax.addView(titleMax);

        for (String elem : elems.getListDmgKeys()) {
            int minDmg=selectedDamageShortList.filterByElem(elem).getMinDmg();
            int moyDmg=selectedDamageShortList.filterByElem(elem).getDmgMoy();
            int maxDmg=selectedDamageShortList.filterByElem(elem).getMaxDmg();
            boolean dmgNull = minDmg == 0 && moyDmg == 0 && maxDmg ==0;
            if (!dmgNull && mapElemCheckbox.get(elem).isChecked()) {
                int colorInt = elems.getColorIdSombre(elem);
                TextView telemMin = createTextElement(String.valueOf(minDmg));
                telemMin.setTextColor(mC.getColor(colorInt));
                lineMin.addView(telemMin);
                TextView telemMoy = createTextElement(String.valueOf(moyDmg));
                telemMoy.setTextColor(mC.getColor(colorInt));
                lineMoy.addView(telemMoy);
                TextView telemMax = createTextElement(String.valueOf(maxDmg));
                telemMax.setTextColor(mC.getColor(colorInt));
                lineMax.addView(telemMax);
            }
        }
    }

    private void addInfosRecent(LinearLayout bloc2) {
        TextView t = new TextView(mC);
        t.setText("Récent");
        t.setGravity(Gravity.CENTER);
        t.setTextSize(18);
        t.setTextColor(Color.BLACK);
        bloc2.addView(t);

        LinearLayout lineScore = createLine();
        bloc2.addView(lineScore);
        LinearLayout linePercent = createLine();
        bloc2.addView(linePercent);

        TextView titleScore = createTextElement("val");
        lineScore.addView(titleScore);
        TextView titlePercent = createTextElement(">=%");
        linePercent.addView(titlePercent);

        for (String elem : elems.getListDmgKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                int colorInt = elems.getColorIdSombre(elem);

                DamagesShortListElement lastElement = selectedDamageShortList.getLastDamageElement();
                if(lastElement.getElement().equalsIgnoreCase(elem)){
                    TextView telemScore = createTextElement(String.valueOf(lastElement.getDmgSum()));
                    telemScore.setTextColor(mC.getColor(colorInt));
                    lineScore.addView(telemScore);
                    TextView telemPercent = createTextElement(calculateAbovePercentage(selectedDamageShortList, elem));
                    telemPercent.setTextColor(mC.getColor(colorInt));
                    linePercent.addView(telemPercent);
                } else {
                    TextView telemScore = createTextElement("-");
                    telemScore.setTextColor(Color.DKGRAY);
                    lineScore.addView(telemScore);
                    TextView telemPercent = createTextElement("-");
                    telemPercent.setTextColor(Color.DKGRAY);
                    linePercent.addView(telemPercent);
                }
            }
        }
    }

    private String calculateAbovePercentage(DamagesShortList damagesShortList, String elem) {
        int lastElemDmg=damagesShortList.getLastDamageElement().getDmgSum();
        int sup=0;
        for(DamagesShortListElement element: damagesShortList.filterByElem(elem).asList()){
            if(element.getDmgSum()<=lastElemDmg){
                sup++;
            }
        }
        float percent=100f*(((1f*sup)/(1f*damagesShortList.filterByElem(elem).size())));
        int roundPercent=Math.round(percent);
        return roundPercent+"%";
    }

    private void addInfosDetails(LinearLayout bloc2) {
        TextView t = new TextView(mC);
        t.setText("Rank (moy)");
        t.setGravity(Gravity.CENTER);
        t.setTextSize(18);
        t.setTextColor(Color.BLACK);
        bloc2.addView(t);

        LinearLayout lineRank = createLine();
        bloc2.addView(lineRank);
        LinearLayout lineMeta = createLine();
        bloc2.addView(lineMeta);
        LinearLayout lineArcane = createLine();
        bloc2.addView(lineArcane);
        LinearLayout lineMythic = createLine();
        bloc2.addView(lineMythic);

        TextView titleRank = createTextElement("sort");
        titleRank.setTextColor(Color.DKGRAY);
        lineRank.addView(titleRank);
        TextView rankText = createTextElement(String.valueOf(selectedDamageShortList.getRankMoy()));
        rankText.setTextColor(Color.DKGRAY);
        lineRank.addView(rankText);

        TextView titleCrit = createTextElement("métamagie");
        titleCrit.setTextColor(Color.DKGRAY);
        lineMeta.addView(titleCrit);
        TextView rangMetaMoyText = createTextElement(String.valueOf(selectedDamageShortList.getMetaRankMoy()));
        rangMetaMoyText.setTextColor(Color.DKGRAY);
        lineMeta.addView(rangMetaMoyText);

        TextView arcaneConvRankMoyTitle = createTextElement("conversion");
        arcaneConvRankMoyTitle.setTextColor(Color.DKGRAY);
        lineArcane.addView(arcaneConvRankMoyTitle);
        TextView arcaneConvRankMoy = createTextElement(String.valueOf(selectedDamageShortList.getArcaneConvRankMoy()));
        arcaneConvRankMoy.setTextColor(Color.DKGRAY);
        lineArcane.addView(arcaneConvRankMoy);

        TextView nMythicMoyTitle = createTextElement("NMythic");
        nMythicMoyTitle.setTextColor(Color.DKGRAY);
        lineMythic.addView(nMythicMoyTitle);
        TextView nMythicMoy = createTextElement(String.valueOf(selectedDamageShortList.getNMythicMoy()));
        nMythicMoy.setTextColor(Color.DKGRAY);
        lineMythic.addView(nMythicMoy);
    }

    private LinearLayout setBlock() {
        LinearLayout frame = new LinearLayout(mC);
        frame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,1));
        frame.setGravity(Gravity.CENTER);
        frame.setOrientation(LinearLayout.VERTICAL);
        return  frame;
    }

    private TextView createTextElement(String txt) {
        TextView textTitle = new TextView(mC);
        textTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1));
        textTitle.setText(txt);
        textTitle.setGravity(Gravity.CENTER);
        textTitle.setTextSize(infoTxtSize);
        textTitle.setTextColor(Color.BLACK);
        return textTitle;
    }

    private LinearLayout createLine() {
        LinearLayout line = new LinearLayout(mC);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        line.setGravity(Gravity.CENTER);
        line.setOrientation(LinearLayout.HORIZONTAL);
        return  line;
    }


}


