package com.roller;

public class RollDetails {
    private final CharSequence name;
    private final int numDice;
    private final boolean isDamage;

    public RollDetails(final CharSequence n, final int d, final boolean damage) {
        name = n;
        numDice = d;
        isDamage = damage;
    }

    public CharSequence getName() { return name; }
    public int getNumDice() { return numDice; }
    public boolean isDamage() { return isDamage; }
}