package com.example.lutemon;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import java.util.List;

/**
 * Fragment for managing battles between Lutemons
 */
public class BattleFragment extends Fragment {
    private static final String TAG = "LutemonBattle";
    private Storage storage;
    private Battle battle;
    private LinearLayout battleLogContainer;
    private View battleLogScroll;
    private TextView statusMessage;
    private Button startBattleButton;
    private BattleArenaView battleArena;
    private List<Lutemon> battleLutemons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Get storage instance from MainActivity and create battle
        storage = ((MainActivity) requireActivity()).getStorage();
        battle = new Battle(storage);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_battle, container, false);
        
        // Find views
        battleLogContainer = view.findViewById(R.id.battle_log_container);
        battleLogScroll = view.findViewById(R.id.battle_log_scroll);
        statusMessage = view.findViewById(R.id.status_message);
        startBattleButton = view.findViewById(R.id.button_start_battle);
        battleArena = view.findViewById(R.id.battle_arena);

        // Setup battle button
        startBattleButton.setOnClickListener(v -> startBattle());

        // Initial update
        updateBattleArea();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBattleArea();
        clearBattleLog();
    }

    /**
     * Updates the battle area display based on available fighters
     */
    private void updateBattleArea() {
        battleLutemons = storage.getLutemonsByLocation(Storage.BATTLE);

        if (battleLutemons.size() < 2) {
            // Not enough fighters
            statusMessage.setText(R.string.waiting_for_fighters);
            startBattleButton.setVisibility(View.GONE);
            battleArena.setVisibility(View.INVISIBLE);
        } else {
            // Show fighters
            statusMessage.setText(R.string.ready_to_battle);
            startBattleButton.setVisibility(View.VISIBLE);
            battleArena.setVisibility(View.VISIBLE);
            
            // Set fighters in battle arena
            Lutemon fighter1 = battleLutemons.get(0);
            Lutemon fighter2 = battleLutemons.get(1);
            battleArena.setFighters(fighter1, fighter2);
            updateHealthBars();
        }
    }

    /**
     * Updates health bars in the battle arena
     */
    private void updateHealthBars() {
        if (battleLutemons.size() >= 2) {
            Lutemon fighter1 = battleLutemons.get(0);
            Lutemon fighter2 = battleLutemons.get(1);
            battleArena.updateHealth(
                (fighter1.getHealth() * 100) / fighter1.getMaxHealth(),
                (fighter2.getHealth() * 100) / fighter2.getMaxHealth()
            );
        }
    }

    /**
     * Starts a battle between the available fighters
     */
    private void startBattle() {
        if (battleLutemons.size() < 2) {
            return; // Should never happen as button is hidden
        }

        clearBattleLog();
        
        // Get the first two Lutemons in battle area
        Lutemon lutemon1 = battleLutemons.get(0);
        Lutemon lutemon2 = battleLutemons.get(1);

        if (!battle.startBattle(lutemon1.getId(), lutemon2.getId())) {
            Log.e(TAG, "Battle initialization failed");
            return;
        }

        // Hide battle button during battle
        startBattleButton.setVisibility(View.GONE);
        statusMessage.setText(R.string.battle_in_progress);

        Log.i(TAG, String.format("Starting battle between %s and %s", 
            lutemon1.getName(), lutemon2.getName()));

        // Start battle turns with animation
        executeBattleTurns();
    }

    /**
     * Executes battle turns with animations and UI updates
     */
    private void executeBattleTurns() {
        // Get current fighters
        Lutemon attacker = battle.getAttacker();
        Lutemon defender = battle.getDefender();

        // Execute the attack after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Show attack animation
            battleArena.showDamageEffect(defender == battleLutemons.get(1));
            
            // Execute attack
            if (battle.executeAttack()) {
                // Update health bars
                updateHealthBars();
                
                // Show latest battle log entries
                displayBattleLog();
                
                // Schedule next attack with delay
                new Handler(Looper.getMainLooper()).postDelayed(
                    this::executeBattleTurns, 1500); // 1.5s between attacks
            } else {
                // Battle is over
                displayBattleLog();
                // Show winner
                battleArena.showWinner(battle.getAttacker().getName());
                // Add extra delay before showing results
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    updateBattleArea(); // This will reset the views
                }, 3000); // 3s delay to read final result and see winner
            }
        }, 1000); // 1s pre-attack delay
    }

    /**
     * Displays the battle log
     */
    private void displayBattleLog() {
        battleLogContainer.removeAllViews();
        List<String> logs = battle.getBattleLog();
        
        // Display logs in normal order (oldest first)
        for (int i = 0; i < logs.size(); i++) {
            TextView logText = new TextView(requireContext());
            logText.setText(logs.get(i));
            logText.setTextSize(14);
            logText.setPadding(8, 4, 8, 4);

            // Highlight the newest log
            if (i == logs.size() - 1) {
                logText.setBackgroundColor(0x33FF5722); // Semi-transparent orange
                logText.setTextSize(16);
                logText.setPadding(16, 8, 16, 8);
            }

            battleLogContainer.addView(logText); // Add logs in sequence
        }

        // Scroll to bottom
        battleLogScroll.post(() -> {
            // Using smooth scroll for better UX
            ((NestedScrollView)battleLogScroll).smoothScrollTo(0,
                battleLogContainer.getHeight());
        });
    }

    /**
     * Clears the battle log
     */
    private void clearBattleLog() {
        if (battleLogContainer != null) {
            battleLogContainer.removeAllViews();
        }
    }
}