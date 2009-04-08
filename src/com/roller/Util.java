package com.roller;

import java.security.SecureRandom;

public final class Util {
    private static final SecureRandom random = new SecureRandom();
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
