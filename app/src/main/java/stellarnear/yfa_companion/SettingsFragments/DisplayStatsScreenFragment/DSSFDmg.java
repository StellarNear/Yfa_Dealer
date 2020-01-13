package stellarnear.yfa_companion.SettingsFragments.DisplayStatsScreenFragment;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.yfa_companion.Activities.MainActivity;
import stellarnear.yfa_companion.Perso.Perso;
import stellarnear.yfa_companion.R;
import stellarnear.yfa_companion.SpellTypes.SpellTypesManager;
import stellarnear.yfa_companion.Stats.DamagesShortList;
import stellarnear.yfa_companion.Tools;

public class DSSFDmg {
    private Perso yfa = MainActivity.yfa;
    private DSSFDmgChartMaker chartMaker;
    private DSSFDmgInfoManager subManager;
    private PieChart pieChart;

    private Context mC;
    private View mainView;
    private SpellTypesManager elems;
    private Map<String, CheckBox> mapElemCheckbox=new HashMap<>();
    private DamagesShortList selectedDamagesShortList =new DamagesShortList();
    private int infoTxtSize = 12;

    private Tools tools=new Tools();

    public DSSFDmg(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.elems=SpellTypesManager.getInstance();

        TextView nAtkTxt = mainView.findViewById(R.id.nDmgTxt);
        nAtkTxt.setText(yfa.getStats().getStatsList().getNDamageSpell() + " jets de dégâts");

        CheckBox checkNone = mainView.findViewById(R.id.dmg_type_none);
        CheckBox checkAcid = mainView.findViewById(R.id.dmg_type_acid);
        CheckBox checkFire = mainView.findViewById(R.id.dmg_type_fire);
        CheckBox checkShock = mainView.findViewById(R.id.dmg_type_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.dmg_type_frost);
        mapElemCheckbox.put("none",checkNone);
        mapElemCheckbox.put("acid",checkAcid);
        mapElemCheckbox.put("fire",checkFire);
        mapElemCheckbox.put("shock",checkShock);
        mapElemCheckbox.put("frost",checkFrost);

        chartMaker = new DSSFDmgChartMaker((BarChart)mainView.findViewById(R.id.bar_chart_dmg),mapElemCheckbox,mC);
        setCheckboxListeners();
        initChartSelectEvent();
        initPieChart();
        subManager=new DSSFDmgInfoManager(mainView,mapElemCheckbox,mC);
    }

    private void setCheckboxListeners() {
        for(String elem : elems.getListDmgKeys()){
            mapElemCheckbox.get(elem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chartMaker.resetChart();
                    chartMaker.buildChart();
                    resetPieChart();
                    buildPieChart();
                    subManager.addInfos(null);
                }
            });
        }
    }

    private void initChartSelectEvent() {
        chartMaker.getChart().setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                resetPieChart();
                selectedDamagesShortList =chartMaker.getMapIStepSelectedDamagesShortList().get((int)e.getX());
                buildPieChart();
                subManager.setSubSelectionBracket(chartMaker.getLabels().get((int)e.getX()));
                subManager.addInfos(selectedDamagesShortList);
            }

            @Override
            public void onNothingSelected() {
                if(chartMaker.getChart().isFocusable()) {
                    reset();
                    resetPieChart();
                    subManager.addInfos(null);
                    buildPieChart();
                }
            }
        });
    }

    // Pie chart

    private void initPieChart(){
        pieChart = mainView.findViewById(R.id.pie_chart_dmg_percent);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        buildPieChart();
        pieChart.animateXY(100,1000);
        pieChart.getLegend().setEnabled(false);
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

        DamagesShortList list;
        if(selectedDamagesShortList !=null && selectedDamagesShortList.size()!=0){
            list= selectedDamagesShortList;
        } else {
            list= yfa.getStats().getStatsList().getDamageShortList();
        }
        int totalDmg=list.getSumDmgTot();
        for(String elem : elems.getListDmgKeys()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                float percent = 100f * (list.filterByElem(elem).getDmgSum() / (float) totalDmg);
                if (percent > 0f) {
                    entries.add(new PieEntry(percent, "", new LargeValueFormatter().getFormattedValue((int) list.filterByElem(elem).getDmgSum()) + " dégats " + elems.getName(elem)));
                    colorList.add(mC.getColor(elems.getColorIdSombre(elem)));
                }
            }
        }
        PieDataSet dataset = new PieDataSet(entries,"");
        dataset.setValueTextSize(infoTxtSize);
        dataset.setColors(colorList);
        dataset.setSliceSpace(3);
        return dataset;
    }

    // Resets

    public void reset() {
        for(String elem : elems.getListDmgKeys()){
            mapElemCheckbox.get(elem).setChecked(true);
        }
        chartMaker.resetChart();
        chartMaker.buildChart();
        subManager.addInfos(null);
        resetPieChart();
        buildPieChart();
    }


    private void resetPieChart() {
        selectedDamagesShortList =new DamagesShortList();
        pieChart.invalidate();
        pieChart.setCenterText("");
        pieChart.highlightValue(null);
    }
}
