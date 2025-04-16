package com.example.lutemon;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages storage and movement of Lutemons between different locations
 */
public class Storage {
    private static final String TAG = "LutemonStorage";
    public static final String HOME = "home";
    public static final String TRAINING = "training";
    public static final String BATTLE = "battle";
    private static final int MAX_BATTLE_LUTEMONS = 2;

    private final Map<String, List<Lutemon>> locationMap;
    private final DataManager dataManager;
    private final GlobalStats stats;

    public Storage(Context context) {
        dataManager = new DataManager(context);
        locationMap = dataManager.loadLutemons();
        stats = dataManager.loadStats();
        updateIdCounter();
        initializeStats();
    }

    /**
     * Updates the Lutemon ID counter based on loaded data
     */
    private void updateIdCounter() {
        int maxId = 0;
        for (List<Lutemon> lutemons : locationMap.values()) {
            for (Lutemon lutemon : lutemons) {
                maxId = Math.max(maxId, lutemon.getId());
            }
        }
        Lutemon.updateIdCounter(maxId);
    }

    /**
     * Initializes stats for all existing Lutemons
     */
    private void initializeStats() {
        for (List<Lutemon> lutemons : locationMap.values()) {
            for (Lutemon lutemon : lutemons) {
                if (stats.getLutemonStats(lutemon.getId()) == null) {
                    LutemonStats newStats = new LutemonStats(lutemon.getId(), lutemon.getColor());
                    stats.addLutemonStats(newStats);
                    newStats.recordStats(lutemon);
                }
            }
        }
        saveStats();
    }

    /**
     * Gets all Lutemons in all locations
     */
    public Map<String, List<Lutemon>> getAllLutemons() {
        return new HashMap<>(locationMap);
    }

    /**
     * Sets all Lutemons from imported data
     */
    public void setAllLutemons(Map<String, List<Lutemon>> newLocationMap) {
        locationMap.clear();
        locationMap.putAll(newLocationMap);
        updateIdCounter();
        initializeStats();
    }

    /**
     * Gets or creates stats for a Lutemon
     */
    private LutemonStats getOrCreateStats(Lutemon lutemon) {
        LutemonStats existingStats = stats.getLutemonStats(lutemon.getId());
        if (existingStats == null) {
            existingStats = new LutemonStats(lutemon.getId(), lutemon.getColor());
            stats.addLutemonStats(existingStats);
        }
        return existingStats;
    }

    /**
     * Adds a new Lutemon to home location
     */
    public int addLutemon(Lutemon lutemon) {
        getLutemonsByLocation(HOME).add(lutemon);
        getOrCreateStats(lutemon).recordStats(lutemon);
        saveLutemons();
        return lutemon.getId();
    }

    /**
     * Gets all Lutemons in a specific location
     */
    public List<Lutemon> getLutemonsByLocation(String location) {
        return locationMap.computeIfAbsent(location, k -> new ArrayList<>());
    }

    /**
     * Gets a Lutemon by its ID from any location
     */
    public Lutemon getLutemon(int id) {
        for (List<Lutemon> lutemons : locationMap.values()) {
            for (Lutemon lutemon : lutemons) {
                if (lutemon.getId() == id) {
                    return lutemon;
                }
            }
        }
        return null;
    }

    /**
     * Gets the location of a Lutemon
     */
    private String getLutemonLocation(int id) {
        for (Map.Entry<String, List<Lutemon>> entry : locationMap.entrySet()) {
            for (Lutemon lutemon : entry.getValue()) {
                if (lutemon.getId() == id) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Moves a Lutemon to a new location
     * @return false if move failed (e.g., battle area full)
     */
    public boolean moveLutemon(int id, String newLocation) {
        if (newLocation.equals(BATTLE) && 
            getLutemonsByLocation(BATTLE).size() >= MAX_BATTLE_LUTEMONS) {
            return false;
        }

        Lutemon lutemon = null;
        for (Map.Entry<String, List<Lutemon>> entry : locationMap.entrySet()) {
            List<Lutemon> lutemons = entry.getValue();
            for (int i = 0; i < lutemons.size(); i++) {
                if (lutemons.get(i).getId() == id) {
                    lutemon = lutemons.remove(i);
                    break;
                }
            }
            if (lutemon != null) break;
        }

        if (lutemon != null) {
            getLutemonsByLocation(newLocation).add(lutemon);
            saveLutemons();
            return true;
        }
        return false;
    }

    /**
     * Records a battle result
     */
    public void recordBattle(int winnerId, int loserId) {
        stats.recordBattle(winnerId, loserId);
        saveStats();
    }

    /**
     * Records a training session
     */
    public void recordTraining(Lutemon lutemon) {
        stats.recordTraining(lutemon);
        getOrCreateStats(lutemon).recordStats(lutemon);
    }

    /**
     * Gets the global stats
     */
    public GlobalStats getStats() {
        return stats;
    }

    /**
     * Saves Lutemons to storage
     */
    public void saveLutemons() {
        if (dataManager.saveLutemons(locationMap)) {
            Log.i(TAG, "Successfully saved Lutemons");
        } else {
            Log.e(TAG, "Failed to save Lutemons");
        }
    }

    /**
     * Saves stats to storage
     */
    public void saveStats() {
        if (dataManager.saveStats(stats)) {
            Log.i(TAG, "Successfully saved stats");
        } else {
            Log.e(TAG, "Failed to save stats");
        }
    }
}