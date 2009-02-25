package com.roller;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RollDetails implements Serializable {
    private final String name;
    private final int numDice;
    private final boolean isDamage;

    public RollDetails(final CharSequence n, final int d, final boolean damage) {
        name = n.toString();
        numDice = d;
        isDamage = damage;
    }

    public String getName() { return name; }
    public int getNumDice() { return numDice; }
    public boolean isDamage() { return isDamage; }
}