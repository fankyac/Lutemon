package com.example.lutemon;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages global statistics and individual Lutemon stats
 */
public class GlobalStats implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int totalBattles;
    private int totalTrainings;
    private Map<Integer, LutemonStats> lutemonStats;

    public GlobalStats() {
        this.totalBattles = 0;
        this.totalTrainings = 0;
        this.lutemonStats = new HashMap<>();
    }

    /**
     * Gets stats for a specific Lutemon
     */
    public LutemonStats getLutemonStats(int lutemonId) {
        return lutemonStats.get(lutemonId);
    }

    /**
     * Adds stats for a Lutemon
     */
    public void addLutemonStats(LutemonStats stats) {
        lutemonStats.put(stats.getLutemonId(), stats);
    }

    /**
     * Records a battle between two Lutemons
     */
    public void recordBattle(int winnerId, int loserId) {
        totalBattles++;
        LutemonStats winnerStats = lutemonStats.get(winnerId);
        LutemonStats loserStats = lutemonStats.get(loserId);
        
        if (winnerStats != null) winnerStats.recordWin();
        if (loserStats != null) loserStats.recordLoss();
    }

    /**
     * Records a training session for a Lutemon
     */
    public void recordTraining(Lutemon lutemon) {
        totalTrainings++;
        LutemonStats stats = lutemonStats.get(lutemon.getId());
        if (stats != null) {
            stats.recordTraining();
            stats.recordStats(lutemon);
        }
    }

    /**
     * Gets stats for color distribution
     */
    public Map<String, Integer> getColorDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        for (LutemonStats stats : lutemonStats.values()) {
            String color = stats.getColorType();
            distribution.merge(color, 1, Integer::sum);
        }
        return distribution;
    }

    /**
     * Gets all Lutemon stats
     */
    public Map<Integer, LutemonStats> getAllLutemonStats() {
        return lutemonStats;
    }

    // Getters
    public int getTotalBattles() { return totalBattles; }
    public int getTotalTrainings() { return totalTrainings; }
}