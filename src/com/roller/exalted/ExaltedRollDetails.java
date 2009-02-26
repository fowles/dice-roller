package com.roller.exalted;

import java.io.Serializable;
import java.util.Random;

@SuppressWarnings("serial")
public class ExaltedRollDetails implements Serializable {
    private static final Random random = new Random();
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
    
    CharSequence calculateResults(final int stunt) {
        final int dice = numDice;
        final boolean damage = isDamage;
        
        final StringBuilder rolls = new StringBuilder();
        int successes = 0;
        boolean botchable = true;
        for (int i = 0; i < dice; ++i) {
            final int r = random.nextInt(10) + 1;
            
            if (i > 0) { rolls.append(", "); }
            rolls.append(r);
            
            if (r != 1) { botchable = false; } 
            if (r >= 7) {
                successes += !damage && r == 10 ? 2 : 1;
            }
        }
        
        final StringBuilder result = new StringBuilder();
        result.append("Successes: ");
        if (botchable && successes == 0) {
            result.append("BOTCH");
        } else {
            result.append(successes);
        }
        result.append("\n").append(rolls);
        return result;
    }
}