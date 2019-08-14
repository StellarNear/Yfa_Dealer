package stellarnear.yfa_dealer.SettingsFragments.DisplayStatsScreenFragment;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import stellarnear.yfa_dealer.Activities.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.SpellTypes.SpellTypesManager;
import stellarnear.yfa_dealer.Stats.Stat;
import stellarnear.yfa_dealer.Stats.StatsList;
import stellarnear.yfa_dealer.Tools;

public class DSSFTime {
    private Perso yfa = MainActivity.yfa;
    private Context mC;
    private View mainView;
    private SpellTypesManager types;
    private List<String> typeSelected;
    private Map<String, StatsList> mapDatetxtStatslist = new LinkedHashMap<>();
    private List<String> labelList=new ArrayList<>();
    private Map<String, CheckBox> mapElemCheckbox=new HashMap<>();
    private int infoTxtSize=10;
    private int nDate=0;
    private LineChart chartDmg;
    private LineChart chartRank;
    private Tools tools=new Tools();

    public DSSFTime(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.types = SpellTypesManager.getInstance();
        CheckBox checkNoDmg = mainView.findViewById(R.id.line_type_time_nodmg);
        CheckBox checkNone = mainView.findViewById(R.id.line_type_time_none);
        CheckBox checkAcid = mainView.findViewById(R.id.line_type_time_acid);
        CheckBox checkFire = mainView.findViewById(R.id.line_type_time_fire);
        CheckBox checkShock = mainView.findViewById(R.id.line_type_time_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.line_type_time_frost);
        mapElemCheckbox.put("",checkNoDmg);mapElemCheckbox.put("aucun",checkNone);mapElemCheckbox.put("acide",checkAcid); mapElemCheckbox.put("feu",checkFire);  mapElemCheckbox.put("foudre",checkShock); mapElemCheckbox.put("froid",checkFrost);
        setCheckboxListeners();
        initLineCharts();
    }

    private void setCheckboxListeners() {
        for(String elem : types.getListKeys()){
            mapElemCheckbox.get(elem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calculateElemToShow();
                    resetChartDmg();
                    resetChartRank();
                    setDmgData();
                    setRankData();
                }
            });
        }
    }

    private void initLineCharts() {
        calculateElemToShow();
        initLineChartDmg();
        initLineChartRank();
        buildCharts();
        chartDmg.animateXY(750, 1000);
        chartRank.animateXY(750, 1000);
    }

    private void initLineChartDmg(){
        chartDmg =mainView.findViewById(R.id.line_chart_time);
        setChartPara(chartDmg);
        chartDmg.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }

            @Override
            public void onNothingSelected() {
                resetChartDmg();
            }
        });
    }

    private void setChartPara(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getXAxis().setDrawGridLines(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void calculateElemToShow() {
        typeSelected = new ArrayList<>();
        for (String elem : types.getListKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                typeSelected.add(elem);
            }
        }
    }

    private void initLineChartRank() {
        chartRank =mainView.findViewById(R.id.line_chart_time_dmg);
        setChartPara(chartRank);
        chartRank.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC,e.getData().toString(),"center");
            }

            @Override
            public void onNothingSelected() {
                resetChartRank();
            }
        });
    }

    private void buildCharts() {
        computeHashmaps();
        setDmgData();
        setRankData();
        formatAxis(chartDmg);
        formatAxis(chartRank);
    }

    private void formatAxis(LineChart chart) {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelList));
        xAxis.setLabelRotationAngle(-90);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
    }

    private void computeHashmaps() {
        mapDatetxtStatslist = new LinkedHashMap<>();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy", Locale.FRANCE);
        for (Stat stat : yfa.getStats().getStatsList().asList()){
            String dateTxt = formater.format(stat.getDate());
            if(mapDatetxtStatslist.get(dateTxt)==null) {
                mapDatetxtStatslist.put(dateTxt,new StatsList());
            }
            mapDatetxtStatslist.get(dateTxt).add(stat);
        }
    }

    private void setDmgData() {
        labelList=new ArrayList<>();
        LineData data = new LineData();

        if(typeSelected.size()==6 || (typeSelected.size()==5 && !typeSelected.contains(""))){
            addDmgAllTypesData(data);
        } else {
            addDmgTypesData(data);
        }
        data.setValueTextSize(infoTxtSize);
        chartDmg.setData(data);
    }

    private void addDmgAllTypesData(LineData data) {
            ArrayList<Entry> listVal = new ArrayList<>();
            nDate=0;
            for (String key : mapDatetxtStatslist.keySet()){
                float dmgSumMoy=0;
                int count=0;
                for (Stat stat : yfa.getStats().getStatsList().filterByDate(key).asList()){
                    if(stat.getMoyDmg()>0){
                        dmgSumMoy+=stat.getMoyDmg();
                        count++;
                    }
                }
                if(dmgSumMoy>0) {
                    listVal.add(new Entry((int) nDate, Math.round(dmgSumMoy/count), Math.round(dmgSumMoy/count) + " dégâts en moyenne\nle " + key + " pour l'ensemble des sorts"));
                }
                nDate++;
                labelList.add(key);
            }

            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, "tout");
                setLinePara(elemSet, R.color.all_stat);
                data.addDataSet(elemSet);
            }
    }

    private void addDmgTypesData(LineData data) {
        for(String type : typeSelected) {
            ArrayList<Entry> listVal = new ArrayList<>();
            nDate=0;
            for (String key : mapDatetxtStatslist.keySet()){
                float dmgSumMoy=0;
                int count=0;
                for (Stat stat : yfa.getStats().getStatsList().filterByDate(key).asList()){
                    if(stat.getElemMoyDmg(type)>0){
                        dmgSumMoy+=stat.getElemMoyDmg(type);
                        count++;
                    }
                }
                if(dmgSumMoy>0) {
                    listVal.add(new Entry((int) nDate, Math.round(dmgSumMoy/count), Math.round(dmgSumMoy/count) + " dégâts en moyenne\nle " + key + " pour les sorts de type"+type));
                }
                nDate++;
                labelList.add(key);

            }

            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, type);
                setLinePara(elemSet, types.getColorIdSombre(type));
                data.addDataSet(elemSet);
            }
        }
    }


    private void setRankData() {
        LineData data = new LineData();

        if(typeSelected.size()==6){
            addMetaAllTypesData(data);
        } else {
            addmetaTypesData(data);
        }
        data.setValueTextSize(infoTxtSize);
        chartRank.setData(data);
    }

    private void addMetaAllTypesData(LineData data) {
            ArrayList<Entry> listVal = new ArrayList<>();
            nDate=0;
            for (String key : mapDatetxtStatslist.keySet()){
                float rankSumMoy=0;
                int count=0;
                for (Stat stat : yfa.getStats().getStatsList().filterByDate(key).asList()){
                    if(stat.getRankMoy()>0){
                        rankSumMoy+=stat.getRankMoy();
                        count++;
                    }
                }
                if(rankSumMoy>0) {
                    listVal.add(new Entry((int) nDate, Math.round(rankSumMoy/count), Math.round(rankSumMoy/count) + " rang de sort en moyenne\nlancé le " + key + " pour l'ensemble des sorts"));
                }
                nDate++;
            }

            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, "tout");
                setLinePara(elemSet, R.color.all_stat);
                data.addDataSet(elemSet);
            }
    }

    private void addmetaTypesData(LineData data) {
        for(String type : typeSelected) {
            ArrayList<Entry> listVal = new ArrayList<>();
            nDate=0;
            for (String key : mapDatetxtStatslist.keySet()){
                float rankSumMoy=0;
                int count=0;
                for (Stat stat : yfa.getStats().getStatsList().filterByDate(key).asList()){
                    if(stat.getElemRankMoy(type)>0){
                        rankSumMoy+=stat.getElemRankMoy(type);
                        count++;
                    }
                }
                if(rankSumMoy>0) {
                    listVal.add(new Entry((int) nDate, Math.round(rankSumMoy/count), Math.round(rankSumMoy/count) + " rang de sort en moyenne\nlancé le " + key + " pour les sorts de type"+type));
                }
                nDate++;
            }

            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, types.getName(type));
                setLinePara(elemSet, types.getColorIdSombre(type));
                data.addDataSet(elemSet);
            }
        }
    }


    private void setLinePara(LineDataSet set,int color) {
        set.setColors(mC.getColor(color));   set.setLineWidth(2f);   set.setCircleRadius(4f); set.setCircleColor(mC.getColor(color)); set.setValueFormatter(new LargeValueFormatter());
    }

    // Resets
    public void reset() {
        for(String elem : types.getListKeys()){
            mapElemCheckbox.get(elem).setChecked(true);
        }
        resetChartDmg();
        resetChartRank();
        buildCharts();
    }

    private void resetChartDmg() {
        chartDmg.invalidate();
        chartDmg.fitScreen();
        chartDmg.highlightValue(null);
    }

    private void resetChartRank() {
        calculateElemToShow();
        chartRank.invalidate();
        chartRank.fitScreen();
        chartRank.highlightValue(null);
    }
}

