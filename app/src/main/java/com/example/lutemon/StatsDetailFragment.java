package com.example.lutemon;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying detailed Lutemon statistics
 */
public class StatsDetailFragment extends Fragment {
    private Storage storage;
    private int lutemonId;
    private String source;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_detail, container, false);
        
        // Get arguments and storage
        if (getArguments() != null) {
            lutemonId = getArguments().getInt("lutemonId", -1);
            source = getArguments().getString("source", "home");
        }
        storage = ((MainActivity) requireActivity()).getStorage();

        // Check for valid lutemonId
        if (lutemonId == -1) {
            requireActivity().onBackPressed();
            return view;
        }
        
        Lutemon lutemon = storage.getLutemon(lutemonId);
        if (lutemon == null) {
            requireActivity().onBackPressed();
            return view;
        }

        // Set up basic info
        LutemonShapeView shapeView = view.findViewById(R.id.lutemon_shape);
        TextView nameText = view.findViewById(R.id.lutemon_name);
        TextView statsText = view.findViewById(R.id.lutemon_stats);
        
        shapeView.setLutemon(lutemon);
        nameText.setText(String.format("%s (%s)", lutemon.getName(), lutemon.getColor()));
        statsText.setText(String.format("Attack: %d, Defense: %d, Exp: %d",
                lutemon.getTotalAttack(), lutemon.getDefense(), lutemon.getExperience()));

        // Set up battle stats
        LutemonStats stats = storage.getStats().getLutemonStats(lutemonId);
        if (stats != null) {
            TextView battlesWonText = view.findViewById(R.id.battles_won);
            TextView trainingCountText = view.findViewById(R.id.training_count);
            
            battlesWonText.setText(String.valueOf(stats.getBattlesWon()));
            trainingCountText.setText(String.valueOf(stats.getTrainingCount()));

            // Set up charts
            setupChart(view.findViewById(R.id.attack_chart), 
                stats.getAttackHistory(), "Attack", Color.RED);
            setupChart(view.findViewById(R.id.experience_chart), 
                stats.getExperienceHistory(), "Experience", Color.GREEN);
        }

        return view;
    }

    private void setupChart(LineChart chart, List<LutemonStats.StatPoint> history, 
            String label, int color) {
        if (history.isEmpty()) {
            chart.setNoDataText("No data available");
            chart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        
        // Create evenly distributed entries
        for (int i = 0; i < history.size(); i++) {
            entries.add(new Entry(i, history.get(i).getValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false); // Hide x-axis labels
        
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(true);
        chart.invalidate();
    }
}