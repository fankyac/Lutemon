package com.example.lutemon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import java.util.List;

/**
 * Fragment for managing Lutemons in training
 */
public class TrainingFragment extends Fragment {
    private Storage storage;
    private LinearLayout lutemonContainer;
    private View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Get storage instance from MainActivity
        storage = ((MainActivity) requireActivity()).getStorage();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        lutemonContainer = view.findViewById(R.id.lutemon_container);
        emptyView = view.findViewById(R.id.empty_view);
        
        // Load and display Lutemons
        updateLutemonList();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLutemonList();
    }

    /**
     * Updates the list of Lutemons displayed
     */
    private void updateLutemonList() {
        lutemonContainer.removeAllViews();
        List<Lutemon> lutemons = storage.getLutemonsByLocation(Storage.TRAINING);
        
        // Show/hide empty view based on list size
        if (lutemons.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            lutemonContainer.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            lutemonContainer.setVisibility(View.VISIBLE);
        }

        for (Lutemon lutemon : lutemons) {
            View lutemonView = createLutemonView(lutemon);
            lutemonContainer.addView(lutemonView);
        }
    }

    /**
     * Creates a view for a single Lutemon
     */
    private View createLutemonView(final Lutemon lutemon) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.item_lutemon_training, lutemonContainer, false);

        TextView nameText = view.findViewById(R.id.lutemon_name);
        TextView statsText = view.findViewById(R.id.lutemon_stats);
        Button trainButton = view.findViewById(R.id.button_train);
        Button homeButton = view.findViewById(R.id.button_home);
        LutemonShapeView shapeView = view.findViewById(R.id.lutemon_shape);

        nameText.setText(getString(R.string.lutemon_name_format, 
            lutemon.getName(), lutemon.getColor()));
        updateStats(lutemon, statsText);
        shapeView.setLutemon(lutemon);

        trainButton.setOnClickListener(v -> {
            lutemon.train();
            storage.recordTraining(lutemon);
            updateStats(lutemon, statsText);
            storage.saveLutemons();
        });

        homeButton.setOnClickListener(v -> {
            storage.moveLutemon(lutemon.getId(), Storage.HOME);
            updateLutemonList();
        });

        // Navigate to stats detail on click
        view.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("lutemonId", lutemon.getId());
            args.putString("source", "training");
            Navigation.findNavController(view)
                .navigate(R.id.action_training_to_stats_detail, args);
        });

        return view;
    }

    /**
     * Updates the stats display for a Lutemon
     */
    private void updateStats(Lutemon lutemon, TextView statsText) {
        statsText.setText(getString(R.string.training_stats_format,
                lutemon.getTotalAttack(), lutemon.getAttack(), lutemon.getExperience(),
                lutemon.getDefense(), lutemon.getExperience()));
    }
}