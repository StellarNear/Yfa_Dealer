package stellarnear.yfa_dealer.SettingsFragments.DisplayStatsScreenFragment;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import stellarnear.yfa_dealer.Activities.MainActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Stats.Stat;

public class DSSFSpell {
    private Perso yfa = MainActivity.yfa;
    private BarChart chart;
    private PieChart pieChart;
    private PieChart pieChartDetails;
    private int nRankMax=-1;

    private Context mC;
    private View mainView;
    private int infoTxtSize=12;
    private int rankSelectedForPieChart =-1;

    public DSSFSpell(View mainView, Context mC) {
        this.mainView=mainView;
        this.mC=mC;

        TextView nAtkTxt = mainView.findViewById(R.id.nSpell);
        nAtkTxt.setText(yfa.getStats().getStatsList().getNSpell()+ " sorts");

        initChart();
        buildChart();
        initPieChart();
        buildPieChart();
        initPieChartDetails();
        buildPieChartDetails();
    }

    private void initChart() {
        chart = mainView.findViewById(R.id.bar_chart_atk);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setFitBars(true);

        buildChart();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(-0.5f);
        //xAxis.setGranularityEnabled(true);
        //xAxis.setGranularity(1.0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.animateXY(500, 1000);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                resetPieChart();
                resetPieChartCrit();
                rankSelectedForPieChart =(int) e.getX();
                buildPieChart();
                buildPieChartDetails();
            }

            @Override
            public void onNothingSelected() {
                resetPieChart();
                resetPieChartCrit();
                rankSelectedForPieChart =-1;
                buildPieChart();
                buildPieChartDetails();
            }
        });
    }

    private void buildChart() {
        BarData data = new BarData();
        data.addDataSet(computeBarDataSet("hit"));
        data.addDataSet(computeBarDataSet("miss"));
        chart.setData(data);
        chart.getXAxis().setLabelCount(nRankMax+1);
    }

    private BarDataSet computeBarDataSet(String mode) {
        Map<Integer,Integer> rankCountHit = new HashMap<>();
        for (Stat stat : yfa.getStats().getStatsList().asList()){
            List<Integer> listRank = new ArrayList<>();
            if (mode.equalsIgnoreCase("hit")){
                listRank=stat.getListHit();
            } else  if (mode.equalsIgnoreCase("miss")){
                listRank=stat.getListMiss();
            }
            for (int rank : listRank){
                int count = 0;
                if (rankCountHit.get(rank) != null ) {
                    count=rankCountHit.get(rank);
                }
                rankCountHit.put(rank,count+1);
                if(rank>nRankMax){nRankMax=rank;}
            }
        }
        ArrayList<BarEntry> listVal = new ArrayList<>();
        for (int i=0;i<=nRankMax;i++){
            int count = 0;
            if (rankCountHit.get(i) != null ) {
                count=rankCountHit.get(i);
            }
            listVal.add(new BarEntry((int)i,(int)count));
        }
        BarDataSet set = new BarDataSet(listVal,"");
        String text="";
        if (mode.equalsIgnoreCase("hit")){
            text="sort qui touche";
            set.setColor(mC.getColor(R.color.hit_stat));
        } else  if (mode.equalsIgnoreCase("miss")){
            text="sort qui rate";
            set.setColor(mC.getColor(R.color.miss_stat));
        }
        set.setLabel(text);
        set.setValueTextSize(infoTxtSize);
        set.setValueFormatter(new LargeValueFormatter());
        return set;
    }


    private void initPieChart() {
        pieChart = mainView.findViewById(R.id.pie_chart_atk_hit);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        buildPieChart();

        pieChart.getLegend().setEnabled(false);
        pieChart.animateXY(100,1000);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChart.invalidate();
                pieChart.setCenterText(e.getData().toString());
            }

            @Override
            public void onNothingSelected() {
                resetPieChart();
            }
        });
    }

    private void buildPieChart() {
        PieData data = new PieData();
        data.addDataSet(computePieDataSet());
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
    }

    private PieDataSet computePieDataSet() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList= new ArrayList<>();
        float hitVal,missVal,percent;
        if(rankSelectedForPieChart ==-1){
            hitVal= yfa.getStats().getStatsList().getNSpellHit();
            missVal= yfa.getStats().getStatsList().getNSpellMiss();
            percent = 100f*(hitVal/(hitVal+missVal));
        } else {
            hitVal= yfa.getStats().getStatsList().getNSpellHitForRank(rankSelectedForPieChart);
            missVal= yfa.getStats().getStatsList().getNSpellMissForRank(rankSelectedForPieChart);
            percent = 100f*(hitVal/(hitVal+missVal));
        }
        if(percent>0f) {
            entries.add(new PieEntry(percent, "",  new LargeValueFormatter().getFormattedValue(1f*hitVal) + " sorts lancés"));
            colorList.add(mC.getColor(R.color.hit_stat));
        }
        if(percent<100f){
            entries.add(new PieEntry(100f-percent,"",new LargeValueFormatter().getFormattedValue(1f*missVal)+" sorts ratés"));
            colorList.add(mC.getColor(R.color.miss_stat));
        }
        PieDataSet dataset = new PieDataSet(entries,"");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    private void initPieChartDetails() {
        pieChartDetails = mainView.findViewById(R.id.pie_chart_spell_details);
        pieChartDetails.setUsePercentValues(true);
        pieChartDetails.getDescription().setEnabled(false);
        pieChartDetails.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        buildPieChartDetails();
        pieChartDetails.setEntryLabelTextSize(infoTxtSize); //ici full size car on mets directement els valeur dans le label
        pieChartDetails.setEntryLabelColor(Color.BLACK);
        pieChartDetails.getLegend().setEnabled(false);
        pieChartDetails.animateXY(100,1000);
        pieChartDetails.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChartDetails.invalidate();
                pieChartDetails.setCenterText(e.getData().toString());
            }

            @Override
            public void onNothingSelected() {
                resetPieChartCrit();
            }
        });
    }

    private void buildPieChartDetails() {
        PieData data = new PieData();
        data.addDataSet(computePieDataSetDetails());
        data.setDrawValues(false);
        pieChartDetails.setData(data);
        pieChartDetails.setUsePercentValues(false);
    }

    private PieDataSet computePieDataSetDetails() {
        float nNormal,nCrit,nGlaeBoost,nContactMiss,nGlaeFail,nResist;
        if(rankSelectedForPieChart ==-1){
            nCrit=yfa.getStats().getStatsList().getNCrit();
            nGlaeBoost=yfa.getStats().getStatsList().getNGlaeBoost();
            nContactMiss=yfa.getStats().getStatsList().getNContactMiss();
            nGlaeFail=yfa.getStats().getStatsList().getNGlaeFail();
            nResist=yfa.getStats().getStatsList().getNResist();
            nNormal=yfa.getStats().getStatsList().getNSpellHit()-nCrit-nGlaeBoost;
        } else {
            nCrit=yfa.getStats().getStatsList().getNCritForRank(rankSelectedForPieChart);
            nGlaeBoost=yfa.getStats().getStatsList().getNGlaeBoostForRank(rankSelectedForPieChart);
            nContactMiss=yfa.getStats().getStatsList().getNContactMissForRank(rankSelectedForPieChart);
            nGlaeFail=yfa.getStats().getStatsList().getNGlaeFailForRank(rankSelectedForPieChart);
            nResist=yfa.getStats().getStatsList().getNResistForRank(rankSelectedForPieChart);
            nNormal=yfa.getStats().getStatsList().getNSpellHitForRank(rankSelectedForPieChart)-nCrit-nGlaeBoost;
        }
        float nTot=nNormal+nCrit+nGlaeBoost+nContactMiss+nGlaeFail+nResist;
        float normalPercent=100f*(nNormal/nTot);
        float critPercent=100f*(nCrit/nTot);
        float glaeBoostPercent=100f*(nGlaeBoost/nTot);
        float contactMissPercent=100f*(nContactMiss/nTot);
        float resistPercent=100f*(nResist/nTot);
        float glaeFailPercent=100f*(nGlaeFail/nTot);

        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList= new ArrayList<>();
        /*
        if(normalPercent>0f){
            entries.add(new PieEntry(normalPercent,"normal",new LargeValueFormatter().getFormattedValue(((int)(nNormal)))+" sorts normaux"));
            colorList.add(mC.getColor(R.color.hit_stat));
        }*/
        if(critPercent>0f){
            entries.add(new PieEntry(critPercent,new PercentFormatter().getFormattedValue(critPercent)+" crit",new LargeValueFormatter().getFormattedValue(((int)(nCrit)))+" sorts critiques"));
            colorList.add(mC.getColor(R.color.crit_stat));
        }

        if(glaeBoostPercent>0f){
            entries.add(new PieEntry(glaeBoostPercent,new PercentFormatter().getFormattedValue(glaeBoostPercent)+" glaeBoost",new LargeValueFormatter().getFormattedValue(((int)(nGlaeBoost)))+" sorts boostés par Glaedäyes"));
            colorList.add(mC.getColor(R.color.bglae_boost_stat));
        }
        if(contactMissPercent>0f){
            entries.add(new PieEntry(contactMissPercent,new PercentFormatter().getFormattedValue(contactMissPercent)+" miss",new LargeValueFormatter().getFormattedValue(((int)(nContactMiss)))+" sorts ratés"));
            colorList.add(mC.getColor(R.color.contact_miss_stat));
        }
        if(resistPercent>0f){
            entries.add(new PieEntry(resistPercent,new PercentFormatter().getFormattedValue(resistPercent)+" resist",new LargeValueFormatter().getFormattedValue(((int)(nResist)))+" sorts résistés"));
            colorList.add(mC.getColor(R.color.resist_stat));
        }
        if(glaeFailPercent>0f){
            entries.add(new PieEntry(glaeFailPercent,new PercentFormatter().getFormattedValue(glaeFailPercent)+" glaeFail",new LargeValueFormatter().getFormattedValue(((int)(nGlaeFail)))+" sorts ratés par Glaedäyes"));
            colorList.add(mC.getColor(R.color.glae_fail_stat));
        }


        PieDataSet dataset = new PieDataSet(entries,"");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    public void reset() {
        rankSelectedForPieChart =-1;
        resetChart();
        resetPieChart();
        resetPieChartCrit();
        buildChart();
        buildPieChart();
        buildPieChartDetails();
    }

    private void resetChart() {
        chart.invalidate();
        chart.fitScreen();
        chart.highlightValue(null);
    }
    private void resetPieChart() {
        pieChart.invalidate();
        pieChart.setCenterText("");
        pieChart.highlightValue(null);
    }
    private void resetPieChartCrit() {
        pieChartDetails.invalidate();
        pieChartDetails.setCenterText("");
        pieChartDetails.highlightValue(null);
    }
}
