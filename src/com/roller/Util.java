package com.roller;

import java.util.Random;

public final class Util {
    private static final Random random = new Random();
    private Util() { }
    
    public static int[] rollDice(final int numDice, final int numSides) {
        final int[] results = new int[numDice];
        for (int i = 0; i < numDice; ++i) {
            final int r = random.nextInt(numSides) + 1;
            results[i] = r;
        }
        return results;
    }
}
