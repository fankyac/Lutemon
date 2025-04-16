package com.example.lutemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks statistics for a single Lutemon
 */
public class LutemonStats implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int lutemonId;
    private final String colorType;
    private int battlesWon;
    private int battlesLost;
    private int trainingCount;
    
    // History of stats changes
    private List<StatPoint> attackHistory;
    private List<StatPoint> experienceHistory;

    public LutemonStats(int lutemonId, String colorType) {
        this.lutemonId = lutemonId;
        this.colorType = colorType;
        this.battlesWon = 0;
        this.battlesLost = 0;
        this.trainingCount = 0;
        this.attackHistory = new ArrayList<>();
        this.experienceHistory = new ArrayList<>();
    }

    /**
     * Records a battle win
     */
    public void recordWin() {
        battlesWon++;
    }

    /**
     * Records a battle loss
     */
    public void recordLoss() {
        battlesLost++;
    }

    /**
     * Records a training session
     */
    public void recordTraining() {
        trainingCount++;
    }

    /**
     * Records current stats as a new history point if they've changed
     */
    public void recordStats(Lutemon lutemon) {
        if (lutemon.getId() != lutemonId) {
            throw new IllegalArgumentException("Wrong Lutemon provided");
        }

        int newAttack = lutemon.getTotalAttack();
        int newExp = lutemon.getExperience();

        // Only add new points if values have changed or history is empty
        if (attackHistory.isEmpty() || attackHistory.get(attackHistory.size() - 1).value != newAttack) {
            attackHistory.add(new StatPoint(newAttack));
        }
        
        if (experienceHistory.isEmpty() || experienceHistory.get(experienceHistory.size() - 1).value != newExp) {
            experienceHistory.add(new StatPoint(newExp));
        }
    }

    // Getters
    public int getLutemonId() { return lutemonId; }
    public String getColorType() { return colorType; }
    public int getBattlesWon() { return battlesWon; }
    public int getBattlesLost() { return battlesLost; }
    public int getTrainingCount() { return trainingCount; }
    public List<StatPoint> getAttackHistory() { return attackHistory; }
    public List<StatPoint> getExperienceHistory() { return experienceHistory; }

    /**
     * Represents a point in the stat history
     */
    public static class StatPoint implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int value;

        public StatPoint(int value) {
            this.value = value;
        }

        public int getValue() { return value; }
    }
}