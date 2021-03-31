package com.bayraktar.scrum.ui.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bayraktar.scrum.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {

    private ChartViewModel mViewModel;

    HorizontalBarChart chart_projects;
    PieChart chart_tasks;

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        chart_projects = view.findViewById(R.id.chart_projects);
        chart_tasks = view.findViewById(R.id.chart_tasks);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartViewModel.class);
        // TODO: Use the ViewModel
        setChart_projects();
        setChart_tasks();
    }

    private void setChart_projects() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 4f, "Proje 1"));
        entries.add(new BarEntry(1f, new float[]{0f, 9f}, "Proje 2"));

        BarDataSet barDataSet = new BarDataSet(entries, "Proje 1");
        int color1 = ContextCompat.getColor(getContext(), R.color.colorGreen);
        int color2 = ContextCompat.getColor(getContext(), R.color.colorOrange);
        barDataSet.setValueTextSize(12f);
        barDataSet.setBarBorderWidth(1f);
        barDataSet.setColors(color1);
        barDataSet.setColors(color2);
        barDataSet.setStackLabels(new String[]{"P 1", "P 2"});

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(15f);

        chart_projects.setData(barData);
        chart_projects.setDrawValueAboveBar(true);

        chart_projects.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Proje 1", "Proje 2"}));
        chart_projects.getXAxis().setGranularity(1f);
        chart_projects.getXAxis().setGranularityEnabled(true);

        Description description = new Description();
        description.setText("Projelerin toplam durumlarını gösteren grafikler");
        description.setEnabled(true);
        chart_projects.setDescription(description);
    }

    private void setChart_tasks() {

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1, "Aktif"));
        entries.add(new PieEntry(2, "Tamamlandı"));
        entries.add(new PieEntry(2, "Askıda"));
        entries.add(new PieEntry(2, "Bekliyor"));

        int color1 = ContextCompat.getColor(getContext(), R.color.colorGreen);
        int color2 = ContextCompat.getColor(getContext(), R.color.colorDarkGrey);
        int color3 = ContextCompat.getColor(getContext(), R.color.colorBlue);
        int color4 = ContextCompat.getColor(getContext(), R.color.colorOrange);
        int color0 = ContextCompat.getColor(getContext(), android.R.color.transparent);
        int colorWhite = ContextCompat.getColor(getContext(), R.color.colorWhite);

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(color1, color2, color3, color4);
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setValueTextColor(colorWhite);
        pieDataSet.setSliceSpace(4);
        pieDataSet.setValueLineWidth(9f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieData.setHighlightEnabled(true);
        chart_tasks.setData(pieData);
        chart_tasks.setCenterText("Görev Grafiği");
        chart_tasks.setCenterTextSize(15f);
        chart_tasks.setEntryLabelColor(color0);
        chart_tasks.setDrawMarkers(true);

        Description description = new Description();
        description.setText("Görevlerin toplam durumlarını gösterir");
        description.setEnabled(true);
        chart_tasks.setDescription(description);
        chart_projects.invalidate(); // refresh
    }

}