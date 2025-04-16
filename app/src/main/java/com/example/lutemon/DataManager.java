package com.example.lutemon;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Manages data persistence for Lutemons and their stats
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static final String LUTEMONS_FILE = "lutemons.dat";
    private static final String STATS_FILE = "stats.dat";
    private static final String EXPORT_FILE = "lutemons.json";
    private final Context context;

    public DataManager(Context context) {
        this.context = context;
    }

    /**
     * Exports Lutemons to external storage
     */
    public boolean exportLutemons(Map<String, List<Lutemon>> locationMap) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File exportFile = new File(downloadsDir, EXPORT_FILE);

            // Create JSON structure
            JSONObject json = new JSONObject();
            
            for (Map.Entry<String, List<Lutemon>> entry : locationMap.entrySet()) {
                JSONArray lutemons = new JSONArray();
                for (Lutemon lutemon : entry.getValue()) {
                    JSONObject lutemonJson = new JSONObject();
                    lutemonJson.put("id", lutemon.getId());
                    lutemonJson.put("name", lutemon.getName());
                    lutemonJson.put("color", lutemon.getColor());
                    lutemonJson.put("attack", lutemon.getAttack());
                    lutemonJson.put("defense", lutemon.getDefense());
                    lutemonJson.put("experience", lutemon.getExperience());
                    lutemonJson.put("maxHealth", lutemon.getMaxHealth());
                    lutemonJson.put("health", lutemon.getHealth());
                    lutemons.put(lutemonJson);
                }
                json.put(entry.getKey(), lutemons);
            }

            // Write to file
            FileWriter writer = new FileWriter(exportFile);
            writer.write(json.toString(2));
            writer.close();

            Log.i(TAG, "Lutemons exported successfully");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error exporting Lutemons: " + e.getMessage());
            return false;
        }
    }

    /**
     * Imports Lutemons from external storage
     */
    public Map<String, List<Lutemon>> importLutemons() {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File importFile = new File(downloadsDir, EXPORT_FILE);

            // Read JSON
            StringBuilder jsonString = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(importFile));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();

            // Parse JSON
            JSONObject json = new JSONObject(jsonString.toString());
            Map<String, List<Lutemon>> locationMap = new HashMap<>();

            for (String location : new String[]{Storage.HOME, Storage.TRAINING, Storage.BATTLE}) {
                if (json.has(location)) {
                    JSONArray lutemons = json.getJSONArray(location);
                    List<Lutemon> lutemonList = new ArrayList<>();

                    for (int i = 0; i < lutemons.length(); i++) {
                        JSONObject lutemonJson = lutemons.getJSONObject(i);
                        Lutemon lutemon = new Lutemon(
                            lutemonJson.getString("name"),
                            lutemonJson.getString("color")
                        );
                        // Update fields that might be different from defaults
                        lutemon.setAttack(lutemonJson.getInt("attack"));
                        lutemon.setDefense(lutemonJson.getInt("defense"));
                        lutemon.setExperience(lutemonJson.getInt("experience"));
                        lutemon.setMaxHealth(lutemonJson.getInt("maxHealth"));
                        lutemon.setHealth(lutemonJson.getInt("health"));
                        lutemonList.add(lutemon);
                    }
                    locationMap.put(location, lutemonList);
                } else {
                    locationMap.put(location, new ArrayList<>());
                }
            }

            Log.i(TAG, "Lutemons imported successfully");
            return locationMap;

        } catch (Exception e) {
            Log.e(TAG, "Error importing Lutemons: " + e.getMessage());
            return null;
        }
    }

    /**
     * Saves Lutemons to internal storage
     */
    public boolean saveLutemons(Map<String, List<Lutemon>> locationMap) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                context.openFileOutput(LUTEMONS_FILE, Context.MODE_PRIVATE))) {
            Map<String, ArrayList<Lutemon>> serializableMap = new HashMap<>();
            for (Map.Entry<String, List<Lutemon>> entry : locationMap.entrySet()) {
                serializableMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            oos.writeObject(serializableMap);
            Log.i(TAG, "Lutemons saved successfully");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving Lutemons: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads Lutemons from internal storage
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<Lutemon>> loadLutemons() {
        try (ObjectInputStream ois = new ObjectInputStream(
                context.openFileInput(LUTEMONS_FILE))) {
            Map<String, ArrayList<Lutemon>> loadedMap = 
                (Map<String, ArrayList<Lutemon>>) ois.readObject();
            Map<String, List<Lutemon>> resultMap = new HashMap<>();
            for (Map.Entry<String, ArrayList<Lutemon>> entry : loadedMap.entrySet()) {
                resultMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            Log.i(TAG, "Lutemons loaded successfully");
            return resultMap;
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No saved Lutemons found, creating new data");
            return createNewLutemonMap();
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "Error loading Lutemons: " + e.getMessage());
            return createNewLutemonMap();
        }
    }

    /**
     * Creates an empty Lutemon location map
     */
    private Map<String, List<Lutemon>> createNewLutemonMap() {
        Map<String, List<Lutemon>> map = new HashMap<>();
        map.put(Storage.HOME, new ArrayList<>());
        map.put(Storage.TRAINING, new ArrayList<>());
        map.put(Storage.BATTLE, new ArrayList<>());
        return map;
    }

    /**
     * Saves stats to internal storage
     */
    public boolean saveStats(GlobalStats stats) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                context.openFileOutput(STATS_FILE, Context.MODE_PRIVATE))) {
            oos.writeObject(stats);
            Log.i(TAG, "Stats saved successfully");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving stats: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads stats from internal storage
     */
    public GlobalStats loadStats() {
        try (ObjectInputStream ois = new ObjectInputStream(
                context.openFileInput(STATS_FILE))) {
            GlobalStats stats = (GlobalStats) ois.readObject();
            Log.i(TAG, "Stats loaded successfully");
            return stats;
        } catch (FileNotFoundException e) {
            Log.i(TAG, "No saved stats found, creating new data");
            return new GlobalStats();
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "Error loading stats: " + e.getMessage());
            return new GlobalStats();
        }
    }

    /**
     * Deletes all saved data
     */
    public void clearData() {
        context.deleteFile(LUTEMONS_FILE);
        context.deleteFile(STATS_FILE);
        Log.i(TAG, "All saved data cleared");
    }
}