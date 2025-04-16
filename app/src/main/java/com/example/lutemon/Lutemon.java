package com.example.lutemon;

import java.io.Serializable;
import java.util.Random;

public class Lutemon implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 0;
    private final int id;
    private final String name;
    private final String color;
    private int attack;
    private int defense;
    private int experience;
    private int maxHealth;
    private int health;
    private int[] shapeIndices;

    // Initial stat values by color
    private static final int[] WHITE_STATS = {5, 4, 20};   // attack, defense, maxHealth
    private static final int[] GREEN_STATS = {6, 3, 19};
    private static final int[] PINK_STATS = {7, 2, 18};
    private static final int[] ORANGE_STATS = {8, 1, 17};
    private static final int[] BLACK_STATS = {9, 0, 16};

    public Lutemon(String name, String color) {
        this.id = ++idCounter;
        this.name = name;
        this.color = color;
        this.experience = 0;
        
        // Set initial stats based on color
        int[] stats = getInitialStats(color);
        this.attack = stats[0];
        this.defense = stats[1];
        this.maxHealth = stats[2];
        this.health = this.maxHealth;
        
        generateShape();
    }

    private int[] getInitialStats(String color) {
        switch (color.toLowerCase()) {
            case "white": return WHITE_STATS;
            case "green": return GREEN_STATS;
            case "pink": return PINK_STATS;
            case "orange": return ORANGE_STATS;
            case "black": return BLACK_STATS;
            default: return WHITE_STATS;
        }
    }

    /**
     * Generates a random shape for the Lutemon
     */
    private void generateShape() {
        Random random = new Random();
        int points = random.nextInt(3) + 3; // 3-5 points
        shapeIndices = new int[points];
        
        // Generate random indices that will create the shape vertices
        for (int i = 0; i < points; i++) {
            shapeIndices[i] = random.nextInt(8); // 0-7 for shape variation
        }
    }

    /**
     * Gets training bonus based on color
     */
    private int getTrainingBonus() {
        switch (color.toLowerCase()) {
            case "white": return 2;
            case "green": return 3;
            case "pink": return 4;
            case "orange": return 5;
            case "black": return 6;
            default: return 2;
        }
    }

    /**
     * Trains the Lutemon, increasing attack and experience
     */
    public void train() {
        attack += getTrainingBonus();
        experience++;
    }

    /**
     * Attacks another Lutemon
     * @return true if target survived, false if defeated
     */
    public boolean attack(Lutemon target) {
        int damage = Math.max(0, getTotalAttack() - target.getDefense());
        target.health = Math.max(0, target.health - damage);
        return target.health > 0;
    }

    /**
     * Resets Lutemon stats to their initial values based on color
     */
    public void resetStats() {
        int[] initialStats = getInitialStats(color);
        this.attack = initialStats[0];
        this.defense = initialStats[1];
        this.maxHealth = initialStats[2];
        this.experience = 0;
    }

    /**
     * Restores health to maximum
     */
    public void heal() {
        health = maxHealth;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public int getAttack() { return attack; }
    public int getTotalAttack() { return attack + experience; }
    public int getDefense() { return defense; }
    public int getExperience() { return experience; }
    public int getMaxHealth() { return maxHealth; }
    public int getHealth() { return health; }

    // Setters for import/export
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public void setHealth(int health) { this.health = health; }

    /**
     * Gets number of points in the Lutemon's shape
     */
    public int getShapePointCount() {
        return shapeIndices.length;
    }

    /**
     * Gets relative radius for a point in the shape
     */
    public float getShapeRadius(int index) {
        if (index < 0 || index >= shapeIndices.length) return 0.5f;
        return (shapeIndices[index] + 4f) / 12f; // Convert 0-7 to ~0.33-0.92
    }

    /**
     * Updates the global ID counter
     */
    public static void updateIdCounter(int maxId) {
        idCounter = maxId;
    }
}