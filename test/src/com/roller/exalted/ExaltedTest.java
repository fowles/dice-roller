package com.roller.exalted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class ExaltedTest {
    @Test
    public void testIsBotch() {
        assertTrue(ExaltedRoll.isBotch(new int[] { 1 }));
        assertTrue(ExaltedRoll.isBotch(new int[] { 2, 1, 3 }));
        assertTrue(ExaltedRoll.isBotch(new int[] { 2, 4, 3, 1 }));
        assertFalse(ExaltedRoll.isBotch(new int[] { 2 }));
        assertFalse(ExaltedRoll.isBotch(new int[] { 2, 3, 4 }));
        assertFalse(ExaltedRoll.isBotch(new int[] { 2, 4, 1, 8 }));
        assertFalse(ExaltedRoll.isBotch(new int[] { 2, 4, 1, 7 }));
        assertFalse(ExaltedRoll.isBotch(new int[] { 10, 4, 1, 6 }));
    }

    @Test
    public void testCountSuccesses() {
        assertEquals(0, ExaltedRoll.countSuccesses(new int[] { 1 }, false));
        assertEquals(0, ExaltedRoll.countSuccesses(new int[] { 4 }, false));
        assertEquals(1, ExaltedRoll.countSuccesses(new int[] { 7 }, false));
        assertEquals(1, ExaltedRoll.countSuccesses(new int[] { 8 }, false));
        assertEquals(1, ExaltedRoll.countSuccesses(new int[] { 9 }, false));
        assertEquals(2, ExaltedRoll.countSuccesses(new int[] { 10 }, false));
        
        assertEquals(0, ExaltedRoll.countSuccesses(new int[] { 1 }, true));
        assertEquals(0, ExaltedRoll.countSuccesses(new int[] { 4 }, true));
        assertEquals(1, ExaltedRoll.countSuccesses(new int[] { 10 }, true));
        
        assertEquals(4, ExaltedRoll.countSuccesses(new int[] { 10, 2, 8, 5, 7, 7 }, true));
        assertEquals(5, ExaltedRoll.countSuccesses(new int[] { 10, 2, 8, 5, 7, 7 }, false));
    }
}
