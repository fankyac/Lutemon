package com.example.lutemon;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

/**
 * Fragment for creating new Lutemons
 */
public class CreateFragment extends Fragment {
    private static final String TAG = "LutemonCreate";
    private Storage storage;
    private EditText nameInput;
    private RadioGroup colorGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Get storage instance from MainActivity
        storage = ((MainActivity) requireActivity()).getStorage();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        // Initialize views
        nameInput = view.findViewById(R.id.name_input);
        colorGroup = view.findViewById(R.id.color_group);
        Button createButton = view.findViewById(R.id.create_button);

        // Setup create button
        createButton.setOnClickListener(v -> createLutemon());

        return view;
    }

    /**
     * Creates a new Lutemon with the input values
     */
    private void createLutemon() {
        String name = nameInput.getText().toString().trim();
        Log.d(TAG, "Attempting to create Lutemon with name: " + name);
        
        // Validate name
        if (name.isEmpty()) {
            Log.w(TAG, "Creation failed: Empty name");
            nameInput.setError(getString(R.string.error_name_required));
            return;
        }

        // Get selected color
        String color;
        int selectedId = colorGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_white) {
            color = "white";
        } else if (selectedId == R.id.radio_green) {
            color = "green";
        } else if (selectedId == R.id.radio_pink) {
            color = "pink";
        } else if (selectedId == R.id.radio_orange) {
            color = "orange";
        } else if (selectedId == R.id.radio_black) {
            color = "black";
        } else {
            Log.w(TAG, "Creation failed: No color selected");
            Toast.makeText(getContext(), getString(R.string.error_color_required), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and add Lutemon
        Lutemon newLutemon = new Lutemon(name, color);
        int id = storage.addLutemon(newLutemon);
        Log.i(TAG, String.format("Successfully created %s Lutemon: %s (ID: %d)",
            color, name, id));

        // Show success message
        Toast.makeText(getContext(),
            getString(R.string.creation_success, color, name),
            Toast.LENGTH_SHORT).show();

        // Clear inputs
        nameInput.setText("");
        colorGroup.clearCheck();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Clear inputs when returning to this fragment
        nameInput.setText("");
        colorGroup.clearCheck();
    }
}