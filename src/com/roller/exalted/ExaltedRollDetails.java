package com.roller.exalted;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ExaltedRollDetails implements Serializable {
    private final String name;
    private final int numDice;
    private final boolean isDamage;

    public ExaltedRollDetails(final CharSequence n, final int d, final boolean damage) {
        name = n.toString();
        numDice = d;
        isDamage = damage;
    }

    public String getName() { return name; }
    public int getNumDice() { return numDice; }
    public boolean isDamage() { return isDamage; }
}