package com.example.lutemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages battle between two Lutemons
 */
public class Battle {
    private final Storage storage;
    private Lutemon attacker;
    private Lutemon defender;
    private List<String> battleLog;
    private boolean isFirstTurn;
    private int initialFighter1Id;
    private int initialFighter2Id;
    private final Random random;
    private static final float CRITICAL_HIT_CHANCE = 0.25f;
    private static final int CRITICAL_HIT_DAMAGE = 5;

    public Battle(Storage storage) {
        this.storage = storage;
        this.battleLog = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Initializes a battle between two Lutemons
     */
    public boolean startBattle(int fighter1Id, int fighter2Id) {
        Lutemon fighter1 = storage.getLutemon(fighter1Id);
        Lutemon fighter2 = storage.getLutemon(fighter2Id);
        
        if (fighter1 == null || fighter2 == null) {
            return false;
        }

        // Set initial battle state
        attacker = fighter1;
        defender = fighter2;
        initialFighter1Id = fighter1Id;
        initialFighter2Id = fighter2Id;
        battleLog.clear();
        isFirstTurn = true;
        return true;
    }

    /**
     * Executes one attack turn and returns if battle should continue
     * @return true if battle should continue, false if battle is over
     */
    public boolean executeAttack() {
        if (attacker == null || defender == null) {
            return false;
        }

        // Record attack
        battleLog.add(String.format("%s attacks %s!", 
            attacker.getName(), defender.getName()));
        
        // Calculate base damage
        int baseDamage = Math.max(0, attacker.getTotalAttack() - defender.getDefense());
        
        // Check for critical hit
        boolean isCriticalHit = random.nextFloat() < CRITICAL_HIT_CHANCE;
        int totalDamage = baseDamage;
        
        if (isCriticalHit) {
            totalDamage += CRITICAL_HIT_DAMAGE;
            battleLog.add("Critical hit! +5 damage!");
        }

        // Apply damage
        defender.setHealth(Math.max(0, defender.getHealth() - totalDamage));
        
        // Record result
        battleLog.add(String.format("%s takes %d damage! (HP: %d/%d)",
            defender.getName(), 
            totalDamage,
            defender.getHealth(), 
            defender.getMaxHealth()));

        // Check for defeat
        if (defender.getHealth() <= 0) {
            handleDefeat();
            return false;
        }

        // Switch roles for next turn
        Lutemon temp = attacker;
        attacker = defender;
        defender = temp;
        isFirstTurn = false;

        return true;
    }

    /**
     * Handles the defeat of a Lutemon
     */
    private void handleDefeat() {
        battleLog.add(String.format("%s has been defeated!", defender.getName()));
        
        // Award experience to winner through training
        attacker.train();
        attacker.train(); // Train twice for victory
        battleLog.add(String.format("%s wins and gains experience!", attacker.getName()));

        // Record battle stats and IDs before potential reset
        int winnerId = attacker.getId();
        int loserId = defender.getId();

        // Move winner home and heal
        storage.moveLutemon(winnerId, Storage.HOME);
        attacker.heal();

        // Reset defeated Lutemon to initial stats
        defender.resetStats();
        defender.heal();
        storage.moveLutemon(loserId, Storage.HOME);
        
        // Record battle outcome after stats are finalized
        storage.recordBattle(winnerId, loserId);
        storage.getStats().getLutemonStats(loserId).recordStats(defender);
        storage.getStats().getLutemonStats(winnerId).recordStats(attacker);
        
        battleLog.add(String.format("%s returns to home to recover!", defender.getName()));

        // Save all changes
        storage.saveLutemons();
        storage.saveStats();
    }

    /**
     * Gets the battle log
     */
    public List<String> getBattleLog() {
        return battleLog;
    }

    /**
     * Gets the current attacker
     */
    public Lutemon getAttacker() {
        return attacker;
    }

    /**
     * Gets the current defender
     */
    public Lutemon getDefender() {
        return defender;
    }
}