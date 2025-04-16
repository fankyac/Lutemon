package com.example.lutemon;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Lutemon class
 */
public class LutemonTest {
    private Lutemon whiteLutemon;
    private Lutemon blackLutemon;

    @Before
    public void setUp() {
        whiteLutemon = new Lutemon("Whitey", "white");
        blackLutemon = new Lutemon("Dark", "black");
    }

    @Test
    public void testInitialStats() {
        // Test white Lutemon initial stats
        assertEquals("white", whiteLutemon.getColor());
        assertEquals(5, whiteLutemon.getAttack());
        assertEquals(4, whiteLutemon.getDefense());
        assertEquals(20, whiteLutemon.getMaxHealth());
        assertEquals(20, whiteLutemon.getHealth());
        assertEquals(0, whiteLutemon.getExperience());

        // Test black Lutemon initial stats
        assertEquals("black", blackLutemon.getColor());
        assertEquals(9, blackLutemon.getAttack());
        assertEquals(0, blackLutemon.getDefense());
        assertEquals(16, blackLutemon.getMaxHealth());
        assertEquals(16, blackLutemon.getHealth());
        assertEquals(0, blackLutemon.getExperience());
    }

    @Test
    public void testTraining() {
        // Initial check
        assertEquals(5, whiteLutemon.getAttack()); // Base attack
        assertEquals(5, whiteLutemon.getTotalAttack()); // Total attack (no experience yet)
        
        // After training
        whiteLutemon.train();
        assertEquals(1, whiteLutemon.getExperience());
        assertEquals(5, whiteLutemon.getAttack()); // Base attack remains unchanged
        assertEquals(6, whiteLutemon.getTotalAttack()); // 5 base + 1 experience
    }

    @Test
    public void testHealing() {
        // Simulate damage by direct battle
        blackLutemon.attack(whiteLutemon);
        int damagedHealth = whiteLutemon.getHealth();
        assertTrue(damagedHealth < whiteLutemon.getMaxHealth());

        // Test healing
        whiteLutemon.heal();
        assertEquals(whiteLutemon.getMaxHealth(), whiteLutemon.getHealth());
    }

    @Test
    public void testBattle() {
        // Black Lutemon attacks White Lutemon
        boolean survived = blackLutemon.attack(whiteLutemon);
        
        // Expected damage = attacker's total attack - defender's defense = 9 - 4 = 5
        int expectedHealth = whiteLutemon.getMaxHealth() - (blackLutemon.getTotalAttack() - whiteLutemon.getDefense());
        assertEquals(expectedHealth, whiteLutemon.getHealth());
        assertTrue(survived);
    }

    @Test
    public void testExperienceEffect() {
        // Initial state
        assertEquals(9, blackLutemon.getAttack());
        assertEquals(9, blackLutemon.getTotalAttack());
        
        // After training twice
        blackLutemon.train();
        blackLutemon.train();
        
        // Base attack should remain same
        assertEquals(9, blackLutemon.getAttack());
        // Total attack should increase with experience
        assertEquals(11, blackLutemon.getTotalAttack()); // 9 base + 2 experience
        blackLutemon.train();
        
        // Base attack should remain same
        assertEquals(9, blackLutemon.getAttack());
        // Total attack should increase with experience
        assertEquals(12, blackLutemon.getTotalAttack()); // 9 base + 3 experience
    }


    @Test(expected = IllegalArgumentException.class)
    public void testInvalidColor() {
        new Lutemon("Invalid", "purple"); // Should throw IllegalArgumentException
    }

    @Test
    public void testToString() {
        String stats = whiteLutemon.toString();
        assertTrue(stats.contains("Whitey"));
        assertTrue(stats.contains("white"));
        assertTrue(stats.contains("att: 5"));
        assertTrue(stats.contains("def: 4"));
    }
}