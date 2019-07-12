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

import stellarnear.yfa_dealer.Elems.ElemsManager;
import stellarnear.yfa_dealer.MainActivity;
import stellarnear.yfa_dealer.Perso.Perso;
import stellarnear.yfa_dealer.R;
import stellarnear.yfa_dealer.Stats.Stat;
import stellarnear.yfa_dealer.Stats.StatsList;
import stellarnear.yfa_dealer.Tools;

public class DSSFTime {
    private Perso yfa = MainActivity.yfa;
    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private List<String> elemsSelected;
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
        this.elems= ElemsManager.getInstance();
        CheckBox checkNone = mainView.findViewById(R.id.line_type_time_none);
        CheckBox checkAcid = mainView.findViewById(R.id.line_type_time_acid);
        CheckBox checkFire = mainView.findViewById(R.id.line_type_time_fire);
        CheckBox checkShock = mainView.findViewById(R.id.line_type_time_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.line_type_time_frost);
        mapElemCheckbox.put("aucun",checkNone);mapElemCheckbox.put("acide",checkAcid); mapElemCheckbox.put("feu",checkFire);  mapElemCheckbox.put("foudre",checkShock); mapElemCheckbox.put("froid",checkFrost);
        setCheckboxListeners();
        initLineCharts();
    }

    private void setCheckboxListeners() {
        for(String elem : elems.getListKeys()){
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
        elemsSelected = new ArrayList<>();
        for (String elem : elems.getListKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                elemsSelected.add(elem);
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

        for(String elem : elemsSelected) {
            ArrayList<Entry> listVal = new ArrayList<>();
            nDate=0;
            for (String key : mapDatetxtStatslist.keySet()){
                int dmgMoyMeta=0;
                for (Stat stat : yfa.getStats().getStatsList().filterByDate(key).asList()){
                    if(stat.getElemMoyDmg(elem)>0){
                        dmgMoyMeta+=stat.getElemMoyDmg(elem);
                    }
                }
                if(dmgMoyMeta>0) {
                    listVal.add(new Entry((int) nDate, (int) dmgMoyMeta, dmgMoyMeta + " dégâts en moyenne\nle " + key + " pour les sorts de type"+elem));
                }
                nDate++;
                labelList.add(key);

            }

            if(listVal.size()>0) {
                LineDataSet elemSet = new LineDataSet(listVal, elem);
                setLinePara(elemSet, elems.getColorIdSombre(elem));
                data.addDataSet(elemSet);
            }
        }
        data.setValueTextSize(infoTxtSize);
        chartDmg.setData(data);
    }


    private void setRankData() {
        LineData data;
        if(elemsSelected.size()==4){
            data=getDmgDataSolo();
        } else {
            data=getDmgDataElems();
        }
        data.setValueTextSize(infoTxtSize);
        chartRank.setData(data);
    }

    private LineData getDmgDataSolo() {

        ArrayList<Entry> listValDmgMoy = new ArrayList<>();
        int index=0;
        for (String key : mapDatetxtStatslist.keySet()){
            int dmgMoy=0;//mapDatetxtStatslist.get(key).getMoyDmg();
            listValDmgMoy.add(new Entry((int) index, dmgMoy,dmgMoy+" dégâts en moyenne le "+key));
            index++;
        }
        LineDataSet setHit = new LineDataSet(listValDmgMoy,"tout");
        setLinePara(setHit,R.color.dmg_stat);
        LineData data = new LineData();
        data.addDataSet(setHit);
        return data;

    }

    private LineData getDmgDataElems() {
        LineData data = new LineData();
        for(String elem : elemsSelected) {
            ArrayList<Entry> listDmgMoy = new ArrayList<>();
            int index = 0;
            for (String key : mapDatetxtStatslist.keySet()) {
                int dmgMoy = 0;//mapDatetxtStatslist.get(key).getMoyDmgElem(elem);
                listDmgMoy.add(new Entry((int) index, dmgMoy,dmgMoy+" dégâts de "+elems.getName(elem)+" en moyenne le "+key));
                index++;
            }
            LineDataSet setVal = new LineDataSet(listDmgMoy, elems.getName(elem));
            setLinePara(setVal, elems.getColorId(elem));
            data.addDataSet(setVal);
        }
        return data;
    }

    private void setLinePara(LineDataSet set,int color) {
        set.setColors(mC.getColor(color));   set.setLineWidth(2f);   set.setCircleRadius(4f); set.setCircleColor(mC.getColor(color)); set.setValueFormatter(new LargeValueFormatter());
    }

    // Resets
    public void reset() {
        for(String elem : elems.getListKeys()){
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

