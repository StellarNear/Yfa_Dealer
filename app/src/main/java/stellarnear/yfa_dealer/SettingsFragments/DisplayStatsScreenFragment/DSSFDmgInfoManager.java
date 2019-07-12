package stellarnear.yfa_dealer.SettingsFragments.DisplayStatsScreenFragment;

/*

public class DSSFDmgInfoManager {
    private Perso wedge = MainActivity.wedge;

    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private Map<String, CheckBox> mapElemCheckbox;
    private StatsList selectedStats=new StatsList();
    private String selectedBracket;
    private boolean allStats;
    private int infoTxtSize = 12;

    private Tools tools=new Tools();

    public DSSFDmgInfoManager(View mainView, Map<String,CheckBox> mapElemCheckbox, Context mC) {
        this.mainView = mainView;
        this.elems=ElemsManager.getInstance(mC);
        this.mapElemCheckbox=mapElemCheckbox;
        this.mC = mC;

        addInfos(null);
    }

    public void setSubSelectionBracket(String s) {
        this.selectedBracket=s;
    }

    public void addInfos(StatsList selectedStats){
        if(selectedStats==null){
            this.selectedStats=wedge.getStats().getStatsList();
            this.allStats=true;
        } else { this.selectedStats=selectedStats; this.allStats=false;}
        if(this.selectedStats.size()>0){addInfos();}
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
        t.setTextSize(20);
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

        for (String elem : elems.getListKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                int colorInt = elems.getColorId(elem);
                TextView telemMin = createTextElement(String.valueOf(selectedStats.getMinDmgElem(elem)));
                telemMin.setTextColor(colorInt);
                lineMin.addView(telemMin);
                TextView telemMoy = createTextElement(String.valueOf(selectedStats.getMoyDmgElem(elem)));
                telemMoy.setTextColor(colorInt);
                lineMoy.addView(telemMoy);
                TextView telemMax = createTextElement(String.valueOf(selectedStats.getMaxDmgElem(elem)));
                telemMax.setTextColor(colorInt);
                lineMax.addView(telemMax);
            }
        }
    }

    private void addInfosRecent(LinearLayout bloc2) {
        TextView t = new TextView(mC);
        t.setText("Récent");
        t.setGravity(Gravity.CENTER);
        t.setTextSize(20);
        t.setTextColor(Color.BLACK);
        bloc2.addView(t);

        LinearLayout lineScore = createLine();
        bloc2.addView(lineScore);
        LinearLayout linePercent = createLine();
        bloc2.addView(linePercent);

        TextView titleScore = createTextElement("score");
        lineScore.addView(titleScore);
        TextView titlePercent = createTextElement(">=%");
        linePercent.addView(titlePercent);

        for (String elem : elems.getListKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                int colorInt = elems.getColorId(elem);
                TextView telemScore = createTextElement(String.valueOf(selectedStats.getLastStat().getElemSumDmg().get(elem)));
                telemScore.setTextColor(colorInt);
                lineScore.addView(telemScore);
                TextView telemPercent = createTextElement(calculateAbovePercentage(selectedStats, elem));
                telemPercent.setTextColor(colorInt);
                linePercent.addView(telemPercent);
            }
        }
    }

    private String calculateAbovePercentage(StatsList selectedStats, String elem) {
        int lastElemDmg=selectedStats.getLastStat().getElemSumDmg().get(elem);
        int sup=0;
        for(Stat stat: selectedStats.asList()){
            if(stat.getElemSumDmg().get(elem)<=lastElemDmg){
                sup++;
            }
        }
        float percent=100f*(((1f*sup)/(1f*selectedStats.size())));
        int roundPercent=Math.round(percent);
        return roundPercent+"%";
    }

    private void addInfosDetails(LinearLayout bloc2) {
        TextView t = new TextView(mC);
        t.setText("Détails (moy)");
        t.setGravity(Gravity.CENTER);
        t.setTextSize(20);
        t.setTextColor(Color.BLACK);
        bloc2.addView(t);

        LinearLayout lineHit = createLine();
        bloc2.addView(lineHit);
        LinearLayout lineCrit = createLine();
        bloc2.addView(lineCrit);
        LinearLayout lineCritNat = createLine();
        bloc2.addView(lineCritNat);

        TextView titleHit = createTextElement("touché");
        titleHit.setTextColor(mC.getColor(R.color.hit_stat));
        lineHit.addView(titleHit);
        int hitVal=selectedStats.getNAtksHit();
        int missVal=selectedStats.getNAtksMiss();
        int percent =Math.round(100f*((1f*hitVal)/(1f*hitVal+missVal)));
        float hitValMoy = selectedStats.size()>1 ?(1f*hitVal)/selectedStats.size() : 0;
        TextView percentText = createTextElement(percent+"%" + " ("+String.format ("%,.1f", hitValMoy)+")");
        percentText.setTextColor(mC.getColor(R.color.hit_stat));
        lineHit.addView(percentText);

        int nHit=selectedStats.getNAtksHit();
        int nCrit=selectedStats.getNCrit();
        int nCritNat=selectedStats.getNCritNat();
        float nCritMoy = selectedStats.size()>0 ? (1f*(nCrit-nCritNat))/selectedStats.size() : 0;
        float nCritNatMoy = selectedStats.size()>0 ? (1f*nCritNat)/selectedStats.size() : 0;

        int critPercent=Math.round(100f*(nCrit-nCritNat)/(1f*nHit));
        int critNatPercent=Math.round(100f*(nCritNat/(1f*nHit)));
        TextView titleCrit = createTextElement("crit");
        titleCrit.setTextColor(mC.getColor(R.color.crit_stat));
        lineCrit.addView(titleCrit);
        TextView percentCritText = createTextElement(critPercent+"%" +" ("+String.format ("%,.1f",nCritMoy)+")");
        percentCritText.setTextColor(mC.getColor(R.color.crit_stat));
        lineCrit.addView(percentCritText);

        TextView titleCritNat = createTextElement("critNat");
        titleCritNat.setTextColor(mC.getColor(R.color.crit_nat_stat));
        lineCritNat.addView(titleCritNat);
        TextView percentCritNatText = createTextElement(critNatPercent+"%" +" ("+String.format ("%,.1f",nCritNatMoy)+")");
        percentCritNatText.setTextColor(mC.getColor(R.color.crit_nat_stat));
        lineCritNat.addView(percentCritNatText);
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
*/
