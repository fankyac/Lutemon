package com.example.lutemon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import java.util.List;

/**
 * Fragment displaying Lutemons at home and allowing them to be moved
 */
public class HomeFragment extends Fragment {
    private Storage storage;
    private LinearLayout lutemonContainer;
    private View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Get storage instance from MainActivity
        storage = ((MainActivity) requireActivity()).getStorage();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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
        List<Lutemon> lutemons = storage.getLutemonsByLocation(Storage.HOME);
        
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
                .inflate(R.layout.item_lutemon, lutemonContainer, false);

        // Set Lutemon info
        TextView nameText = view.findViewById(R.id.lutemon_name);
        TextView statsText = view.findViewById(R.id.lutemon_stats);
        Button trainButton = view.findViewById(R.id.button_train);
        Button battleButton = view.findViewById(R.id.button_battle);
        LutemonShapeView shapeView = view.findViewById(R.id.lutemon_shape);

        nameText.setText(getString(R.string.lutemon_name_format, 
            lutemon.getName(), lutemon.getColor()));
        statsText.setText(String.format("Attack: %d, Defense: %d, Exp: %d, HP: %d/%d",
                lutemon.getTotalAttack(), lutemon.getDefense(),
                lutemon.getExperience(), lutemon.getHealth(), lutemon.getMaxHealth()));
        shapeView.setLutemon(lutemon);

        // Setup buttons
        trainButton.setOnClickListener(v -> {
            storage.moveLutemon(lutemon.getId(), Storage.TRAINING);
            updateLutemonList();
        });

        battleButton.setOnClickListener(v -> {
            if (!storage.moveLutemon(lutemon.getId(), Storage.BATTLE)) {
                // Show error message if move failed (likely because battle area is full)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_battle_full),
                    Toast.LENGTH_SHORT
                ).show();
            }
            updateLutemonList();
        });

        // Navigate to stats detail on click
        view.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("lutemonId", lutemon.getId());
            args.putString("source", "home");
            Navigation.findNavController(view)
                .navigate(R.id.action_home_to_stats_detail, args);
        });

        return view;
    }
}