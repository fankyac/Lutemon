package com.example.lutemon;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for the Battle class
 */
public class BattleTest {
    private Storage storage;
    private Battle battle;
    private Lutemon whiteLutemon;
    private Lutemon blackLutemon;
    private int whiteId;
    private int blackId;

    @Before
    public void setUp() {
        storage = new Storage();
        battle = new Battle(storage);
        
        // Create test Lutemons
        whiteLutemon = new Lutemon("White1", "white");
        blackLutemon = new Lutemon("Black1", "black");
        
        // Add to storage and move to battle area
        whiteId = storage.addLutemon(whiteLutemon);
        blackId = storage.addLutemon(blackLutemon);
        storage.moveLutemon(whiteId, Storage.BATTLE);
        storage.moveLutemon(blackId, Storage.BATTLE);
    }

    @Test
    public void testBattleInitialization() {
        assertTrue(battle.startBattle(whiteId, blackId));
        assertTrue(battle.isOngoing());
        
        List<String> log = battle.getBattleLog();
        assertFalse(log.isEmpty());
        assertTrue(log.get(0).contains("White1"));
        assertTrue(log.get(1).contains("Black1"));
    }

    @Test
    public void testInvalidBattleStart() {
        // Try to battle with non-existent Lutemon
        assertFalse(battle.startBattle(999, blackId));
        
        // Move one Lutemon out of battle area
        storage.moveLutemon(whiteId, Storage.HOME);
        assertFalse(battle.startBattle(whiteId, blackId));
    }

    @Test
    public void testBattleExecution() {
        battle.startBattle(whiteId, blackId);
        
        // Execute first turn
        assertTrue(battle.executeTurn());
        
        List<String> log = battle.getBattleLog();
        assertTrue(log.stream().anyMatch(s -> s.contains("attacks")));
        assertTrue(log.stream().anyMatch(s -> s.contains("managed to escape death")));
    }

    @Test
    public void testBattleToCompletion() {
        battle.startBattle(whiteId, blackId);
        
        // Battle until completion
        while(battle.executeTurn()) {
            assertTrue(battle.isOngoing());
        }
        
        // After battle ends
        assertFalse(battle.isOngoing());
        List<String> log = battle.getBattleLog();
        assertTrue(log.stream().anyMatch(s -> s.contains("has been defeated")));
        assertTrue(log.stream().anyMatch(s -> s.contains("wins and gains experience")));
        
        // Check that loser was removed and winner was moved home
        assertTrue(storage.getLutemonCount() == 1);
        
        // Find the surviving Lutemon and verify its location
        Lutemon survivor = storage.getLutemon(whiteId);
        if (survivor == null) {
            survivor = storage.getLutemon(blackId);
        }
        assertNotNull(survivor);
        assertEquals(Storage.HOME, storage.getLutemonLocation(survivor.getId()));
        assertEquals(1, survivor.getExperience()); // Winner should gain 1 experience
    }

    @Test
    public void testBattleLogContents() {
        battle.startBattle(whiteId, blackId);
        battle.executeTurn();
        
        List<String> log = battle.getBattleLog();
        
        // Check initial state logging
        assertTrue(log.get(0).contains(whiteLutemon.getName()));
        assertTrue(log.get(1).contains(blackLutemon.getName()));
        
        // Check attack logging
        assertTrue(log.stream().anyMatch(s -> s.contains("attacks")));
        
        // Check state update logging
        assertTrue(log.stream().anyMatch(s -> 
            s.contains(whiteLutemon.getName()) && s.contains("health")));
        assertTrue(log.stream().anyMatch(s -> 
            s.contains(blackLutemon.getName()) && s.contains("health")));
    }

    @Test
    public void testNoBattleExecutionBeforeStart() {
        assertFalse(battle.executeTurn()); // Should fail if battle hasn't started
        assertTrue(battle.getBattleLog().isEmpty());
    }
}