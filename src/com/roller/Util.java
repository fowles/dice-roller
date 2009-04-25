package com.roller;

import java.security.SecureRandom;

public final class Util {
    private static final SecureRandom random = new SecureRandom();
    private Util() { }
    
    public static int rollDie(final int numSides) {
        return random.nextInt(numSides) + 1;
    }
    
    public static int[] rollDice(final int numDice, final int numSides) {
        final int[] results = new int[numDice];
        for (int i = 0; i < numDice; ++i) {
            results[i] = rollDie(numSides);
        }
        return results;
    }
}
