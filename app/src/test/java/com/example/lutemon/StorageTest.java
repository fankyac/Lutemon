package com.example.lutemon;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for the Storage class
 */
public class StorageTest {
    private Storage storage;
    private Lutemon whiteLutemon;
    private Lutemon blackLutemon;

    @Before
    public void setUp() {
        storage = new Storage();
        whiteLutemon = new Lutemon("White1", "white");
        blackLutemon = new Lutemon("Black1", "black");
    }

    @Test
    public void testAddLutemon() {
        int id = storage.addLutemon(whiteLutemon);
        assertEquals(1, storage.getLutemonCount());
        assertEquals(whiteLutemon, storage.getLutemon(id));
        assertEquals(Storage.HOME, storage.getLutemonLocation(id));
    }

    @Test
    public void testRemoveLutemon() {
        int id = storage.addLutemon(whiteLutemon);
        Lutemon removed = storage.removeLutemon(id);
        assertEquals(whiteLutemon, removed);
        assertEquals(0, storage.getLutemonCount());
        assertNull(storage.getLutemonLocation(id));
    }

    @Test
    public void testMoveLutemon() {
        int id = storage.addLutemon(whiteLutemon);
        
        // Move to training
        assertTrue(storage.moveLutemon(id, Storage.TRAINING));
        assertEquals(Storage.TRAINING, storage.getLutemonLocation(id));
        
        // Move to battle
        assertTrue(storage.moveLutemon(id, Storage.BATTLE));
        assertEquals(Storage.BATTLE, storage.getLutemonLocation(id));
        
        // Invalid move
        assertFalse(storage.moveLutemon(id, "invalid_location"));
        assertEquals(Storage.BATTLE, storage.getLutemonLocation(id));
    }

    @Test
    public void testGetLutemonsByLocation() {
        int whiteId = storage.addLutemon(whiteLutemon);
        int blackId = storage.addLutemon(blackLutemon);
        
        // Initially both are at home
        List<Lutemon> homeList = storage.getLutemonsByLocation(Storage.HOME);
        assertEquals(2, homeList.size());
        assertTrue(homeList.contains(whiteLutemon));
        assertTrue(homeList.contains(blackLutemon));
        
        // Move one to training
        storage.moveLutemon(whiteId, Storage.TRAINING);
        List<Lutemon> trainingList = storage.getLutemonsByLocation(Storage.TRAINING);
        assertEquals(1, trainingList.size());
        assertTrue(trainingList.contains(whiteLutemon));
        
        // Check home list again
        homeList = storage.getLutemonsByLocation(Storage.HOME);
        assertEquals(1, homeList.size());
        assertTrue(homeList.contains(blackLutemon));
    }

    @Test
    public void testHealingWhenReturningHome() {
        int id = storage.addLutemon(whiteLutemon);
        int maxHealth = whiteLutemon.getMaxHealth();
        
        // Move to battle
        storage.moveLutemon(id, Storage.BATTLE);
        
        // Simulate damage
        whiteLutemon.defense(15); // Take some damage
        assertTrue(whiteLutemon.getHealth() < maxHealth);
        
        // Return home
        storage.moveLutemon(id, Storage.HOME);
        assertEquals(maxHealth, whiteLutemon.getHealth());
    }

    @Test
    public void testInvalidMoves() {
        // Try to move non-existent Lutemon
        assertFalse(storage.moveLutemon(999, Storage.TRAINING));
        
        int id = storage.addLutemon(whiteLutemon);
        // Try to move to invalid location
        assertFalse(storage.moveLutemon(id, "invalid_location"));
        assertEquals(Storage.HOME, storage.getLutemonLocation(id));
    }

    @Test
    public void testGetNonExistentLutemon() {
        assertNull(storage.getLutemon(999));
        assertNull(storage.getLutemonLocation(999));
    }
}